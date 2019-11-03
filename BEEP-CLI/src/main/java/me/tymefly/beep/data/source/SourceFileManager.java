package me.tymefly.beep.data.source;

import java.io.File;
import java.util.HashMap;
import java.util.function.Function;

import javax.annotation.Nonnull;


/**
 * Manager for {@link SourceFile}s
 */
public class SourceFileManager {
    private static final HashMap<String, Function<File, SourceFile>> TYPES =
        new HashMap<String, Function<File, SourceFile>>() {{
            // SRecord file types
            put("s19", SRecordSourceFile::new);
            put("s28", SRecordSourceFile::new);
            put("s37", SRecordSourceFile::new);
            put("s", SRecordSourceFile::new);
            put("s1", SRecordSourceFile::new);
            put("s2", SRecordSourceFile::new);
            put("s3", SRecordSourceFile::new);
            put("sx", SRecordSourceFile::new);
            put("srec", SRecordSourceFile::new);
            put("mot", SRecordSourceFile::new);
            put("mx", SRecordSourceFile::new);

            // Binary file types
            put("raw", RawSourceFile::new);
            put("dat", RawSourceFile::new);
            put("data", RawSourceFile::new);
            put("bin", RawSourceFile::new);
        }};


    private SourceFileManager() {
    }


    /**
     * Load a SourceFile from the local file system.
     * The expected format of the data data file is determined by the final extension of {@code file}
     * @param file          File to be loaded
     * @return              A Source File
     * @throws SourceFileException  if {@code file} has an unknown extension
     */
    @Nonnull
    public static SourceFile load(@Nonnull File file) throws SourceFileException {
        String extension = getExtension(file);
        Function<File, SourceFile> constructor = TYPES.get(extension);

        if (constructor == null) {
            throw new SourceFileException("Unknown data type '%s'", extension);
        }

        return constructor.apply(file);
    }


    @Nonnull
    private static String getExtension(@Nonnull File file) {
        String fileName = file.getName();
        int index = fileName.lastIndexOf('.');
        String extension = (index == -1 ? "" : fileName.substring(index + 1));

        return extension.toLowerCase();
    }
}
