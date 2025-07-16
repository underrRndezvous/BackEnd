package com.underrRndezvous.backend.config.bean;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;

@Component
@Slf4j
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        ResponseErrorHandler.super.handleError(url, method, response);

        if (response.getStatusCode().is4xxClientError()) {
            log.error("Rest API Call fail url: {}, method: {}, response code: {}, resposne body: {}", url, method, response.getStatusCode(), response.getBody());
            throw new ClientRequestException();
        }

        if (response.getStatusCode().is5xxServerError()) {
            log.error("Rest API Call fail url: {}, method: {}, response code: {}, resposne body: {}", url, method, response.getStatusCode(), response.getBody());
            throw new ServerRequestException();
        }

    }

    class ClientRequestException extends RuntimeException {
        public ClientRequestException() {
            super("클라이언트 요청 값이 잘못 되었습니다.");
        }
    }

    class ServerRequestException extends RuntimeException {
        public ServerRequestException() {
            super("서버 내부 오류입니다.");
        }
    }
}