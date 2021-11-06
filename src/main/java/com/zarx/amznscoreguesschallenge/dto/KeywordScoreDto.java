package com.zarx.amznscoreguesschallenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"Keyword", "score"})
public class KeywordScoreDto {

    @JsonProperty("Keyword")
    private String keyword;

    private Integer score;

}
