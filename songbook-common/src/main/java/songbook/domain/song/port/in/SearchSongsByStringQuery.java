package songbook.domain.song.port.in;

import songbook.domain.song.entity.Song;
import songbook.domain.user.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchSongsByStringQuery {
    Page<Song> searchSongs(String searchString, User user, Pageable pageable);
}
