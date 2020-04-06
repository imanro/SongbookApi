package songbook.suggest.entity;

import org.springframework.beans.factory.annotation.Value;
import songbook.concert.entity.Concert;
import songbook.song.entity.Song;

// Open interface
// public class SongStatProj {

// Using DTO with constructors will cause N+1 queries (
// Using native query has disadvantege
public interface SongStatProj {

    long getTotal();

    SongStatProj setTotal(long total);

    @Value("#{target.get(\"lastConcert\").id}")
    Long getLastConcertId();

    SongStatProj setLastConcertId(long id);

    Concert getLastConcert();

    SongStatProj setLastConcert(Concert concert);

    // Jooo, i've found it (if we have multiple results in select then) target is of type TupletBackedMap)
    // @Value("#{target.get(\"entity\").song}")
    Song getSong();

    SongStatProj setSong(Song song);
}
