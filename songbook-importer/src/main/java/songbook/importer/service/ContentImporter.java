package songbook.importer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import songbook.song.entity.Song;
import songbook.song.entity.SongContent;
import songbook.song.entity.SongContentTypeEnum;
import songbook.song.repository.SongContentDao;
import songbook.song.repository.SongDao;
import songbook.user.entity.User;
import songbook.user.entity.repository.UserDao;

import java.util.List;
import java.util.Optional;

@Service
public class ContentImporter {
    @Autowired
    private songbook.importer.dao.source.SongContentDaoImpl songContentDaoSrc;

    @Autowired
    private SongDao songDaoTrg;

    @Autowired
    private UserDao userDao;

    @Autowired
    private SongContentDao songContentDaoTrg;

    public void runImport() {
        // get all src song content, for each
        User user = getDefaultUser();

        List<songbook.importer.model.source.SongContent> srcContents = songContentDaoSrc.findAll();

        srcContents.forEach((srcContent) -> {

            Song song = this.songDaoTrg.findBySbV1Id(srcContent.getSongId());
            // find corresponding song by field sb_v1_id

            if (song != null) {
                System.out.println("The song with sb_v1_id #" + song.getSbV1Id() + " found, importing content");

                SongContent content = new SongContent();
                content.setUser(user);
                content.setSong(song);

                SongContentTypeEnum type;

                try {
                    type = convertType(srcContent.getType());
                } catch(ContentImporterException e) {
                    System.out.println("Unknown type given, " + srcContent.getType());
                    // skip
                    return;
                }
                content.setType(type);

                content.setContent(srcContent.getContent());
                content.setMimeType(srcContent.getMimeType());
                content.setIsFavorite(srcContent.getIsFavorite());
                content.setFileName(srcContent.getFileName());
                songContentDaoTrg.save(content);


                // double save to persist createTime
                System.out.println("The create time is " + srcContent.getCreateTime().toString());
                content.setCreateTime(srcContent.getCreateTime());
                songContentDaoTrg.save(content);

            } else {
                System.out.println("There is no such song with sb_v1_id #" + song.getSbV1Id() + ", skipping");
            }
        });
    }

    public User getDefaultUser() {
        long id = 1;
        Optional<User> user = userDao.findById(id);
        if(user.isPresent()) {
            System.out.println("Default user is found, getting by ID");
            return user.get();
        } else {
            System.out.println("Creating a new default user");
            return createDefaultUser();
        }
    }

    public SongContentTypeEnum convertType(String type) throws ContentImporterException {
        switch(type) {
            case("header"):
                return SongContentTypeEnum.HEADER;
            case("inline"):
                return SongContentTypeEnum.INLINE;
            case("link"):
                return SongContentTypeEnum.LINK;
            case("gdrive_cloud_file"):
                return SongContentTypeEnum.GDRIVE_CLOUD_FILE;
            default:
                throw new ContentImporterException("Unknown type given");
        }
    }

    private User createDefaultUser() {
        User newUser = new User();
        newUser.setId(1);
        newUser.setEmail("nobody@example.com");
        userDao.save(newUser);
        return newUser;
    }
}
