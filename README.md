# TuneJar
[![Circle CI](https://circleci.com/gh/sudiamanj/TuneJar.svg?style=svg)](https://circleci.com/gh/sudiamanj/TuneJar)  
![](https://raw.githubusercontent.com/sudiamanj/TuneJar/master/src/main/resources/img/icon.png)

### What is TuneJar?
TuneJar (powered by **Java**) is a music player that is **lightweight**, **cross-platform**, and best of all, **open source**.

### Why TuneJar?
Here's a few reasons why TuneJar might just be the right player for you.  

- Library management is quick and painless. Simply choose which folders to track and let TuneJar do the hard work for you.
- M3U playlists can be created, imported (e.g. from iTunes), and exported for use in other music players.
- Metadata (Title/Artist/Album) can be edited directly.
- TuneJar is skinnable via CSS and will ship with multiple premade themes.

# Getting Started with TuneJar
Follow these steps if you would like to use TuneJar.

#### Prerequisite Software
- [Java 8u40 or later](https://java.com/)

#### Installation
Since TuneJar runs off of a single jar file, no formal installation is required. You may download TuneJar binaries from either of the following sources:
- [Official Releases](https://github.com/sudiamanj/TuneJar/releases)
- [Snapshot Builds](http://code.fosshub.com/TuneJar/downloads)

#### Running TuneJar
Unzip the zip file into a preferred location, such as your desktop. On **Windows**, it is recomended to use ``TuneJar.bat`` to start the program, as that will display the console logger in a command prompt. On **Mac or Linux**, you can either launch TuneJar directly from ``TuneJar.jar``, or by entering ``java -jar TuneJar.jar`` in Terminal (which will display the console logger).

# Getting Started with TuneJar Development
Follow these steps if you would like to run TuneJar in a proper development environment.

#### Prerequisite Software
- [JDK 8u40 or later](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
- [Git](https://git-scm.com/downloads) (Optional)

Since this is a Maven project, you will also need an IDE with Maven support, such as [Eclipse](https://eclipse.org/downloads/), [IntelliJ](https://www.jetbrains.com/idea/download/), or [NetBeans](https://netbeans.org/downloads/). If you'd rather not use an IDE, you may also download [Apache Maven](http://maven.apache.org/download.cgi) directly.

**Note**: If using Eclipse, I strongly suggest installing the [**e(fx)clipse**](http://www.eclipse.org/efxclipse/install.html) plugin as well.

#### Installation
If using Git, clone this repository using ``git clone https://github.com/sudiamanj/TuneJar.git <destination>``. Otherwise, simply [download the ZIP file](https://github.com/sudiamanj/TuneJar/archive/master.zip).

#### Running TuneJar
If using an IDE, import as a **Maven project**.  Then run ``src/main/java/tunejar.player.Player.java``.

If you aren't using an IDE, you can run TuneJar using the following Maven commands:
```
mvn compile
mvn exec:java -Dexec.mainClass="tunejar.player.Player"
```

Happy developing! Don't forget to submit proposed changes to the [issue tracker](https://github.com/sudiamanj/TuneJar/issues). For long code snippets, please use either [Gist](https://gist.github.com/), [PasteBin](http://pastebin.com/), or a [forked repository](https://help.github.com/articles/fork-a-repo/).

# Issues
If you run into any issues, please submit them [here](https://github.com/sudiamanj/TuneJar/issues).
