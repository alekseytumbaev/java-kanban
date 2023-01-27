package api.task_request_processors;

import constant.HttpCode;

public class Response {

    private final HttpCode httpCode;
    private final String body;

    public Response(HttpCode httpCode, String body) {
        this.httpCode = httpCode;
        this.body = body;
    }

    public Response(HttpCode httpCode) {
        this.httpCode = httpCode;
        body = "";
    }

    public HttpCode getHttpCode() {
        return httpCode;
    }

    public String getBody() {
        return body;
    }
}
