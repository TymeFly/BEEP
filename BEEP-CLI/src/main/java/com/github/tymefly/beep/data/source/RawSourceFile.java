package com.github.tymefly.beep.data.source;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;


/**
 * {@link SourceFile} implementation for files that contain binary data.
 * The assumption is that the data starts from address 0.
 */
class RawSourceFile implements SourceFile {
    private final byte[] dataBuffer;

    RawSourceFile(@Nonnull File file) {
        try {
            dataBuffer = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new SourceFileException("Failed to load " + file.getAbsolutePath(), e);
        }
    }


    @Nonnull
    @Override
    public List<String> getHeaders() {
        return Collections.emptyList();
    }


    @Override
    public short getStartAddress() {
        return 0;
    }


    @Override
    public short getEndAddress() {
        return (short) (dataBuffer.length - 1);
    }


    @Override
    public int size() {
        return dataBuffer.length;
    }


    @Nonnull
    @Override
    public byte[] getData() {
        return dataBuffer.clone();
    }
}
