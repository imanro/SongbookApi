package songbook.song.repository;

import songbook.domain.song.entity.Song;
import songbook.user.entity.User;

public interface SongDaoCustom {
    void refresh(Song song);

    void initContentUserFilter(User user);

//    void initHeaderTypeFilter();
}
