package songbook.domain.song.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import songbook.domain.song.entity.Song;
import songbook.domain.song.port.out.FindSongsByTagsAndStringPort;
import songbook.domain.song.port.out.FindSongsByTagsPort;
import songbook.domain.user.entity.User;

import java.util.List;

@Service
public class SearchSongsByTagsAndStringQuery implements songbook.domain.song.port.in.SearchSongsByStringAndTagsQuery {

    private final FindSongsByTagsAndStringPort findSongsByTagsAndStringPort;

    private final FindSongsByTagsPort findSongsByTagsPort;

    @Autowired
    public SearchSongsByTagsAndStringQuery(FindSongsByTagsAndStringPort findSongsByTagsAndStringPort, FindSongsByTagsPort findSongsByTagsPort) {
        this.findSongsByTagsAndStringPort = findSongsByTagsAndStringPort;
        this.findSongsByTagsPort = findSongsByTagsPort;
    }

    @Override
    public Page<Song> getSongsByTagsAndString(String searchString, List<Long> tagIds, User user, Pageable pageable) {
        if (searchString == null || searchString.length() < 2) {
            return findSongsByTagsPort.findSongsByTags(tagIds, user, pageable);
        } else {
            return this.findSongsByTagsAndStringPort.findSongsByTagsAndString(searchString, tagIds, user, pageable);
        }

    }
}
