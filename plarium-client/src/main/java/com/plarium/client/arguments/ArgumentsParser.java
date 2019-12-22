package com.plarium.client.arguments;

import com.plarium.client.ConsoleJsonLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ArgumentsParser {
    private Logger logger = Logger.getLogger(ArgumentsParser.class.getName());

    public ArgumentsParser() {}

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
                        try {
                            arguments.setBatchSize(Integer.parseInt(nextArg));
                        } catch (NumberFormatException e) {
                            exit("Invalid number format of batch size.");
                        }
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
                        try {
                            arguments.setTimeoutInSeconds(Long.parseLong(nextArg));
                        } catch (NumberFormatException e) {
                            exit("Invalid number format of timeout.");
                        }
                        break;
                    case "-r":
                    case "--retries":
                        try {
                            arguments.setRetriesCount(Integer.parseInt(nextArg));
                        } catch (NumberFormatException e) {
                            exit("Invalid number format of retries count.");
                        }
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
