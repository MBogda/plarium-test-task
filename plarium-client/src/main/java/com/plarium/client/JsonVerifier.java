package com.plarium.client;

import java.util.logging.Logger;

public class JsonVerifier {

    private JsonVerifier() {}

    private static Logger logger = Logger.getLogger(ConsoleJsonLoader.class.getName());

    public static boolean verifyFormat(String entry) {
        // note: not full verification, because I don't want to create a custom JSON parser and can't use an existing one.
        if (entry.isBlank()) {
            return false;
        }
        if (!entry.contains("\"type\"")) {
            logger.warning("Entry " + entry + " doesn't contains \"type\" pattern - skip.");
            return false;
        }
        return true;
    }
}
