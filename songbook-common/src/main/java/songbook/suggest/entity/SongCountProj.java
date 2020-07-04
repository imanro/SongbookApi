package songbook.suggest.entity;

import songbook.domain.song.entity.Song;

public interface SongCountProj {

    long getTotal();

    Song getSong();
}
