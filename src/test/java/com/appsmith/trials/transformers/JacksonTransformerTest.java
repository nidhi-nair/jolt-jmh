package com.appsmith.trials.transformers;

import com.bazaarvoice.jolt.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JacksonTransformerTest {

    ObjectMapper objectMapper = new ObjectMapper();
    public Map<String, HashMap<?, ?>> inputMap = new HashMap<>();
    public Map<String, Object> specMap = new HashMap<>();
    public List<String> transformationList = List.of(
            Transformations.IDENTITY,
            Transformations.MAP,
            Transformations.INVERT_MAP
    );
    JoltTransformer joltTransformer = new JoltTransformer();

    public void setup() {
        transformationList.forEach(pathName -> {
            InputStream input = this.getClass().getClassLoader()
                    .getResourceAsStream(pathName + "/input.json");
            try {
                inputMap.put(pathName, objectMapper.readValue(input, new TypeReference<>() {
                }));
                Object spec = JsonUtils.classpathToObject("/" + pathName + "/spec.json");
                specMap.put(pathName, spec);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Before
    public void init() {
        if (inputMap.isEmpty()) {
            setup();
        }

    }

    @Test
    public void transformIdentity() throws IOException {
        final String transformation = Transformations.IDENTITY;

        if (inputMap.isEmpty()) {
            setup();
        }
        Transformations transformer = new JacksonTransformer();
        final Object o = transformer.transformIdentity(
                inputMap.get(transformation),
                objectMapper);

        InputStream output = this.getClass().getClassLoader()
                .getResourceAsStream(transformation + "/output.json");

        final Object transform = joltTransformer.transform(inputMap.get(transformation), specMap.get(transformation));

        final HashMap<?, ?> jsonNode = objectMapper.readValue(output, new TypeReference<>() {
        });


        Assert.assertEquals(jsonNode, o);
        System.out.println(transform.getClass());
        Assert.assertEquals(jsonNode, transform);
    }

    @Test
    public void transformMap() throws IOException {
        final String transformation = Transformations.MAP;
        final HashMap<?, ?> inputJson = inputMap.get(transformation);
        assert inputJson != null;
        final Object spec = specMap.get(transformation);
        assert spec != null;
        Transformations transformer = new JacksonTransformer();
        final Object o = transformer.transformMap(
                inputJson,
                objectMapper);

        InputStream output = this.getClass().getClassLoader()
                .getResourceAsStream(transformation + "/output.json");

        final Object transform = joltTransformer.transform(inputJson, spec);

        final HashMap<?, ?> jsonNode = objectMapper.readValue(output, new TypeReference<>() {
        });

        Assert.assertEquals(jsonNode, o);
        System.out.println(transform.getClass());
        Assert.assertEquals(jsonNode, transform);
    }

    @Test
    public void transformInvertMap() throws IOException {
        final String transformation = Transformations.INVERT_MAP;
        final HashMap<?, ?> inputJson = inputMap.get(transformation);
        assert inputJson != null;
        final Object spec = specMap.get(transformation);
        assert spec != null;
        Transformations transformer = new JacksonTransformer();
        final Object o = transformer.transformInvertMap(
                inputJson,
                objectMapper);

        InputStream output = this.getClass().getClassLoader()
                .getResourceAsStream(transformation + "/output.json");

        final Object transform = joltTransformer.transform(inputJson, spec);

        final HashMap<?, ?> jsonNode = objectMapper.readValue(output, new TypeReference<>() {
        });

        Assert.assertEquals(jsonNode, o);
        System.out.println(transform.getClass());
        Assert.assertEquals(jsonNode, transform);
    }
}