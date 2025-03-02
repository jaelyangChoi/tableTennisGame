package my.tableTennisGame.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.tableTennisGame.config.ExternalApiProperties;
import my.tableTennisGame.config.ExternalApiProperties.FakerApi;
import my.tableTennisGame.web.dto.init.FakerApiRespDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Objects;
@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalApiService {

    private final WebClient webClient; //Spring WebFlux의 비동기 HTTP 클라이언트
    private final ExternalApiProperties externalApiProperties;

    /**
     * faker API 호출하여 body data 추출
     */
    public List<FakerApiRespDto.UserDto> fetchFakers(int seed, int quantity) {
        log.info("Fetching fakerapi");
        FakerApi fakerApi = externalApiProperties.getFakerApi();

        return Objects.requireNonNull(webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .scheme("https")
                                .host(fakerApi.getBaseUrl().replace("https://", ""))
                                .path(fakerApi.getEndpoint())
                                .queryParam("_seed", seed)
                                .queryParam("_quantity", quantity)
                                .queryParam("_locale", fakerApi.getLocale())
                                .build())
                        .retrieve() //HTTP 요청을 실행하고 응답을 받음
                        .bodyToMono(FakerApiRespDto.class) //응답을 객체로 변환
                        .block()) //비동기 데이터를 동기적으로 변환. 비동기 실행을 멈추고 동기적으로 데이터가 반환될 때까지 기다림.
                .getData();
    }
}
