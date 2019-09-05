package songbook.song.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Tag {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    public long getId() {
        return id;
    }

    public Tag setId(long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Tag setTitle(String title) {
        this.title = title;
        return this;
    }

    public Set<Song> getSongs() {
        return songs;
    }

    public Tag setSongs(Set<Song> songs) {
        this.songs = songs;
        return this;
    }

    private String title;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "tags")
    private Set<Song> songs = new HashSet<>();

}
