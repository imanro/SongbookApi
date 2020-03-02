package songbook.importer.model.source;

import java.util.Date;

public class ConcertItem {
    private long id;

    private Date create_time;

    private int song_id;

    private int concert_id;

    private int concert_group_id;

    private int order;

    public long getId() {
        return id;
    }

    public ConcertItem setId(long id) {
        this.id = id;
        return this;
    }

    public Date getCreateTime() {
        return create_time;
    }

    public ConcertItem setCreateTime(Date create_time) {
        this.create_time = create_time;
        return this;
    }

    public int getSongId() {
        return song_id;
    }

    public ConcertItem setSongId(int song_id) {
        this.song_id = song_id;
        return this;
    }

    public int getConcertId() {
        return concert_id;
    }

    public ConcertItem setConcertId(int concert_id) {
        this.concert_id = concert_id;
        return this;
    }

    public int getConcertGroupId() {
        return concert_group_id;
    }

    public ConcertItem setConcertGroupId(int concert_group_id) {
        this.concert_group_id = concert_group_id;
        return this;
    }

    public int getOrder() {
        return order;
    }

    public ConcertItem setOrder(int order) {
        this.order = order;
        return this;
    }
}

