package songbook.song.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
// import org.springframework.boot.test.context.SpringBootTest;
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


    void addSongContent() {
        String string = "content 1";
        SongContentTypeEnum type = SongContentTypeEnum.HEADER;
        User user = mock(User.class);
        Song song = mock(Song.class);

        /*
        SongContent songContent = new SongContent();
        when(songContentDao.save(any())).thenReturn(songContent);
        when(songService.createSongContent()).thenReturn(songContent);

        SongContent savedContent = songService.addSongContent(string, type, song, user);

        assertNotNull(savedContent, "The return value is wrong (mocking problem perhaps)");
        // mocking this made me create new method in songService: createSongContent

        assertEquals(string, savedContent.getContent(), String.format("The \"%s\" value is wrong", "content"));
        assertEquals(user, savedContent.getUser(), String.format("The \"%s\" value is wrong", "user"));
        assertEquals(song, savedContent.getSong(), String.format("The \"%s\" value is wrong", "song"));
        assertEquals(type, savedContent.getType(), String.format("The \"%s\" value is wrong", "type"));
*/
        // I worry about actual results, not about calls
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


    private SongContent initSongContentCloud(Song song) {
        SongContent content = new SongContent();
        content.setType(SongContentTypeEnum.GDRIVE_CLOUD_FILE);
        content.setSong(song);
        return content;
    }

}