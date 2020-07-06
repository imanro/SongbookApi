package songbook.domain.song.port.out;

import songbook.domain.song.entity.Song;
import songbook.domain.user.entity.User;

import java.util.Optional;

public interface FindSongByIdPort {
    Optional<Song> findSongById(long id, User user);
}
