package songbook.suggest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import songbook.concert.entity.Concert;
import songbook.suggest.entity.SongStatProj;

import java.util.List;

public interface SongStatService {
    List<Long> extractConcertIds(Page<SongStatProj> items);

    Page<SongStatProj> attachConcertsToStat(Page<SongStatProj> items, List<Concert> concerts, Pageable req);
}
