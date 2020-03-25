package songbook.cloud.repository;

import songbook.cloud.CloudException;
import songbook.cloud.CloudFileNotFoundException;
import songbook.cloud.driver.CloudDriverException;
import songbook.cloud.entity.CloudFile;
import songbook.song.entity.Song;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import songbook.cloud.driver.GDrive;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import com.google.api.services.drive.model.File;

@Repository
public class CloudDao {

    private GDrive driver;

    private String rootFolderName;

    // TODO: create test for GDrive driver especially
    public CloudDao() throws CloudException {
        // create the driver
        // FIXME: strategy or smth like this
        this.driver = new GDrive();
    }

    /**
     * Returns CloudFile list
     * @param song
     * @return
     * @throws CloudException
     */
    public List<CloudFile> getSongFiles(Song song) throws CloudException {
        return driver.findFilesByFolderName(String.valueOf(song.getId()));
    }

    public CloudFile uploadSongFile(Song song, java.io.File filePath, String fileName) throws CloudException {
        // get song folder first
        CloudFile songFolder = driver.findFolder(String.valueOf(song.getId()));

        // okay, now detect file's mimetype
        Path path = filePath.toPath();

        String mimeType;

        try {
            mimeType = Files.probeContentType(path);
        } catch (IOException e) {
            mimeType = "application/octet-stream";
        }

        // we have all set, lets go
        return driver.uploadFile(filePath, fileName, mimeType, songFolder);
    }

    public CloudDao setRootFolderName(String name) {
        this.rootFolderName = name;
        this.driver.setRootFolderName(this.rootFolderName);
        return this;
    }

    public String getRootFolderName() {
        return this.rootFolderName;
    }

    boolean downloadFile(Song song, String name, String path) {
        return true;
    }

    String getFileMimeType() {
        return "";
    }


    //    fixme return type
    private void getSongFolder(Song song) {

    }

}
