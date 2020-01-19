import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * Sorts a given file limiting memory use bounded by given batch size
 *
 * Read input file line by line as batches
 * Each batch is sorted in memory and written to disk
 * Read the sorted batch files one line at a time in to a min heap
 *   Keep removing the top element from min heap and write to sorted result file
 *   Keep adding to min heap from sorted batch files one line at a time
 *      While reading from the same file with lowest value
 *   This keeps the min heap bounded to batch size, while generating the sorted result file
 *
 * NOTE: Proof of concept, functional version only
 *       Uses line number count as the batch size for demo, could change to memory size as needed
 *       Uses a single character lines, so printing of each step fits nicely on console
 *       Prints all file content and in memory contents to console for quick inspection/validation
 *
 * TODO: Switch Character to String as the line
 *       Memory size for batching instead of line count
 *       Proper logging, better exception handling etc.
 */
public class FileSorter {

    public static void main(String[] args) throws IOException {
        new FileSorter().test();
    }

    private File createFile(String prefix) throws IOException {
        return File.createTempFile(prefix + "-", "-tmp.txt");
    }

    /**
     * Create a test file of all lower case alphabets in random order as the input
     * Each call creates a different order of unsorted alphabets
     */
    private File createTestFile() throws IOException {
        File file = createFile("unsorted");
        List<Character> letters = new ArrayList<>();
        for (int i = 0; i < 26; i++) {
            letters.add((char)(97 + i)); // ASCII lower case starts at 97
        }
        Collections.shuffle(letters);
        try (FileWriter writer = new FileWriter(file)) {
            letters.stream().forEach(letter -> {
                try {
                    writer.append(letter + System.lineSeparator()); // append as a new line
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } // Try with auto closes all open resources
        return file;
    }

    private void mergeSortedBatches(ArrayList<File> files) throws IOException {

        // Create scanners that will read one line at a time from sorted batch files
        ArrayList<Scanner> scanners = new ArrayList<>();
        files.forEach(file -> {
            try {
                scanners.add(new Scanner(new FileInputStream(file)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });

        File file = createFile("sorted");

        try (FileWriter writer = new FileWriter(file)) {
            // In memory batching as a min heap of natural order
            PriorityQueue<Character> batch = new PriorityQueue<>();
            // Use an array list for index tracking of scanners
            List<Character> scannerIndex = new ArrayList<>();

            // Populate initial batch min heap bounded by number of batch files
            scanners.forEach(scanner -> {
                if (scanner.hasNextLine()) {
                    Character c = scanner.nextLine().charAt(0);
                    batch.add(c);
                    scannerIndex.add(c);
                }
            });
            while (!batch.isEmpty()) {
                print(batch);
                int index = scannerIndex.indexOf(batch.peek());
                // Remove top elemenet from min heap
                writer.append(batch.poll() + System.lineSeparator());
                if (scanners.get(index).hasNext()) {
                    Character c = scanners.get(index).nextLine().charAt(0);
                    // Add next element from the same scanner to min heap
                    batch.add(c);
                    scannerIndex.set(index, c);
                }
            }
        } // Try with auto closes all open resources

        print(file);
    }

    private void print(ArrayList<File> files) {
        files.forEach(file -> {
            try {
                print(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    private void print(File file) throws FileNotFoundException {
        try (Scanner scanner = new Scanner(new FileInputStream(file))) {
            System.out.print(file.getName() + ": ");
            while (scanner.hasNextLine()) {
                System.out.print(scanner.nextLine() + " ");
            }
            System.out.println();
        } // Try with auto closes all open resources
    }

    private void print(PriorityQueue<Character> queue) {
        System.out.print("Sorting batch in memory: ");
        queue.forEach(c -> {
            System.out.print(c + " ");
        });
        System.out.println();
    }

    /**
     * Sort the given input file with the constraint of maximum number of lines that
     * can be loaded at a time
     *
     * @param inputFile the input file to sort
     * @param batchSize the maximum lines that can be loaded at a time
     * @throws IOException
     */
    public void sort(File inputFile, int batchSize) throws IOException {
        print(inputFile);

        ArrayList<File> sortedBatchFiles = new ArrayList<>();

        // Read in batches, sort and write to separate files
        try (Scanner scanner = new Scanner(new FileInputStream(inputFile))) {
            sortedBatchFiles.add(createFile("batch"));
            FileWriter writer = null;
            try  {
                writer = new FileWriter(sortedBatchFiles.get(sortedBatchFiles.size() - 1));
                List<Character> batch = new ArrayList<>();
                int batchCounter = 1;

                while (scanner.hasNextLine()) {
                    batch.add(scanner.nextLine().charAt(0));
                    if (batchCounter++ >= batchSize) {
                        batchCounter = 1;
                        sortedBatchFiles.add(createFile("batch"));
                        Collections.sort(batch);
                        for (int i = 0; i < batch.size(); i++) {
                            writer.append(batch.get(i) + System.lineSeparator());
                        }
                        batch.clear();
                        writer.close();
                        writer = new FileWriter(sortedBatchFiles.get(sortedBatchFiles.size() - 1));
                    }
                }
                // Left over partial batch
                if (batch.size() > 0) {
                    Collections.sort(batch);
                    for (int i = 0; i < batch.size(); i++) {
                        writer.append(batch.get(i) + System.lineSeparator());
                    }
                }
            } finally {
                if(writer!=null) {
                    writer.close();
                }
            }
        } // Try with auto closes all open resources

        print(sortedBatchFiles);

        mergeSortedBatches(sortedBatchFiles);
    }

    private void test() throws IOException {
        sort(createTestFile(), 5);
    }
}

