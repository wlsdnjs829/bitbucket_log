package com.example.bitbucket_log.model;

import com.example.bitbucket_log.enums.BitbucketCommitType;
import lombok.Getter;
import lombok.Setter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@SuppressWarnings("unchecked")
public class CommitVO {

    private List<BitbucketCommitVO> commits = new ArrayList<>();

    private Object pagelen;

    private static final String ZONE_ID = "Asia/Seoul";

    /**
     * 커밋 응답 객체 변환
     *
     * @author : ljw0829
     * @date : 2021-07-14 15:33
     */
    public static CommitVO of(Object result) {
        CommitVO rs = new CommitVO();

        if (!(result instanceof LinkedHashMap)) {
            return rs;
        }

        final LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>) result;

        final Object pagelen = body.get(BitbucketCommitType.pagelen.name());
        rs.setPagelen(pagelen);

        final Object valuesObject = body.get(BitbucketCommitType.values.name());

        if (!(valuesObject instanceof ArrayList)) {
            return rs;
        }

        final ArrayList<Object> values = (ArrayList<Object>) valuesObject;
        final List<BitbucketCommitVO> commits = rs.getCommits();
        values.forEach(value -> getOptionalBitbucketCommit(value).ifPresent(commits::add));
        return rs;
    }

    /**
     * 커밋 객체 조회
     *
     * @author : ljw0829
     * @date : 2021-07-14 15:33
     */
    private static Optional<BitbucketCommitVO> getOptionalBitbucketCommit(Object value) {
        if (!(value instanceof LinkedHashMap)) {
            return Optional.empty();
        }

        BitbucketCommitVO commit = new BitbucketCommitVO();

        final LinkedHashMap<String, Object> valueMap = (LinkedHashMap<String, Object>) value;

        final Object type = valueMap.get(BitbucketCommitType.type.name());
        commit.setType(type);

        final Object message = valueMap.get(BitbucketCommitType.message.name());
        commit.setCommitMessage(message);

        final Object date = valueMap.get(BitbucketCommitType.date.name());

        Optional.ofNullable(date)
                .ifPresent(dateObject -> {
                    final DateTimeFormatter dateTimeFormatter =
                            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL).withZone(ZoneId.of(ZONE_ID));
                    commit.setDate(dateTimeFormatter.format(ZonedDateTime.parse(String.valueOf(dateObject))));
                });

        final Object hash = valueMap.get(BitbucketCommitType.hash.name());
        commit.setHash(hash);

        final Object author = valueMap.get(BitbucketCommitType.author.name());

        if (author instanceof LinkedHashMap) {
            final LinkedHashMap<String, Object> authorMap = (LinkedHashMap<String, Object>) author;
            final Object raw = authorMap.get(BitbucketCommitType.raw.name());
            commit.setAuthorName(raw);
        }

        final Object links = valueMap.get(BitbucketCommitType.links.name());

        if (links instanceof LinkedHashMap) {
            final LinkedHashMap<String, Object> linksMap = (LinkedHashMap<String, Object>) links;
            final Object html = linksMap.get(BitbucketCommitType.html.name());
            final Optional<Object> optionalHref = getOptionalHref(html);
            optionalHref.ifPresent(commit::setCommitHref);
        }

        return Optional.of(commit);
    }

    /* 주소 조회 */
    private static Optional<Object> getOptionalHref(Object html) {
        if (html instanceof LinkedHashMap) {
            final LinkedHashMap<String, Object> htmlMap = (LinkedHashMap<String, Object>) html;
            final Object href = htmlMap.get(BitbucketCommitType.href.name());
            return Optional.of(href);
        }

        return Optional.empty();
    }

}