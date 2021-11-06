package com.zarx.amznscoreguesschallenge.controllers;

import com.zarx.amznscoreguesschallenge.dto.KeywordScoreDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScoreController {

    @GetMapping("/estimate")
    public KeywordScoreDto getScoreForKeyword(@RequestParam String keyword) {
        return new KeywordScoreDto(keyword, 0);
    }

}
