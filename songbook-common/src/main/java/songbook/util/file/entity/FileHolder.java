package songbook.util.file.entity;

import java.io.File;

public class FileHolder {

    File file;

    String originalName;

    public FileHolder(File file, String originalName) {
        this.file = file;
        this.originalName = originalName;
    }

    public File getFile() {
        return file;
    }

    public String getOriginalName() {
        return originalName;
    }
}
