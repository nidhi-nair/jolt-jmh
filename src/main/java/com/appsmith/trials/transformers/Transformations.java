package com.appsmith.trials.transformers;


public interface Transformations {

    Object transformIdentity(Object input, Object objectMapper);

    Object transformMap(Object input, Object objectMapper);

    String IDENTITY = "identity";
    String MAP = "map";
    String INVERT_MAP = "invertmap";

    Object transformInvertMap(Object input, Object objectMapper);
}
