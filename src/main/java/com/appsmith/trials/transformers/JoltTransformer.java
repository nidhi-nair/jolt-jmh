package com.appsmith.trials.transformers;

import com.bazaarvoice.jolt.Shiftr;

public class JoltTransformer {

    public Object transform(Object input, Object spec) {
        // The incoming object is a JsonNode

        final Shiftr shiftr = new Shiftr(spec);

        return shiftr.transform(input);
    }

}
