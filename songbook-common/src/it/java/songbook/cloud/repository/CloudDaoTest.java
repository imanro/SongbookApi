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

import java.io.*;
import java.util.List;

@SpringBootTest
class CloudDaoTest {

    @Autowired
    private CloudDao cloudDao;

    @Test
    void canReadSongFilesFolderExists() {
        System.out.println("Executing");

        Song song = new Song();
        song.setCode(this.getExistingSongCode());

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
        song.setId(Long.valueOf(rand));

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
    void canUploadTheSongFile() throws NullPointerException, CloudException {

        Song song = new Song();
        song.setCode(this.getExistingSongCode());

        String fileName = "test.txt";

        File file;

        // https://www.baeldung.com/reading-file-in-java
        file = new File(getClass().getClassLoader().getResource(fileName).getFile());

        CloudFile cloudFile = cloudDao.uploadSongFile(song, file, fileName);

        Assertions.assertNotNull(cloudFile, "The file didn't uploaded");
    }

    @Test
    void fileOfRegularMimeTypeCanBeDownloaded() throws CloudException {
        CloudFile fileToDownload = new CloudFile();
        fileToDownload.setId("1vA092O5FP1DtZ7p__JtYyJbx-jF8WIvo");
        fileToDownload.setMimeType("application/pdf");

        ByteArrayOutputStream stream = cloudDao.getFileContents(fileToDownload);
        byte[] bytes = stream.toByteArray();

        Assertions.assertTrue(bytes.length > 0, "The bytes of a given file are null");
        // stream.get
        // Assertions.assert
    }

    @Test
    void fileOfGoogleDriveTypeCanBeDownloaded() throws CloudException {
        CloudFile fileToDownload = new CloudFile();
        fileToDownload.setId("10TxbOByfMUb1ytFQ72XNHWQHiAHHwjKEzNE7BJBhFJA");
        fileToDownload.setMimeType("application/vnd.google-apps.document");

        ByteArrayOutputStream stream = cloudDao.getFileContents(fileToDownload);
        byte[] bytes = stream.toByteArray();

        Assertions.assertTrue(bytes.length > 0, "The bytes of a given file are null");
        // stream.get
        // Assertions.assert
    }

    private int getRandom() {
        int min = 10000;
        int max = 100000;

        return (int)(Math.random() * (max - min + 1) + min);
    }

    private String getExistingSongCode() {
        return "2";
    }
}
