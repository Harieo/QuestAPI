# QuestAPI
![Java CI - Build](https://github.com/MCTimecrafter/QuestAPI/workflows/Java%20CI%20-%20Build/badge.svg)

A library for creating quests; a set of tasks which a party of players can complete for rewards or to progress in the story.  

All quests can be routed to a single `Quest` interface which is the basis for all quests in this library.  
A common implementation of this is through the `LinearQuest` abstract class which details a simple linear quest which does not change path from start to finish.

## Installation
The remote Maven repository for this project is **private** and **you do not have permission** to use any code in this project 
without the express permission of the repository owner.

If you are an authorised user, please follow these steps to import this project via Maven.  
You will need your repository username and password.

Firstly, add the remote Maven repository:
```
<repository>
    <id>nexus-timecrafter</id>
    <name>Releases</name>
    <url>https://repo.timecrafter.net/repository/maven-releases</url>
</repository>
```

Secondly, import the project as a dependency:
```
<dependency>
    <groupId>net.timecrafter</groupId>
    <artifactId>QuestAPI</artifactId>
    <version>1.0.1</version>
    <scope>compile</scope>
</dependency>
```
Finally, you will need to shade this library into whichever project you are working on.  
This library does not stand-alone because it has no stand-alone features. Therefore, it won't be present unless compiled into your project.
