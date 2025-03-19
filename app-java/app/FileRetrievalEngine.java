package app;

public class FileRetrievalEngine {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java FileRetrievalEngine <number_of_worker_threads>");
            System.exit(1);
        }

        int numWorkerThreads;
        try {
            numWorkerThreads = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid number. Please enter a valid integer.");
            System.exit(1);
            return;
        }

        IndexStore indexStore = IndexStore.getInstance();
        ProcessingEngine engine = new ProcessingEngine(indexStore, numWorkerThreads);
        AppInterface appInterface = new AppInterface(engine);

        // Run the application commands
        appInterface.read_command();

        // Perform cleanup after commands are executed
        cleanup(indexStore);

        // Shutdown the engine
        engine.shutdown();
    }

    // Cleanup method to clear IndexStore
    public static void cleanup(IndexStore indexStore) {
        indexStore.clearIndexStore();
        System.out.println("IndexStore cleared.");
    }
}
