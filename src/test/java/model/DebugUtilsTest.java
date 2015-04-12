package model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.*;
import java.util.logging.Level;

import org.junit.Before;
import org.junit.Test;

public class DebugUtilsTest {

    Exception e;
    File logFile;

    @Before
    public void setUp() throws Exception {
        logFile = new File("log.txt");
        e = new NullPointerException();
        assertNotNull(DebugUtils.LOGGER);
    }

    @Test
    public void testLogger() throws Exception {
        // Log an exception.
        try {
            throw e;
        } catch (NullPointerException e) {
            DebugUtils.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        // Find the exception in the log file.
        boolean foundException = false;
        BufferedReader reader = new BufferedReader(new FileReader(logFile));
        for(String nextLine; (nextLine = reader.readLine()) != null; ) {
            if(nextLine.contains("NullPointerException")) {
                foundException = true;
                break;
            }
        }
        assertTrue(foundException);

        reader.close();
    }

}