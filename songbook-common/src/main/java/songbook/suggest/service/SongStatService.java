package songbook.suggest.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import songbook.concert.entity.Concert;
import songbook.suggest.entity.PopularSongProj;

import java.util.List;

public interface SongStatService {
    List<Long> extractConcertIds(Page<PopularSongProj> items);

    Page<PopularSongProj> attachConcertsToStat(Page<PopularSongProj> items, List<Concert> concerts, Pageable req);
}
