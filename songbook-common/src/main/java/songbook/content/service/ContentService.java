package songbook.content.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import songbook.cloud.CloudException;
import songbook.cloud.entity.CloudFile;
import songbook.cloud.repository.CloudDao;
import songbook.song.entity.SongContent;
import songbook.song.entity.SongContentTypeEnum;
import songbook.song.service.SongService;
import songbook.song.service.SongServiceException;
import songbook.util.file.FileException;
import songbook.util.file.TmpDirStorage;
import songbook.util.file.TmpResourceResolver;
import songbook.util.file.entity.FileHolder;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ContentService {

    @Autowired
    TmpResourceResolver tmpResourceResolver;

    @Autowired
    SongService songService;

    @Autowired
    CloudDao cloudDao;

    /**
     * Downloads contents that support this action (currently, only GDRIVE_CLOUD_FILE)
     * @return FileHolder
     */
    public TmpDirStorage downloadSongContent(List<SongContent> contents) throws ContentServiceException {

        List<SongContent> downloadableContent = contents.stream().filter(songContent ->
            songContent.getType() == SongContentTypeEnum.GDRIVE_CLOUD_FILE
        ).collect(Collectors.toList());

        TmpDirStorage storage = initTmpDirStorage();
        return downloadFilteredContent(downloadableContent, storage);
    }

    /**
     * Downloads contents that support this action (currently, only GDRIVE_CLOUD_FILE)
     * @return FileHolder
     */
    public TmpDirStorage downloadSongContent(List<SongContent> contents, Predicate<SongContent> additionalFilter) throws ContentServiceException {

        List<SongContent> downloadableContent = contents.stream().filter(songContent ->
                songContent.getType() == SongContentTypeEnum.GDRIVE_CLOUD_FILE
        ).filter(additionalFilter).collect(Collectors.toList());

        TmpDirStorage storage = initTmpDirStorage();
        return downloadFilteredContent(downloadableContent, storage);
    }

// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
// Privates

    private TmpDirStorage initTmpDirStorage() throws ContentServiceException {
        TmpDirStorage storage;

        try {
            storage = new TmpDirStorage(tmpResourceResolver);
        } catch(FileException e) {
            throw new ContentServiceException("Unable to embed files due to exception", e);
        }

        return storage;
    }

    private TmpDirStorage downloadFilteredContent(List<SongContent> contents, TmpDirStorage storage) throws ContentServiceException {

        for(SongContent content : contents) {
            byte[] bytes = downloadSongContentItem(content);
            try {
                storage.storeByteContent(bytes, content.getFileName());
            } catch (FileException e) {
                throw new ContentServiceException("Unable to store content in file, an exception has occurred", e);
            }
        }

        return storage;
    }


    private byte[] downloadSongContentItem(SongContent content) throws ContentServiceException {
        CloudFile cloudFile;

        try {
            cloudFile = songService.createCloudContentFromSongContent(content);
        } catch (SongServiceException e) {
            throw new ContentServiceException("Unable to download content file, an exception occurred when creating cloud file", e);
        }

        ByteArrayOutputStream bs;

        // UT 3 - unable to download file
        try {
            bs = cloudDao.getFileContents(cloudFile);
        } catch (CloudException e) {
            throw new ContentServiceException("Unable to download song content, an exception occurred when downloading a content (" + e.getMessage() + ")", e);
        }

        return bs.toByteArray();
    }
}
