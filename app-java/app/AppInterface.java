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

            if () { 
                // TO-DO implement for "Quit"
            } else if () { 
                // TO-DO implement for "Index"
                // TO-DO implement "invalid indexing path"
            } else if () { 
                // TO-DO implement for "Search"
                // TO-DO implement "invalid search query"
            } else {
                // TO-DO implement "Invalid Option
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
