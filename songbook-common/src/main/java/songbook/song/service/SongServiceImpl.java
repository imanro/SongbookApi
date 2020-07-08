package songbook.song.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import songbook.cloud.CloudException;
import songbook.cloud.entity.CloudFile;
import songbook.cloud.repository.CloudDao;
import songbook.concert.entity.ConcertItem;
import songbook.concert.repository.ConcertItemDao;
import songbook.domain.song.entity.Song;
import songbook.song.entity.SongContent;
import songbook.song.entity.SongContentTypeEnum;
import songbook.song.repository.SongDao;
import songbook.song.repository.SongContentDao;
import songbook.tag.entity.Tag;
import songbook.user.entity.User;

import javax.transaction.Transactional;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

@Service
@Transactional
public class SongServiceImpl implements SongService {

    @Autowired
    private SongDao songDao;

    @Autowired
    private SongContentDao songContentDao;

    @Autowired
    private ConcertItemDao concertItemDao;

    @Autowired
    private CloudDao cloudDao;

    @Override
    public CloudFile createCloudContentFromSongContent(SongContent content) throws SongServiceException {

        if(content.getType() != SongContentTypeEnum.GDRIVE_CLOUD_FILE) {
            throw new SongServiceException("The given songContent entity has not right type (" + content.getType() + ")");
        }
        CloudFile cloudFile = new CloudFile();
        cloudFile.setId(content.getContent());
        cloudFile.setName(content.getFileName());
        cloudFile.setMimeType(content.getMimeType());
        return cloudFile;
    }

    private <T, U> List<T> findMissingItems(List<T> first, List<U> second, BiPredicate<T, U> compareLambda) {

        List<T> missing = new ArrayList();

        // We SHOULD init stream each time when we using it, because it gets rot
        first.stream().forEach(item -> {
            // 4) search CloudFile id withing content's "content" property
            U foundContent = second.stream().filter(compareItem -> compareLambda.test(item, compareItem)).findAny().orElse(null);

            // 5) Store missing cloudFiles in new list
            if(foundContent == null) {
                missing.add(item);
            }
        });

        return missing;
    }

}
