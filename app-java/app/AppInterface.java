package app;

import java.lang.System;
import java.util.Scanner;

public class AppInterface {
    private ProcessingEngine engine;
    
    public AppInterface(ProcessingEngine engine) {
        this.engine = engine;
    }

    public void read_command() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*****  Choose from the below options to use the File Retrieval Engine application  *****");


        while (true) {
            printBorder();
            System.out.println("\n Option 1 : Enter Folder path for search : index <FolderPath>");
            System.out.println("\n Option 2 : Enter word to search in specified file : search <word_to_search> ");
            System.out.println("\n Option 3 : Exit the application : quit");
            printBorder();
            System.out.print("\nEnter your Option: ");
            String command = scanner.nextLine().trim();

            if (command.equalsIgnoreCase("quit")) {
                break;
            } else if (command.startsWith("index")) {
                String datasetPath = PathFolder(command);
                if (datasetPath != null) {
                    engine.indexFiles(datasetPath);
                } else {
                    System.out.println("Invalid 'index' option. Please provide the folder path.");
                }
            } else if (command.toLowerCase().startsWith("search") ) {
                if (command.length() > 7) {
                    String query = command.substring(7).trim();
                    engine.search(query);
                } else {
                    System.out.println("Invalid 'search' option. Please provide a query.");
                }
            }else {
                System.out.println("Option Invalid . Please try again.");
            }
        }

        scanner.close();
    }

    private String PathFolder(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length >= 2) {
            return parts[1];
        }
        return null;
    }
    private void printBorder() {
        System.out.println("-".repeat(100)); // Adjust the number of asterisks for border size
    }

}
