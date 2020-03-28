package songbook.cloud.repository;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import songbook.cloud.CloudException;
import songbook.cloud.driver.CloudDriver;
import songbook.cloud.entity.CloudFile;
import songbook.song.entity.Song;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import songbook.cloud.driver.GDrive;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Repository
public class CloudDao {

    private CloudDriver driver;

    private String rootFolderName;

    @Autowired
    private AutowireCapableBeanFactory autowireCapableBeanFactory;


    // TODO: create test for GDrive driver especially
    public CloudDao() throws CloudException {
        // create the driver
        // FIXME: strategy or smth like this
        // this.driver = new GDrive();
    }

    /**
     * Returns CloudFile list
     * @param song
     * @return
     * @throws CloudException
     */
    public List<CloudFile> getSongFiles(Song song) throws CloudException {
        return getDriver().findFilesByFolderName(String.valueOf(song.getId()));
    }

    public CloudFile uploadSongFile(Song song, java.io.File filePath, String fileName) throws CloudException {

        // get song folder first
        CloudFile songFolder = getDriver().findFolder(String.valueOf(song.getId()));

        // okay, now detect file's mimetype
        Path path = filePath.toPath();

        String mimeType;

        try {
            mimeType = Files.probeContentType(path);
        } catch (IOException e) {
            mimeType = "application/octet-stream";
        }

        // we have all set, lets go
        return getDriver().uploadFile(filePath, fileName, mimeType, songFolder);
    }

    public ByteArrayOutputStream getFileContents(CloudFile file) throws CloudException {
        // okay, lets get the mime substitute for file mime type
        return getDriver().getFileContents(file);
    }

    protected CloudDriver getDriver() throws CloudException {
        if(this.driver == null) {
            CloudDriver driver = new GDrive();
            this.autowireCapableBeanFactory.autowireBean(driver);
            this.setDriver(driver);
        }

        return this.driver;
    }

    protected CloudDao setDriver(CloudDriver driver) {
        this.driver = driver;
        return this;
    }
}
