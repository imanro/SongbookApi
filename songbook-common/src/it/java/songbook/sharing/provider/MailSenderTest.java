package songbook.sharing.provider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import songbook.song.entity.SongContent;
import songbook.song.entity.SongContentTypeEnum;
import songbook.domain.user.entity.User;
import songbook.user.repository.UserDao;
import songbook.util.file.FileException;
import songbook.util.file.TmpDirStorage;
import songbook.util.file.TmpResourceResolver;
import songbook.util.file.entity.FileHolder;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;
import javax.mail.Transport;

@SpringBootTest
public class MailSenderTest {

    @Autowired
    private UserDao userDao;

    @Autowired
    private MailSender mailSender;

    @Autowired
    TmpResourceResolver tmpResourceResolver;

    @BeforeEach
    void clearDb() {
        userDao.deleteAll();
    }

    @Test
    void contentMailCanBeCreatedAndSent() throws FileException, MailSenderException, MessagingException {

        User user1 = new User();
        user1.setEmail("user@example.com");
        userDao.save(user1);

        String recipients = "roman.denisov@gmail.com,formanro@ukr.net";
        String subject = "My mail subject";
        String body = "%fileList%\n\nThis mail has been sent using Songbook v2 application";

        TmpDirStorage storage = new TmpDirStorage(tmpResourceResolver);

        String fileName1 = "test1.txt";
        String contents1 = "I am string content of the file\n";

        String fileName2 = "test2.txt";
        String contents2 = "I am another string content of the file\n";

        storage.storeByteContent(contents1.getBytes(), fileName1);
        storage.storeByteContent(contents2.getBytes(), fileName2);

        List<FileHolder> files = storage.getFiles();

        SongContent content1 = new SongContent();
        content1.setType(SongContentTypeEnum.GDRIVE_CLOUD_FILE);
        content1.setContent("1Qz5n0_SbKuqrqkCBe-SxSYbxUwNMchV9");

        SongContent content2 = new SongContent();
        content2.setType(SongContentTypeEnum.GDRIVE_CLOUD_FILE);
        content2.setContent("114TUrqcNsS71yPABBpeYKMqverGccBAA");

        SongContent content3 = new SongContent();
        content3.setType(SongContentTypeEnum.LINK);
        content3.setContent("https://www.youtube.com/watch?v=4ktVQFisrGo");

        MimeMessage message = mailSender.createContentMail(recipients, subject, body, user1, files);

        Transport transport = mailSender.getMailSession().getTransport();
        transport.connect();
        // this way: Needed for tests
        Transport.send(message);
        transport.close();
        // message.
    }
}
