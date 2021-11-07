package com.zarx.amznscoreguesschallenge.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class SuggestionDto {
    private String suggType;
    private String type;
    private String value;
    private String refTag;
    private String candidateSources;
    private String strategyId;
    private Double prior;
    private Boolean ghost;
    private Boolean help;
    private Boolean blackListed;
    private Boolean xcatOnly;
    private Boolean fallback;
    private Boolean spellCorrected;

    private Map<String, Object> unmappedFields = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getUnmappedFields() {
        return unmappedFields;
    }

    @JsonAnySetter
    public void setUnmappedField(String name, Object value) {
        this.unmappedFields.put(name, value);
    }
}
