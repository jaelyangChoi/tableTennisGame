package my.tableTennisGame.service.init;

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

    private final WebClient webClient;
    private final ExternalApiProperties externalApiProperties;

    /**
     * faker API 호출하여 body data 추출
     */
    public List<FakerApiRespDto.UserDto> fetchFakers(String seed, String quantity) {
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
                        .retrieve()
                        .bodyToMono(FakerApiRespDto.class)
                        .block())
                .getData();
    }
}
