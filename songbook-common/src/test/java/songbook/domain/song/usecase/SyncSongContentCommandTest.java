package songbook.domain.song.usecase;

import songbook.cloud.entity.CloudFile;
import songbook.domain.song.entity.Song;
import songbook.domain.song.port.in.SyncSongContentNotInitializedException;
import songbook.domain.song.port.in.SyncSongContentSongNotFoundException;
import songbook.domain.song.port.in.SyncSongContentUnableToGetSongCloudFilesException;
import songbook.domain.song.port.out.*;
import songbook.song.entity.SongContent;
import songbook.song.entity.SongContentTypeEnum;
import songbook.user.entity.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mockito;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SyncSongContentCommand test")
public class SyncSongContentCommandTest {

    private FindSongByIdPort findSongByIdPort;
    private DeleteSongContentPort deleteSongContentPort;
    private SaveSongPort saveSongPort;
    private FindSongContentsBySongPort findSongContentsBySongPort;
    private SaveSongContentPort saveSongContentPort;
    private FindCloudSongFilesPort findCloudSongFilesPort;
    private SyncSongContentCommand syncSongContentCommand;

    @BeforeEach
    void setUp() {
        // re-init mocks of ports each time to not preserve any behavior between tests
        findSongByIdPort = Mockito.mock(FindSongByIdPort.class);
        deleteSongContentPort = Mockito.mock(DeleteSongContentPort.class);
        saveSongPort = Mockito.mock(SaveSongPort.class);
        findSongContentsBySongPort = Mockito.mock(FindSongContentsBySongPort.class);
        saveSongContentPort = Mockito.mock(SaveSongContentPort.class);
        findCloudSongFilesPort = Mockito.mock(FindCloudSongFilesPort.class);
        syncSongContentCommand = new SyncSongContentCommand(findSongByIdPort, deleteSongContentPort, saveSongPort, findSongContentsBySongPort, saveSongContentPort, findCloudSongFilesPort);
    }

    @Test
    @DisplayName("Syncing Song Content Will add Missing CloudFiles as Content into database")
    void syncingSongContentWillAddMissingCloudFilesAsContent() throws
            FindCloudSongFilesPortException,
            SyncSongContentSongNotFoundException,
            SyncSongContentNotInitializedException,
            SyncSongContentUnableToGetSongCloudFilesException {

        // To use mockito methods, we must mock base entity;
        long songId = 2L;
        User user = Mockito.mock(User.class);
        Song song = Mockito.mock(Song.class);

        ArrayList<SongContent> songContentList = new ArrayList<>();
        SongContent songContent1 = initSongContentCloud(song);
        songContent1.setContent("iiii");
        songContentList.add(songContent1);

        String cloudId1 = "aaaa1";
        String cloudId2 = "aaaa2";

        CloudFile cloudFile1 = new CloudFile();
        cloudFile1.setId(cloudId1);

        CloudFile cloudFile2 = new CloudFile();
        cloudFile2.setId(cloudId2);

        List<CloudFile> cloudFiles = new ArrayList<>();
        cloudFiles.add(cloudFile1);
        cloudFiles.add(cloudFile2);

        // mocking the behavior
        when(findSongByIdPort.findSongById(songId, user)).thenReturn(Optional.of(song));
        when(findSongContentsBySongPort.findSongContents(song)).thenReturn(songContentList);
        when(findCloudSongFilesPort.findSongFiles(song)).thenReturn(cloudFiles);

        syncSongContentCommand.syncSongContent(songId, user);

        verify(saveSongContentPort, times(2)).saveSongContent(any());
    }

    // Throws is okay in tests!
    // https://stackoverflow.com/questions/16596418/how-to-handle-exceptions-in-junit
    @Test
    @DisplayName("Syncing Song Content Will add Missing CloudFiles as Content into database")
    void syncingSongContentWillRemoveExtraSongContentWhenMissingInCloud() throws
            FindCloudSongFilesPortException,
            SyncSongContentSongNotFoundException,
            SyncSongContentNotInitializedException,
            SyncSongContentUnableToGetSongCloudFilesException {

        User user = mock(User.class);
        Song song = mock(Song.class);
        long songId = 2L;

        String cloudId1 = "aaaa1";
        String cloudId2 = "aaaa2";

        ArrayList<SongContent> songContentList = new ArrayList<>();
        SongContent songContent1 = initSongContentCloud(song);
        songContent1.setContent("iiii");
        songContentList.add(songContent1);

        SongContent songContent2 = initSongContentCloud(song);
        songContent2.setContent("jjjjj");
        songContentList.add(songContent2);

        SongContent songContent3 = initSongContentCloud(song);
        songContent3.setContent(cloudId1);
        songContentList.add(songContent3);

        CloudFile cloudFile1 = new CloudFile();
        cloudFile1.setId(cloudId1);

        CloudFile cloudFile2 = new CloudFile();
        cloudFile2.setId(cloudId2);

        List<CloudFile> cloudFiles = new ArrayList<>();
        cloudFiles.add(cloudFile1);
        cloudFiles.add(cloudFile2);

        when(findSongByIdPort.findSongById(songId, user)).thenReturn(Optional.of(song));
        when(findSongContentsBySongPort.findSongContents(song)).thenReturn(songContentList);
        when(findCloudSongFilesPort.findSongFiles(song)).thenReturn(cloudFiles);

        // How to handle exceptions in tests?
        syncSongContentCommand.syncSongContent(songId, user);

        verify(deleteSongContentPort, times(2)).deleteSongContent(any(), any());
    }

    @Test
    void syncingSongContentWillUpdateSongCloudContentSyncTimeProperty() throws
            FindCloudSongFilesPortException,
            SyncSongContentSongNotFoundException,
            SyncSongContentNotInitializedException,
            SyncSongContentUnableToGetSongCloudFilesException {
        User user = mock(User.class);
        Song song = mock(Song.class);
        long songId = 2L;

        ArrayList<SongContent> songContentList = new ArrayList<>();
        List<CloudFile> cloudFiles = new ArrayList<>();

        when(findSongByIdPort.findSongById(songId, user)).thenReturn(Optional.of(song));
        when(findCloudSongFilesPort.findSongFiles(song)).thenReturn(cloudFiles);
        when(findSongContentsBySongPort.findSongContents(song)).thenReturn(songContentList);

        when(song.setCloudContentSyncTime(any())).thenCallRealMethod();
        when(song.getCloudContentSyncTime()).thenCallRealMethod();

        Date nowDate = new Date();

        syncSongContentCommand.syncSongContent(songId, user);

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
