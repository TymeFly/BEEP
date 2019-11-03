package me.tymefly.beep.data.source;

import java.util.List;

import javax.annotation.Nonnull;


/**
 * Define the contract used by all objects that can read data files from the local file system
 */
public interface SourceFile {

    /**
     * Returns a list of all the header information in this source file
     * @return a list of all the header information in this source file
     */
    @Nonnull
    List<String> getHeaders();


    /**
     * Returns the address of the first byte in this source file
     * @return the address of the first byte in this source file
     */
    short getStartAddress();


    /**
     * Returns the address of the last byte in this source file
     * @return the address of the last byte in this source file
     */
    short getEndAddress();

    /**
     * Returns the number of bytes in this source file
     * @return the number of bytes in this source file
     */
    int size();


    /**
     * Returns the data in this source file
     * The first byte of the buffer will be at address {@link #getStartAddress()}, the last byte of the
     * buffer will be at address {@link #getEndAddress()} and the size of the buffer is given by {@link #size()}
     * @return the data in this source file
     */
    @Nonnull
    byte[] getData();
}
