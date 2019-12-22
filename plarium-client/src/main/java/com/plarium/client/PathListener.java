package com.plarium.client;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PathListener {

    private Logger logger = Logger.getLogger(PathListener.class.getName());
    private Path pathToListenTo;

    public PathListener(Path pathToListenTo) {
        this.pathToListenTo = pathToListenTo;
    }

    @SafeVarargs
    public final void listenDirectory(Consumer<WatchEvent<?>> consumer, WatchEvent.Kind<Path>... events) throws IOException {
        WatchService watcher = FileSystems.getDefault().newWatchService();
        pathToListenTo.register(watcher, events);
        logger.info("Start listening path " + pathToListenTo);
        while (true) {
            WatchKey watchKey;
            try {
                watchKey = watcher.take();
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Program is interrupted.", e);
                System.exit(-1);
                return;     // otherwise there's an error: Variable 'watchKey' might not have been initialized
            }
            for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
                consumer.accept(watchEvent);
            }

            if (!watchKey.reset()) {
                logger.info("Path " + pathToListenTo + " is no longer accessible.");
                logger.info("Stop listening path " + pathToListenTo);
                return;
            }
        }
    }
}
