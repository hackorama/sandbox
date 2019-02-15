import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Reading data from a large file with a much smaller heap, using multiple readers
 * Please run with a small heap -Xmx64m 
 * 
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public class LargeFileReader {

    private static final String TEST_FILE = "test.data.txt";

    public static void main(String[] args) throws IOException, InterruptedException {
        printHeap("HEAP At beginning");
        long numberOfLinesInFile = 1024 * 1024;
        long eachLineSizeInBytes = 1024;
        long totalFileSizeInBytes = createTestFile(numberOfLinesInFile, eachLineSizeInBytes);
        System.out.format("DATA FILE : %s %,d bytes [%,d lines each of siize %,d]%n", TEST_FILE, totalFileSizeInBytes,
                numberOfLinesInFile, eachLineSizeInBytes);
        System.out.format("Total file size %,d bytes is %,d times the maxium heap %,d bytes%n", totalFileSizeInBytes,
                totalFileSizeInBytes / Runtime.getRuntime().maxMemory(), Runtime.getRuntime().maxMemory());
        printHeap("HEAP Before reading");
        readDataEachThreadReadingAllData(3, numberOfLinesInFile);
        long bufferSize = 100;
        readDataDsitrbutedAcrossThreadsEachThreadReadingPartofTheData(3, numberOfLinesInFile, bufferSize);
        printHeap("HEAP After reading");
    }

    private static void readDataDsitrbutedAcrossThreadsEachThreadReadingPartofTheData(int threadCount, long expectedLinesInFile, long bufferSize) {
        System.out.format("%nData read distributed across %d threads each thread reading part of data with no duplication%n%n", threadCount);
        BlockingQueue<String> buffer = startBufferingData(expectedLinesInFile, bufferSize);
        StartReadingDataFromBuffer(buffer, threadCount, expectedLinesInFile);
    }

    private static void StartReadingDataFromBuffer(BlockingQueue<String> buffer, int threadCount,
            long expectedLinesInFile) {
        final AtomicInteger totalReadCounter = new AtomicInteger(0);
        final int[] readByThreadCounters = new int[threadCount];
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        IntStream.range(0, threadCount).forEach(id -> {
            new Thread(() -> {
                while (totalReadCounter.getAndIncrement() < expectedLinesInFile) {
                    try {
                        String line = buffer.take();
                        if (totalReadCounter.get() % (expectedLinesInFile / 10) == 0) {
                            // Truncated (to first 32 chars) printing of selected 10 lines to the std out  for logging checks
                            System.out.format("%s : %s ...%n", Thread.currentThread().getName(), line.substring(0, 32));
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    readByThreadCounters[id]++;
                }
                System.out.format("Thread %s finished reading %,d lines of expected %,d lines data%n",
                        Thread.currentThread().getName(), readByThreadCounters[id], expectedLinesInFile);
                StringBuffer sb = new StringBuffer();
                sb.append("Data read across " + threadCount + " threads ");
                IntStream.of(readByThreadCounters).forEach(c -> {
                    sb.append(c + " ");
                });
                sb.append(" = " + IntStream.of(readByThreadCounters).sum());
                System.out.println(sb.toString());
            }, "BUFFER_READER_" + id).start();
        });
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static BlockingQueue<String> startBufferingData(long expectedLinesInFile, long bufferSize) {
        BlockingQueue<String> buffer = new LinkedBlockingQueue<>((int) bufferSize);
        new Thread(() -> {
            System.out.format("Thread %s starts buffering data using buffer size %,d of total %,d linesa ...%n",
                    Thread.currentThread().getName(), bufferSize, expectedLinesInFile);
            try (Stream<String> lines = Files.lines(Paths.get(TEST_FILE))) {
                final AtomicInteger totalBufferedCounter = new AtomicInteger(0);
                lines.forEach(line -> {
                    try {
                        totalBufferedCounter.incrementAndGet();
                        buffer.put(line);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                System.out.format("Thread %s finished buffering %,d lines of expected %,d lines data%n",
                        Thread.currentThread().getName(), totalBufferedCounter.get(), expectedLinesInFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "BUFFER_WRITER").start();
        return buffer;
    }

    private static void printHeap(String msg) {
        System.out.format("%s : Total %,d bytes, Max %,d bytes, Free %,d bytes%n", msg,
                Runtime.getRuntime().totalMemory(), Runtime.getRuntime().maxMemory(),
                Runtime.getRuntime().freeMemory());
    }

    private static void readDataEachThreadReadingAllData(int threadCount, long expectedLinesInFile) {
        System.out.format("%nData read by %d threads each thread reading all of the data%n%n", threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        IntStream.range(0, threadCount).forEach(id -> {
            executor.submit(() -> {
                Thread.currentThread().setName("READER_" + id);
                try (Stream<String> lines = Files.lines(Paths.get(TEST_FILE))) {
                    final AtomicInteger counter = new AtomicInteger(0);
                    lines.forEach(line -> {
                        if (counter.incrementAndGet() % (expectedLinesInFile / 10) == 0) {
                            // Truncated (to first 32 chars) printing of selected 10 lines to the std out for logging checks
                            System.out.format("%s : %s ...%n", Thread.currentThread().getName(), line.substring(0, 32));
                        }
                    });
                    System.out.format("Thread %s finished reading %,d lines of expected %,d lines data%n",
                            Thread.currentThread().getName(), counter.get(), expectedLinesInFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static long createTestFile(long lineCount, long lineSize) throws IOException {
        System.out.format("Creating data file %s with %,d lines, each line of size %,d bytes ...%n", TEST_FILE,
                lineCount, lineSize);
        Files.deleteIfExists(Paths.get(TEST_FILE));
        Path path = Files.createFile(Paths.get(TEST_FILE));
        try (PrintWriter printWriter = new PrintWriter(new FileWriter(path.toFile()))) {
            long lineNumber = 0;
            while (lineNumber++ < lineCount) {
                // lineSize = line number, space, data, eol
                char[] chars = new char[(int) (lineSize - Long.toString(lineNumber).length() - 1 - 1)];
                Arrays.fill(chars, 'X');
                printWriter.println(lineNumber + " " + new String(chars));
            }
        }
        System.out.format("Created data file %s with %,d lines, each line of size %,d bytes, total size %,d%n",
                TEST_FILE, lineCount, lineSize, Files.size(Paths.get(TEST_FILE)));
        return Files.size(Paths.get(TEST_FILE));
    }
}

