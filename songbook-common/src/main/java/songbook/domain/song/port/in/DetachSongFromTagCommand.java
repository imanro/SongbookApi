package songbook.domain.song.port.in;

import songbook.domain.song.entity.Song;

import songbook.user.entity.User;

public interface DetachSongFromTagCommand {
    Song detachSongFromTag(long songId, long tagId, User user) throws DetachSongFromTagSongNotFoundException, DetachSongFromTagTagNotFoundException;
}
