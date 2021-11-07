package com.zarx.amznscoreguesschallenge.services;

import com.zarx.amznscoreguesschallenge.dto.KeywordScoreDto;
import com.zarx.amznscoreguesschallenge.dto.SuggestionDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.zarx.amznscoreguesschallenge.configs.CommsConfiguration.CALL_TIMEOUT_GLOBAL_SECONDS;

@Service
@Slf4j
public class KeywordScoringService {

    private static final int MIN_KEYWORD_LENGTH = 2;
    private static final int SCORING_LENGTH_THRESHOLD = 20;

    @Value("${amznscoreguess.normalization.allowed.spellcorrection.distance}")
    private Integer maxCorrectionDistance;

    @Autowired
    private AmazonSuggestApiService amazonSuggestApiService;

    public KeywordScoreDto estimateKeyword(String inputKeyword) {
        String keyword = validateAndNormalizeInput(inputKeyword);
        Map<Integer, Future<List<SuggestionDto>>> apiQueries = new LinkedHashMap<>();
        int maxCheckedLength = Math.min(keyword.length() - 1, SCORING_LENGTH_THRESHOLD + 1);
        for (int i = 1; i <= maxCheckedLength; i++) {
            String testedPrefix = StringUtils.substring(keyword, 0, i);
            Future<List<SuggestionDto>> query = amazonSuggestApiService.getSuggestions(testedPrefix);
            apiQueries.put(i, query);
        }

        for (Map.Entry<Integer, Future<List<SuggestionDto>>> query : apiQueries.entrySet()) {
            List<SuggestionDto> suggestions;
            try {
                suggestions = query.getValue().get(CALL_TIMEOUT_GLOBAL_SECONDS, TimeUnit.SECONDS);
            } catch (InterruptedException | TimeoutException | ExecutionException e) {
                log.warn("Unexpected flow state. Probably connection problems.", e);
                continue;
            }
            Optional<SuggestionDto> matchingSuggestion = suggestions.stream()
                    .filter(s -> keyword.equals(s.getValue()))
                    .findFirst();
            if (matchingSuggestion.isPresent()) {
                return new KeywordScoreDto(keyword, calculateScore(query.getKey()));
            }
        }
        return new KeywordScoreDto(keyword, calculateScore(maxCheckedLength));
    }

    private double calculateScore(int matchingLength) {
        double maxScoreFactor = 100.0d;
        // magic number that corresponds to assumed max matching length; for matching length above SCORING_LENGTH_THRESHOLD calculation gives score < 1;
        double thresholdFactor = -0.15d;
        return maxScoreFactor * Math.exp(thresholdFactor * (matchingLength - 1)); // should be 100*e^0 at match on the first letter;
    }

    private String validateAndNormalizeInput(String input) {
        String keyword = validateInput(input);

        // pre-processing check if Amazon is aware of the keyword
        // if not - it doesn't make sense to do anything further. just falling back.
        // if there are no exact matches but there are Amazon autocorrected suggestions with smaller distance to input
        // it makes sense to process these instead
        Future<List<SuggestionDto>> suggestionsFuture = amazonSuggestApiService
                .getSuggestions(StringUtils.substring(keyword, 0, keyword.length() - 1));
        List<SuggestionDto> suggestions;
        try {
            suggestions = suggestionsFuture.get(CALL_TIMEOUT_GLOBAL_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            log.error("Unexpected flow state. Probably connection problems.", e);
            throw new RuntimeException(e);
        }

        Optional<SuggestionDto> match = suggestions.stream()
                .filter(s -> keyword.equalsIgnoreCase(s.getValue()))
                .findFirst();
        if (!match.isPresent()) {
            match = suggestions.stream()
                    .filter(s -> BooleanUtils.isTrue(s.getSpellCorrected()))
                    .filter(s -> LevenshteinDistance.getDefaultInstance().apply(keyword, s.getValue()) <= maxCorrectionDistance)
                    .findFirst();
        }

        return match
                .map(SuggestionDto::getValue)
                .orElseThrow(() -> new NoSuchElementException("Term doesn't exist in Amazon suggest"));
    }

    private String validateInput(String input) {
        //screening unexpected input
        String keyword = StringUtils.trimToEmpty(input);
        keyword = RegExUtils.replacePattern(keyword, "\\s+", " ");

        if (keyword.length() <= MIN_KEYWORD_LENGTH) {
            throw new IllegalArgumentException("Insufficient keyword length; "
                    + "Scoring doesn't make sense, as such keywords would be filtered out as noise");
        }
        return keyword;
    }
}
