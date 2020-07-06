package songbook.domain.song.usecase;

import org.springframework.stereotype.Service;
import songbook.domain.song.port.out.FindSongByIdPort;
import songbook.domain.song.entity.Song;
import songbook.domain.user.entity.User;

import java.util.Optional;

@Service
public class SongByIdQuery implements songbook.domain.song.port.in.SongByIdQuery {

    private final FindSongByIdPort findSongByIdPort;

    public SongByIdQuery(FindSongByIdPort findSongByIdPort) {
        this.findSongByIdPort = findSongByIdPort;
    }

    @Override
    public Optional<Song> getSongById(Long id, User user) {
        return this.findSongByIdPort.findSongById(id, user);
    }
}
