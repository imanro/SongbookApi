package songbook.song.repository;

import songbook.song.entity.Song;

public interface SongDaoCustom {
    void refresh(Song song);
}
