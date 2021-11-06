package com.zarx.amznscoreguesschallenge.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class AmazonSuggestionsResponseDto {
    private String alias;
    private String prefix;
    private String suffix;
    private List<SuggestionDto> suggestions = new ArrayList<>();
    private String suggestionTitleId;
    private String responseId;
    private Boolean shuffled;

    private Map<String, String> unmappedFields = new HashMap<>();

    @JsonAnyGetter
    public Map<String, String> getUnmappedFields() {
        return unmappedFields;
    }
    @JsonAnySetter
    public void setUnmappedField(String name, String value) {
        this.unmappedFields.put(name, value);
    }
}
