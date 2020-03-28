package songbook.cloud.driver;

import com.google.api.client.http.FileContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import songbook.cloud.CloudException;
import songbook.cloud.configuration.Properties;
import songbook.cloud.entity.CloudFile;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

// @See
// https://developers.google.com/drive/api/v3/quickstart/java?authuser=1

@Component
public class GDrive implements CloudDriver {
    private static final String APPLICATION_NAME = "Songbook API v2";

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final List<String> SCOPES = Arrays.asList(DriveScopes.DRIVE);

    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static final String CREDENTIALS_FILE_PATH = "credentials.json";

    private Drive service;

    private HashMap<String, String> mimesSubstitute;

    @Autowired
    Properties properties;

    public GDrive() throws CloudException {

        System.out.println("Call Constructor!!");

        try {
            this.service = this.initService();

        } catch (GeneralSecurityException e) {
            throw new CloudException("Security exception occurred", e);

        } catch (IOException e) {
            throw new CloudException("IO exception", e);
        }

        initMimesSubstitute();
    }

    /**
     * Returns certain folder by namve in root songook's folder
     *
     * @return folder
     * @throws CloudDriverException
     */
    public CloudFile findFolder(String name) throws CloudDriverException {

        CloudFile rootFolder;

        if ((rootFolder = findRootFolder()) == null) {
            rootFolder = createRootFolder();
        }

        try {
            FileList result = this.getService().files().list()
                    .setQ("mimeType='application/vnd.google-apps.folder' and '" + rootFolder.getId() + "' in parents and name='" + name + "' and trashed=false")
                    .setSpaces("drive")
                    .execute();

            if (result.getFiles() != null && result.getFiles().size() > 0) {
                return initCloudFile(result.getFiles().get(0));

            } else {
                return null;
            }

        } catch(IOException e) {
            throw new CloudDriverException("Unable to find the folder " + name, e);
        }
    }

    /**
     * Returns certain folder by namve in root songook's folder
     *
     * @return folder
     * @throws CloudDriverException
     */
    public List<CloudFile> findFiles(CloudFile parentFolder) throws CloudDriverException {

        try {
            FileList result = getService().files().list()
                    .setQ("'" + parentFolder.getId() + "' in parents and trashed=false")
                    .setSpaces("drive")
                    .execute();

            if (result.getFiles() != null) {
                return initCloudFiles(result.getFiles());

            } else {
                return new ArrayList<CloudFile>();
            }

        } catch(IOException e) {
            throw new CloudDriverException("Unable to list files for the foloder " + parentFolder.getName(), e);
        }
    }

    public List<CloudFile> findFilesByFolderName(String folderName) throws CloudDriverException {
        CloudFile folder = findFolder(folderName);

        // if folder is not found, attempt to create it
        if (folder == null) {
            CloudFile rootFolder = findRootFolder();
            folder = createFolder(folderName, rootFolder);
        }

        return findFiles(folder);
    }

    /**
     * Creates folder google drive space
     *
     * @param name
     * @return abstract CloudFile
     * @throws CloudDriverException
     */
    @Override
    public CloudFile createFolder(String name) throws CloudDriverException {
        File fileMetadata = new File();
        fileMetadata.setName(name);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");

        try {
            File file = getService().files().create(fileMetadata)
                    .setFields("id, name")
                    .execute();

            return initCloudFile(file);

        } catch(IOException e) {
            throw new CloudDriverException("Unable to create folder " + name + ", an exception occurred", e);
        }
    }

    @Override
    public CloudFile createFolder(String name, CloudFile parentFolder) throws CloudDriverException {
        File fileMetadata = new File();
        fileMetadata.setName(name);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        fileMetadata.setParents(Arrays.asList(parentFolder.getId()));

        try {
            File file = getService().files().create(fileMetadata)
                    .setFields("id, name")
                    .execute();

            return initCloudFile(file);

        } catch(IOException e) {
            throw new CloudDriverException("Unable to create folder " + name + ", an exception occurred", e);
        }
    }

