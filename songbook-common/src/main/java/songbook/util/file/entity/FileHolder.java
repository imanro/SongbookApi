package songbook.util.file.entity;

import java.io.File;

public class FileHolder {

    File file;

    String originalName;

    public FileHolder(File file, String originalName) {
        this.setFile(file);
        this.setOriginalName(originalName);
    }

    public File getFile() {
        return file;
    }

    public FileHolder setFile(File file) {
        this.file = file;
        return this;
    }

    public String getOriginalName() {
        return originalName;
    }

    public FileHolder setOriginalName(String originalName) {
        this.originalName = originalName;
        return this;
    }
}
