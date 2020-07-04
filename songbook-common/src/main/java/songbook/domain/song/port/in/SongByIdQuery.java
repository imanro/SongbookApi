package songbook.domain.song.port.in;

import songbook.domain.song.entity.Song;
import songbook.user.entity.User;

import java.util.Optional;

public interface SongByIdQuery {
    Optional<Song> getSongById(Long id, User user);
}
