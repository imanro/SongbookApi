package songbook.adapter.tag.out;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import songbook.domain.song.port.out.FindTagByIdPort;
import songbook.tag.entity.Tag;
import songbook.tag.repository.TagDao;

import java.util.Optional;

@Component
public class TagRdbmAdapter implements FindTagByIdPort {
    private final TagDao tagDao;

    @Autowired
    public TagRdbmAdapter(TagDao tagDao) {
        this.tagDao = tagDao;
    }

    @Override
    public Optional<Tag> findTagById(long id) {
        return tagDao.findById(id);
    }

    public Tag saveTag(Tag tag) {
        return tagDao.save(tag);
    }
}
