package songbook.song.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.junit.jupiter.MockitoExtension;
import songbook.cloud.CloudException;
import songbook.cloud.entity.CloudFile;
import songbook.cloud.repository.CloudDao;
import songbook.song.entity.Song;
import songbook.song.entity.SongContent;
import songbook.song.entity.SongContentTypeEnum;
import songbook.song.repository.SongContentDao;
import songbook.song.repository.SongDao;
import songbook.user.entity.User;

import java.util.*;

import static org.mockito.Mockito.*;

// SpringBootTest only required for integration tests!
// @SpringBootTest
@ExtendWith(MockitoExtension.class)
@DisplayName("SongService test")
class SongServiceTest {

    @Mock
    private SongDao songDao;

    @Mock
    private SongContentDao songContentDao;

    @Mock
    private CloudDao cloudDao;

    @Spy
    @InjectMocks
    private SongServiceImpl songService;

    @BeforeEach
    void init() {
    }

    // + test that we cannot use dao without of setting root folder first

    @Test
    @DisplayName("Syncing Song Content Will add Missing CloudFiles as Content into database")
    void syncingSongContentWillAddMissingCloudFilesAsContent() throws CloudException, SongServiceException {
        User user = mock(User.class);

        // To use mockito methods, we must mock base entity;
        Song song = mock(Song.class);

        Set<SongContent> emptySet = new HashSet<SongContent>();
        SongContent songContent1 = initSongContentCloud(song);
        songContent1.setContent("iiii");
        emptySet.add(songContent1);

        String cloudId1 = "aaaa1";
        String cloudId2 = "aaaa2";

        CloudFile cloudFile1 = new CloudFile();
        cloudFile1.setId(cloudId1);

        CloudFile cloudFile2 = new CloudFile();
        cloudFile2.setId(cloudId2);

        List<CloudFile> cloudFiles = new ArrayList<>();
        cloudFiles.add(cloudFile1);
        cloudFiles.add(cloudFile2);

        when(song.getContent()).thenReturn(emptySet);
        when(cloudDao.getSongFiles(song)).thenReturn(cloudFiles);

        // How to handle exceptions in tests?
        songService.syncCloudContent(song, user);

        verify(songContentDao, times(2)).save(any());
    }

    // Throws is okay in tests!
    // https://stackoverflow.com/questions/16596418/how-to-handle-exceptions-in-junit
    @Test
    @DisplayName("Syncing Song Content Will add Missing CloudFiles as Content into database")
    void syncingSongContentWillRemoveExtraSongContentWhenMissingInCloud() throws CloudException, SongServiceException {

        User user = mock(User.class);

        // To use mockito methods, we must mock base entity;
        Song song = mock(Song.class);

        String cloudId1 = "aaaa1";
        String cloudId2 = "aaaa2";

        Set<SongContent> songContentSet = new HashSet<SongContent>();
        SongContent songContent1 = initSongContentCloud(song);
        songContent1.setContent("iiii");
        songContentSet.add(songContent1);

        SongContent songContent2 = initSongContentCloud(song);
        songContent2.setContent("jjjjj");
        songContentSet.add(songContent2);

        SongContent songContent3 = initSongContentCloud(song);
        songContent3.setContent(cloudId1);
        songContentSet.add(songContent3);

        CloudFile cloudFile1 = new CloudFile();
        cloudFile1.setId(cloudId1);

        CloudFile cloudFile2 = new CloudFile();
        cloudFile2.setId(cloudId2);

        List<CloudFile> cloudFiles = new ArrayList<>();
        cloudFiles.add(cloudFile1);
        cloudFiles.add(cloudFile2);

        when(song.getContent()).thenReturn(songContentSet);
        when(cloudDao.getSongFiles(song)).thenReturn(cloudFiles);

        // How to handle exceptions in tests?
        songService.syncCloudContent(song, user);

        verify(songContentDao, times(2)).delete(any());
    }

    @Test
    void syncingSongContentWillUpdateSongCloudContentSyncTimeProperty() throws CloudException, SongServiceException {
        User user = mock(User.class);
        Song song = mock(Song.class);

        Set<SongContent> songContentSet = new HashSet<SongContent>();
        List<CloudFile> cloudFiles = new ArrayList<>();
        when(cloudDao.getSongFiles(song)).thenReturn(cloudFiles);
        when(song.getContent()).thenReturn(songContentSet);

        when(song.setCloudContentSyncTime(any())).thenCallRealMethod();
        when(song.getCloudContentSyncTime()).thenCallRealMethod();

        Date nowDate = new Date();

        songService.syncCloudContent(song, user);

        assertNotNull(song.getCloudContentSyncTime(), "The cloudContentSyncTime property is null");
        // the same second as exec time
        assertEquals(Math.round((float)(nowDate.getTime() / 1000)), Math.round((float)(song.getCloudContentSyncTime().getTime() / 1000)), "The cloudContentSyncTime is wrong ");
    }

    private SongContent initSongContentCloud(Song song) {
        SongContent content = new SongContent();
        content.setType(SongContentTypeEnum.GDRIVE_CLOUD_FILE);
        content.setSong(song);
        return content;
    }

}