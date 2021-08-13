# Getting Started With Utility

### About Utility

This utility update the NULL value of **GDSINSTANCEID** column from **CONTENT** column that contain INSTANCEID, where
FULLORPARTIAL is ***F***.

* source table: **GEMS.GEMSEVTGDS**
* target table: **GEMS.GEMSEVTGDS**

#### Supported Profile
* it
* pp (Pre-Production)
* prod (Production)

It can be changed via VM Args using ``-Dspring.profiles.active=it|-Dspring.profiles.active=pp|-Dspring.profiles.active=prod``

### Setup

Local Build

```shell
$ git clone git@github.com:singh-saurav/tpdoc-util.git
$ cd tpdoc-util
$ mvn clean install
```

Now You have the jar built and ready under **target** folder, put this jar on server where you want to run.

### Ready To Run

Use below command to run the jar file

```shell
java -Xms1G -Xmx2G -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:ParallelGCThreads=20 -XX:ConcGCThreads=5 -XX:InitiatingHeapOccupancyPercent=70 -DDB_USER=<username> -DDB_PASSWORD=<password> -Dspring.profiles.active=<profile> -jar tpdoc-util-0.0.1-SNAPSHOT.jar
```
#### Note : Please replace below variables:
* -DDB_USER=<username> -> <username> is Database user
* -DDB_PASSWORD=<password> -> <password> is Database password 
* -Dspring.profiles.active=<profile> -> <profile> is it,pp, prod