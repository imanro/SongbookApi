package songbook.domain.song.port.out;

import songbook.concert.entity.ConcertItem;
import songbook.domain.song.entity.Song;

import java.util.List;

public interface FindConcertItemsBySongIdPort {
    List<ConcertItem> findConcertItemsBySongId(long songId);
}
