# Variables
export JAR_FILE=TuneJar-v0.1-beta-jar-with-dependencies.jar

# Build
rm TuneJar.dmg
cd ..
rm -rf target
mvn install
cd build

# OS X
jdk=$(/usr/libexec/java_home)
$jdk/bin/javapackager -deploy -native dmg -srcfiles ../target/$JAR_FILE \
    -appclass com.sudicode.tunejar.player.Player -name TuneJar -outdir deploy \
    -outfile TuneJar -v -Bicon=TuneJar.icns
cp deploy/bundles/TuneJar-1.0.dmg TuneJar.dmg
rm -rf deploy
