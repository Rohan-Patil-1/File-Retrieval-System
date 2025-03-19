# File Retrieval System


## Overview
This repository contains a Java-based solution for a file retrieval system designed to index and search large datasets. The system supports full-text search functionality, enabling efficient indexing and querying of documents. It was built using Maven on Ubuntu 24.04.1 LTS.


The solution contains key components that work together to provide the following features:

1. Indexing: Efficiently processes and indexes a given dataset.
2. Search: Executes full-text searches with support for basic and advanced queries.
3. Performance: The application can handle large datasets with quick response times for both indexing and searching.

   
## File Structure

The project contains the following Java files:

• AppInterface.java: Provides the user interface to interact with the application, including commands to index and search.

• FileRetrievalEngine.java: The main class that runs the file retrieval system. It manages the indexing and searching functionalities.

• IndexStore.java: Responsible for managing the index data and ensuring it is stored efficiently for fast retrieval.

• ProcessingEngine.java: Contains the logic for processing and indexing documents, as well as executing search queries.


#### Additionally, the project includes:

• pom.xml: The Maven configuration file, which defines the project's dependencies and build process.



## Prerequisites
Before you start, ensure that you have the following installed:

• Java 8 or higher
• Maven
• Ubuntu 24.04.1 LTS (or similar Linux-based OS)


## How to Build and Compile
To build and compile the Java project, use the following commands:

```
cd app-java
mvn compile
mvn package
```

These commands will compile the source code and package the application into a JAR file located in the target directory.


## How to Run the Application

After successfully building the project, you can run the application using the following command:

```
java -cp target/app-java-1.0-SNAPSHOT.jar app.FileRetrievalEngine
```

## Example Usage

Once the application is running, you can interact with it by issuing commands. Here’s an example of how to use the application:

```
> index ../datasets/dataset1 // Change the dataset destination
Completed indexing 134321105 bytes of data
Completed indexing in 10.386 seconds

> search at
Search completed in 0.4 seconds
Search results (top 10 out of 0):

> search child-like
Search completed in 2.8 seconds
Search results (top 10 out of 15):
* folder7/Document10926.txt:4
* folder3/Document10379.txt:3
* folder6/Document10866.txt:2
* folder8/Document1108.txt:1
* folder7/folderD/Document11050.txt:1
* folder6/Document10848.txt:1
* folder6/Document1082.txt:1
* folder4/Document10681.txt:1
* folder4/Document10669.txt:1
* folder3/Document10387.txt:1

> search distortion AND adaptation
Search completed in 3.27 seconds
Search results (top 10 out of 4):
* folder7/folderC/Document10998.txt:6
* folder4/Document10516.txt:3
* folder8/Document11159.txt:2
* folder8/Document11157.txt:2

> quit

```

## Commands

• index <dataset_path>: Indexes the dataset located at ```<dataset_path>```. The system will process and index the data from the specified directory.

• search <query>: Searches for the given query in the indexed documents. You can use basic keywords or more complex Boolean queries (e.g., ```distortion AND adaptation```).

• quit: Exits the application.


## Building the Project

This project uses Maven for building and dependency management. The build process will create a JAR file that contains all the necessary files for running the application.

•	Compile the source code using ```mvn clean compile```.

•	Package the application with ```mvn package```. This generates the ```app-java-1.0-SNAPSHOT.jar``` file located in the ```target``` directory.

•	Run the application with the ```java``` command as outlined above.

## Contributing

If you'd like to contribute to the development of this project, feel free to fork the repository, make your changes, and submit a pull request.

## License

This project is open-source and available under the GNU License. See the LICENSE file for more information.
