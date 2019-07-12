# TuneJar

<img src="/screenshot.png" alt="Screenshot" width="800" height="480">

**TuneJar** is a Java-based music player that is lightweight, cross-platform, and best of all, open source.

[![Build Status](https://travis-ci.org/sudiamanj/TuneJar.svg?branch=master)](https://travis-ci.org/sudiamanj/TuneJar) [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=sudiamanj_TuneJar&metric=coverage)](https://sonarcloud.io/dashboard?id=sudiamanj_TuneJar) [![GitHub release](https://img.shields.io/github/release/sudiamanj/TuneJar.svg)](https://github.com/sudiamanj/TuneJar/releases/latest)

## TuneJar is...

### Elegant
TuneJar's interface is built with JavaFX, the latest and greatest in Java GUI technology. Choose from various modern themes that look great on any OS. If you're looking for a true 21st century music player, this is it.

### Fast
Performance tests show that TuneJar can load over *100 gigabytes* of songs in just under ten seconds.

<sup>Tested on a MacBook Pro. Your results may vary.</sup>

### User-Friendly
Avoid the hassles of manual library management. If you add or delete files from your music folders, TuneJar will automatically pick up the changes and update your library accordingly (on its next launch). Let TuneJar do all the hard work for you, so that you can spend more time enjoying your tunes.

## Developing TuneJar

### Running TuneJar using the Command Line
[Install Apache Maven](https://maven.apache.org/install.html), then run TuneJar using the following commands:

```shell
mvn compile
mvn exec:java -Dexec.mainClass="com.sudicode.tunejar.player.Player"
```

### Running TuneJar using an IDE
Import the TuneJar folder as a Maven project, then run `src/main/java/com.sudicode.tunejar.player.Player.java`.

**Note:** If Eclipse gives you an access restriction error, install the [e(fx)clipse](http://www.eclipse.org/efxclipse/index.html) plugin to fix that.
