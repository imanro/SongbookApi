package songbook.domain.song.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import songbook.cloud.entity.CloudFile;
import songbook.domain.song.entity.Song;
import songbook.domain.song.port.in.SyncSongContentUnableToGetSongCloudFilesException;
import songbook.domain.song.port.out.*;
import songbook.song.entity.SongContent;
import songbook.song.entity.SongContentTypeEnum;
import songbook.user.entity.User;
import songbook.domain.song.port.in.SyncSongContentNotInitializedException;
import songbook.domain.song.port.in.SyncSongContentSongNotFoundException;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

@Service
public class SyncSongContentCommand implements songbook.domain.song.port.in.SyncSongContentCommand {

    private final FindSongByIdPort findSongByIdPort;

    private final DeleteSongContentPort deleteSongContentPort;

    private final SaveSongPort saveSongPort;

    private final FindSongContentsBySongPort findSongContentsBySongPort;

    private final SaveSongContentPort saveSongContentPort;

    private final FindCloudSongFilesPort findCloudSongFilesPort;

    @Autowired
    public SyncSongContentCommand(FindSongByIdPort findSongByIdPort,
                                  DeleteSongContentPort deleteSongContentPort,
                                  SaveSongPort saveSongPort,
                                  FindSongContentsBySongPort findSongContentsBySongPort,
                                  SaveSongContentPort saveSongContentPort,
                                  FindCloudSongFilesPort findCloudSongFilesPort) {
        this.findSongByIdPort = findSongByIdPort;
        this.deleteSongContentPort = deleteSongContentPort;
        this.findSongContentsBySongPort = findSongContentsBySongPort;
        this.saveSongPort = saveSongPort;
        this.saveSongContentPort = saveSongContentPort;
        this.findCloudSongFilesPort = findCloudSongFilesPort;
    }

    @Override
    public Song syncSongContent(long songId, User user) throws SyncSongContentNotInitializedException, SyncSongContentSongNotFoundException, SyncSongContentUnableToGetSongCloudFilesException {

        // adapter can implement multiple interfaces

        // usecases can share these interfaces

        // i will separate commands and query adapters
        Song song = findSongByIdPort.findSongById(songId, user).orElseThrow(() -> new SyncSongContentSongNotFoundException("Song is not found"));

        // 1) get song content
        List<SongContent> contents = findSongContentsBySongPort.findSongContents(song);

        if(contents == null) {
            throw new SyncSongContentNotInitializedException("The song content is not initialized yet");
        }

        // 2) filter for only GDRIVE ITEMS
        List<SongContent> songCloudContent = contents.stream().filter(content -> content.getType() == SongContentTypeEnum.GDRIVE_CLOUD_FILE).collect(Collectors.toList());

        // 3) get clouddao, get cloudFile by song
        List<CloudFile> cloudFiles;

        try {
            cloudFiles = findCloudSongFilesPort.findSongFiles(song);
        } catch (FindCloudSongFilesPortException e) {
            System.out.println("An exception occurred:" + e.getMessage());
            throw new SyncSongContentUnableToGetSongCloudFilesException("Unable to get cloud files for song #" + song.getId() + ", an exception occurred", e);
        }

        System.out.println("Found " + cloudFiles.size() + " files in cloud");

        BiPredicate<SongContent, CloudFile> compareContentWithCloudFile = (x, y) -> x.getContent().equals(y.getId());

        BiPredicate<CloudFile, SongContent> compareCloudFileWithContent = (x, y) -> x.getId().equals(y.getContent());

        List<CloudFile> missingCloudFiles = this.findMissingItems(cloudFiles, songCloudContent, compareCloudFileWithContent);

        List<SongContent> extraSongContent = this.findMissingItems(songCloudContent, cloudFiles, compareContentWithCloudFile);

        System.out.println("Found " + missingCloudFiles.size() + " extra files in cloud");

        System.out.println("The size of content before is:" + song.getContent().size());

        // 8) Initiate new content items by missing cloudFiles list
        missingCloudFiles.forEach(cloudFile -> {

            SongContent songContent = new SongContent();
            songContent.setSong(song);
            songContent.setFileName(cloudFile.getName());
            songContent.setType(SongContentTypeEnum.GDRIVE_CLOUD_FILE);
            songContent.setContent(cloudFile.getId());
            songContent.setMimeType(cloudFile.getMimeType());
            songContent.setUser(user);

            saveSongContentPort.saveSongContent(songContent);

            System.out.println("Add the content: " + songContent.getFileName());
            song.getContent().add(songContent);
        });

        System.out.println("The size of content now is:" + song.getContent().size());
//        songDao.save(song);

        // 9) Delete extra contents (that was not found in cloud)
        extraSongContent.forEach(songContent -> {
            System.out.println("Remove extra content " + songContent.getId());
            deleteSongContentPort.deleteSongContent(song, songContent);
        });


        song.setCloudContentSyncTime(new Date());

        saveSongPort.saveSong(song);

        return song;
    }

    private <T, U> List<T> findMissingItems(List<T> first, List<U> second, BiPredicate<T, U> compareLambda) {

        List<T> missing = new ArrayList<>();

        // We SHOULD init stream each time when we using it, because it gets rot
        first.stream().forEach(item -> {
            // 4) search CloudFile id withing content's "content" property
            U foundContent = second.stream().filter(compareItem -> compareLambda.test(item, compareItem)).findAny().orElse(null);

            // 5) Store missing cloudFiles in new list
            if(foundContent == null) {
                missing.add(item);
            }
        });

        return missing;
    }
}
