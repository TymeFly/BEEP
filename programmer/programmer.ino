/**
 * This sketch is designed to provide a general implantation of the Ben Eater EEPROM Programmer
 * as seen at
 *     https://github.com/beneater/eeprom-programmer
 *     
 * Rather than having the embedded application work out what needs to be programmed for each chip, 
 * this sketch provides all the basic functions of an EEPROM programmer.
 *   - Dump the contents of an EEPROM
 *   - Erase the EEPROM
 *   - Fill the EEPROM with a specific byte
 *   - Write a test pattern and verify it was written
 *   - Program an EEPROM from data streamed in over the serial port.
 *   
 * While this approach is a bit more complex than the one Ben Eater used there are a couple of advantages  
 * 
 * 1. We don't have to continually reprogram the Arduino for each chip or revision of the program
 * 2. We don't have to try and write a program in under 2Kb that's capable of writing the program we want
 *    (which may be more then 2Kb)
 */


#define EEPROM_SIZE (8 * 1024)
#define MIN_ADDRESS 0
#define MAX_ADDRESS (EEPROM_SIZE - 1)

#define BLANK_BYTE 0xff
#define WRITE_DELAY 11

#define SHIFT_DATA 2
#define SHIFT_CLK 3
#define SHIFT_LATCH 4
#define EEPROM_D0 5
#define EEPROM_D7 12
#define WRITE_EN 13

#define SERIAL_TIMEOUT 200
#define PROGRAM_TIMEOUT_SECONDS 30
#define SERIAL_POLL_DELAY_MS 25


static const char *spaces = "                                                     ";
static const byte testData[] = { 0x00, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x55, 0xaa };      // Length 11, which is prime

/*
 * Programmer initialisation entry point 
 */
void setup() {
  pinMode(SHIFT_DATA, OUTPUT);
  pinMode(SHIFT_CLK, OUTPUT);
  pinMode(SHIFT_LATCH, OUTPUT);

  digitalWrite(WRITE_EN, HIGH);
  pinMode(WRITE_EN, OUTPUT);

  Serial.setTimeout(SERIAL_TIMEOUT);
  Serial.begin(57600);
}


/**
 * CLI Entry point. All serial communications is at 57600 baud.
 * The programmer indicates it's ready for a command by sending the "+ Ready" prompt to the client. 
 * The client can send '?' over the serial port to see what commands are available.
 * The client can send '-' over the serial port to prompt the programmer to send the prompt again.
 */
void loop() {
  Serial.println("+ Ready");

  String command = readSerial();
  bool valid = false;

  command.trim();
  command.toLowerCase();

  switch (command[0]) {
    case 'd':
      valid = dump(command);
      break;

    case 'f':
      valid = fill(command);
      break;

    case 'e':
      valid = erase();
      break;

    case 't':
      valid = test();
      break;

    case 'p':
      valid = program(command);
      break;

    case '-':               // ping
      valid = true;
      break;

    case '?':
      valid = help();
      break;

    default:
      valid = false;
      break;
  }

  if (!valid) {
    Serial.print("ERROR: Invalid command: ");
    Serial.println(command);
  }
}



/**
 * The Dump Command. Used to dump data from the EEPROM to the serial port in both ASCII and hex format 
 * @param command     The command line passed from the serial port. This is in the format d[start[:end]]
 * @return            true only if the command succeeded
 */
static bool dump(String command) {
  char *index = (char *) command.c_str();
  int start = scanHex(&(++index), 4, MIN_ADDRESS);                                 // Skip leading 'd'
  int end = (*index == ':' ? scanHex(&(++index), 4, MAX_ADDRESS) : MAX_ADDRESS);   // Skip optional leading ':'
  bool valid = (*index == 0);

  if (valid) {
    dump(start, end);
  }

  return valid;
}


/*
 * Dump the contents of the EEPROM to the serial monitor in both ASCII and hex format 
 * @param start  address of the first byte to dump
 * @param end    address of the last byte to dump
 */
static void dump(int start, int end) {
  int count = 16;
  int next = start;
  char address[4];
  char hex[60];
  char ascii[20];
  char *hexPtr = hex;
  char *asciiPtr = ascii;

  while (next <= end) {
    byte data = readEEPROM(next++);

    if ((count % 8) == 0) {
      hexPtr += sprintf(hexPtr, "  ");
      asciiPtr += sprintf(asciiPtr, " ");
    }

    hexPtr += sprintf(hexPtr, "%02x ", data);
    asciiPtr += sprintf(asciiPtr, "%c", isPrintable(data) ? data : '.');

    bool newLine = ((--count == 0) || (next > end));
    if (newLine) {
      int indent = (hexPtr - hex);

      sprintf(address, "%04x:", start);

      Serial.print(address);
      Serial.print(hex);
      Serial.print(&spaces[indent]);
      Serial.print(ascii);
      Serial.println();

      hexPtr = hex;
      asciiPtr = ascii;
      count = 16;
      start = next;
    }
  }

  Serial.println();
}


