package songbook.domain.song.usecase;

import songbook.domain.song.entity.Song;
import songbook.domain.song.port.in.AttachSongToTagSongNotFoundException;
import songbook.domain.song.port.in.DetachSongFromTagSongNotFoundException;
import songbook.domain.song.port.in.DetachSongFromTagTagNotFoundException;
import songbook.domain.song.port.out.*;
import songbook.tag.entity.Tag;
import songbook.user.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DetachSongFromTagCommand implements songbook.domain.song.port.in.DetachSongFromTagCommand {

    private final FindSongByIdPort findSongByIdPort;

    private final FindTagByIdPort findTagByIdPort;

    private final RemoveTagFromSongPort removeTagFromSongPort;

    private final SaveSongPort saveSongPort;

    @Autowired
    public DetachSongFromTagCommand(FindSongByIdPort findSongByIdPort,
                                    FindTagByIdPort findTagByIdPort,
                                    RemoveTagFromSongPort removeTagFromSongPort,
                                    SaveSongPort saveSongPort) {
        this.findSongByIdPort = findSongByIdPort;
        this.findTagByIdPort = findTagByIdPort;
        this.removeTagFromSongPort = removeTagFromSongPort;
        this.saveSongPort = saveSongPort;
    }

    @Override
    public Song detachSongFromTag(long songId, long tagId, User user) throws
            DetachSongFromTagSongNotFoundException, DetachSongFromTagTagNotFoundException {

        Song song = findSongByIdPort.findSongById(songId, user).orElseThrow(() -> new DetachSongFromTagSongNotFoundException("The song is not found"));
        Tag tag = findTagByIdPort.findTagById(tagId).orElseThrow(() -> new DetachSongFromTagTagNotFoundException("The tag is not found"));

        // dont perform any checks
        removeTagFromSongPort.removeTagFromSong(song, tag);

        saveSongPort.saveSong(song);

        // to return tags in right order, re-read the song
        return findSongByIdPort.findSongById(songId, user).orElseThrow(() -> new DetachSongFromTagSongNotFoundException("The song is not found"));
    }
}
