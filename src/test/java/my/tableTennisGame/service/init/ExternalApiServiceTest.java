package my.tableTennisGame.service.init;

import my.tableTennisGame.config.ExternalApiProperties;
import my.tableTennisGame.web.dto.init.FakerApiRespDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.function.Function;

import static my.tableTennisGame.config.ExternalApiProperties.FakerApi;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalApiServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private ExternalApiProperties externalApiProperties;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private ExternalApiService externalApiService;


    @DisplayName("faker API 호출 테스트")
    @Test
    void fetchFakers_success() {
        // given
        int seed = 10;
        int quantity = 5;

        // 외부 API 호출 정보 설정 (FakerApi)
        FakerApi fakerApi = new FakerApi();
        fakerApi.setBaseUrl("https://fakeapi.it"); // service 코드에서는 "https://"를 제거합니다.
        fakerApi.setEndpoint("/api/v1/users");
        fakerApi.setLocale("ko_KR");

        // 응답 DTO 설정
        FakerApiRespDto.UserDto userDto = new FakerApiRespDto.UserDto();
        List<FakerApiRespDto.UserDto> userDtos = List.of(userDto);
        FakerApiRespDto fakerApiRespDto = new FakerApiRespDto();
        fakerApiRespDto.setData(userDtos);

        // stubbing
        when(externalApiProperties.getFakerApi()).thenReturn(fakerApi);
        // WebClient 체이닝 호출 모킹
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri((Function<UriBuilder, URI>) any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(FakerApiRespDto.class)).thenReturn(Mono.just(fakerApiRespDto));

        // when
        List<FakerApiRespDto.UserDto> result = externalApiService.fetchFakers(seed, quantity);

        //then
        assertThat(result).isEqualTo(userDtos);
    }
}