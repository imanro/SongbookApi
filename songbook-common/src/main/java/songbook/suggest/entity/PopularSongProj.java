package songbook.suggest.entity;

import org.springframework.beans.factory.annotation.Value;
import songbook.concert.entity.Concert;
import songbook.song.entity.Song;

// Open interface
// public class SongStatProj {

// Using DTO with constructors will cause N+1 queries (
// Using native query has disadvantege
public interface PopularSongProj {

    long getTotal();

    PopularSongProj setTotal(long total);

    @Value("#{target.get(\"lastConcert\").id}")
    Long getLastConcertId();

    PopularSongProj setLastConcertId(long id);

    Concert getLastConcert();

    PopularSongProj setLastConcert(Concert concert);

    // Jooo, i've found it (if we have multiple results in select then) target is of type TupletBackedMap)
    // @Value("#{target.get(\"entity\").song}")
    Song getSong();

    PopularSongProj setSong(Song song);
}
