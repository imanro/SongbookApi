package songbook.domain.song.port.out;

import songbook.domain.song.entity.Song;

public interface DeleteSongPort {
    void deleteSongAtOnce(long songId);
}
