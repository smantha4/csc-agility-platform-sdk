# csc-agility-platform-sdk
The projects contained in this repository are used for developing Cloud Adapters or Service Adapters for the CSC Agility Platform&trade;. The projects are as follows.
* com.servicemesh.core - Foundation classes for every Agility Platform SDK.
* com.servicemesh.io - Supports I/O via HTTP/HTTPS, Proxies, and Remote Shell.
* com.servicemesh.agility.api - Agility Platform&trade; Scripting APIs
* com.servicemesh.agility.distributed.sync - Provides synchronization for an Agility Platform&trade; running in its distributed architecture.
* agility-platform-services-sdk - A SDK used to develop asynchronous service adapters for the Agility Platform&trade;
* agility-platform-async-cloud-sdk - A SDK used to develop asynchronous cloud adapters for the Agility Platform&trade;

## How to Build this project:

**To Build all modules:**

-Dmaven.legacyLocalRepo=true parameter is required. Otherwise, maven fails to resolve some project local repository dependencies, on its very first run.

From project root folder, run

<code>mvn clean install -Dmaven.legacyLocalRepo=true</code>

**To Build dependent projects like service adapters:**

make sure you ran maven build, then from project root folder, run

<code>ant clean init</code>

This arranges dependencies into "dist", "ivy-lib", "lib" and"com.servicemesh.io/lib" folders as required for service adapter projects.


For more information, see [the Wiki](https://github.com/csc/csc-agility-platform-sdk/wiki).

## Eclipse Configuration

* Make sure Java 8, maven are installed
* Make sure m2e plugin is installed. [m2e is a Maven integration in Eclipse.]
* Install m2e connector for build-helper-maven-plugin (http://repo1.maven.org/maven2/.m2e/connectors/m2eclipse-buildhelper/0.15.0/N/0.15.0.201207090124/)
* Run maven build step mentioned in "How to Build this project" section above
* Update this eclipse setting. Eclipse -> preferences -> Maven -> Errors/Warnings -> "Plugin execution not covered by lifecycle configuration" to "ignore"
* import project using "Import existing maven project"

## License
All of the code provided in this repository is licensed under the Apache Licence, Version 2.0. See [LICENSE](https://github.com/csc/csc-agility-platform-sdk/blob/master/LICENSE) for the full license text.