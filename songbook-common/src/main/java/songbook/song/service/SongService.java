package songbook.song.service;
import songbook.cloud.entity.CloudFile;
import songbook.song.entity.Song;
import songbook.song.entity.SongContent;
import songbook.user.entity.User;

import java.util.List;

public interface SongService {

    Song syncCloudContent(Song song, User user) throws SongServiceException;

    CloudFile createCloudContentFromSongContent(SongContent content) throws SongServiceException;

    int mergeSongs(long masterID, List<Long> toMerge);
}
