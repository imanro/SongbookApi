package songbook.domain.song.port.out;

import songbook.domain.song.entity.Song;

public interface SaveSongPort {
    Song saveSong(Song song);
}
