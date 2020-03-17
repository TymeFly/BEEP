package com.github.tymefly.beep.data.source;

import java.io.File;
import java.util.List;

import javax.annotation.Nonnull;

import com.github.tymefly.srec.SReader;


/**
 * Adaptor {@link SReader} objects so that they conform to SourceFile
 */
class SRecordSourceFile implements SourceFile {
    private final SReader reader;

    SRecordSourceFile(@Nonnull File file) {
        reader = SReader.load(file);
    }


    @Nonnull
    @Override
    public List<String> getHeaders() {
        return reader.getHeaders();
    }


    @Override
    public short getStartAddress() {
        return reader.getStartAddress();
    }


    @Override
    public short getEndAddress() {
        return reader.getEndAddress();
    }


    @Override
    public int size() {
        return reader.size();
    }


    @Nonnull
    @Override
    public byte[] getData() {
        return reader.getData();
    }
}
