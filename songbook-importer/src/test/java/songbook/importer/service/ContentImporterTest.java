package songbook.importer.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import songbook.song.entity.SongContentTypeEnum;
import songbook.user.entity.User;
import songbook.user.repository.UserDao;
import org.mockito.*;
import org.junit.jupiter.api.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// SpringBootTest only required for integration tests!
// https://stackoverflow.com/questions/41730099/two-spring-boot-projects-both-with-springbootapplication
// @SpringBootTest(classes = songbook.ImporterApplication.class)

// for mockito, use following:
// https://dzone.com/articles/spring-boot-2-with-junit-5-and-mockito-2-for-unit
@ExtendWith(MockitoExtension.class)
@DisplayName("SongService test")
public class ContentImporterTest {
    @Mock
    private UserDao userDao;

    @Spy
    @InjectMocks
    private ContentImporter contentImporter;

    @Test
    @DisplayName("Will create a user when user is not found in DB")
    void getDefaultUserUserNotFound() {

        Optional<User> empty = Optional.empty();

        when(userDao.findById(any())).thenReturn(empty);

        User user = contentImporter.getDefaultUser();
        assertNotNull(user);
        assertEquals(user.getId(), 1);
        assertEquals(user.getEmail(), "nobody@example.com");

        // also check that user will be saved in db
        verify(userDao).save(user);
    }

    @Test
    @DisplayName("Will create a user when user is not found in DB")
    void getDefaultUserUserExists() {

        long existingId = 222;
        String existingEmail = "vasja@example.com";

        User existingUser = new User();
        existingUser.setId(existingId);
        existingUser.setEmail(existingEmail);
        Optional<User> userOpt = Optional.of(existingUser);

        when(userDao.findById(any())).thenReturn(userOpt);

        User user = contentImporter.getDefaultUser();
        assertNotNull(user);
        assertEquals(user.getId(), existingId);
        assertEquals(user.getEmail(), existingEmail);
    }

    @Test
    @DisplayName("Type can be converted")
    void typeCanBeConverted() throws ContentImporterException {
        assertEquals(contentImporter.convertType("header"), SongContentTypeEnum.HEADER, "Type converted improperly");
        assertEquals(contentImporter.convertType("gdrive_cloud_file"), SongContentTypeEnum.GDRIVE_CLOUD_FILE, "Type converted improperly");
        assertEquals(contentImporter.convertType("inline"), SongContentTypeEnum.INLINE, "Type converted improperly");
        assertEquals(contentImporter.convertType("link"), SongContentTypeEnum.LINK, "Type converted improperly");
    }
}
