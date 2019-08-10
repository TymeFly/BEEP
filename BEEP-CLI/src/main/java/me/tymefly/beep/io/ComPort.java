package me.tymefly.beep.io;


import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.Nonnull;

import com.fazecast.jSerialComm.SerialPort;
import me.tymefly.beep.config.CliParser;
import me.tymefly.beep.utils.Preconditions;


/**
 * Wrapper for the serial port
 */
class ComPort implements AutoCloseable {
    private static final int BAUD_RATE = 57600;
    private static final int DATA_BITS = 8;

    private final String portName;
    private boolean isOpen;
    private SerialPort port;                    // Only accessed via getPort()


    ComPort() {
        portName = "COM" + CliParser.getInstance().getPort();
        isOpen = true;
    }


    @Nonnull
    private SerialPort getPort() {
        if (port == null) {
            CliParser config = CliParser.getInstance();

            port = SerialPort.getCommPort(portName);
            port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING,
                                    config.getReadTimeoutMs(),
                                    config.getWriteTimeoutMs());
            port.setBaudRate(BAUD_RATE);
            port.setNumDataBits(DATA_BITS);
            port.setNumStopBits(SerialPort.ONE_STOP_BIT);
            port.setParity(SerialPort.NO_PARITY);

            if (!port.openPort()) {
                throw new DriverException("Failed to open %s", portName);
            }
        }

        return port;
    }


    @Nonnull
    InputStream getInputStream() {
        Preconditions.checkState(isOpen, "%s has been closed", portName);

        InputStream inputStream = getPort().getInputStream();

        Preconditions.checkState((inputStream != null), "%s can not be opened for reading", portName);

        return inputStream;
    }


    @Nonnull
    OutputStream getOutputStream() {
        Preconditions.checkState(isOpen, "%s has been closed", portName);

        OutputStream outputStream = getPort().getOutputStream();

        Preconditions.checkState((outputStream != null), "%s can not be opened for reading", portName);

        return outputStream;
    }


    @Override
    public void close() {
        if (port != null) {             // Port may be closed before we try to open it
            port.closePort();

            port = null;
        }

        isOpen = false;
    }
}
