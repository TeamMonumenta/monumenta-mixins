package com.playmonumenta.papermixins;

import java.io.File;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MonumentaMod {
    public static final Logger LOGGER = getLogger("");

    // we create this dummy file object that never exists
    // this tricks MC into not reading from file
    public static final File FAKE_FILE = new File("") {
        @Override
        public boolean exists() {
            return false;
        }
    };

    // State management
    public static final ThreadLocal<Function<? super Double, Double>> IFRAME_FUNC = new ThreadLocal<>();
    public static final ThreadLocal<Double> IFRAME_VALUE = new ThreadLocal<>();

    public static String getIdentifier() {
        return String.format("MonumentaPaper (%s) %s", VersionInfo.MOD_ID, VersionInfo.VERSION);
    }

    public static Logger getLogger(String subsystem) {
        return LogManager.getLogger(VersionInfo.IDENTIFIER + "/" + subsystem);
    }
}
