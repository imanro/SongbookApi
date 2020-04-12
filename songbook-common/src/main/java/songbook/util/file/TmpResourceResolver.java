package songbook.util.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import songbook.util.file.configuration.FileProperties;

import java.io.File;

@Component
public class TmpResourceResolver {

    @Autowired
    FileProperties properties;

    public String createRandomTmpFileName(File tmpDir) throws FileException {
        final int limit = 10;
        int counter = 0;
        String tmpFileName;
        File checkFile;

        do {
            tmpFileName = tmpDir.getAbsolutePath() + "/" + getFileNameBasedOnRandomNumber();
            checkFile = new File(tmpFileName);

        } while(checkFile.exists() && counter++ < limit);

        if(!checkFile.exists()) {
            return tmpFileName;
        } else {
            throw new FileException("Unable to generate free resource name");
        }
    }

    public File createRandomTmpDir() throws FileException {

        String rootTmpDirName;
        try {
             rootTmpDirName = properties.getTmpDirFsPath();
        } catch (Exception e) {
            throw new FileException("Unable to get rootTmpDir name from properties", e);
        }

        File rootTmpDir = new File(rootTmpDirName);

        String tmpDirName = createRandomTmpFileName(rootTmpDir);

        File tmpDir = new File(tmpDirName);

        if (!tmpDir.mkdir() ) {
            throw new FileException("Cannot create the tmp dir \"" + tmpDirName + "\"!");
        }

        return tmpDir;
    }

    private String getFileNameBasedOnRandomNumber() {
        return String.valueOf(getRandom());
    }

    private int getRandom() {
        int min = 10000;
        int max = 100000;

        return (int)(Math.random() * (max - min + 1) + min);
    }

}
