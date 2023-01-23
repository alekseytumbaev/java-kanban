package api.processor;

public class Response {

    private final int httpCode;
    private final String body;

    public Response(int httpCode, String body) {
        this.httpCode = httpCode;
        this.body = body;
    }

    public Response(int httpCode) {
        this.httpCode = httpCode;
        body = "";
    }

    public int getHttpCode() {
        return httpCode;
    }

    public String getBody() {
        return body;
    }
}
