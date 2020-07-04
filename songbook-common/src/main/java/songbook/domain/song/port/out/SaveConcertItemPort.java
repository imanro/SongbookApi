package songbook.domain.song.port.out;

import songbook.concert.entity.ConcertItem;

public interface SaveConcertItemPort {
    ConcertItem saveConcertItem(ConcertItem item);
}
