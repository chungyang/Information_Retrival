# Information_Retrival

### Maven
This project uses Maven to handle dependency management and build. If you don't have Maven already on your system. Download it here "https://maven.apache.org/download.cgi". Once you downloadeit and unzip it, you can just add its bin folder to your PATH variable.

### Dependencies

The project uses Jackson, its depencies is included in Maven build files. As long as you are connected to the internet, the dependencies should be resolved by Maven correctly.

### Build
To build,
```
mvn -f pom.xml compile assembly:single

```

### Run
To run,
```
java -jar target/Info_Retrival-1.0-SNAPSHOT-jar-with-dependencies.jar true queries.txt 10
```

```