/*
 * The Erase Command. Errase of the EEPROM by writing the value 0xff to all locations
 * @return            true only if the command succeeded
 */
static bool erase() {
  Serial.println("Erasing EEPROM");

  fill(MIN_ADDRESS, MAX_ADDRESS, BLANK_BYTE);

  return true;
}


/**
 * The Fill Command. Used to dump data from the EEPROM to the serial port in both ASCII and hex format 
 * @param command     The command line passed from the serial port. This is in the format d[start[:end]]
 */
static bool fill(String command) {
  char *index = (char *) command.c_str();
  int start = scanHex(&(++index), 4, MIN_ADDRESS);                                 // Skip leading 'f'
  int end = (*index == ':' ? scanHex(&(++index), 4, MAX_ADDRESS) : MAX_ADDRESS);   // Skip optional leading ':'
  int val = (*index == ':' ? scanHex(&(++index), 2, 0) : 0);                       // Skip optional leading ':'
  bool valid = (*index == 0);

  Serial.println("Filling EEPROM");

  if (valid) {
    fill(start, end, val);
  }

  return valid;
}


/*
 * Fill the EEPROM with a specific byte
 * @param start  address of the first byte to dump
 * @param end    address of the last byte to dump
 * @param val    value to write to the EEPROM
 */
static void fill(int start, int end, byte val) {
  int count = 0;
  
  for (int address = start; address <= end; address += 1) {
    writeEEPROM(address, val);

    if (++count % 64 == 0) {            // Need to send some data to keep the client from timing out
      Serial.print(".");
    }

    if ((count% 1024) == 0) {
      Serial.println();
    }
  }

  Serial.println();
}


/**
 * The Test Command. Write a pattern to the EEPROM and then read it back again. 
 * This can be used to check the validity of the EEPROM or the wiring of the programmer!
 * @return            true only if the test succeeded
 */
static bool test() {
  bool valid = true;
  Serial.println("Testing EEPROM");

  // Write Phase
  int offset = 0;
  for (int address = MIN_ADDRESS; address <= MAX_ADDRESS; address += 1) {
    byte value = testData[offset++];
    offset %= (sizeof testData);

    writeEEPROM(address, value);

    if ((address % 64) == 0) {
      Serial.print(".");
    }
    
    if ((address != 0) && (address % 1024) == 0) {
      Serial.println();
    }
  }

  Serial.println();

  // Read Phase
  offset = 0;
  for (int address = MIN_ADDRESS; address <= MAX_ADDRESS; address += 1) {
    byte expected = testData[offset++];
    byte actual = readEEPROM(address);

    if (expected != actual) {
      char buffer[50];

      sprintf(buffer, "ERROR at address %04x. Expected %04x, but was %04x", address, expected, actual);
      Serial.println(buffer);

      valid = false;
    }

    offset %= (sizeof testData);
  }

  Serial.println(valid ? "Test PASSED" : "Test FALIED");

  return true;
}


/**
* The Program Command. Used to stream data from the serial port and write it into the EEPROM.
 * The protocol for programming is:    
 *                    
 * 1. The client send the programming command
 * 2. The programmer responds with the "Programming EEPROM" message    
 * 3. The Client send up to 64 binary bytes of data to the programmer
 * 4. The programmer writes the data to the EEPROM
 * 5. The programmer tells the client the data has been written by sending the number
 *    of bytes still reaming back to the client
 * 6. If the number of bytes remaining is not zero, both sizes jump back to step 3
 * 7. When all of the data has been written to the EEPROM the main prompt is written to the serial Port
 
 * The client can aborted the programming by simply waiting for 30 seconds.
 * 
 * @param command     The command line passed from the serial port. pstart,len 
 *                    Where: 
 *                        'start' is the address of the first byte to write
 *                        'len' is the number of bytes to write
 *  *                    The client should send small amount of data (less than 64 bytes)
 * @return            true only if the command succeeded
 */
static bool program(String command) {
  char *index = (char *) command.c_str();
  int start = scanHex(&(++index), 4, MIN_ADDRESS);
  bool valid = (*index == ',');
  int length = (valid ? scanHex(&(++index), 4, -1) : -1);

  valid = ((*index == 0) && length != -1);

  if (valid) {
    program(start, length);
  }

  return valid;
}


/**
 * Implement the programming protocol
 * @param address   The start address for writing data
 * @param length    The number of bytes to program
 */
static void program(int address, int length) {
  int bytesIn = 0;
  int remaining = length;
  
  Serial.println("Programming EEPROM");

  while (remaining != 0)  {
    bool haveData = waitForData();
    
    if (!haveData) { 
      Serial.println("ERROR: Timeout");
      Serial.print("   Read ");
      Serial.print(bytesIn);
      Serial.print(" bytes read out of ");
      Serial.println(length);
      break;
    } else {  
      do {
        byte value = Serial.read();
        remaining--;
        bytesIn++;

        writeEEPROM(address++, value);
      } while (Serial.available() > 0);

      Serial.print("*");                                    // Tell User how many bytes are remaining
      Serial.println(remaining);
    }
  }
}


