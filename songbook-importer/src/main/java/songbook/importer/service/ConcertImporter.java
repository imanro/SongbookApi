package songbook.importer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import songbook.concert.entity.Concert;
import songbook.concert.entity.ConcertGroup;
import songbook.concert.entity.ConcertItem;
import songbook.importer.dao.source.ConcertSrcDao;
import songbook.importer.dao.source.ConcertGroupSrcDao;
import songbook.importer.dao.source.ConcertItemSrcDao;
import songbook.concert.repository.ConcertDao;
import songbook.concert.repository.ConcertGroupDao;
import songbook.concert.repository.ConcertItemDao;
import songbook.domain.song.entity.Song;
import songbook.song.repository.SongDao;
import songbook.domain.user.entity.User;

import java.util.List;

@Service
public class ConcertImporter extends AbstractImporter {

    @Autowired
    private ConcertSrcDao concertSrcDao;

    @Autowired
    private ConcertItemSrcDao concertItemSrcDao;

    @Autowired
    private ConcertGroupSrcDao concertGroupSrcDao;

    @Autowired
    private ConcertDao concertTrgDao;

    @Autowired
    private ConcertItemDao concertItemTrgDao;

    @Autowired
    private ConcertGroupDao concertGroupTrgDao;

    @Autowired
    private SongDao songTrgDao;


    public void runImport() {
        User user = getDefaultUser();

        List<songbook.importer.model.source.Concert> srcConcerts = concertSrcDao.findAll();

        srcConcerts.forEach((srcConcert) -> {

            // we need to create trg concert
            Concert concert = new Concert();
            concert.setUser(user);

            concert.setTime(srcConcert.getTime());
            concert.setSbV1Id(srcConcert.getId());

            concertTrgDao.save(concert);

            // rewrite createtime
            concert.setCreateTime(srcConcert.getCreateTime());
            concertTrgDao.save(concert);

            // then, we need to find src concert items for this concert... (by id)
            List<songbook.importer.model.source.ConcertItem> srcConcertItems = concertItemSrcDao.findAllByConcertId(srcConcert.getId());

            // plus, find concertGroups by this concert; we will then search between them by id,
            List<songbook.importer.model.source.ConcertGroup> srcConcertGroups = concertGroupSrcDao.findAllByConcertId(srcConcert.getId());

            srcConcertItems.forEach((srcConcertItem) -> {
                // for each concert item

                // search for a corresponding song
                Song song = this.songTrgDao.findByCode(String.valueOf(srcConcertItem.getSongId()));

                if(song != null) {
                    // if song is found

                    // create concert item
                    ConcertItem concertItem = new ConcertItem();

                    // set concert
                    concertItem.setConcert(concert);

                    concertItem.setOrderValue(srcConcertItem.getOrder());

                    // add song
                    concertItem.setSong(song);

                    // if there is a group
                    if(srcConcertItem.getConcertGroupId() != 0) {

                        // search in groups, if found
                        songbook.importer.model.source.ConcertGroup srcConcertGroup = ConcertGroupSrcDao.findConcertGroupsById(srcConcertGroups, srcConcertItem.getConcertGroupId());

                        if(srcConcertGroup != null) {

                            // create group
                            ConcertGroup concertGroup = new ConcertGroup();

                            // assign name and concert
                            concertGroup.setName(srcConcertGroup.getName());

                            concertGroup.setConcert(concert);

                            // save
                            this.concertGroupTrgDao.save(concertGroup);

                            // asssign this group to concertItem
                            concertItem.setConcertGroup(concertGroup);
                        }
                    }

                    this.concertItemTrgDao.save(concertItem);

                    // double save createTime to really save ite
                    concertItem.setCreateTime(srcConcertItem.getCreateTime());
                    this.concertItemTrgDao.save(concertItem);
                    // store concert item :)))
                }
            });



            System.out.println("Concert " + srcConcert.getId() + " found, its time is: " + srcConcert.getTime().toString() +
                    " And there is " + srcConcertItems.size() + " concert items for it; concert groups amount: " + srcConcertGroups.size());
        });
    }
}
