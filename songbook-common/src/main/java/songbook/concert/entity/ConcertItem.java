package songbook.concert.entity;
import songbook.song.entity.Song;
import org.hibernate.annotations.*;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.*;

import java.util.Date;

@Entity
@Table(name = "concert_item")
@SqlResultSetMapping(
        name="pci",
        entities={
                @EntityResult(
                        entityClass= ConcertItem.class,
                        fields={
                                @FieldResult(name="id", column = "ci.id"),
                                @FieldResult(name="createTime", column = "ci.create_time"),
                                @FieldResult(name="concert", column = "ci.concert_id"),
                                @FieldResult(name="song", column = "ci.song_id"),
                                @FieldResult(name="concertGroup", column = "ci.concert_group_id"),
                                @FieldResult(name="orderValue", column = "ci.order_value")
                        }
                )
        },
        columns= {
                @ColumnResult(name="total")
        }

)
@NamedNativeQuery(name="ConcertItem.findPopularConcertItemss", query="SELECT count(1) total,  ci.* FROM concert_item ci GROUP BY ci.song_id", resultSetMapping = "pci")
public class ConcertItem {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @CreationTimestamp
    @Column(name = "create_time")
    private Date createTime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "song_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    // @MapsId
    private Song song;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "concert_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    private Concert concert;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "concert_group_id", nullable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonBackReference
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