/**
 * Wait for up to PROGRAM_TIMEOUT_SECONDS for some data to be received over the serial port
 * @return   true only of some data was received; false indicates a timeout
 */
static bool waitForData() {
  bool timeOut = false;
  long count = (PROGRAM_TIMEOUT_SECONDS * 1000) / SERIAL_POLL_DELAY_MS;
  
  while ((Serial.available() <= 0) && !timeOut) {
    delay(SERIAL_POLL_DELAY_MS);

    timeOut = (--count < 0);
  }
      
  delay(SERIAL_POLL_DELAY_MS);                // Short delay while the rest of the string is recieved 

  return !timeOut;
}


/**
 * The Help command. Used to tell the client which commands are supported.
 * @return            true only if the command succeeded
 */
boolean help() {
  Serial.println("Help Page");
  Serial.println();
  
  Serial.println("d[start[:end]]         dump");
  Serial.println("                       Dump EEPROM contents.");
  Serial.println("                         'start' will default to address 0. Values are in hex");
  Serial.println("                         'end' will default to last address. Values are in hex");
  Serial.println();
  
  Serial.println("f[:start[:end[:hex]]]  fill");
  Serial.println("                       Fill EEPROM between 'start' and 'end' with 'hex'");
  Serial.println("                         'start' will default to address 0. Values are in hex");
  Serial.println("                         'end' will default to last address. Values are in hex");
  Serial.println("                         'val' will default to 0x00. Values are in hex");
  Serial.println();
  
  Serial.println("e                      erase");
  Serial.println("                       Fill complete EEPROM with 0xff");
  Serial.println();
  
  Serial.println("t                      test");
  Serial.println("                       Fill EEPROM with a test sequence and read it back");
  Serial.println();
  
  Serial.println("pstart,len             program");
  Serial.println("                       Program EEPROM with 'len' bytes from 'start'. Values are in hex");
  Serial.println("                       Binary data will be read from serial port");
  Serial.println("                       'start' and 'len' are both required");
  Serial.println();
  
  Serial.println("-                      ping");
  Serial.println("                       Redisplay the command prompt");
  Serial.println();

  Serial.println("?                      help");
  Serial.println("                       Display this page");
  
  return true;
}


/**
 * Wait for a String to be received over the serial port
 * @return  The string
 */
static String readSerial() {
  while (Serial.available() <= 0) {
    delay(SERIAL_POLL_DELAY_MS);
  }

  return Serial.readString();
}


/**
* Scan a null terminated buffer for a hex value. 
 * @param buffer        A pointer to the address of the buffer. 
 *                      This will be updated as the string is consumed so that when this function returns
 *                      it points to the first character AFTER the hex value
 * @param maxChars      The maximum number of characters the hex value can take;           
 *                      2 represents a byte; 4 an address in the EEPROM
 * @param defaultValue  A value that will be returned if the buffer is empty.
 */
static int scanHex(char **buffer, int maxChars, int defaultValue) {
  char *ptr = *buffer;
  int value = 0;
  char input = *ptr;

  while (isHexadecimalDigit(input) && (maxChars-- != 0)) {
    value = (value << 4) + (isDigit(input) ? (input - '0') : (input - 'a' + 10));
    input = *(++ptr);
  }

  if (*buffer == ptr) {
    value = defaultValue;
  } else {
    *buffer = ptr;
  }

  return value;
}


/*
 * Read a byte from the EEPROM at the specified address.
 */
byte readEEPROM(int address) {
  for (int pin = EEPROM_D0; pin <= EEPROM_D7; pin += 1) {
    pinMode(pin, INPUT);
  }
  setAddress(address, true);

  byte data = 0;
  for (int pin = EEPROM_D7; pin >= EEPROM_D0; pin -= 1) {
    data = (data << 1) + digitalRead(pin);
  }
  return data;
}


/*
 * Write a byte to the EEPROM at the specified address.
 */
void writeEEPROM(int address, byte data) {
  setAddress(address, false);
  for (int pin = EEPROM_D0; pin <= EEPROM_D7; pin += 1) {
    pinMode(pin, OUTPUT);
  }

  for (int pin = EEPROM_D0; pin <= EEPROM_D7; pin += 1) {
    digitalWrite(pin, data & 1);
    data = data >> 1;
  }

  digitalWrite(WRITE_EN, LOW);
  delayMicroseconds(1);
  digitalWrite(WRITE_EN, HIGH);
  delay(WRITE_DELAY);
}


/*
 * Output the address bits and outputEnable signal using shift registers.
 */
void setAddress(int address, bool outputEnable) {
  shiftOut(SHIFT_DATA, SHIFT_CLK, MSBFIRST, (address >> 8) | (outputEnable ? 0x00 : 0x80));
  shiftOut(SHIFT_DATA, SHIFT_CLK, MSBFIRST, address);

  digitalWrite(SHIFT_LATCH, LOW);
  digitalWrite(SHIFT_LATCH, HIGH);
  digitalWrite(SHIFT_LATCH, LOW);
}
