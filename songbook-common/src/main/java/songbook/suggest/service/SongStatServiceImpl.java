package songbook.suggest.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import songbook.concert.entity.Concert;
import songbook.suggest.entity.SongStatProj;
import songbook.suggest.entity.SongStatProjImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SongStatServiceImpl implements SongStatService {
    @Override
    public List<Long> extractConcertIds(Page<SongStatProj> items) {
        List<Long> ids = new ArrayList<>();

        for(SongStatProj item : items.getContent()) {
            ids.add(item.getLastConcertId());
            // System.out.println(item.getLastConcertId());
        }

        return ids;
    }

    @Override
    public Page<SongStatProj> attachConcertsToStat(Page<SongStatProj> items, List<Concert> concerts, Pageable req) {

        return new PageImpl<SongStatProj>(
                items.getContent().stream()
                        .map(item -> {

                            SongStatProj newItem = new SongStatProjImpl(item.getSong(), item.getTotal(), item.getLastConcertId());
                            Concert concert = concerts.stream().filter(curConcert -> curConcert.getId() == item.getLastConcertId()).findAny().orElse(null);

                            if(concert != null){
                                newItem.setLastConcert(concert);
                            }

                            return newItem;
                        }).collect(Collectors.toList()),
                req, items.getContent().size());
    }
}
