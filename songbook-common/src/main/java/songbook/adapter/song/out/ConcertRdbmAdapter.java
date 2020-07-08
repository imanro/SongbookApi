package songbook.adapter.song.out;

import songbook.concert.entity.Concert;
import songbook.concert.entity.ConcertItem;
import songbook.concert.repository.ConcertDao;
import songbook.concert.repository.ConcertItemDao;
import songbook.domain.song.port.out.FindConcertItemsBySongIdPort;
import songbook.domain.song.port.out.SaveConcertItemPort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
public class ConcertRdbmAdapter implements FindConcertItemsBySongIdPort, SaveConcertItemPort {

    private final ConcertDao concertDao;

    private final ConcertItemDao concertItemDao;

    @Autowired
    public ConcertRdbmAdapter(ConcertDao concertDao, ConcertItemDao concertItemDao) {
        this.concertDao = concertDao;
        this.concertItemDao = concertItemDao;
    }

    @Override
    public List<ConcertItem> findConcertItemsBySongId(long songId) {
        return this.concertItemDao.findBySongId(songId);
    }

    public Optional<Concert> findConcertById(long concertId) {
        return this.concertDao.findById(concertId);
    }

    public Concert saveConcert(Concert concert) {
        return concertDao.save(concert);
    }

    @Override
    public ConcertItem saveConcertItem(ConcertItem item) {
        return this.concertItemDao.save(item);
    }
}
