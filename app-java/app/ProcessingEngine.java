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
        long timeStart = System.currentTimeMillis();

        File datasetFolder = new File(datasetPath);
        if (!datasetFolder.isDirectory()) {
            System.err.println("Invalid folder path: " + datasetPath);
            return;
        }

        long totalBytes = indexFolder(datasetFolder);

        long timeEnd = System.currentTimeMillis();
        long elapsedTime = timeEnd - timeStart;

        double throughput = calculateTime(totalBytes, elapsedTime);

        System.out.println("Completed indexing " + totalBytes + " bytes of data with " + numWorkerThreads + " worker threads");
        System.out.println("Completed indexing in " + String.format("%.3f", elapsedTime / 1000.0) + " seconds");
        System.out.println("Indexing throughput: " + String.format("%.2f", throughput) + " MB/s");
    }

    private long indexFolder(File folder) {
        try {
            List<Path> filePaths = Files.walk(Paths.get(folder.getAbsolutePath()))
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().toLowerCase().endsWith(".txt"))
                .collect(Collectors.toList());

            List<List<Path>> partitions = partitionDataset(filePaths, numWorkerThreads);

            List<Future<Long>> futures = new ArrayList<>();
            for (List<Path> partition : partitions) {
                futures.add(executorService.submit(() -> processPartition(partition)));
            }

            long totalBytes = 0;
            for (Future<Long> future : futures) {
                totalBytes += future.get();
            }

            return totalBytes;
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private List<List<Path>> partitionDataset(List<Path> filePaths, int numPartitions) {
        List<List<Path>> partitions = new ArrayList<>(numPartitions);
        for (int i = 0; i < numPartitions; i++) {
            partitions.add(new ArrayList<>());
        }
        for (int i = 0; i < filePaths.size(); i++) {
            partitions.get(i % numPartitions).add(filePaths.get(i));
        }
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
            String content = Files.readString(path, StandardCharsets.UTF_8);
            long fileSize = content.length();
            List<String> keywords = Arrays.asList(content.toLowerCase().split("\\s+"));
            store.putDocument(path.toString(), keywords);

            Map<String, Integer> termFrequency = new HashMap<>();
            for (String term : keywords) {
                termFrequency.merge(term, 1, Integer::sum);
            }

            for (Map.Entry<String, Integer> entry : termFrequency.entrySet()) {
                store.updateIndex(entry.getKey(), path.toString(), entry.getValue());
            }

            return fileSize;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void search(String query) {
        long timeStart = System.nanoTime();

        List<String> results;
        String[] terms;

        if (query.toLowerCase().contains(" and ")) {
            terms = query.toLowerCase().split(" and ");
            results = store.sortedByOccurrences(terms);
        } else {
            terms = new String[]{query.toLowerCase()};
            Set<String> resultSet = store.getDocument(terms[0]);
            results = new ArrayList<>(resultSet);
            results.sort((file1, file2) -> Integer.compare(frequencyCount(file2, terms[0]), frequencyCount(file1, terms[0])));
        }

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
            String content = Files.readString(Paths.get(file), StandardCharsets.UTF_8);
            String[] words = content.toLowerCase().split("\\s+");
            return (int) Arrays.stream(words).filter(word -> word.equals(term)).count();
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
