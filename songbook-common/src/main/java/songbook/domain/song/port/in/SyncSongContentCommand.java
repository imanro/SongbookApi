package songbook.domain.song.port.in;

import songbook.user.entity.User;
import songbook.domain.song.entity.Song;

public interface SyncSongContentCommand {

    Song syncSongContent(long songId, User user) throws SyncSongContentNotInitializedException, SyncSongContentSongNotFoundException, SyncSongContentUnableToGetSongCloudFilesException;
}
