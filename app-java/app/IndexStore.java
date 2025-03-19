package app;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class IndexStore {
    private static IndexStore instance;
    private final ConcurrentHashMap<String, Set<String>> documentMap; // Maps keywords to documents
    private final ConcurrentHashMap<String, Map<String, Integer>> termInvertedIndex; // Maps terms to document frequencies

    private IndexStore() {
        this.documentMap = new ConcurrentHashMap<>();
        this.termInvertedIndex = new ConcurrentHashMap<>();
    }
	
    public void clearIndexStore() {
    documentMap.clear();
    termInvertedIndex.clear();
    System.out.println("IndexStore has been cleared.");
    }


    public static synchronized IndexStore getInstance() {
        if (instance == null) {
            instance = new IndexStore();
        }
        return instance;
    }

    /**
     * Store document paths in the document map for a given keyword.
     */
    public void putDocument(String documentPath, List<String> keywords) {
        for (String keyword : keywords) {
            documentMap.computeIfAbsent(keyword, k -> ConcurrentHashMap.newKeySet()).add(documentPath);
        }
    }

    /**
     * Update the index with term frequency for a document.
     */
    public void updateIndex(String term, String documentPath, int frequency) {
        termInvertedIndex
                .computeIfAbsent(term, k -> new ConcurrentHashMap<>())
                .merge(documentPath, frequency, Integer::sum);
    }

    /**
     * Get documents matching a keyword.
     */
    public Set<String> getDocument(String keyword) {
        return documentMap.getOrDefault(keyword, Collections.emptySet());
    }

    /**
     * Retrieve documents sorted by occurrences of terms.
     */
    public List<String> sortedByOccurrences(String[] terms) {
        Map<String, Integer> fileOccurrences = new HashMap<>();

        for (String term : terms) {
            termInvertedIndex.getOrDefault(term, Collections.emptyMap())
                             .forEach((file, freq) -> fileOccurrences.merge(file, freq, Integer::sum));
        }

        // Sort files by occurrences in descending order
        List<String> sortedFiles = new ArrayList<>(fileOccurrences.keySet());
        sortedFiles.sort((file1, file2) -> Integer.compare(fileOccurrences.get(file2), fileOccurrences.get(file1)));
        return sortedFiles;
    }

    /**
     * Index documents in batches to reduce memory usage.
     * This method will process files in smaller batches (e.g., 100 files per batch).
     */
    public void indexFolderInBatches(String folderPath, int batchSize) {
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Invalid folder path.");
            return;
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null || files.length == 0) {
            System.out.println("No text files found in the folder.");
            return;
        }

        List<File> batch = new ArrayList<>();
        for (File file : files) {
            batch.add(file);

            // Process the batch once the size reaches the limit
            if (batch.size() == batchSize) {
                processBatch(batch);
                batch.clear();  // Clear the batch for the next set of files
            }
        }

        // Process any remaining files that didn't make up a full batch
        if (!batch.isEmpty()) {
            processBatch(batch);
        }

        System.out.println("Indexing complete.");
    }

    /**
     * Process a batch of files.
     */
    private void processBatch(List<File> batch) {
        for (File file : batch) {
            String documentPath = file.getAbsolutePath();
            Map<String, Integer> termFrequency = extractTerms(file);
            termFrequency.forEach((term, freq) -> updateIndex(term, documentPath, freq));
        }
        System.out.println("Processed " + batch.size() + " files.");
    }

    /**
     * Extract terms and their frequency from a file.
     */
    private Map<String, Integer> extractTerms(File file) {
        Map<String, Integer> termFrequency = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\W+"); // Split the line into words
                for (String word : words) {
                    if (word.length() > 3) { // Only consider words with length > 3
                        termFrequency.put(word.toLowerCase(), termFrequency.getOrDefault(word.toLowerCase(), 0) + 1);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + file.getName());
        }
        return termFrequency;
    }
}
