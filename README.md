## Java web server

#### General info

Multi-threaded HTTP server written in Java with thread pooling. This is done using Java Non-blocking I/O Library for socket multiplexing and TCP event handling. 

NIO buffers are used to allow fast streaming of data to the HTTP client. 
As soon as a channel becomes readable, the main thread creates a task to allow parallel processing of requests. The size of the worker pool has to be tuned according the expected TPS and balance between context switching and average duration of HTTP request.

This serves three kinds of requests:
| URL path  	| Result								 |
| ------------- | -------------------------------------- |
|	/sample		| Sample text used for testing purposes  |
|	/config		| Runtime server configuration output	 |
|	/<any>		| Static file path 						 |

#### Configuration files

* src/main/conf/config.properties  	- Server configuration file
* src/main/conf/log4j.properties   	- Can alter logging level, format
* www/								- Default 
 

#### Starting the server

You can start the server by running the App main class from an IDE, setting up the classath and running manually, or through maven:

```bash
cd server
mvn exec:java

2016-07-03 18:46:38 INFO  Server:73 - Server stage changed from [STOPPED] to [STARTED]
2016-07-03 18:46:38 INFO  Server:73 - Server stage changed from [STARTED] to [RUNNING]
2016-07-03 18:46:38 DEBUG CleanupWorker:33 - Starting idle channel cleanup for 1 keys
```

#### Testing

Run unit tests manually, or through maven:
```bash
cd server
mvn clean test
```

Make HTTP request through browser, REST client, or curl:
```bash
curl -v localhost:10093/test localhost:10093/test 2>&1 | egrep -i '(connection #0|hello)'
```

You should be getting something like:
```
* STATE: CONNECT => WAITCONNECT handle 0x80070598; line 1455 (connection #0)
(...)
* Connection #0 to host localhost left intact
Adobe Server hello* STATE: INIT => CONNECT handle 0x80070598; line 1402 (connection #-5000)
(...)
* Connection #0 to host localhost left intact
Adobe Server hello

```

##### Libraries
This was built using [EasyMock](http://easymock.org/), [log4j](http://logging.apache.org/log4j/2.x/), [Jackson JSON](http://wiki.fasterxml.com/JacksonLicensing), [Apache commons IO](https://commons.apache.org/proper/commons-io/) and standard Java libraries.




## Javascript Drag and drop application

#### General info
This is a JS UI application to drag and drop any DOM element(in this case, some sample divs are used). An iFrame with a droppable div is dynamically crated, and once dragged objects are over the area, they are cloned inside the given div. 

Dynamic elements have a specific name "Element #index" and custom data that has the index embedded. Multiple elements are grouped in a helper div and cloned together when added to the drop zone.

#### Running the application

Page can be viewed by opening **web-page/testPage.html**. Drag and drop any the divs at the top of the screen inside the iFrame. 
You can select multiple object by holding down CTRL key and clicking on one of the objects. This will become highlighted and a console message will appear. After highlighting multiple objects, they should move and be cloned together inside the drop space.

A JS console message (can be viewed natively on Chrome , or through Firebug) appears denoting the custom data of dropped elements. This server (the one included above) response will also appear of server is running.

#### Testing

Test results can be run and viewed by opening **web-page/testPage.html**. This is done through QUnit 2.0. 

#### Libraries
This was built using [jQuery](https://jquery.com/), [jQuery UI](https://jqueryui.com/) and [QUnit](http://qunitjs.com/) libraries.