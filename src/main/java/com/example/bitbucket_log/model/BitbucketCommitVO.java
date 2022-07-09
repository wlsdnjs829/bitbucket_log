package com.example.bitbucket_log.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 빗버킷 커밋 VO
 */
@Getter
@Setter
public class BitbucketCommitVO {

    private Object commitMessage;
    private Object commitHref;
    private Object authorName;
    private Object hash;
    private Object date;
    private Object type;

}

