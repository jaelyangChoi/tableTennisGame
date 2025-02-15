package my.tableTennisGame.service.common;

import lombok.RequiredArgsConstructor;
import my.tableTennisGame.config.ExternalApiProperties;
import my.tableTennisGame.config.ExternalApiProperties.FakerApi;
import my.tableTennisGame.web.dto.common.FakerApiRespDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ExternalApiService {

    private final WebClient webClient;
    private final ExternalApiProperties externalApiProperties;

    /**
     * faker API 호출하여 body data 추출
     */
    public List<FakerApiRespDto.UserDto> fetchFakers(String seed, String quantity) {
        FakerApi fakerApi = externalApiProperties.getFakerApi();

        return Objects.requireNonNull(webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .scheme("https")
                                .host(fakerApi.getBaseUrl().replace("https://", ""))
                                .path(fakerApi.getEndpoints())
                                .queryParam("_seed", seed)
                                .queryParam("_quantity", quantity)
                                .queryParam("_locale", fakerApi.getLocale())
                                .build())
                        .retrieve()
                        .bodyToMono(FakerApiRespDto.class)
                        .block())
                .getData();
    }
}
