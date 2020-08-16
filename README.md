# BEEP
Ben Eater EEPROM Programmer (BEEP)


## Documentation

This repo contains code for a simple EEPROM programmer based on Ben Eater's EEPROM Programmer Design 
(https://github.com/beneater/eeprom-programmer)

While Ben's original design is a wonderful way to learn how to build a simple EEPROM programmer, there is 
a limitation in what it can do; the Arduino Nano application has to be clever enough to work out what to program the 
EEPROM with. This left me with two problems

1.	Every time I wanted to write a different EEPROM I’d have to write and download a new Arduino application.
2.	The Arduino has a limited about of memory – less than that of the EEPROMs it’s trying to write. 
    To program a more complex EEPROM it becomes difficult to write an application that is small enough to 
	fit into the Arduino


My solution is to split the job in half; the embedded Arduino application knows how to perform basic EEPROM
related tasks but knows nothing about any specific EEPROM, and a Java client that can manage the programmer. 
This client can download specific EEPROM data to the programmer in (very) small chunks, and thus get around the
memory and complexity limits.


### The embedded Arduino application.

The embedded application communicates with the Java client through the serial port at 57600 baud.
It understands the following commands:

* d[start[:end]]

  Dump EEPROM contents.'start' defaults to 0. 'end' defaults to 0x1fff

* f[:start[:end[:hex]]]  

  Fill EEPROM between 'start' and 'end' with 'hex'.'start' defaults to 0, 'end' defaults to 0x1fff, 'val' defaults to 0

* e

  Fill the EEPROM with 0xff

* t

  Fill EEPROM with a test sequence and read it back

* pstart,len            

  Enter Programming mode. 'start' address of first byte to write,'number' of bytes to write
  
* \-                    

  Redisplay the command prompt
						
* \?

  Display the commands available

All values are given in Hex


### The Java based CLI 

To use the CLI be sure to download and compile my S-Rec library first (https://github.com/TymeFly/S-Rec)

#### Usage:
  java com.github.tymefly.beep.Main  --programmerTimeout N --readTimeout N --writeTimeout N -p (--port) N

 [dump | fill | ping | erase | test | program | verify | upload | describe]

-p (--port) N            COM port programmer is connected to

--programmerTimeout N    timeout in milliseconds when waiting  for programmer to respond (default:  10000)

--readTimeout N          read timeout in milliseconds when waiting reading the COM port  (default: 1000)

--writeTimeout N         write timeout in milliseconds when waiting reading the COM port (default: 100)

			
#### File Formats:

When uploading data from an EEPROM the generated file will always be in SRecord format - see https://en.wikipedia.org/wiki/SREC_(file_format)
When downloading data into an EEPROM the application can use the following file formats:
-	SRecord (.s19, .s28, .s37, .s, .s1, .s2, .s3, .sx, .srec, .mot, .mx)
-	Binary files (.raw, .dat, .data, .bin)


#### Examples:

 java com.github.tymefly.beep.Main  -?   
-	Display the help page

 java com.github.tymefly.beep.Main  -p 7 ping
-	Ping the Arduino application to see if it can communicate

 java com.github.tymefly.beep.Main  -p 7 dump --start 0x20 --end 0x46
-	Display the content of the EEPROM on the screen (Standard Out) in hex and ASCII 

 java com.github.tymefly.beep.Main  -p 7 erase
-	Erase the EEPROM (write 0xff to all locations)

 java com.github.tymefly.beep.Main  -p 7 fill --byte 0x5a --start 0x100 --end 0x200
-	Write the value 0x5a to all locations in the EEPROM between 0x100 and 0x200

 java com.github.tymefly.beep.Main  -p 7 test
-	Write a test pattern to all locations in the EEPROM and check they have been written correctly

 java com.github.tymefly.beep.Main  -p 7 program --file MyFile.srec
-	Write the content of MyFile.srec to the EERPOM

 java com.github.tymefly.beep.Main  -p 7 upload --header "My Description" --out MyFile.srec
-	Read the content of the EEPROM and write it to MyFile.srec to 

 java com.github.tymefly.beep.Main  -p 7 verify --MyFile.srec --maxErrors 2
-	Check that the EEPROM contains the content of MyFile. Give up after 2 errors.

 java com.github.tymefly.beep.Main  describe --file MyFile.srec
-	Dump the SRecord metadata to standard out. This includes the start address, end address, images size
    and all header fields
	
## Libraries used

* My own S-Rec library for reading/writing S-Records files (https://github.com/TymeFly/S-Rec)
* JSR305 for documenting null object references
* args4j for handling the CLI parsing
