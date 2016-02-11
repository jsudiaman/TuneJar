/*
 * TuneJar <http://sudicode.com/tunejar/>
 * Copyright (C) 2016 Jonathan Sudiaman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sudicode.tunejar.player;

import com.jayway.awaitility.Awaitility;
import com.jayway.awaitility.core.ConditionFactory;
import com.sudicode.tunejar.config.Defaults;
import com.sudicode.tunejar.config.Options;
import com.sudicode.tunejar.config.TestDefaults;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.loadui.testfx.GuiTest;

import java.io.File;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.scene.Parent;

public abstract class IntegrationTest {

    private static boolean initialized = false;
    private static GuiTest driver;

    /**
     * Starts the TuneJar player.
     *
     * @throws Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        if (!initialized)
            init();
    }

    private static void init() throws Exception {
        // Delete existing TuneJar files
        File tuneJarHome = Defaults.TUNEJAR_HOME.toFile();
        if (tuneJarHome.exists()) {
            FileUtils.deleteDirectory(tuneJarHome);
        }

        // Download sample music files, if necessary
        FileUtils.forceMkdir(Defaults.LOG_FOLDER.toFile());
        FileUtils.forceMkdir(Defaults.PLAYLISTS_FOLDER.toFile());
        FileUtils.forceMkdir(TestDefaults.RESOURCES.resolve("mp3").toFile());
        FileUtils.forceMkdir(TestDefaults.RESOURCES.resolve("mp4").toFile());
        FileUtils.forceMkdir(TestDefaults.RESOURCES.resolve("wav").toFile());
        for (Entry<File, URL> entry : TestDefaults.SAMPLE_MUSIC_MAP.entrySet()) {
            if (!entry.getKey().exists()) {
                System.out.print("Downloading " + entry.getValue() + "...");
                FileUtils.copyURLToFile(entry.getValue(), entry.getKey());
                System.out.println("Done.");
            }
        }

        // Set media directories
        Options options = new Options(Defaults.OPTIONS_FILE.toFile());
        Set<File> dirs = new LinkedHashSet<>();
        dirs.add(new File("src/test/resources/"));
        options.setDirectories(dirs);

        // Launch application
        new Thread(() -> Application.launch(Player.class)).start();
        await().ignoreException(NullPointerException.class).until(() -> getPlayer().isInitialized());
        TimeUnit.SECONDS.sleep(1);

        // Set driver
        setDriver(new GuiTest() {
            @Override
            protected Parent getRootNode() {
                return Player.getPlayer().getScene().getRoot();
            }
        });

        // Initialization complete
        initialized = true;
    }

    protected static PlayerController getController() {
        return getPlayer().getController();
    }

    /**
     * Used to manipulate the TuneJar player.
     *
     * @see GuiTest
     */
    protected static GuiTest getDriver() {
        return driver;
    }

    private static void setDriver(GuiTest driver) {
        IntegrationTest.driver = driver;
    }

    protected static Player getPlayer() {
        return Player.getPlayer();
    }

    protected static ConditionFactory await() {
        return Awaitility.await().atMost(30, TimeUnit.SECONDS);
    }

}
