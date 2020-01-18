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
 *   This keeps the min heap bounded to batch size, while generating the sorted result file
 *
 * NOTE: Proof of concept, functional version only
 *       Uses line number count as the batch size for demo, could change to memory as needed
 *       Uses a single character lines of alphabet so steps printing fits nicely on console
 *       Prints all file content and in memory contents to console for quick inspection/validation
 *
 * TODO: Switch Character to String as the line
 *       Memory size for batching instead of line count
 *       Better exception handling etc.
 *       Proper logging
 */
public class FileSorter {

	public static void main(String[] args) throws IOException {
		new FileSorter().test();
	}

	private File createFile(String prefix) throws IOException {
		return File.createTempFile(prefix + "-", "-tmp.txt");
	}

	/**
	 * Use a test file of all lower case alphabets in random order as the input
	 *
	 * @return Unsorted input file
	 * @throws IOException
	 */
	public File createTestFile() throws IOException {
		File file = createFile("unsorted");
		String random = "bynljgftpukdqazoirhxsmvwec";
		try (FileWriter writer = new FileWriter(file)) {
			for (int i = 0; i < random.length(); i++) {
				writer.append(random.charAt(i) + System.lineSeparator());
			}
		}
		return file;
	}

	private void mergeSortBatches(ArrayList<File> files) throws IOException {
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
			// In memory batch as a min heap of natural order
			PriorityQueue<Character> batch = new PriorityQueue<>();
			// Populate initial batch mon heap bounded by number of batches
			scanners.forEach(scanner -> {
				if (scanner.hasNextLine()) {
					batch.add(scanner.nextLine().charAt(0)); // Skip eol
				}
			});
			print(batch);
			for (int i = 0; i < scanners.size(); i++) {
				scanners.forEach(scanner -> {
					if (scanner.hasNextLine()) {
						try {
							// Remove the top element and write to the sorted result file
							writer.append(batch.poll() + System.lineSeparator()); // Add eol
						} catch (IOException e) {
							e.printStackTrace();
						}
						// Add next element after removal so batch size stays bounded
						batch.add(scanner.nextLine().charAt(0)); // Skip eol
                        print(batch);
					}
				});
				print(batch);
			}
			// Print the remaining elements to the sorted result file
			while (!batch.isEmpty()) {
				writer.append(batch.poll() + System.lineSeparator()); // Add eol
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
		// Lines are streamed one at a time by scanner
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
		int batchCounter = 1;

		try (Scanner scanner = new Scanner(new FileInputStream(inputFile))) {

			sortedBatchFiles.add(createFile("batch"));
			FileWriter writer = new FileWriter(sortedBatchFiles.get(sortedBatchFiles.size() - 1));
			List<Character> batch = new ArrayList<>();

			while (scanner.hasNextLine()) {
				batch.add(scanner.nextLine().charAt(0)); // Skip eol
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

			if (batch.size() > 0) {
				Collections.sort(batch);
				for (int i = 0; i < batch.size(); i++) {
					writer.append(batch.get(i) + System.lineSeparator());
				}
				writer.close();
			}
		} // Try with auto closes all open resources

		print(sortedBatchFiles);

		mergeSortBatches(sortedBatchFiles);
	}

	private void test() throws IOException {
		sort(createTestFile(), 5);
	}
}

