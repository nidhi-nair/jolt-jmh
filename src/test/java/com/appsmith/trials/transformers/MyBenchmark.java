/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.appsmith.trials.transformers;

import com.bazaarvoice.jolt.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MyBenchmark {

    @State(Scope.Benchmark)
    public static class InputSpecs {

        ObjectMapper objectMapper = new ObjectMapper();
        public Map<String, HashMap<?, ?>> inputMap = new HashMap<>();
        public Map<String, Object> specMap = new HashMap<>();
        public List<String> transformationList = List.of(
                Transformations.IDENTITY,
                Transformations.MAP,
                Transformations.INVERT_MAP
        );

        @Setup
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

    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void testIdentity_Jolt(InputSpecs inputSpecs, Blackhole blackhole) {
        JoltTransformer transformer = new JoltTransformer();
        blackhole.consume(transformer.transform(
                inputSpecs.inputMap.get(Transformations.IDENTITY),
                inputSpecs.specMap.get(Transformations.IDENTITY)
        ));
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void testIdentity_Jackson(InputSpecs inputSpecs, Blackhole blackhole) {
        Transformations transformer = new JacksonTransformer();
        blackhole.consume(transformer.transformIdentity(
                inputSpecs.inputMap.get(Transformations.IDENTITY),
                null));
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void testMap_Jolt(InputSpecs inputSpecs, Blackhole blackhole) {
        JoltTransformer transformer = new JoltTransformer();
        blackhole.consume(transformer.transform(
                inputSpecs.inputMap.get(Transformations.MAP),
                inputSpecs.specMap.get(Transformations.MAP)
        ));
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void testMap_Jackson(InputSpecs inputSpecs, Blackhole blackhole) {
        Transformations transformer = new JacksonTransformer();
        blackhole.consume(transformer.transformMap(
                inputSpecs.inputMap.get(Transformations.MAP),
                inputSpecs.objectMapper));
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void testInvertMap_Jolt(InputSpecs inputSpecs, Blackhole blackhole) {
        JoltTransformer transformer = new JoltTransformer();
        blackhole.consume(transformer.transform(
                inputSpecs.inputMap.get(Transformations.INVERT_MAP),
                inputSpecs.specMap.get(Transformations.INVERT_MAP)
        ));
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void testInvertMap_Jackson(InputSpecs inputSpecs, Blackhole blackhole) {
        Transformations transformer = new JacksonTransformer();
        blackhole.consume(transformer.transformInvertMap(
                inputSpecs.inputMap.get(Transformations.INVERT_MAP),
                inputSpecs.objectMapper));
    }
}
