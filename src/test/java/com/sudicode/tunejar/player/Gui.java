package com.sudicode.tunejar.player;

import com.sudicode.tunejar.config.Defaults;
import com.sudicode.tunejar.config.Options;
import javafx.application.Application;
import javafx.scene.Parent;
import org.loadui.testfx.GuiTest;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import static com.jayway.awaitility.Awaitility.await;
import static com.jayway.awaitility.Duration.ONE_MINUTE;

/**
 * Wraps an instance of the player for use in unit tests.
 */
public class Gui {

    private static Gui instance;
    private final GuiTest robot;

    /**
     * Start the application (if necessary). Once it's running, return the instance.
     *
     * @return Instance of {@link Gui}.
     */
    public static synchronized Gui getInstance() {
        if (instance == null) {
            instance = new Gui();
        }
        return instance;
    }

    /**
     * Start the application and initialize the robot.
     */
    private Gui() {
        // Adjust options
        try {
            URL resourceURL = Thread.currentThread().getContextClassLoader().getResource("");
            if (resourceURL == null) {
                throw new NullPointerException("Could not locate resources folder");
            }
            File resourceFolder = new File(resourceURL.toURI());
            Options options = new Options(Defaults.PREFERENCES_NODE);
            options.setDirectories(new LinkedHashSet<>(Collections.singleton(resourceFolder)));
            options.setPlaylists(new LinkedHashMap<>());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        // Launch application
        new Thread(() -> Application.launch(Player.class)).start();
        await().atMost(ONE_MINUTE).ignoreException(NullPointerException.class).until(() -> getPlayer().isInitialized());

        // Init robot
        this.robot = new GuiTest() {
            @Override
            protected Parent getRootNode() {
                return Player.getPlayer().getScene().getRoot();
            }
        };
    }

    /**
     * Wrapper-like interface that makes it easier to chain together multiple robot methods while adding a number of
     * convenience methods, such as finding a given node, scene or window via a <code>PointQuery</code>, a
     * <code>Predicate</code>, or a <code>Matcher</code>.
     *
     * @return Robot used to manipulate the player.
     */
    public GuiTest getRobot() {
        return robot;
    }

    /**
     * @return The {@link Player}.
     */
    public Player getPlayer() {
        return Player.getPlayer();
    }

    /**
     * @return The {@link PlayerController}.
     */
    public PlayerController getController() {
        return getPlayer().getController();
    }

}
