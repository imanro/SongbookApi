package songbook.suggest.entity;

import org.hibernate.annotations.Formula;
import org.springframework.beans.factory.annotation.Value;
import songbook.song.entity.Song;

// Open interface
public interface SongStatProj {

    long getTotal();

    // Jooo, i've found it (if we have multiple results in select then) target is of type TupletBackedMap)
    @Value("#{target.get(\"entity\").song}")
    Song getSong();
}
