package csc435.app;

import java.nio.file.Files;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class ProcessingEngine {
    private final IndexStore store;
    private final int numWorkerThreads;
    private final ExecutorService executorService;

    public ProcessingEngine(IndexStore store, int numWorkerThreads) {
        this.store = store;
        this.numWorkerThreads = numWorkerThreads;
        this.executorService = Executors.newFixedThreadPool(numWorkerThreads);
    }

    public void indexFiles(String datasetPath) {
        // TO-DO implement indexing of the files

        System.out.println("Completed indexing " + totalBytes + " bytes of data with " + numWorkerThreads + " worker threads");
        System.out.println("Completed indexing in " + String.format("%.3f", elapsedTime / 1000.0) + " seconds");
        System.out.println("Indexing throughput: " + String.format("%.2f", throughput) + " MB/s");
    }

    private long indexFolder(File folder) {
        try {
            // TO-DO implement indexing of the folders
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private List<List<Path>> partitionDataset(List<Path> filePaths, int numPartitions) {
        // TO-DO implement partitioning Dataset
        return partitions;
    }

    private long processPartition(List<Path> partition) {
        long bytes = 0;
        for (Path path : partition) {
            bytes += processFile(path);
        }
        return bytes;
    }

    private long processFile(Path path) {
        try {
            // TO-DO implement processing of file
            return fileSize;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void search(String query) {
        // TO-DO implement searching query

        resultsShow(results, terms);

        long timeEnd = System.nanoTime();
        double elapsedTime = (timeEnd - timeStart) / 1e9;
        System.out.println("Search completed in " + String.format("%.2f", elapsedTime) + " seconds");
    }

    private void resultsShow(List<String> results, String[] terms) {
        if (results.isEmpty()) {
            System.out.println("No match found in the given document path.");
        } else {
            System.out.println("Search results (top 10 out of " + results.size() + "):");
            results.stream().limit(10).forEach(file -> {
                int occurrences = Arrays.stream(terms).mapToInt(term -> frequencyCount(file, term)).sum();
                System.out.println("* " + file + ":" + occurrences);
            });
        }
    }

    private int frequencyCount(String file, String term) {
        try {
            // TO-DO implement frequency count 
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private double calculateTime(long dataSizeBytes, long elapsedTimeMillis) {
        double elapsedTimeSecs = elapsedTimeMillis / 1000.0;
        double dataSizeMB = dataSizeBytes / (1024.0 * 1024.0);
        return dataSizeMB / elapsedTimeSecs;
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
