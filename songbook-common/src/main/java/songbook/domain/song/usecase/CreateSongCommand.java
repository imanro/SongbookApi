package songbook.domain.song.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import songbook.domain.song.entity.Song;
import songbook.domain.song.port.out.SaveSongPort;

@Service
public class CreateSongCommand implements songbook.domain.song.port.in.CreateSongCommand {

    private final SaveSongPort saveSongPort;

    @Autowired
    public CreateSongCommand(SaveSongPort saveSongPort) {
        this.saveSongPort = saveSongPort;
    }

    @Override
    public Song createSong(Song song) {
        return saveSongPort.saveSong(song);
    }
}
