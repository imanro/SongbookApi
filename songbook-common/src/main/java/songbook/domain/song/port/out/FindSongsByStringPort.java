package songbook.domain.song.port.out;

import songbook.domain.song.entity.Song;
import songbook.user.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface FindSongsByStringPort {
    Page<Song> findSongsByString(String searchString, User user, Pageable pageable);
}
