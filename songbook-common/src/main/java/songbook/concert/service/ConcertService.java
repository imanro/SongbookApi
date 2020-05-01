package songbook.concert.service;

import songbook.concert.entity.ConcertItem;

import java.util.List;

public interface ConcertService {
    void setNewConcertItemOrderValue(ConcertItem item) throws ConcertServiceException;

    void setConcertItemsOrderValues(List<ConcertItem> items);
}
