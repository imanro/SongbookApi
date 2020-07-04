package songbook.domain.song.port.in;

import songbook.domain.song.entity.Song;

public interface AddSongQuery {
    Song addSong(Song song);
}
