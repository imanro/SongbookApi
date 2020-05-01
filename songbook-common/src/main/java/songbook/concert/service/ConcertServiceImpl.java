package songbook.concert.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import songbook.concert.entity.Concert;
import songbook.concert.entity.ConcertItem;
import songbook.concert.repository.ConcertDao;
import songbook.concert.repository.ConcertItemDao;

import java.util.List;

@Service
public class ConcertServiceImpl implements ConcertService {

    @Autowired
    ConcertDao concertDao;

    @Autowired
    ConcertItemDao conertItemDao;

    @Override
    public void setNewConcertItemOrderValue(ConcertItem item) throws ConcertServiceException {
        Concert checkConcert = concertDao.findById(item.getConcert().getId()).orElseThrow(() -> new ConcertServiceException("A concert wasnt found"));
        int lastOrder = checkConcert.getItems().size();
        item.setOrderValue(lastOrder);
    }

    @Override
    public void setConcertItemsOrderValues(List<ConcertItem> items) {
        for(int i = 0; i < items.size(); i++) {
            ConcertItem item = items.get(i);
            item.setOrderValue(i + 1);
        }
    }
}
