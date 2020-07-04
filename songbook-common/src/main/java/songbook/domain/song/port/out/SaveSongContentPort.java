package songbook.domain.song.port.out;

import songbook.song.entity.SongContent;

public interface SaveSongContentPort {
    SongContent saveSongContent(SongContent song);
}
