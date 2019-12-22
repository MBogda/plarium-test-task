package com.plarium.client;

import java.io.IOException;
import java.net.HttpURLConnection;
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

    private Logger logger = Logger.getLogger(PlariumHttpClient.class.getName());

    private URI uri;
    private int retriesCount;
    private long timeout;

    public PlariumHttpClient(String serviceUrl, String uploadPath, int retriesCount, long timeout) {
        this.uri = URI.create(serviceUrl + uploadPath);
        this.retriesCount = retriesCount;
        this.timeout = timeout;
    }

    public void sendBatch(List<String> batch) throws IOException, InterruptedException, IllegalStateException {
        if (batch.isEmpty()) {
            logger.info("Empty list is skipped.");
            return;
        }

        HttpClient client = getHttpClient();

        String requestBody = buildRequestBody(batch);
        HttpRequest request = getHttpRequest(requestBody);

        logger.info("Sending data: " + requestBody);
        sendWithRetrying(client, request);
    }

    private HttpClient getHttpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(timeout))
                .build();
    }

    private HttpRequest getHttpRequest(String requestBody) {
        return HttpRequest
                    .newBuilder(uri)
                    .header("Content-Type", "application/json")     // todo? not hardcode
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
    }

    private String buildRequestBody(List<String> batch) {
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

    private void sendWithRetrying(HttpClient client, HttpRequest request)
            throws IOException, InterruptedException, IllegalStateException {
        int retriesCount = this.retriesCount;
        HttpResponse<String> response;
        while (true) {
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
            break;
        }
        if (response.statusCode() == HttpURLConnection.HTTP_OK) {
            logger.info("Request OK");
        } else {
            logger.info("Request failed");
            throw new IllegalStateException("Request failed");
        }
    }
}
