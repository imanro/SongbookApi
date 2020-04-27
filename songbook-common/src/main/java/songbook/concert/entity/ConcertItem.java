package songbook.concert.entity;
import songbook.song.entity.Song;
import org.hibernate.annotations.*;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.*;
import songbook.concert.view.Details;

import java.util.Date;
// import javax.validation.constraints.NotNull;

@Entity
@Table(name = "concert_item")
public class ConcertItem {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @CreationTimestamp
    @Column(name = "create_time")
    private Date createTime;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "song_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonView(Details.class)
    // @MapsId
    private Song song;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "concert_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference(value = "concert-item")
    private Concert concert;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "concert_group_id", nullable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonBackReference(value = "group-item")
    private ConcertGroup concertGroup;

    @Column(name = "order_value")
    private int orderValue;


    public long getId() {
        return id;
    }

    public ConcertItem setId(long id) {
        this.id = id;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public ConcertItem setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public Song getSong() {
        return song;
    }

    public ConcertItem setSong(Song song) {
        this.song = song;
        return this;
    }

    public Concert getConcert() {
        return concert;
    }

    public ConcertItem setConcert(Concert concert) {
        this.concert = concert;
        return this;
    }

    public ConcertGroup getConcertGroup() {
        return concertGroup;
    }

    public ConcertItem setConcertGroup(ConcertGroup concertGroup) {
        this.concertGroup = concertGroup;
        return this;
    }

    public int getOrderValue() {
        return orderValue;
    }

    public ConcertItem setOrderValue(int orderValue) {
        this.orderValue = orderValue;
        return this;
    }
}
