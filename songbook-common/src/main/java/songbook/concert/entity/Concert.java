package songbook.concert.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.OrderBy;

import org.hibernate.annotations.*;
import java.util.Date;
import java.util.Set;
import songbook.user.entity.User;
import com.fasterxml.jackson.annotation.*;
import songbook.concert.view.Summary;
import songbook.concert.view.Details;

@Entity
public class Concert {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @JsonView(Summary.class)
    private long id;

    @CreationTimestamp
    @Column(name = "create_time")
    @JsonView(Summary.class)
    private Date createTime;

    @JsonView(Summary.class)
    private Date time;

    @JsonView(Summary.class)
    private String title;

    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Column(name="is_draft", nullable = false, columnDefinition = "tinyint unsigned default 0")
    private boolean isDraft;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonView(Details.class)
    private User user;

    @Column(name = "sb_v1_id")
    private long sbV1Id;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "concert")
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy("orderValue")
    @JsonView(Details.class)
    private Set<ConcertItem> items;

    public long getId() {
        return id;
    }

    public Concert setId(long id) {
        this.id = id;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Concert setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public Date getTime() {
        return time;
    }

    public Concert setTime(Date time) {
        this.time = time;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Concert setUser(User user) {
        this.user = user;
        return this;
    }

    public long getSbV1Id() {
        return sbV1Id;
    }

    public Concert setSbV1Id(long sbV1Id) {
        this.sbV1Id = sbV1Id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Concert setTitle(String title) {
        this.title = title;
        return this;
    }

    public boolean getIsDraft() {
        return isDraft;
    }

    public Concert setIsDraft(boolean draft) {
        isDraft = draft;
        return this;
    }

    public Set<ConcertItem> getItems() {
        return items;
    }

    public Concert setItems(Set<ConcertItem> items) {
        this.items = items;
        return this;
    }
}
