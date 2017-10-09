package com.microsoft.azure;

import com.microsoft.rest.RestProxy;
import com.microsoft.rest.SwaggerMethodParser;
import com.microsoft.rest.http.HttpRequest;
import com.microsoft.rest.http.HttpResponse;
import rx.Observable;
import rx.Single;
import rx.exceptions.Exceptions;
import rx.functions.Func1;

import java.io.IOException;
import java.lang.reflect.Type;

public class ProvisioningStatePollStrategy extends PollStrategy {
    private final HttpRequest originalRequest;
    private HttpResponse latestHttpResponse;

    ProvisioningStatePollStrategy(RestProxy restProxy, HttpRequest originalRequest, String provisioningState, long delayInMilliseconds) {
        super(restProxy, delayInMilliseconds);

        this.originalRequest = originalRequest;
        setProvisioningState(provisioningState);
    }

    @Override
    HttpRequest createPollRequest() {
        return new HttpRequest(originalRequest.callerMethod(), "GET", originalRequest.url());
    }

    @Override
    Single<HttpResponse> updateFromAsync(final HttpResponse httpPollResponse) {
        latestHttpResponse = httpPollResponse.buffer();
        return latestHttpResponse.bodyAsStringAsync()
                .map(new Func1<String, HttpResponse>() {
                    @Override
                    public HttpResponse call(String responseBody) {
                        try {
                            final ResourceWithProvisioningState resource = deserialize(responseBody, ResourceWithProvisioningState.class);
                            if (resource == null || resource.properties() == null || resource.properties().provisioningState() == null) {
                                setProvisioningState(ProvisioningState.IN_PROGRESS);
                            }
                            else {
                                setProvisioningState(resource.properties().provisioningState());
                            }
                        } catch (IOException e) {
                            throw Exceptions.propagate(e);
                        }
                        return latestHttpResponse;
                    }
                });
    }

    @Override
    boolean isDone() {
        return ProvisioningState.isCompleted(provisioningState());
    }
}