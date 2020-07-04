package songbook.domain.song.usecase;

import songbook.domain.song.port.in.AttachSongToTagTagNotFoundException;
import songbook.domain.song.port.out.*;
import songbook.tag.entity.Tag;
import songbook.user.entity.User;
import songbook.domain.song.entity.Song;
import songbook.domain.song.port.in.AttachSongToTagSongNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AttachSongToTagCommand implements songbook.domain.song.port.in.AttachSongToTagCommand {

    private final FindSongByIdPort findSongByIdPort;

    private final FindTagByIdPort findTagByIdPort;

    private final AddTagToSongPort addTagToSongPort;

    private final SaveSongPort saveSongPort;

    @Autowired
    public AttachSongToTagCommand(FindSongByIdPort findSongByIdPort,
                                  FindTagByIdPort findTagByIdPort,
                                  AddTagToSongPort addTagToSongPort,
                                  SaveSongPort saveSongPort) {
        this.findSongByIdPort = findSongByIdPort;
        this.findTagByIdPort = findTagByIdPort;
        this.addTagToSongPort = addTagToSongPort;
        this.saveSongPort = saveSongPort;
    }

    @Override
    public Song attachSongToTag(long songId, long tagId, User user) throws AttachSongToTagSongNotFoundException, AttachSongToTagTagNotFoundException {

        Song song = findSongByIdPort.findSongById(songId, user).orElseThrow(() -> new AttachSongToTagSongNotFoundException("The song is not found"));
        Tag tag = findTagByIdPort.findTagById(tagId).orElseThrow(() -> new AttachSongToTagTagNotFoundException("The tag is not found"));

        // dont perform any checks
        addTagToSongPort.addTagToSong(song, tag);

        saveSongPort.saveSong(song);

        // to return tags in right order, re-read the song
        return findSongByIdPort.findSongById(songId, user).orElseThrow(() -> new AttachSongToTagSongNotFoundException("The song is not found"));
    }
}
