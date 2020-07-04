package songbook.domain.song.usecase;

import org.springframework.stereotype.Service;
import songbook.domain.song.port.out.FindSongsByStringPort;
import songbook.domain.song.port.out.FindSongsPort;
import songbook.domain.song.entity.Song;
import songbook.user.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class SearchSongsByStringQuery implements songbook.domain.song.port.in.SearchSongsByStringQuery {

    private final FindSongsByStringPort findSongsByStringPort;

    private final FindSongsPort findSongsPort;

    @Autowired
    public SearchSongsByStringQuery(
            FindSongsByStringPort findSongsByStringPort,
            FindSongsPort findSongsPort
    ) {
        this.findSongsByStringPort = findSongsByStringPort;
        this.findSongsPort = findSongsPort;
    }

    @Override
    public Page<Song> searchSongs(String searchString, User user, Pageable pageable) {
        if (searchString == null || searchString.length() < 2) {
            return this.findSongsPort.findSongs(user, pageable);
        } else {
            return this.findSongsByStringPort.findSongsByString(searchString, user, pageable);
        }
    }
}
