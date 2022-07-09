package com.example.bitbucket_log.utils;


import com.example.bitbucket_log.model.BitbucketCommitVO;
import com.example.bitbucket_log.model.CommitVO;
import com.google.gson.JsonParser;
import lombok.experimental.UtilityClass;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.json.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 빗버킷 Util
 */
@UtilityClass
public final class BitbucketUtil {

    /* CONSTANTS */
    private final String CLIENT_CREDENTIALS = "client_credentials";
    private final String CLIENT_SECRET = "client_secret";
    private final String ACCESS_TOKEN = "access_token";
    private final String BEARER_AUTH_HEADER_PREFIX = "Bearer ";
    private final String BRANCH = "branch";
    private final String PAGE_LEN = "pagelen";
    private final String PAGE = "page";

    /**
     * 액세스 토큰 조회

     */
    public String getAccessToken(String bitbucketOauthUrl, String clientId, String clientSecretKey) {
        final RestTemplate restTemplate = new RestTemplate();
        final HttpComponentsClientHttpRequestFactory factory = getHttpRequestFactory();
        restTemplate.setRequestFactory(factory);

        final HttpEntity<MultiValueMap<String, Object>> httpEntity = getOauthHttpEntity(clientId, clientSecretKey);
        final ResponseEntity<String> response = restTemplate.postForEntity(bitbucketOauthUrl, httpEntity, String.class);

        final JsonObject resultJson = (JsonObject) (new JsonParser().parse(Objects.requireNonNull(response.getBody())));
        return Optional.ofNullable(resultJson.get(ACCESS_TOKEN))
                .orElseThrow(RuntimeException::new)
                .getValueType()
                .name();
    }

    /* HttpRequestFactory 조회 */
    private HttpComponentsClientHttpRequestFactory getHttpRequestFactory() {
        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

        final HttpClient httpClient = HttpClientBuilder.create()
                .setRedirectStrategy(new LaxRedirectStrategy())
                .build();

        factory.setHttpClient(httpClient);
        return factory;
    }

    /* Oauth HttpEntity 조회 */
    private HttpEntity<MultiValueMap<String, Object>> getOauthHttpEntity(String clientId,
                                                                         String clientSecretKey) {
        final MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add(OAuth2Utils.GRANT_TYPE, CLIENT_CREDENTIALS);
        parts.add(OAuth2Utils.CLIENT_ID, clientId);
        parts.add(CLIENT_SECRET, clientSecretKey);

        final HttpHeaders headers = new HttpHeaders();
        final List<MediaType> mediaTypeList = new ArrayList<>();

        mediaTypeList.add(MediaType.APPLICATION_JSON);
        headers.setAccept(mediaTypeList);
        return new HttpEntity<>(parts, headers);
    }

    /* Common HttpEntity 조회 */
    private HttpEntity<String> getCommonHttpEntity(String accessToken) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HttpHeaders.AUTHORIZATION, BEARER_AUTH_HEADER_PREFIX + accessToken);
        return new HttpEntity<>(headers);
    }

    /**
     * 원본 커밋 리스트 조회
     */
    public Object getOriginalCommits(String accessToken, String bitbucketCommitsUrl, Integer pagelen,
                                     Integer page, String branch) {
        final RestTemplate restTemplate = new RestTemplate();
        final HttpComponentsClientHttpRequestFactory factory = getHttpRequestFactory();
        restTemplate.setRequestFactory(factory);

        final HttpEntity<String> httpEntity = getCommonHttpEntity(accessToken);
        final Map<String, String> param = getParam(branch, pagelen, page);
        final ResponseEntity<Object> response =
                restTemplate.exchange(bitbucketCommitsUrl, HttpMethod.GET, httpEntity, Object.class, param);

        return response.getBody();
    }

    /* PARAM 조회 */
    private Map<String, String> getParam(String branch, Integer pagelen, Integer page) {
        final Map<String, String> param = new HashMap<>();
        param.put(BRANCH, branch);
        param.put(PAGE_LEN, String.valueOf(pagelen));
        param.put(PAGE, String.valueOf(page));
        return param;
    }

    /**
     * 가공된 커밋 리스트 조회
     */
    public CommitVO getCommits(String accessToken, String bitbucketCommitsUrl, Integer pagelen,
                               Integer page, String branch) {
        final Object originalCommits = getOriginalCommits(accessToken, bitbucketCommitsUrl, pagelen, page, branch);
        return CommitVO.of(originalCommits);
    }

    /**
     * 검색된 커밋 리스트 조회
     */
    public CommitVO getSearchCommits(String accessToken, String bitbucketCommitsUrl, Integer pagelen,
                                     Integer page, String branch, String keyword) {
        final Object originalCommits = getOriginalCommits(accessToken, bitbucketCommitsUrl, pagelen, page, branch);
        final CommitVO vo = CommitVO.of(originalCommits);
        final List<BitbucketCommitVO> commits = vo.getCommits();

        final List<BitbucketCommitVO> filterCommits = commits.stream()
                .filter(commit -> String.valueOf(commit.getAuthorName()).contains(keyword) ||
                        String.valueOf(commit.getCommitMessage()).contains(keyword))
                .collect(Collectors.toList());

        vo.setCommits(filterCommits);
        return vo;
    }

}