package songbook.song.entity;

import org.hibernate.annotations.*;
import songbook.user.entity.User;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.util.Date;
import com.fasterxml.jackson.annotation.*;

@Entity
public class SongContent {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private SongContentType type;

    private String url;

    // @Column(columnDefinition="TEXT")
    @Type(type = "org.hibernate.type.TextType")
    private String content;

    @Column(name="file_name")
    private String fileName;

    @Column(name="mime_type")
    private String mimeType;

    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Column(name="is_favorite", nullable = false, columnDefinition = "tinyint unsigned default 0")
    private boolean isFavorite;

    @CreationTimestamp
    @Column(name = "create_time")
    private Date createTime;

    /*
     * https://stackoverflow.com/questions/3331907/what-is-the-difference-between-manytooneoptional-false-vs-columnnullable-f
     * optional=false is a runtime instruction. The primary functional thing it does is related to Lazy Loading.
     * You can't lazy load a non-collection mapped entity unless you remember to set optional=false
     * (because Hibernate doesn't know if there should be a proxy there or a null,
     * unless you tell it nulls are impossible, so it can generate a proxy.)
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "song_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    private Song song;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonIgnore
    private User user;

    public long getId() {
        return id;
    }

    public SongContent setId(long id) {
        this.id = id;
        return this;
    }

    public SongContentType getType() {
        return type;
    }

    public SongContent setType(SongContentType type) {
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

    public String getContent() {
        return content;
    }

    public SongContent setContent(String content) {
        this.content = content;
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

    public boolean getIsFavorite() {
        return isFavorite;
    }

    public SongContent setIsFavorite(boolean favorite) {
        isFavorite = favorite;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public SongContent setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public Song getSong() {
        return song;
    }

    public SongContent setSong(Song song) {
        this.song = song;
        return this;
    }

    public User getUser() {
        return user;
    }

    public SongContent setUser(User user) {
        this.user = user;
        return this;
    }

    @Override
    public String toString() {
        return String.format("SongContent.java{id=%d}", id);
    }
}
