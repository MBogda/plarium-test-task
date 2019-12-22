package com.plarium.client.arguments;

import java.nio.file.Files;
import java.nio.file.Path;

public class ArgumentsVerifier {

    private Arguments arguments;

    public ArgumentsVerifier(Arguments arguments) {
        this.arguments = arguments;
    }

    public void verity() {
        verifyPathToListenTo();
        verifyNumbers();
    }

    private void verifyPathToListenTo() {
        Path pathToListenTo = Path.of(arguments.getPathToListenTo());
        if (!Files.exists(pathToListenTo)) {
            exit("Provided path " + pathToListenTo + " is not exist.\nExit.");
        } else if (!Files.isReadable(pathToListenTo)) {
            exit("Provided path " + pathToListenTo + " is not readable.\nExit.");
        } else if (!Files.isDirectory(pathToListenTo)) {
            exit("Provided path " + pathToListenTo + " is not a directory.\nExit.");
        }
    }

    private void verifyNumbers() {
        if (arguments.getBatchSize() <= 0) {
            exit("Batch size must be greater that zero.");
        }
        if (arguments.getTimeoutInSeconds() <= 0) {
            exit("Timeout must be greater that zero.");
        }
        if (arguments.getRetriesCount() <= 0) {
            exit("Retries count must be greater that zero.");
        }
    }

    private void exit(String message) {
        // todo: move to main class?
        System.out.println(message);
        System.exit(-1);
    }
}
