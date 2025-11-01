package com.huang.test.grphqltest;

import graphql.ExecutionResult;
import graphql.ExecutionResultImpl;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class SecurityGraphQlStrategy implements WebGraphQlInterceptor {

    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
        boolean regine = false;
        for (String s : request.getHeaders().keySet()) {
            List<String> v = request.getHeaders().get(s);
            if (v != null && s.equals("authorization") && v.get(0).equals("123")) {
                regine = true;
            }
        }
        if (!regine) {
            return Mono.error(new RuntimeException("token is null"));
        }
        return chain.next(request);
    }
}
