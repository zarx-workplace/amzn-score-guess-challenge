package com.zarx.amznscoreguesschallenge.controllers;

import com.zarx.amznscoreguesschallenge.dto.KeywordScoreDto;
import com.zarx.amznscoreguesschallenge.services.KeywordScoringService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@RestController
@Slf4j
public class KeywordScoringController {

    private static final String KEYWORD_ATTR = "keyword";

    @Autowired
    private KeywordScoringService keywordScoringService;

    @GetMapping("/estimate")
    public KeywordScoreDto getScoreForKeyword(@RequestParam String keyword) {
        long processingStart = System.currentTimeMillis();
        RequestContextHolder.currentRequestAttributes().setAttribute(KEYWORD_ATTR, keyword, RequestAttributes.SCOPE_REQUEST);

        KeywordScoreDto result = keywordScoringService.estimateKeyword(keyword);

        log.info("Request processed. input: '{}', processed keyword: '{}', estimation: {}, process time: {} ms.",
                keyword, result.getKeyword(),
                result.getScore(), System.currentTimeMillis() - processingStart);

        return result;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<KeywordScoreDto> handleExceptions(Exception e) {
        String keyword = (String) RequestContextHolder.getRequestAttributes()
                .getAttribute(KEYWORD_ATTR, RequestAttributes.SCOPE_REQUEST);
        log.error("Failed to process request for keyword: '{}'", keyword, e);

        return new ResponseEntity<>(new KeywordScoreDto(keyword, 0), HttpStatus.OK);
    }

}
