package songbook.concert.entity;

import org.hibernate.annotations.*;
import javax.persistence.*;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.*;

@Entity
public class ConcertGroup {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "concert_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    private Concert concert;

    private String name;

    public long getId() {
        return id;
    }

    public ConcertGroup setId(long id) {
        this.id = id;
        return this;
    }

    public Concert getConcert() {
        return concert;
    }

    public ConcertGroup setConcert(Concert concert) {
        this.concert = concert;
        return this;
    }

    public String getName() {
        return name;
    }

    public ConcertGroup setName(String name) {
        this.name = name;
        return this;
    }
}
