package com.synchrony.assignment.userprofile.gateway;

import com.synchrony.assignment.userprofile.exception.ImgurGatewayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

/**
 * @author sanketku
 */
public class ImgurGatewayResponseErrorHandler implements ResponseErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(ImgurGatewayResponseErrorHandler.class);

    /**
     * Indicate whether the given response has any errors.
     * <p>Implementations will typically inspect the
     * {@link ClientHttpResponse#getStatusCode() HttpStatus} of the response.
     *
     * @param response the response to inspect
     * @return {@code true} if the response indicates an error; {@code false} otherwise
     * @throws IOException in case of I/O errors
     */

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().series() != HttpStatus.Series.SUCCESSFUL;

    }

    /**
     * Handle the error in the given response.
     * <p>This method is only called when {@link #hasError(ClientHttpResponse)}
     * has returned {@code true}.
     *
     * @param response the response with the error
     * @throws IOException in case of I/O errors
     */
    @Override
    public void handleError(ClientHttpResponse response) throws IOException {

        logger.error("Received exception {} while communication with imgur API with status code {}", response.getBody(), response.getRawStatusCode());
        throw new ImgurGatewayException(response.getRawStatusCode(), "imgur service is currently unavailable");


    }
}
