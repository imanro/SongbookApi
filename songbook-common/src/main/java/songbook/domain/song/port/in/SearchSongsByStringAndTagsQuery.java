package songbook.domain.song.port.in;

import songbook.domain.song.entity.Song;
import songbook.domain.user.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SearchSongsByStringAndTagsQuery {
    Page<Song> getSongsByTagsAndString(String searchString, List<Long> tagIds, User user, Pageable pageable);
}
