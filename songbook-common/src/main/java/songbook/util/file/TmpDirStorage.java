package songbook.util.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import songbook.util.file.entity.FileHolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


@Component
public class TmpDirStorage {

    private TmpResourceResolver resolver;

    private File tmpDir;

    private List<FileHolder> files;

    public TmpDirStorage(TmpResourceResolver resolver) throws FileException {
        this.resolver = resolver;
        tmpDir = resolver.createRandomTmpDir();
        files = new ArrayList<>();
    }

    FileHolder storeByteContent(byte[] contents, String originalFileName) throws FileException {

        String tmpFileName = resolver.createRandomTmpFileName(tmpDir);
        File file = new File(tmpFileName);

        try {
            if(!file.createNewFile()){
                throw new FileException("Unable to create empty file in \"" + tmpFileName + "\"");
            }
        } catch (IOException e) {
            throw new FileException("Unable to create empty file in \"" + tmpFileName + "\"", e);
        }

        OutputStream os;

        try {
            os = new FileOutputStream(file);
        } catch(IOException e) {
            throw new FileException("Unable to receive output stream into file \"" + tmpFileName + "\"", e);
        }

        try {
            os.write(contents);
        } catch(IOException e) {
            throw new FileException("Unable to write bytes into file file \"" + tmpFileName + "\"", e);
        }

        FileHolder holder = new FileHolder(file, originalFileName);
        files.add(holder);

        return holder;
    }

    public List<FileHolder> getFiles() {
        return files;
    }

    void removeTmpDirContents() {

        int succCounter = 0;
        for (FileHolder fileHolder : files ) {
            if(fileHolder.getFile().delete()) {
                succCounter++;
            }
        }

        if(succCounter == files.size()) {
            // means that we've deleted all files
            tmpDir.delete();
        }
    }

}
