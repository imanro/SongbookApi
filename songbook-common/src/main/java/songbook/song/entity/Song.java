package songbook.song.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.*;

import com.fasterxml.jackson.annotation.*;

@Entity
public class Song {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    // This is "cannonical" title of song
    // User titles is stored in SongContent[type=HEADER]
    private String title;

    private String author;

    private String copyright;

    @CreationTimestamp
    @Column(name = "create_time")
    private Date createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "cloud_content_sync_time")
    private String cloudContentSyncTime;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(
            name = "tag_song",
            joinColumns = { @JoinColumn(name = "song_id") },
            inverseJoinColumns = { @JoinColumn(name = "tag_id") }
    )
    Set<Tag> tags = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "song")
    @OrderBy("isFavorite DESC")
    // @JsonManagedReference
    private Set<SongContent> headers;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "song")
    private Set<SongContent> content;

    public long getId() {
        return id;
    }

    public Song setId(long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Song setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public Song setAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getCopyright() {
        return copyright;
    }

    public Song setCopyright(String copyright) {
        this.copyright = copyright;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Song setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public Song setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public String getCloudContentSyncTime() {
        return cloudContentSyncTime;
    }

    public Song setCloudContentSyncTime(String cloudContentSyncTime) {
        this.cloudContentSyncTime = cloudContentSyncTime;
        return this;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public Song setTags(Set<Tag> tags) {
        this.tags = tags;
        return this;
    }

    public Collection getHeaders() {
        return headers;
    }

    public Song setHeaders(Set<SongContent> headers) {
        this.headers = headers;
        return this;
    }

    public Set<SongContent> getContent() {
        return content;
    }

     public Song setContent(Set<SongContent> content) {
        this.content = content;
        return this;
    }

    public Song addContent(SongContent content) {
        this.content.add(content);
        return this;
    }

    @Override
    public String toString() {
        return String.format("SongDao.java{id=%d}", id);
    }
}
