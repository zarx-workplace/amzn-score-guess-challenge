package com.zarx.amznscoreguesschallenge.services;

import com.zarx.amznscoreguesschallenge.dto.SuggestionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.zarx.amznscoreguesschallenge.configs.CommsConfiguration.CALL_TIMEOUT_GLOBAL_SECONDS;

@Service
@Slf4j
@ConditionalOnProperty(value = "amznscoreguess.prewarm.enabled", havingValue = "true")
public class PrewarmService {

    @Autowired
    private AmazonSuggestApiService apiService;

    @Autowired
    public void prewarm() {
        Future<List<SuggestionDto>> prewarmSuggestions = apiService.getSuggestions("kettl");
        try {
            List<SuggestionDto> suggestions = prewarmSuggestions.get(CALL_TIMEOUT_GLOBAL_SECONDS, TimeUnit.SECONDS);
            Optional<SuggestionDto> fullmatch = suggestions.stream()
                    .filter(s -> "kettle".equals(s.getValue()))
                    .findFirst();
            if (!fullmatch.isPresent()) {
                log.warn("Pre-warming failed; Couldn't get match for 'kettle' keyword");
            }
        } catch (Exception e) {
            log.warn("Pre-warming failed with exception", e);
        }
    }

}
