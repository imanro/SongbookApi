package songbook.song.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import songbook.cloud.CloudException;
import songbook.cloud.entity.CloudFile;
import songbook.cloud.repository.CloudDao;
import songbook.song.entity.Song;
import songbook.song.entity.SongContent;
import songbook.song.entity.SongContentTypeEnum;
import songbook.song.repository.SongDao;
import songbook.song.repository.SongContentDao;
import songbook.user.entity.User;

import javax.transaction.Transactional;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class SongServiceImpl implements SongService {

    @Autowired
    private SongDao songDao;

    @Autowired
    private SongContentDao songContentDao;

    @Autowired
    private CloudDao cloudDao;

    @Override
    public int mergeSongs(long masterID, List<Long> toMerge) {
        return 0;
    }

    @Override
    public Song syncCloudContent(Song song, User user) throws SongServiceException {

        // 1) get song content
        if(song.getContent() == null) {
            throw new SongServiceException("The song content is not initialized yet");
        }

        // 2) filter for only GDRIVE ITEMS
        List<SongContent> songCloudContent = song.getContent().stream().filter(content -> content.getType() == SongContentTypeEnum.GDRIVE_CLOUD_FILE).collect(Collectors.toList());

        // 3) get clouddao, get cloudFile by song
        List<CloudFile> cloudFiles;

        try {
            cloudFiles = cloudDao.getSongFiles(song);
        } catch (CloudException e) {
            System.out.println("An exception occurred:" + e.getMessage());
            throw new SongServiceException("Unable to get cloud files for song #" + song.getId() + ", an exception occurred", e);
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

            songContentDao.save(songContent);

            System.out.println("Add the content: " + songContent.getFileName());
            song.getContent().add(songContent);
        });

        System.out.println("The size of content now is:" + song.getContent().size());
//        songDao.save(song);

        // 9) Delete extra contents (that was not found in cloud)
        extraSongContent.forEach(songContent -> {
            System.out.println("Remove extra content " + songContent.getId());
            songContentDao.delete(songContent);
           song.getContent().remove(songContent);
        });

//        songDao.save(song);
        song.setCloudContentSyncTime(new Date());
        songDao.save(song);

//        System.out.println("re-read the song");
//        songDao.refresh(song);

        return song;
        // return songDao.findByIdWithHeaders(song.getId(), user).orElseThrow(() -> new SongServiceException("Unable to get the refreshed song"));
    }

    @Override
    public CloudFile createCloudContentFromSongContent(SongContent content) throws SongServiceException {

        if(content.getType() != SongContentTypeEnum.GDRIVE_CLOUD_FILE) {
            throw new SongServiceException("The given songContent entity has not right type (" + content.getType() + ")");
        }
        CloudFile cloudFile = new CloudFile();
        cloudFile.setId(content.getContent());
        cloudFile.setName(content.getFileName());
        cloudFile.setMimeType(content.getMimeType());
        return cloudFile;
    }

    private <T, U> List<T> findMissingItems(List<T> first, List<U> second, BiPredicate<T, U> compareLambda) {

        List<T> missing = new ArrayList();

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
