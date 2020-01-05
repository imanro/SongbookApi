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
import songbook.song.entity.Song;
import songbook.song.entity.SongContentTypeEnum;
import songbook.song.repository.SongContentDao;
import songbook.song.repository.SongDao;
import songbook.user.entity.User;

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

    @Spy
    @InjectMocks
    private SongServiceImpl songService;

    @BeforeEach
    void init() {
    }

    @Test
    void getSongById() {
//        long id = 12;
//        songService.getSongById(id);
//        verify(songDao).findById(id);
    }

    @Test
    @DisplayName("SongIdWithHeader dependants call")
    void getSongByIdWithHeader() {
        long id = 12;
        User user = mock(User.class);
        /*
        songService.getSongByIdWithHeader(id, user);
         verify(songDao).findByIdWithHeader(id, user);
         */
    }

    @Test
    @DisplayName("Can add string content")
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

}