package com.example.bitbucket_log.controller;

import com.example.bitbucket_log.controller.rqrs.BuildInfoRs;
import com.example.bitbucket_log.controller.rqrs.CommitRq;
import com.example.bitbucket_log.controller.rqrs.CommitSearchRq;
import com.example.bitbucket_log.model.CommitVO;
import com.example.bitbucket_log.utils.BitbucketUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BitbucketController {

    @Value("${bitbucket.oauth.url}")
    private String BITBUCKET_OAUTH_URL;

    @Value("${bitbucket.commits.url}")
    private String BITBUCKET_COMMITS_URL;

    @Value("${bitbucket.id}")
    private String CLIENT_ID;

    @Value("${bitbucket.secret}")
    private String CLIENT_SECRET_KEY;

    @Value("${git.branch}")
    private String GIT_BRANCH;

    @Value("${git.build.time}")
    private String GIT_BUILD_TIME;

    @Value("${git.build.version}")
    private String GIT_BUILD_VERSION;

    @Value("${git.commit.message.full}")
    private String GIT_COMMIT_MESSAGE_FULL;

    @Value("${git.commit.time}")
    private String GIT_COMMIT_TIME;

    /**
     * 빌드 정보 조회
     */
    @GetMapping(value = "/build-info")
    public BuildInfoRs buildInfo() {
        return new BuildInfoRs(GIT_BRANCH, GIT_BUILD_TIME, GIT_BUILD_VERSION, GIT_COMMIT_MESSAGE_FULL, GIT_COMMIT_TIME);
    }

    /**
     * 커밋 내역 조회
     */
    @GetMapping(value = "/commits")
    public CommitVO commits(CommitRq rq) {
        final String accessToken = BitbucketUtil.getAccessToken(BITBUCKET_OAUTH_URL, CLIENT_ID, CLIENT_SECRET_KEY);
        return BitbucketUtil.getCommits(
                accessToken, BITBUCKET_COMMITS_URL, rq.getPagelen(), rq.getPage(), rq.getBranch());
    }

    /**
     * 원본 커밋 내역 조회
     */
    @GetMapping(value = "/original-commits")
    public Object originalCommits(CommitRq rq) {
        final String accessToken = BitbucketUtil.getAccessToken(BITBUCKET_OAUTH_URL, CLIENT_ID, CLIENT_SECRET_KEY);
        return BitbucketUtil.getOriginalCommits(
                accessToken, BITBUCKET_COMMITS_URL, rq.getPagelen(), rq.getPage(), rq.getBranch());
    }

    /**
     * 커밋 내역 검색
     */
    @GetMapping(value = "/search-commits")
    public CommitVO searchCommits(CommitSearchRq rq) {
        final String accessToken = BitbucketUtil.getAccessToken(BITBUCKET_OAUTH_URL, CLIENT_ID, CLIENT_SECRET_KEY);
        return BitbucketUtil.getSearchCommits(
                accessToken, BITBUCKET_COMMITS_URL, 100, rq.getPage(), rq.getBranch(), rq.getKeyword());
    }

}

