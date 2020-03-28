package songbook.util.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import songbook.util.file.configuration.FileProperties;

import java.io.File;

@Component
public class TmpResourceResolver {

    @Autowired
    FileProperties properties;

    public File getRandomTmpDirName() throws FileException {

        String rootTmpDir;
        try {
             rootTmpDir = properties.getTmpDirFsPath();
        } catch (Exception e) {
            throw new FileException("Unable to get rootTmpDir name from properties", e);
        }

        File tmpDir;
        final int limit = 10;
        int counter = 0;

        do {
            String tmpDirName = rootTmpDir + "/" + getFileNameBasedOnRandomNumber();
            tmpDir = new File(tmpDirName);

        } while(tmpDir.exists() && counter++ < limit);

        if(!tmpDir.exists()) {
            return tmpDir;
        } else {
            throw new FileException("Unable to generate free tmp dir name");
        }
    }

    public String getFileNameBasedOnRandomNumber() {
        return String.valueOf(getRandom());
    }

    private int getRandom() {
        int min = 10000;
        int max = 100000;

        return (int)(Math.random() * (max - min + 1) + min);
    }

}
