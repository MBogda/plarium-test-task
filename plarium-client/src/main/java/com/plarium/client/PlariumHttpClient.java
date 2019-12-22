package com.plarium.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class PlariumHttpClient {

    private static Logger logger = Logger.getLogger(PlariumHttpClient.class.getName());

    public static void sendBatch(List<String> batch) throws IOException, InterruptedException {
        if (batch.isEmpty()) {
            logger.info("Empty list is skipped.");
            return;
        }

        URI uri = URI.create("http://localhost:8080/" + "upload_json");     // todo: not hardcode
        int retriesCount = 5;   // todo: not hardcode

        String requestBody = buildRequestBody(batch);
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))      // todo: not hardcode
                .build();
        HttpRequest request = HttpRequest
                .newBuilder(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        logger.info("Sending data: " + requestBody);
        do {
            HttpResponse<String> response;
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (HttpConnectTimeoutException e) {
                if (retriesCount > 0) {
                    logger.warning("Connection time out, will try " + retriesCount + " more time(s).");
                    retriesCount--;
                    continue;
                } else {
                    throw e;
                }
            }
            if (response.statusCode() == 200) {     // todo: not hardcode
                logger.info("Request OK");
            } else {
                logger.info("Request failed");  // todo: not delete after failed
            }
            break;
        } while (true);
    }

    private static String buildRequestBody(List<String> batch) {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<String> batchIterator = batch.iterator();
        stringBuilder.append('[');
        stringBuilder.append(batchIterator.next());
        while (batchIterator.hasNext()) {
            stringBuilder.append(',');
            stringBuilder.append(batchIterator.next());
        }
        stringBuilder.append(']');
        return stringBuilder.toString();
    }
}
