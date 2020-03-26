package songbook.cloud.repository;

import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;
import songbook.cloud.CloudException;
import songbook.cloud.entity.CloudFile;
import songbook.song.entity.Song;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@SpringBootTest
@TestPropertySource(locations = "/cloud-dao.properties")
class CloudDaoTest {

    @Autowired
    private CloudDao cloudDao;

    private static final String ROOT_FOLDER_NAME = "songbook";

    @Test
    void throwsExceptionWhenRootFolderIsNotSet() {
        Song song = new Song();
        song.setId(2);

        Assertions.assertThrows(CloudException.class, () -> {
            List<CloudFile> files = cloudDao.getSongFiles(song);
        });
    }

    @Test
    void canReadSongFilesFolderExists() {
        System.out.println("Executing");

        Song song = new Song();
        song.setId(this.getExistingSongId());

        List<CloudFile> files;

        try {
            files = cloudDao.getSongFiles(song);
        } catch(CloudException e) {
            System.out.println("An exception occurred");
            files = null;
        }

        Assertions.assertNotNull(files, "The cloud didn't return a result");
        Assertions.assertTrue(files.size() > 0, "The files size is zero");
    }

    @Test
    void canReadSongFilesFolderDontExists() {
        int rand = getRandom();
        Song song = new Song();
        song.setId(rand);

        List<CloudFile> files;

        try {
            // means that folder will be created
            files = cloudDao.getSongFiles(song);
        } catch(CloudException e) {
            System.out.println("An exception occurred");
            files = null;
        }

        Assertions.assertNotNull(files, "The cloud didn't return a result");
    }

    @Test
    void canUploadTheSongFile() {

        Song song = new Song();
        song.setId(this.getExistingSongId());


        String fileName = "test.txt";

        File file;
        try {
            // https://www.baeldung.com/reading-file-in-java
            file = new File(getClass().getClassLoader().getResource(fileName).getFile());
        } catch(NullPointerException e){
            System.out.println("The file " + fileName + " could not be readed");
            throw e;
        }

        CloudFile cloudFile = null;

        try {
            cloudFile = cloudDao.uploadSongFile(song, file, fileName);
        } catch(CloudException e) {
            System.out.println("An exception occurred");
        }

        Assertions.assertNotNull(cloudFile, "The file didn't uploaded");
    }

    private int getRandom() {
        int min = 10000;
        int max = 100000;

        return (int)(Math.random() * (max - min + 1) + min);
    }

    private int getExistingSongId() {
        return 2;
    }
}
