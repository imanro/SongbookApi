package songbook.song.entity;

import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import com.fasterxml.jackson.annotation.*;
import songbook.common.entity.BaseEntity;
import songbook.song.view.HeaderSummary;
import songbook.song.view.HeaderTagSummary;
import songbook.util.view.Summary;
import songbook.song.view.Details;
import songbook.tag.entity.Tag;

@Entity
public class Song extends BaseEntity {

    // This is "cannonical" title of song
    // User titles is stored in SongContent[type=HEADER]
    @JsonView(Summary.class)
    private String title;

    @JsonView(Summary.class)
    private String author;

    @JsonView(Summary.class)
    private String copyright;

    @CreationTimestamp
    @Column(name = "create_time")
    @JsonView(Summary.class)
    private Date createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    @JsonView(Summary.class)
    private Date updateTime;

    @Column(name = "cloud_content_sync_time")
    @JsonView(Summary.class)
    private Date cloudContentSyncTime;

    @Transient
    @JsonView(Summary.class)
    private boolean isCloudContentNeedsToBeSynced;

    @Column(name = "sb_v1_id")
    private int sbV1Id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @ManyToMany(fetch = FetchType.LAZY,
            // THE cascade is required if you want to save items by getTags().add() and then songDao.save(item) !!!!
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy("title ASC")
    @JoinTable(
            name = "tag_song",
            joinColumns = {@JoinColumn(name = "song_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")}
    )
    @JsonView(HeaderTagSummary.class)
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "song")
    @Fetch(FetchMode.SUBSELECT)
    @Where(clause = "type='HEADER'")
    @OrderBy("isFavorite DESC")
    // @JsonManagedReference
    @JsonView(HeaderSummary.class)
    @Filters({
            @Filter(name = "contentUser"),
    })
    private Set<SongContent> headers;

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "song"
            /*,
            // THE cascade is required if you want to save items by getTags().add() and then songDao.save(item) !!!!
            cascade = {
                    CascadeType.ALL
            }*/)
    @JsonView(Details.class)
    @Filters({
            @Filter(name = "contentUser"),
    })
    private Set<SongContent> content;

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

    public Date getCloudContentSyncTime() {
        return cloudContentSyncTime;
    }

    public Song setCloudContentSyncTime(Date cloudContentSyncTime) {
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

    public int getSbV1Id() {
        return sbV1Id;
    }

    public Song setSbV1Id(int sbV1Id) {
        this.sbV1Id = sbV1Id;
        return this;
    }

    public String getCode() {
        return code;
    }

    public Song setCode(String code) {
        this.code = code;
        return this;
    }

    public boolean getIsCloudContentNeedsToBeSynced() {
        return isCloudContentNeedsToBeSynced;
    }

//    @Override
//    public String toString() {
//        return String.format("SongDao.java{id=%d}", id);
//    }

    @PostLoad
    private void postLoad() {
        this.verifyCloudContentSyncTime();
    }

    @PrePersist
    private void prePersist() throws NoSuchAlgorithmException {
        if (code == null) {
            code = generateCode();
        }
    }

    private void verifyCloudContentSyncTime() {
        if (this.cloudContentSyncTime != null) {
            Date currentDate = new Date();

            long sec = (currentDate.getTime() - this.cloudContentSyncTime.getTime()) / 1000;
            this.isCloudContentNeedsToBeSynced = sec > 120;

        } else {
            this.isCloudContentNeedsToBeSynced = true;
        }
    }

    private String generateCode() throws NoSuchAlgorithmException {
        Date currentDate = new Date();
        String md5Base = String.valueOf(currentDate.getTime());
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(md5Base.getBytes());
        byte[] digest = md5.digest();
        return DatatypeConverter.printHexBinary(digest).toLowerCase();
    }
}