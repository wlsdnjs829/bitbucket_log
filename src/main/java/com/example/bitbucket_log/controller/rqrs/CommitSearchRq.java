package com.example.bitbucket_log.controller.rqrs;

import lombok.Getter;
import lombok.Setter;

/**
 * 커밋 검색 요청
 */
@Getter
@Setter
public class CommitSearchRq {

    private Integer page = 1;

    private String branch;

    private String keyword;

}
