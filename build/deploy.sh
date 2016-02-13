# Variables
export JAR_FILE=TuneJar-v0.1-beta-jar-with-dependencies.jar

# Remove existing binaries
rm -rf dist
mkdir dist
rm TuneJar.dmg
cd ..
rm -rf target

# Build JAR
mvn install
cd build

# Build OS X binaries
jdk=$(/usr/libexec/java_home)
$jdk/bin/javapackager -deploy -native dmg -srcfiles ../target/$JAR_FILE \
    -appclass com.sudicode.tunejar.player.Player -name TuneJar -outdir deploy \
    -outfile TuneJar -v -Bicon=TuneJar.icns
cp deploy/bundles/TuneJar-1.0.dmg dist/TuneJar.dmg

# Generic
cp ../target/$JAR_FILE dist/TuneJar.jar

# Cleanup
rm -rf deploy
