package com.appsmith.trials.transformers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JacksonTransformer implements Transformations {

    @Override
    public Object transformIdentity(Object input, Object objectMapper) {
        return input;
    }

    @Override
    public Object transformMap(Object input, Object objectMapper) {
        final Map<?, ?> rating = (Map<?, ?>) ((Map<?, ?>) input).get("rating");

        final Iterator<?> fields = rating.keySet().iterator();
        final Map<String, Object> secondaryNode = new LinkedHashMap<>();

        while (fields.hasNext()) {
            final String next = (String) fields.next();
            if ("primary".equalsIgnoreCase(next))
                continue;
            final Map<?, ?> nextValue = (Map<?, ?>) rating.get(next);
            final Map<?, ?> value = Map.of("Id", next,
                    "Value", (Integer) nextValue.get("value"),
                    "Range", (Integer) nextValue.get("max"));
            secondaryNode.put(next, value);
        }

        return Map.of(
                "Rating", (Integer) ((Map<?, ?>) rating.get("primary")).get("value"),
                "RatingRange", (Integer) ((Map<?, ?>) rating.get("primary")).get("max"),
                "SecondaryRatings", secondaryNode);
    }

    @Override
    public Object transformInvertMap(Object input, Object objectMapper) {
        final Map<?, ?> catalogConfig = (Map<?, ?>) ((Map<?, ?>) input).get("catalogConfig");
        final Iterator<?> fields = catalogConfig.keySet().iterator();
        final Map<String, Map<String, List<String>>> map = new HashMap<>();

        while (fields.hasNext()) {
            final String nextKey = (String) fields.next();
            final Map<?, ?> next = (Map<?, ?>) ((Map<?, ?>) catalogConfig.get(nextKey)).get("fields");
            final List<String> review = (List<String>) next.get("review");
            final List<String> question = (List<String>) next.get("question");

            if (review != null) {
                review.forEach(x -> {
                    if (map.containsKey(x)) {
                        final Map<String, List<String>> stringListMap = map.get(x);
                        if (stringListMap.containsKey("review")) {
                            stringListMap.get("review").add(nextKey);
                        } else {
                            stringListMap.put("review", new ArrayList<>(Collections.singletonList(nextKey)));
                        }
                    } else {
                        map.put(x, new HashMap<>(Collections.singletonMap(
                                "review",
                                new ArrayList<>(Collections.singletonList(nextKey)))));
                    }
                });
            }
            if (question != null) {
                question.forEach(x -> {
                    if (map.containsKey(x)) {
                        final Map<String, List<String>> stringListMap = map.get(x);
                        if (stringListMap.containsKey("question")) {
                            stringListMap.get("question").add(nextKey);
                        } else {
                            stringListMap.put("question", new ArrayList<>(Collections.singletonList(nextKey)));
                        }
                    } else {
                        map.put(x, new HashMap<>(Collections.singletonMap(
                                "question",
                                new ArrayList<>(Collections.singletonList(nextKey)))));
                    }
                });

            }
        }

        return map;
    }
}
