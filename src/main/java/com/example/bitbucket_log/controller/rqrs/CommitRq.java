package com.example.bitbucket_log.controller.rqrs;

import lombok.Getter;
import lombok.Setter;

/**
 * 커밋 응답 요청
 */
@Getter
@Setter
public class CommitRq {

    private Integer pagelen = 10;

    private Integer page = 1;

    private String branch;

}
