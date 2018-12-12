import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Synchronize efficiently files across directories.
 * 
 * There are two directories: source and destination where source could be a git
 * or other repository which could be continuously updated to get latest data.
 * 
 * The data needs to be going to different destination directories (information
 * provided by a utility function getDestinationDirectory(fullpath_filename))
 *
 * TODO: Input validation and file permission checks
 */
public class FileSync {

    private static final long DEFAULT_INTERVAL_MS = TimeUnit.SECONDS.toMillis(5); // defaults to every 5 second
    private static final String DEFAULT_DESTINATION = "/tmp";

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String destinationRootPath = DEFAULT_DESTINATION;
    private Map<WatchKey, Path> keyPaths = new HashMap<>();
    private WatchService watcher;
    private String sourceRootPath;

    private void deleteIfExists(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (path.toFile().exists()) {
            log("DEBUG", "DELETE " + filePath);
            Files.delete(path);
        }
    }

    private String getDestinationDirectory(String childPath) {
        // NOTE: Uses a fixed destination, but this could provides different destination
        // for each file
        return destinationRootPath + "/" + childPath.substring(sourceRootPath.length() + 1);
    }

    private void incrementalSync(String sourcePath, long syncIntervalMilliSecs)
            throws IOException, InterruptedException {
        WatchKey key = null;
        while (true) {
            key = watcher.poll(syncIntervalMilliSecs, TimeUnit.MILLISECONDS);
            if (key != null) {
                Path path = keyPaths.get(key);
                for (WatchEvent<?> event : key.pollEvents()) {
                    log("DEBUG", "Tracked event : " + event.kind().name() + " " + path.toString() + "/" + event.context());
                    File file = new File(path.toString() + "/" + event.context());
                    if ("ENTRY_DELETE".equals(event.kind().name())) { // delete applies to both file and folders
                        deleteIfExists(getDestinationDirectory(file.getPath()));
                    } else if (!(file.isDirectory() && "ENTRY_MODIFY".equals(event.kind().name()))) {
                        // for folders ignore modify notification triggered on its children
                        syncFile(file.getPath(), getDestinationDirectory(file.getPath()));
                    }
                }
                key.reset();
            }
        }
    }

    private void initialSync(String sourcePath) throws IOException {
        register(sourcePath);
        File files = new File(sourcePath);
        for (File file : files.listFiles()) {
            syncFile(file.getPath(), getDestinationDirectory(file.getPath()));
            if (file.isDirectory()) {
                initialSync(file.getPath());
            }
        }
    }

    private boolean isReadableFolder(File file) {
        return file.exists() && file.isDirectory() && file.canRead();
    }

    private boolean isReadableFolder(String path) {
        return notEmpty(path) ? isReadableFolder(new File(path)) : false;
    }

    private boolean isWritableFolder(File file) {
        return file.exists() && file.isDirectory() && file.canWrite();
    }

    private boolean isWritableFolder(String path) {
        return notEmpty(path) ? isWritableFolder(new File(path)) : false;

    }

    private void log(String type, String msg) { // TODO: Do proper logging using logging framework
        System.out.format("%s %s : %s%n", dateFormat.format(new Date()), type, msg);
    }

    private boolean notEmpty(String path) {
        return path != null && !path.isEmpty();
    }

    private void register(String sourcePath) throws IOException {
        WatchKey key = Paths.get(sourcePath).register(watcher, java.nio.file.StandardWatchEventKinds.ENTRY_CREATE,
                java.nio.file.StandardWatchEventKinds.ENTRY_DELETE, java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY);
        keyPaths.put(key, Paths.get(sourcePath));
    }

    private void setDestination(String destinationRootPath) {
        this.destinationRootPath = destinationRootPath;
    }
    
    public void setTestDestination() throws IOException {
        Path tmpDir = Files.createTempDirectory("filesync-");
        destinationRootPath = tmpDir.toFile().getAbsolutePath();
    }

    public void sync(String sourcePath) throws IOException, InterruptedException {
        sync(sourcePath, DEFAULT_INTERVAL_MS);
    }

    /**
     * Synchronize files across directories.
     * 
     * @param sourcePath
     *            The source directory to sync from
     * @param syncIntervalMilliSecs
     *            the time interval to sync
     * @throws IOException
     *             if either source or destination path has I/O issues
     * @throws InterruptedException
     *             if file watcher polling was interrupted
     */
    public void sync(String sourceRootPath, long syncIntervalMilliSecs) throws IOException, InterruptedException {
        this.sourceRootPath = sourceRootPath;
        log("INFO", "SOURCE : " + sourceRootPath);
        log("INFO", "DESTINATION : " + destinationRootPath);
        log("INFO", "SYNC INTERVAL MILLISECS  : " + syncIntervalMilliSecs);
        if (!isReadableFolder(sourceRootPath) || !isWritableFolder(destinationRootPath)) {
            throw new IOException(sourceRootPath + " must be a valid readable folder and " + destinationRootPath
                    + "must be a valid writable path");
        }
        initialSync(sourceRootPath);
        incrementalSync(sourceRootPath, syncIntervalMilliSecs);
    }

    private void syncFile(String sourcePath, String destinationPath) throws IOException {
        log("DEBUG", "COPY " + sourcePath + " -> " + destinationPath);
        Files.copy(FileSystems.getDefault().getPath(sourcePath), FileSystems.getDefault().getPath(destinationPath),
                StandardCopyOption.REPLACE_EXISTING);
    }

    public FileSync() throws IOException {
        watcher = FileSystems.getDefault().newWatchService();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length < 2) {
            System.out.println(
                    "Usage: java FileSync <source_folder> <destination_folder> [update polling interval in millisecs]");
            System.exit(1);
        }
        FileSync fileSync = new FileSync();
        fileSync.setDestination(args[1]);
        if (args.length > 2) {
            fileSync.sync(args[0], Long.parseLong(args[2]));
        } else {
            fileSync.sync(args[0]);
        }
    }
}
