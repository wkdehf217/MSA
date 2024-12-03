package com.sparta.msa_exam.client.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@Component
public class WeightRoutingFilterFactory implements GatewayFilterFactory<WeightRoutingFilterFactory.Config> {

    @Override
    public GatewayFilter apply(Config config) {
        return new WeightRoutingFilter(config);
    }

    @Override
    public Class<Config> getConfigClass() {
        return Config.class;
    }

    // Config 클래스는 필터 설정을 위한 클래스
    public static class Config {
        // 여기에 필터에서 사용할 속성을 정의할 수 있습니다.
    }

    // 실제 필터 구현
    public static class WeightRoutingFilter implements GatewayFilter {
        private final Config config;

        public WeightRoutingFilter(Config config) {
            this.config = config;
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            log.info("####################filter##################");

            // uri 분배 로직
            String targetUri = determineTargetUri();

            // 기존 요청 URI의 경로와 쿼리 파라미터를 포함하여 새로운 URI로 설정
            URI currentUri = exchange.getRequest().getURI();
            String path = currentUri.getPath();  // 기존 경로는 그대로 유지
            String query = currentUri.getQuery();  // 기존 쿼리 파라미터도 유지

            // 경로 중복을 방지하고, targetUri에 기존 경로와 쿼리를 덧붙임
            URI newUri = URI.create(targetUri + path + (query != null ? "?" + query : ""));
            log.info("newUri: "+ newUri);

            // 새로 수정된 URI로 ServerWebExchange 객체를 재구성
            ServerHttpRequest newRequest = exchange.getRequest().mutate().uri(newUri).build();
            ServerWebExchange newExchange = exchange.mutate().request(newRequest).build(); // 새로운 ServerWebExchange 생성

            return chain.filter(newExchange);  // 새로운 exchange 객체를 필터 체인에 전달
        }

        private String determineTargetUri() {
            double random = Math.random();
            return (random < 0.7) ? "http://localhost:19093" : "http://localhost:19094";
        }
    }
}