package songbook.cloud.entity;

public class CloudFile {
    private String id;

    private String name;

    private String mimeType;

    public String getId() {
        return id;
    }

    public CloudFile setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public CloudFile setName(String name) {
        this.name = name;
        return this;
    }

    public String getMimeType() {
        return mimeType;
    }

    public CloudFile setMimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
    }
}
