package songbook.cloud.driver;

import songbook.cloud.entity.CloudFile;
import java.util.List;

public interface CloudDriver {

    CloudFile findFolder(String name) throws CloudDriverException;

    List<CloudFile> findFiles(CloudFile parentFolder) throws CloudDriverException;

    List<CloudFile> findFilesByFolderName(String folderName) throws CloudDriverException;

    CloudFile createFolder(String name) throws CloudDriverException;

    CloudFile createFolder(String name, CloudFile parentFolder) throws CloudDriverException;

    CloudFile uploadFile(java.io.File filePath, String fileName, String mimeType, CloudFile parentFolder) throws CloudDriverException;
}
