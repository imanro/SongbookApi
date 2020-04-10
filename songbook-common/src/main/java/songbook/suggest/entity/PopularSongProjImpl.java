package songbook.suggest.entity;

import songbook.concert.entity.Concert;
import songbook.song.entity.Song;

public class PopularSongProjImpl implements PopularSongProj {

    private long total;

    private long lastConcertId;

    private Song song;

    private Concert lastConcert;

    public PopularSongProjImpl(Song song, long total, long lastConcertId) {
        this.setTotal(total);
        this.setSong(song);
        this.setLastConcertId(lastConcertId);
    }

    @Override
    public long getTotal() {
        return this.total;
    }

    @Override
    public PopularSongProj setTotal(long total) {
        this.total = total;
        return this;
    }

    @Override
    public Long getLastConcertId() {
        return lastConcertId;
    }

    @Override
    public PopularSongProj setLastConcertId(long id) {
        this.lastConcertId = id;
        return this;
    }

    @Override
    public Concert getLastConcert() {
        return lastConcert;
    }

    @Override
    public PopularSongProj setLastConcert(Concert concert) {
        this.lastConcert = concert;
        return this;
    }

    @Override
    public Song getSong() {
        return song;
    }

    @Override
    public PopularSongProj setSong(Song song) {
        this.song = song;
        return this;
    }
}
