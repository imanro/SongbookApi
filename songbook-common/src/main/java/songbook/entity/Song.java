package songbook.entity;

import javax.persistence.Entity;

@Entity
public class Song {

    private long id;

    @Override
    public String toString() {
        return String.format("Song{id=%d}", id);
    }

    // To remove, made just for test
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
