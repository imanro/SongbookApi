package songbook.importer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import songbook.importer.dao.source.SongSrcDao;
import songbook.song.entity.Song;
import songbook.song.repository.SongDao;

@Service
public class SongImporter {
    @Autowired
    private SongSrcDao songSrcDao;

    @Autowired
    private SongDao songDaoTrg;

    public void runImport() {
        // get all src songs
        List<songbook.importer.model.source.Song> srcSongs = songSrcDao.findAll();

        srcSongs.forEach((srcSong) -> {
            System.out.println("Importing \"" + srcSong.getId() + "\" song");

            Song song = new Song();
            song.setAuthor(srcSong.getAuthor());
            song.setTitle(srcSong.getTitle());
            song.setUpdateTime(srcSong.getCreateTime());
            song.setCode(String.valueOf(srcSong.getId()));
            songDaoTrg.save(song);

            System.out.println("The create time is " + srcSong.getCreateTime().toString());
            song.setCreateTime(srcSong.getCreateTime());
            // double save to preserve date
            songDaoTrg.save(song);
        });
    }



}
