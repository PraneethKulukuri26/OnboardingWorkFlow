# ImportFlow Framework

**ImportFlow** is a generic, pluggable **import pipeline framework** built in Java 17. It provides the core infrastructure for reading data from any source, parsing it into domain objects, validating, transforming, deduplicating, storing, and reporting — all through a composable pipeline.

The framework contains **zero domain knowledge** (e.g., no Employee, Student, or Product classes). It works purely with generics (`<T>`), allowing you to build plugins for any domain entity.

## 🚀 Key Features

* **Composable Pipeline**: Build only the stages you need (e.g., `read() -> parse() -> validate() -> store()`).
* **Partial Failure Handling**: If row 5 fails validation, rows 1-4 and 6+ will still succeed.
* **Extensible Architecture**: Swap out readers (CSV, JSON, DB) or storage mechanisms easily via the Strategy pattern.
* **Detailed Context Tracking**: Automatically tracks success, failure, statistics, and execution logs for every run.

---

## 📦 How to Use (GitHub Packages)

This package is published to GitHub Packages. To use it in your own projects, you first need to configure your Maven settings to authenticate with GitHub.

### 1. Update `~/.m2/settings.xml`
You need a GitHub Personal Access Token (with `read:packages` scope) to download the package. Add this to your `settings.xml`:

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0">
  <servers>
    <server>
      <id>github</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>ghp_YOUR_PERSONAL_ACCESS_TOKEN</password>
    </server>
  </servers>
</settings>
```

### 2. Add Dependency to your `pom.xml`
Add the repository and dependency to the project where you want to use ImportFlow:

```xml
<repositories>
    <repository>
        <id>github</id>
        <name>GitHub Packages</name>
        <url>https://maven.pkg.github.com/PraneethKulukuri26/OnboardingWorkFlow</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.PraneethKulukuri26</groupId>
        <artifactId>onboardingworkflow</artifactId>
        <version>1.0.2</version>
    </dependency>
</dependencies>
```

---

## 🛠️ Building a Pipeline (Code Snippet)

Here is a full example of how to configure and execute an ImportFlow pipeline. 
*(Note: `MyDomainParser`, `MyValidator`, etc., represent your custom plugin implementations).*

```java
import com.company.importflow.core.pipeline.ImportPipeline;
import com.company.importflow.core.context.ImportContext;

public class Main {
    public static void main(String[] args) {
        
        // 1. Build the Pipeline
        ImportPipeline<MyDomainObject> pipeline = ImportPipeline.<MyDomainObject>builder()
            .read(new CsvReader("data.csv"))
            .parse(new MyDomainParser())
            .transform(new MyNormalizer())
            .validate(new MyValidator())
            .deduplicate(new MyDuplicateChecker())
            .store(new MyDatabaseRepository())
            .report(new ConsoleReporter())
            .build();
            
        // 2. Execute the Pipeline
        ImportContext<MyDomainObject> resultContext = pipeline.execute();
        
        // 3. Review Results
        System.out.println("Total Processed: " + resultContext.getStatistics().getTotalProcessed());
        System.out.println("Successful: " + resultContext.getStatistics().getSuccessfulRecords());
        System.out.println("Failed: " + resultContext.getStatistics().getFailedRecords());
    }
}
```

---

## 💻 Useful Maven & Git Commands

If you are developing or extending the `ImportFlow` framework locally, use the following commands inside the `importflow` directory:

### Maven Build Commands

* **Compile Code:** (Checks for syntax errors)
  ```bash
  mvn compile
  ```
* **Run Tests:** (Executes unit and integration tests)
  ```bash
  mvn test
  ```
* **Package:** (Creates the `.jar` file in `/target`)
  ```bash
  mvn clean package
  ```
* **Install Locally:** (Installs the JAR to `~/.m2` so other local projects can use it)
  ```bash
  mvn clean install
  ```
* **Deploy to GitHub Packages:** (Publishes a new release to GitHub)
  ```bash
  mvn clean deploy
  ```

### Git Workflow

* **Check status of changes:**
  ```bash
  git status
  ```
* **Stage all changes:**
  ```bash
  git add .
  ```
* **Commit changes:**
  ```bash
  git commit -m "Your descriptive commit message"
  ```
* **Push to GitHub:**
  ```bash
  git push origin master
  ```
