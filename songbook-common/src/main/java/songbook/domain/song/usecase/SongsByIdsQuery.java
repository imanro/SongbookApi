package songbook.domain.song.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import songbook.domain.song.port.out.FindSongsByIdsPort;
import songbook.domain.song.entity.Song;
import songbook.domain.user.entity.User;

import java.util.List;

@Service
public class SongsByIdsQuery implements songbook.domain.song.port.in.SongsByIdsQuery {

    private final FindSongsByIdsPort findSongsByIdsPort;

    @Autowired
    public SongsByIdsQuery(FindSongsByIdsPort findSongsByIdsPort) {
        this.findSongsByIdsPort = findSongsByIdsPort;
    }

    @Override
    public List<Song> getSongsByIds(List<Long> ids, User user) {
        return findSongsByIdsPort.findSongsByIds(ids, user);
    }
}
