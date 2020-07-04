package songbook.adapter.song.out;

import songbook.domain.song.entity.Song;
import songbook.domain.song.port.out.*;
import songbook.song.entity.SongContent;
import songbook.song.repository.SongContentDao;
import songbook.song.repository.SongDao;
import songbook.tag.entity.Tag;
import songbook.user.entity.User;
import songbook.util.list.ListSort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class SongRdbmAdapter implements FindSongsPort,
        FindSongByIdPort,
        FindSongsByTagsPort,
        FindSongsByStringPort,
        FindSongsByTagsAndStringPort,
        FindSongsByIdsPort,
        FindSongContentsBySongPort,
        FindSongTagsBySongPort,
        SaveSongPort,
        SaveSongContentPort,
        DeleteSongPort,
        DeleteSongContentPort,
        AddTagToSongPort,
        RemoveTagFromSongPort {

    private final SongDao songDao;

    private final SongContentDao songContentDao;

    private final ListSort<Song> listSortUtil;

    @Autowired
    public SongRdbmAdapter(
            SongDao songDao,
            SongContentDao songContentDao,
            ListSort<Song> listSortUtil
    ) {
        this.songDao = songDao;
        this.songContentDao = songContentDao;
        this.listSortUtil = listSortUtil;
    }

    @Override
    public Optional<Song> findSongById(long id, User user) {
        songDao.initContentUserFilter(user);
        return songDao.findById(id);
    }

    @Override
    public List<Song> findSongsByIds(List<Long> ids, User user) {
        songDao.initContentUserFilter(user);
        List<Song> songs = songDao.findAllById(ids);
        listSortUtil.sortListEntitiesByIds(songs, ids);
        return songs;
    }

    @Override
    public Page<Song> findSongsByString(String searchString, User user, Pageable pageable) {
        songDao.initContentUserFilter(user);
        return songDao.findAllByHeaderWithHeadersAndTags(searchString, pageable);
    }

    @Override
    public Page<Song> findSongsByTagsAndString(String searchString, List<Long> tagIds, User user, Pageable pageable) {
        songDao.initContentUserFilter(user);
        return songDao.findAllByTagsAndContent(tagIds, searchString, pageable);
    }

    @Override
    public Page<Song> findSongsByTags(List<Long> tagIds, User user, Pageable pageable) {
        songDao.initContentUserFilter(user);
        return songDao.findAllByTags(tagIds, pageable);
    }

    @Override
    public Page<Song> findSongs(User user, Pageable pageable) {
        songDao.initContentUserFilter(user);
        return songDao.findAllWithHeadersAndTags(pageable);
    }

    @Override
    public List<SongContent> findSongContents(Song song) {
        return new ArrayList<>(song.getContent());
    }

    @Override
    public Song saveSong(Song song) {
        Song savedSong = songDao.save(song);
        // CHECKME
        songDao.flush();
        songDao.refresh(song);
        return savedSong;
    }

    @Override
    public SongContent saveSongContent(SongContent songContent) {
        return this.songContentDao.save(songContent);
    }

    @Override
    public boolean deleteSongContent(Song song, SongContent content) {
        songContentDao.delete(content);
        return song.getContent().remove(content);
    }

    @Override
    public List<Tag> findSongTagsBySong(Song song) {
        return new ArrayList<>(song.getTags());
    }

    @Override
    public void deleteSongAtOnce(long songId) {
        songDao.deleteAtOnce(songId);
    }

    @Override
    public Song addTagToSong(Song song, Tag tag) {
        song.getTags().add(tag);
        return song;
    }

    @Override
    public Song removeTagFromSong(Song song, Tag tag) {
        song.getTags().remove(tag);
        return song;
    }
}
