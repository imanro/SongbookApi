package songbook.song.service;

import org.springframework.stereotype.Service;
import songbook.song.entity.Song;
import songbook.song.entity.SongContent;
import songbook.tag.entity.Tag;
import songbook.user.entity.User;

import java.util.List;

public interface SongService {

    Song syncCloudContent(Song song, User user) throws SongServiceException;

    int mergeSongs(long masterID, List<Long> toMerge);
}
