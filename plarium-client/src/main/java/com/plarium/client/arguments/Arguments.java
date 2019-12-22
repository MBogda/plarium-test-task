package com.plarium.client.arguments;

public class Arguments {
    private String pathToListenTo;
    private int batchSize;
    private String serviceUrl;
    private String uploadPath;
    private long timeoutInSeconds;
    private int retriesCount;

    public Arguments() {
        pathToListenTo = null;
        batchSize = DefaultArguments.BATCH_SIZE;
        serviceUrl = DefaultArguments.SERVICE_URL;
        uploadPath = DefaultArguments.UPLOAD_PATH;
        timeoutInSeconds = DefaultArguments.TIMEOUT_IN_SECONDS;
        retriesCount = DefaultArguments.RETRIES_COUNT;
    }

    public String getPathToListenTo() {
        return pathToListenTo;
    }

    public void setPathToListenTo(String pathToListenTo) {
        this.pathToListenTo = pathToListenTo;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public long getTimeoutInSeconds() {
        return timeoutInSeconds;
    }

    public void setTimeoutInSeconds(long timeoutInSeconds) {
        this.timeoutInSeconds = timeoutInSeconds;
    }

    public int getRetriesCount() {
        return retriesCount;
    }

    public void setRetriesCount(int retriesCount) {
        this.retriesCount = retriesCount;
    }
}
