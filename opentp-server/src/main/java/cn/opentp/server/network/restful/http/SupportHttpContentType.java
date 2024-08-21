package cn.opentp.server.network.restful.http;

public enum SupportHttpContentType {

    APPLICATION_JSON("application/json");

    private final String contentType;

    SupportHttpContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }
}
