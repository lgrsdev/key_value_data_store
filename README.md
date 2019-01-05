***********
assignment
***********

Please implement a key-value data store server, which holds a list of values per key.
 
•         The keys are from type string</br>
•         The values are a list of strings</br>
•         The server should accept connections via a custom (non HTTP) protocol over TCP</br>
•         The server should have persist the data into the file system</br>
•         The server should be able to cache the key-value data in memory, so not every request will go all the way into the disk (for performance reasons)

It’s API includes:
 
•         getAllKeys(String pattern) – Returns all keys matching pattern.</br>
•         rightAdd(String K, String V) – adds a value to key K, from the right</br>
•         leftAdd(String K, String V) – adds a value to key K, from the left</br>
•         set(String K, List<String> V)</br>
•         get(String K) – gets a list by its key</br></br>
 
 
•         Create a java client that calls all the mentioned API methods</br>
•         Create a documentation explaining: how did you create the server + client, how to operate the client, what were your difficulties during the process</br>
•         Upload your project to github and send us the link</br>
•         The assignment will be written in java


**********************
key-value data store
**********************

key-value data store is a TCP server based which holds a list of values per key and utilize an in-memory cache for performance reasons.
 
A JSON formatted TCP protocol was implemented via Netty — an asynchronous event-driven network application framework.
The main purpose of Netty is building high-performance protocol servers based on NIO (or possibly NIO.2) with separation and loose coupling of the network and business logic components. 

LRU cache with Write-back policy was implemented with two main data structures - ConcurrentLinkedDeque to store all the values and ConcurrentHashMap with value identifier as key and address of the corresponding queue element as value.
Write I/O is directed to cache and completion is immediately confirmed to the client. This results in low latency and high throughput, 
but there is data availability exposure risk because the only copy of the written data is in cache. 

Eventually, data Persisted to file-system while each key-value is written to disk as file name and file content respectively. 
That way, it is possible to append a single value to a single key without holding the entire store and also can get a key much faster
depending on the file-system. The persisted data itself is human readable, which is always nice. 

##### Difficulties during the process
- getting more familiar with different APIs and the principles behind them (e.g. Java NIO, sockets, threads).
- understanding Netty framework architecture and its profits (e.g. multi-threaded event loop, flow of handlers in the pipeline).
- choosing the ideal data structures and locking mechanism with complexity and concurrency in mind.


##### How-to operate the client
- Run Server class first and then Client.

client gets single-line JSONs commands from console. Examples:

{"command":"SET","key":"1","values":["b1","c1"]}</br>
{"command":"GET","key":"1"}</br>
{"command":"RIGHT_ADD","key":"1","values":["d1"]}</br>
{"command":"LEFT_ADD","key":"1","values":["a1"]}</br>
{"command":"GET_ALL_KEYS","key":"1"}


##### TODO - some missing stuff and limitations
 - input validations (can use 64encoding to prevent file-naming conventions violations)
 - need better exception handling (Important! should flush all cache to disk in case of failure, for data integrity)
 - should write unit + integration tests (particular for different OSs due to the use in the file-system, currently tested on macOS only) 
 - should add configuration file to read some properties (e.g. MAX_CACHE_SIZE, HOST... etc)
 - should add log
 - should use dependency injection
 - can add Spring integration with Netty
 - maybe use more efficient cache (e.g. Caffeine which implement a W-TinyLFU cache)
