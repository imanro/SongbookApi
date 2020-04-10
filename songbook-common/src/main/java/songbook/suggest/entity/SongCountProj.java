package songbook.suggest.entity;

import songbook.song.entity.Song;

public interface SongCountProj {

    long getTotal();

    Song getSong();
}
