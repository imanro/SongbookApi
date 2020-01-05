package songbook.importer.model.source;

import java.util.Date;

public class SongContent {
    private int id;

    private int song_id;

    private String type;

    private String url;

    private int user_id;

    private String content;

    private boolean isFavorite;

    private String fileName;

    private String mimeType;

    private Date createTime;

    public int getId() {
        return id;
    }

    public SongContent setId(int id) {
        this.id = id;
        return this;
    }

    public int getSongId() {
        return song_id;
    }

    public SongContent setSongId(int song_id) {
        this.song_id = song_id;
        return this;
    }

    public String getType() {
        return type;
    }

    public SongContent setType(String type) {
        this.type = type;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public SongContent setUrl(String url) {
        this.url = url;
        return this;
    }

    public int getUserId() {
        return user_id;
    }

    public SongContent setUserId(int user_id) {
        this.user_id = user_id;
        return this;
    }

    public String getContent() {
        return content;
    }

    public SongContent setContent(String content) {
        this.content = content;
        return this;
    }

    public boolean getIsFavorite() {
        return isFavorite;
    }

    public SongContent setIsFavorite(boolean favorite) {
        isFavorite = favorite;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public SongContent setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getMimeType() {
        return mimeType;
    }

    public SongContent setMimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public SongContent setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }
}
