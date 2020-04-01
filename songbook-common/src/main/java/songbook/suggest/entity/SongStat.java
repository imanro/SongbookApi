package songbook.suggest.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import songbook.concert.entity.ConcertItem;
import songbook.song.entity.Song;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

public class SongStat {

    public long total;

    private ConcertItem e;

    public SongStat(long total, ConcertItem e) {
        this.total = total;
        this.e = e;
    }

    public long getTotal() {
        return total;
    }

    public ConcertItem getConcertItem() {
        return e;
    }

}
