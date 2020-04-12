package songbook.util.file;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;

@SpringBootTest
public class TmpResourceResolverTest {

    private File checkFile;

    @AfterEach
    void cleanFiles() {
        if(checkFile.exists()) {
            checkFile.delete();
        }
    }

    @Autowired
    private TmpResourceResolver tmpResourceResolver;

    // check that we can generate random name inside "/tmp" directory and that file will be available to writing
    @Test
    void randomTmpFilePathCanBeCreated() throws FileException, IOException {
        String rootTmpDirName = "/tmp";
        File tmpDir = new File(rootTmpDirName);

        String fileName = tmpResourceResolver.createRandomTmpFileName(tmpDir);
        checkFile = new File(fileName);
        checkFile.createNewFile();

        Assertions.assertTrue(checkFile.exists(), "The generated file does not exist");
    }


    // check that we can create tmp dir inside "/tmp"
    @Test
    void randomTmpDirCanBeCreated() throws FileException, IOException {
        checkFile = tmpResourceResolver.createRandomTmpDir();
        Assertions.assertTrue(checkFile.exists(), "The generated tmp dir does not exist");
    }
}
