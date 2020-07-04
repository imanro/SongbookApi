package songbook.domain.song.port.in;

import songbook.domain.song.entity.Song;

public interface CreateSongCommand {
    Song createSong(Song song);
}
