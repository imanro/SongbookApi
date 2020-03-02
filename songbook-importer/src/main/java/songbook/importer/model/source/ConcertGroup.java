package songbook.importer.model.source;

public class ConcertGroup {
    private long id;

    private int concert_id;

    private String name;

    public long getId() {
        return id;
    }

    public ConcertGroup setId(long id) {
        this.id = id;
        return this;
    }

    public int getConcertId() {
        return concert_id;
    }

    public ConcertGroup setConcertId(int concertId) {
        this.concert_id = concertId;
        return this;
    }

    public String getName() {
        return name;
    }

    public ConcertGroup setName(String name) {
        this.name = name;
        return this;
    }
}
