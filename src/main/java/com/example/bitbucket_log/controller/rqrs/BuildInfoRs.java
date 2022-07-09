package com.example.bitbucket_log.controller.rqrs;

import lombok.Getter;

/**
 * 빌드 정보 응답 객체
 */
@Getter
public record BuildInfoRs(
        String branch,
        String buildTime,
        String version,
        String lastCommitMessage,
        String lastCommitTime
) {
}
