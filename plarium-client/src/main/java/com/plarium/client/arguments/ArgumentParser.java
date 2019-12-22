package com.plarium.client.arguments;

import com.plarium.client.ConsoleJsonLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ArgumentParser {
    private Logger logger = Logger.getLogger(ArgumentParser.class.getName());

    public ArgumentParser() {}

    public Arguments parse(String[] args) {
        Arguments arguments = new Arguments();
        for (int i = 0; i < args.length; ++i) {
            String arg = args[i];
            if (arg.startsWith("-")) {
                if (i + 1 >= args.length) {
                    exit("Keyword argument " + arg + " set but not provided.");
                }
                String nextArg = args[i + 1];
                // todo: not hardcode
                switch (arg) {
                    case "-b":
                    case "--batch_size":
                        arguments.setBatchSize(Integer.parseInt(nextArg));
                        break;
                    case "-u":
                    case "--url":
                        arguments.setServiceUrl(nextArg);
                        break;
                    case "-p":
                    case "--upload_path":
                        arguments.setUploadPath(nextArg);
                        break;
                    case "-t":
                    case "--timeout":
                        arguments.setTimeoutInSeconds(Long.parseLong(nextArg));
                        break;
                    case "-r":
                    case "--retries":
                        arguments.setRetriesCount(Integer.parseInt(nextArg));
                        break;
                    default:
                        exit("Not known key argument " + arg + " " + nextArg);
                        break;
                }
                i++;
            } else if (arguments.getPathToListenTo() == null) {
                arguments.setPathToListenTo(arg);
            } else {
                exit("Need only one positional argument, extra argument " + arg + " is provided.");
            }
        }
        if (arguments.getPathToListenTo() == null) {
            exit("Positional argument path_to_listen_to is not provided.");
        }
        return arguments;
    }

    private void exit(String message) {
        System.out.println(message);
        printUsage();
        System.exit(-1);
    }

    private void printUsage() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("usage.txt");
        try {
            String text = new String(inputStream.readAllBytes())
                    .replace("$program_name", ConsoleJsonLoader.class.getName())
                    .replace("$path_to_listen_to", "path_to_listen_to")
                    .replace("$batch_size", "-b, --batch_size")
                    .replace("$default_batch_size", Integer.toString(DefaultArguments.BATCH_SIZE))
                    .replace("$url", "-u, --url")
                    .replace("$default_url", DefaultArguments.SERVICE_URL)
                    .replace("$upload_path", "-p, --upload_path")
                    .replace("$default_upload_path", DefaultArguments.UPLOAD_PATH)
                    .replace("$timeout", "-t, --timeout")
                    .replace("$default_timeout", Long.toString(DefaultArguments.TIMEOUT_IN_SECONDS))
                    .replace("$retries", "-r, --retries")
                    .replace("$default_retries", Integer.toString(DefaultArguments.RETRIES_COUNT))
                    ;
            System.out.print(text);
        } catch (IOException | NullPointerException e) {
            logger.log(Level.SEVERE, "Exception during reading resource.", e);
        }
    }
}
