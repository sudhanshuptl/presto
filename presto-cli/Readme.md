# Introduction
The Presto CLI provides a terminal-based interactive shell for running queries. The CLI is a self-executing JAR file,
 which means it acts like a normal UNIX executable. <br/>

# What's New Here
Presto is great tool, That is why lots of data oriented people use this. <br />
I came across a situation to use data from presto and send it to tableau for further analysis purposes. <br />
`Tableau` provide very unique way of input data for lighting fast processing and analyzing i.e `hyper`file. <br />
So We customised `presto-cli` to export Query output as `.hyper` file that we can directly send to tableau. <br />

We are using `tableau Hyper API` instead of `tableau Extract 2.0` to convert Query Output into `hyper` file as per our current 
results performance of conversion is improved upto >3x .

# How I can try this new feature without much effort.
It is always better to tryout new functionality before goig into setup and details, So We build a docker image for that.
Steps:
1. Start Docker Engine in your system.
2. pull latest docker image : 
`make pull` 
or `docker pull docker.pkg.github.com/sudhanshuptl/presto/presto-to-hyper:latest`.
3. Run docker bash terminal : 
`make run` 
or `docker run -it --rm --entrypoint /bin/bash docker.pkg.github.com/sudhanshuptl/presto/presto-to-hyper:latest`.
4. execute your command
 `presto-cli --server <server> --catalog <catalog> --schema <schema> --output-format "HYPER" --hyperfile <Output.hyper> --user <user@domain.com> --execute <"select * from my_table limit 10"> --password`
     


# Why Manual setup Required.
Since Tableau hyper API is not available in maven repository , so we need to setup them in our local cache.<br/>
Make sure to put `hyperAPI dependent binaries` withing the same directory as of executable jars.
    * You can setup Your personal maven repository to minimise manual setup.
    * you use maven plugin `ant` to automate download, extract, move and permission if tableau hyperAPI binaries.


## Setup custom dependent jars to use presto-cli in macOS.
We need to custom install new dependent `jars` in order to build `presto-cli` as `jars` related to `HyperAPI` not available in maven repository.
1. Download java `Hyper API` libraries for macOS : http://downloads.tableau.com/tssoftware/tableauhyperapi-java-macos-x86_64-release-hyperapi_release_2.0.0.8953.r50e2ce3a.zip
2. Extract Zip and move to `lib`  directory where `jars` are available.
3. Install jars as local cache.
    * `mvn install:install-file -Dfile=jna-5.2.0.jar -DgroupId=hyper.mac.com.sun.jna -DartifactId=jna -Dversion=5.2.0 -Dpackaging=jar`
    * `mvn install:install-file -Dfile=tableauhyperapi.jar -DgroupId=hyper.mac.com.tableau -DartifactId=hyperapi -Dversion=0.8953 -Dpackaging=jar`
    * `mvn install:install-file -Dfile=tableauhyperapi-macos.jar -DgroupId=hyper.mac -DartifactId=darwin -Dversion=0.8953 -Dpackaging=jar`
    * `mvn install:install-file -Dfile=tableauhyperapi-javadoc.jar -DgroupId=hyper.mac.com.tableau -DartifactId=hyperapi_doc -Dversion=0.8953 -Dpackaging=jar`

## Setup custom dependent jars to use presto-cli in Linux.
We need to custom install new dependent `jars` in order to build `presto-cli` as `jars` related to `HyperAPI` not available in maven repository.
1. Download java `Hyper API` libraries for Linux :http://downloads.tableau.com/tssoftware/tableauhyperapi-java-linux-x86_64-release-hyperapi_release_2.0.0.8953.r50e2ce3a.zip
2. Extract Zip and move to `lib`  directory where `jars` are available.
3. Install jars as local cache.
    * `mvn install:install-file -Dfile=jna-5.2.0.jar -DgroupId=hyper.linux.com.sun.jna -DartifactId=jna -Dversion=5.2.0 -Dpackaging=jar`
    * `mvn install:install-file -Dfile=tableauhyperapi-linux.jar -DgroupId=hyper.linux -DartifactId=linux -Dversion=0.8953 -Dpackaging=jar`
    * `mvn install:install-file -Dfile=tableauhyperapi-javadoc.jar -DgroupId=hyper.linux.com.tableau -DartifactId=hyperapi_doc -Dversion=0.8953 -Dpackaging=jar`
    * `mvn install:install-file -Dfile=tableauhyperapi.jar -DgroupId=hyper.linux.com.tableau -DartifactId=hyperapi -Dversion=0.8953 -Dpackaging=jar`



## Build presto-cli
1. Move to `presto-cli`.
2. Build: 
    * MacOS: `mvn clean install -Pmac`
    * Linux: `mvn clean install -Plinux` or `mvn clean install`
3. It will create executable `presto-cli-.*-executable.jar` in `<project-root>/presto-cli/target`. 
   You can clear all files in `<project-root>/presto-cli/target` except executable jar.   
4. Copy `hyper` folder containing dependent executable from `lib` directory of unzipped `Hyper API` setup into `presto/presto-cli/target`
5. Add permission to libraries `chmod +x <project-root>/presto-cli/target/hyper/*`

## Running Queries
 * `./presto-cli-0.231-SNAPSHOT-executable.jar --server https://presto.internal.adroll.com:8443/ --catalog hive --schema bi --user <user@adroll.com> --password`
 * Execute Queries

### Export Query output as hyper file
 * `./presto-cli-0.231-SNAPSHOT-executable.jar --server https://presto.internal.adroll.com:8443/ --catalog hive --schema bi --output-format "HYPER" --hyperfile Outpur.hyper --user <user@adroll.com>  --execute "Query" --password`
 * It will create `Output.hyper` within same directory.
