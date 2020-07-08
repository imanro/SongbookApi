package songbook.song.service;
import songbook.cloud.entity.CloudFile;
import songbook.domain.song.entity.Song;
import songbook.song.entity.SongContent;
import songbook.user.entity.User;

public interface SongService {
    CloudFile createCloudContentFromSongContent(SongContent content) throws SongServiceException;
}
