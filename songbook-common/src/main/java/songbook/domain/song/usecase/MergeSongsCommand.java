package songbook.domain.song.usecase;

import songbook.concert.entity.ConcertItem;
import songbook.domain.song.entity.Song;
import songbook.domain.song.port.in.MergeSongsSongNotFoundException;
import songbook.domain.song.port.out.*;
import songbook.song.entity.SongContent;
import songbook.tag.entity.Tag;
import songbook.domain.user.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

// public/not public class??
@Service
public class MergeSongsCommand implements songbook.domain.song.port.in.MergeSongsCommand {

    private final FindSongByIdPort findSongByIdPort;

    private final FindSongContentsBySongPort findSongContentsBySongPort;

    private final FindConcertItemsBySongIdPort findConcertItemsBySongIdPort;

    private final FindSongTagsBySongPort findSongTagsBySongPort;

    private final SaveSongPort saveSongPort;

    private final SaveSongContentPort saveSongContentPort;

    private final SaveConcertItemPort saveConcertItemPort;

    private final DeleteSongPort deleteSongPort;

    @Autowired
    public MergeSongsCommand(FindSongByIdPort findSongByIdPort,
                             FindSongContentsBySongPort findSongContentsBySongPort,
                             FindConcertItemsBySongIdPort findConcertItemsBySongIdPort,
                             FindSongTagsBySongPort findSongTagsBySongPort,
                             SaveSongPort saveSongPort,
                             SaveSongContentPort saveSongContentPort,
                             SaveConcertItemPort saveConcertItemPort,
                             DeleteSongPort deleteSongPort) {
        this.findSongByIdPort = findSongByIdPort;
        this.findSongContentsBySongPort = findSongContentsBySongPort;
        this.findConcertItemsBySongIdPort = findConcertItemsBySongIdPort;
        this.findSongTagsBySongPort = findSongTagsBySongPort;
        this.saveSongPort = saveSongPort;
        this.saveSongContentPort = saveSongContentPort;
        this.saveConcertItemPort = saveConcertItemPort;
        this.deleteSongPort = deleteSongPort;
    }

    @Override
    public Song mergeSongs(long mergedSongId, long masterSongId, User user) throws MergeSongsSongNotFoundException {
        Song masterSong = findSongByIdPort.findSongById(masterSongId, user).orElseThrow(() -> new MergeSongsSongNotFoundException("The master song wasn't found"));
        Song mergedSong = findSongByIdPort.findSongById(mergedSongId, user).orElseThrow(() -> new MergeSongsSongNotFoundException("The merged song wasn't found"));

        // take all merged song's content,
        List<SongContent> songContents =  findSongContentsBySongPort.findSongContents(mergedSong);

        // take all concertItems by merged song
        List<ConcertItem> concertItems =  findConcertItemsBySongIdPort.findConcertItemsBySongId(mergedSong.getId());

        // take all tags of merged song
        List<Tag> tags = this.findSongTagsBySongPort.findSongTagsBySong(mergedSong);

        // re-assign song of contents
        songContents.stream().forEach(songContent -> {
            songContent.setSong(masterSong);
            saveSongContentPort.saveSongContent(songContent);
        });

        // re-assign concert items
        concertItems.stream().forEach(concertItem -> {
            concertItem.setSong(masterSong);
            saveConcertItemPort.saveConcertItem(concertItem);
        });

        // re-assign tags
        tags.stream().forEach(tag -> {
            masterSong.getTags().add(tag);
        });

        saveSongPort.saveSong(masterSong);

        // otherwise the delete won't work
        deleteSongPort.deleteSongAtOnce(mergedSong.getId());

        return masterSong;
    }
}
