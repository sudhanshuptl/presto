# Introduction
The Presto CLI provides a terminal-based interactive shell for running queries. The CLI is a self-executing JAR file,
 which means it acts like a normal UNIX executable. <br/>
 We further customise `presto-cli` to export query output as `hyper` file.

## Setup custom dependent jars to use presto-cli in macOS.
We need to custom install new dependent `jars` in order to build `presto-cli` as `jars` related to `HyperAPI` not available in maven repository.
1. Download java `Hyper API` libraries for macOS : http://downloads.tableau.com/tssoftware/tableauhyperapi-java-macos-x86_64-release-hyperapi_release_2.0.0.8953.r50e2ce3a.zip
2. Extract Zip and move to `lib`  directory where `jars` are available.
3. Install jars as local cache.
    * `mvn install:install-file -Dfile=jna-5.2.0.jar -DgroupId=hyper.mac.com.sun.jna -DartifactId=jna -Dversion=5.2.0 -Dpackaging=jar`
    * `mvn install:install-file -Dfile=tableauhyperapi.jar -DgroupId=hyper.mac.com.tableau -DartifactId=hyperapi -Dversion=0.8953 -Dpackaging=jar`
    * `mvn install:install-file -Dfile=tableauhyperapi-macos.jar -DgroupId=hyper.mac.darwin -DartifactId=darwin -Dversion=0.8953 -Dpackaging=jar`
    * `mvn install:install-file -Dfile=tableauhyperapi-javadoc.jar -DgroupId=hyper.mac.com.tableau -DartifactId=hyperapi_doc -Dversion=0.8953 -Dpackaging=jar`
4. Open `vim ~/.m2/repository/hyper/mac/com/tableau/hyperapi/0.8953/hyperapi-0.8953.pom` and add these dependencies in the `pom` file.
    ```xml
        <dependencies>
            <dependency>
                <groupId>hyper.mac.darwin</groupId>
                <artifactId>darwin</artifactId>
                <version>0.8953</version>
            </dependency>
    
            <dependency>
                <groupId>hyper.mac.om.sun.jna</groupId>
                <artifactId>jna</artifactId>
                <version>5.2.0</version>
            </dependency>
            <dependency>
                <groupId>hyper.mac.com.tableau</groupId>
                <artifactId>hyperapi_doc</artifactId>
                <version>0.8953</version>
            </dependency>
        </dependencies>
    ```
## Setup custom dependent jars to use presto-cli in Linux.
We need to custom install new dependent `jars` in order to build `presto-cli` as `jars` related to `HyperAPI` not available in maven repository.
1. Download java `Hyper API` libraries for Linux :http://downloads.tableau.com/tssoftware/tableauhyperapi-java-linux-x86_64-release-hyperapi_release_2.0.0.8953.r50e2ce3a.zip
2. Extract Zip and move to `lib`  directory where `jars` are available.
3. Install jars as local cache.
    * `mvn install:install-file -Dfile=jna-5.2.0.jar -DgroupId=com.sun.jna -DartifactId=jna -Dversion=5.2.0 -Dpackaging=jar`
    * `mvn install:install-file -Dfile=tableauhyperapi-linux.jar -DgroupId=linux -DartifactId=linux -Dversion=0.8953 -Dpackaging=jar`
    * `mvn install:install-file -Dfile=tableauhyperapi-javadoc.jar -DgroupId=com.tableau -DartifactId=hyperapi_doc -Dversion=0.8953 -Dpackaging=jar`
    * `mvn install:install-file -Dfile=tableauhyperapi.jar -DgroupId=com.tableau -DartifactId=hyperapi -Dversion=0.8953 -Dpackaging=jar`

4. Open `vim ~/.m2/repository/com/tableau/hyperapi/0.8953/hyperapi-0.8953.pom` and add these dependencies in the `pom` file.
    ```xml
        <dependencies>
            <dependency>
                <groupId>linux</groupId>
                <artifactId>linux</artifactId>
                <version>0.8953</version>
                <description>POM was created from install:install-file</description>
            </dependency>

            <dependency>
                <groupId>com.sun.jna</groupId>
                <artifactId>jna</artifactId>
                <version>5.2.0</version>
               <description>POM was created from install:install-file</description>
            </dependency>
            <dependency>
                <groupId>com.tableau</groupId>
                <artifactId>hyperapi_doc</artifactId>
                <version>0.8953</version>
                <description>POM was created from install:install-file</description>
            </dependency>
        </dependencies>
    ```

## Build presto-cli
1. Move to `presto-cli`.
2. Build : `mvn clean install`. It will create executable `presto-cli-.*-executable.jar` in `<project-root>/presto-cli/target`. 
You can clear all files in `<project-root>/presto-cli/target` except executable jar.
3. Copy `hyper` folder containing dependent executable from `lib` directory of unzipped `Hyper API` setup into `presto/presto-cli/target`
4. Add permission to libraries `chmod +x <project-root>/presto-cli/target/hyper/*`

## Running Queries
 * `./presto-cli-0.198-executable.jar --server https://presto.internal.adroll.com:8443/ --catalog hive --schema bi --user <user@adroll.com> --password`
 * Execute Queries

### Export Query output as hyper file
 * `./presto-cli-0.198-executable.jar --server https://presto.internal.adroll.com:8443/ --catalog hive --schema bi --output-format "HYPER" --hyperfile Outpur.hyper --user <user@adroll.com>  --execute "Query" --password`
 * It will create `Output.hyper` within same directory.

`