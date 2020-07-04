package songbook.adapter.song.out;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import songbook.cloud.CloudException;
import songbook.cloud.entity.CloudFile;
import songbook.cloud.repository.CloudDao;
import songbook.domain.song.entity.Song;
import songbook.domain.song.port.out.FindCloudSongFilesPort;
import songbook.domain.song.port.out.FindCloudSongFilesPortException;

import java.util.List;

@Component
public class CloudContentAdapter implements FindCloudSongFilesPort {
    private final CloudDao cloudDao;

    @Autowired
    public CloudContentAdapter(CloudDao cloudDao) {
        this.cloudDao = cloudDao;
    }

    @Override
    public List<CloudFile> findSongFiles(Song song) throws FindCloudSongFilesPortException {
        try {
            return cloudDao.getSongFiles(song);
        } catch(CloudException e) {
            throw new FindCloudSongFilesPortException("Unable to get song files for the song, an exception has occurred", e);
        }
    }
}
