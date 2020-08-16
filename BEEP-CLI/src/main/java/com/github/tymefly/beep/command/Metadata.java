package com.github.tymefly.beep.command;

import javax.annotation.Nonnull;

import com.github.tymefly.beep.data.source.SourceFile;


/**
 * Common Metadata reporting methods.
 */
class Metadata {
    private Metadata() {
    }


    static void dumpStats(@Nonnull SourceFile reader) {
        System.out.printf("Start Address: 0x%04x%n", reader.getStartAddress());
        System.out.printf("End Address  : 0x%04x%n", reader.getEndAddress());
        System.out.printf("Size         : %d%n", reader.size());
    }


    static void dumpHeaders(@Nonnull SourceFile reader) {
        for (String header : reader.getHeaders()) {
            System.out.printf("Header: %s%n", header);
        }
    }
}
