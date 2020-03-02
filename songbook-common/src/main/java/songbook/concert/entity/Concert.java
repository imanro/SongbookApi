package songbook.concert.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.persistence.Entity;

import org.hibernate.annotations.*;
import java.util.Date;
import songbook.user.entity.User;
import com.fasterxml.jackson.annotation.*;

@Entity
public class Concert {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @CreationTimestamp
    @Column(name = "create_time")
    private Date createTime;

    @Column(nullable = false)
    private Date time;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private User user;

    @Column(name = "sb_v1_id")
    private long sbV1Id;

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
}
