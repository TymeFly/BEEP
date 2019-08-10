package me.tymefly.beep.io;

import java.io.OutputStream;

/**
 * An {@link OutputStream} that dumps all the data written to it.
 */
public class NullOutputStream extends OutputStream {
    @Override
    public void write(int b) {
    }
}