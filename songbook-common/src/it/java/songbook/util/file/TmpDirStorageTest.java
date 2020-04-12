package songbook.util.file;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Assertions;
import songbook.util.file.entity.FileHolder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootTest
public class TmpDirStorageTest {

    @Autowired
    TmpResourceResolver tmpResourceResolver;

    @Test
    void fileContentCanBeStored() throws FileException, IOException {

        TmpDirStorage storage = new TmpDirStorage(tmpResourceResolver);

        String fileName1 = "test1.txt";
        String contents1 = "I am string content of the file\n";
        int length1 = contents1.length();

        String fileName2 = "test2.txt";
        String contents2 = "I am another string content of the file\n";
        int length2 = contents2.length();

        // check that we can store some byte contents using this class:
        storage.storeByteContent(contents1.getBytes(), fileName1);
        storage.storeByteContent(contents2.getBytes(), fileName2);

        Assertions.assertEquals(2, storage.getFiles().size(), "The size of stored files is not match to the expected value");

        // we can receive then file itself, and the link on container
        FileHolder storedFile1 = storage.getFiles().get(0);
        FileHolder storedFile2 = storage.getFiles().get(1);

        String storedContents1 = new String(Files.readAllBytes(Paths.get(storedFile1.getFile().getAbsolutePath())));
        String storedContents2 = new String(Files.readAllBytes(Paths.get(storedFile2.getFile().getAbsolutePath())));


        Assertions.assertEquals(length1, storedContents1.length(), "The length of stored file 1 is not matched to expected");
        Assertions.assertEquals(length2, storedContents2.length(), "The length of stored file 2 is not matched to expected");

        Assertions.assertEquals(storedFile1.getOriginalName(), fileName1, "The stored name of file 1 doesn't match to expected one");
        Assertions.assertEquals(storedFile2.getOriginalName(), fileName2, "The stored name of file 2 doesn't match to expected one");
    }




    // and, check that deleting also works
}
