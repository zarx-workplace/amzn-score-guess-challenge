package com.zarx.amznscoreguesschallenge.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonPropertyOrder({"keyword", "score"})
public class KeywordScoreDto {

    private String keyword;

    @JsonIgnore
    private double score;

    @JsonProperty("score")
    public int getScoreAsInt() {
        return (int) score;
    }
}
