package songbook.domain.song.port.out;

import songbook.cloud.entity.CloudFile;
import songbook.domain.song.entity.Song;

import java.util.List;

public interface FindCloudSongFilesPort {
    List<CloudFile> findSongFiles(Song song) throws FindCloudSongFilesPortException;
}
