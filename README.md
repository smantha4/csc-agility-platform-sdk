# agility-platform-sdk
The projects contained in this repository are used for developing Cloud Adapters or Service Adapters for the Agility Platform&trade;. The projects are as follows.
* com.servicemesh.core - Foundation classes for every Agility Platform SDK.
* com.servicemesh.io - Supports I/O via HTTP/HTTPS, Proxies, and Remote Shell.
* com.servicemesh.agility.api - Agility Platform&trade; Scripting APIs
* com.servicemesh.agility.distributed.sync - Provides synchronization for an Agility Platform&trade; running in its distributed architecture.
* agility-platform-services-sdk - A SDK used to develop asynchronous service adapters for the Agility Platform&trade;
* agility-platform-async-cloud-sdk - A SDK used to develop asynchronous cloud adapters for the Agility Platform&trade;

## Build Configuration

The projects in this repository are compatible with Java 8 and ant 1.9.3.

If you want to edit these projects using eclipse you will need to set up 3 build path variables.
* IVY-LIB - Contains the path to the ivy-lib directory under agility-platform-sdk
* COMMON-LIB - Contains the path to the lib directory under agility-platform-sdk
* DIST - Contains the path to the dist directory under agility-platform-sdk

For more information, see [the Wiki](https://github.com/csc/agility-platform-sdk/wiki).

## License
All of the code provided in this repository is licensed under the Apache Licence, Version 2.0. See [LICENSE](https://github.com/csc/agility-platform-sdk/blob/master/LICENSE) for the full license text.