    @Override
    public CloudFile uploadFile(java.io.File filePath, String fileName, String mimeType, CloudFile parentFolder) throws CloudDriverException {
        File fileMetadata = new File();
        fileMetadata.setParents(Arrays.asList(parentFolder.getId()));
        fileMetadata.setName(fileName);

        FileContent mediaContent = new FileContent(mimeType, filePath);

        try {
            File file = getService().files().create(fileMetadata, mediaContent)
                    .setFields("id, name")
                    .execute();

            return initCloudFile(file);

        } catch (IOException e) {
            throw new CloudDriverException("Could not upload the file, an exception occurred", e);
        }
    }

    @Override
    public ByteArrayOutputStream getFileContents(CloudFile file) throws CloudDriverException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String targetMimeType = this.getSubstituteMimeType(file.getMimeType());

        if (this.isTheMimeTypeGoogleDocsFormat(file.getMimeType())) {
            try {
                getService().files().export(file.getId(), targetMimeType)
                        .executeMediaAndDownloadTo(outputStream);
            } catch (IOException e) {
                throw new CloudDriverException("Could not get file contents, an exception has occurred", e);
            }
        } else {
            try {
                getService().files().get(file.getId())
                        .executeMediaAndDownloadTo(outputStream);
            } catch (IOException e) {
                throw new CloudDriverException("Could not get file contents, an exception has occurred", e);
            }
        }

        return outputStream;
    }

    /**
     * Creates abstract cloud files by given gdrive, set its properties
     *
     * @param gdriveFile
     * @return
     */
    private CloudFile initCloudFile(File gdriveFile) {
        CloudFile file = new CloudFile();
        file.setId(gdriveFile.getId());
        file.setMimeType(gdriveFile.getMimeType());
        file.setName(gdriveFile.getName());

        return file;
    }

    /**
     * Converts gdrive files into abstract Cloud files
     *
     * @param gdriveFiles
     * @return
     */
    private List<CloudFile> initCloudFiles(List<File> gdriveFiles) {
        List<CloudFile> files = new ArrayList<CloudFile>();

        gdriveFiles.forEach((gdriveFile) -> {
            CloudFile file = initCloudFile(gdriveFile);
            files.add(file);
        });

        return files;
    }


    /**
     * Returns root folder that contain all song folders on Google drive
     *
     * @return root folder
     * @throws CloudDriverException
     */
    private CloudFile findRootFolder() throws CloudDriverException {
        try {
            FileList result = this.getService().files().list()
                    .setQ("mimeType='application/vnd.google-apps.folder' and 'root' in parents and name='" + properties.getRootFolderName() + "' and trashed=false")
                    .setSpaces("drive")
                    .execute();

            if (result.getFiles() != null && result.getFiles().size() > 0) {
                return initCloudFile(result.getFiles().get(0));

            } else {
                return null;
            }

        } catch(IOException e) {
            throw new CloudDriverException("Unable to find Root folder", e);
        }
    }

    private CloudFile createRootFolder() throws CloudDriverException {
        return createFolder(properties.getRootFolderName());
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {

        // Load client secrets.
        InputStream in = getClass().getClassLoader().getResourceAsStream(CREDENTIALS_FILE_PATH);

        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private Drive initService() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private Drive getService() {
        return service;
    }

    private boolean isTheMimeTypeGoogleDocsFormat(String mimeType) {
        return mimeType.contains("application/vnd.google-apps");
    }

    private void initMimesSubstitute() throws CloudDriverException {
        this.mimesSubstitute = new HashMap<>();
        this.mimesSubstitute.put("application/vnd.google-apps.document", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        this.mimesSubstitute.put("application/vnd.google-apps.presentation", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
    }

    private String getSubstituteMimeType(String mimeType) throws CloudDriverException {
        if(this.isTheMimeTypeGoogleDocsFormat(mimeType)) {
            if(this.mimesSubstitute.containsKey(mimeType)) {
                return this.mimesSubstitute.get(mimeType);
            } else {
                throw new CloudDriverException("There is no substitute for the type " + mimeType);
            }
        } else {
            return mimeType;
        }
    }
}
