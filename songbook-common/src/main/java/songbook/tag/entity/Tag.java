package songbook.tag.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import songbook.song.entity.Song;
import songbook.tag.view.Summary;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Tag {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @JsonView(Summary.class)
    private long id;

    @JsonView(Summary.class)
    private String title;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "tags")
    @JsonIgnore
    private Set<Song> songs = new HashSet<>();

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
}
