package songbook.domain.song.port.in;

import songbook.domain.song.entity.Song;
import songbook.user.entity.User;

public interface AttachSongToTagCommand {
    Song attachSongToTag(long songId, long tagId, User user) throws AttachSongToTagSongNotFoundException, AttachSongToTagTagNotFoundException;
}
