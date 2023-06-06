/**
 * Jacotura Gradle Plugin
 * <p>
 * BSD 3-Clause License
 * <p>
 * Copyright (c) 2022, Chirag Gupta
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p>
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p>
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package io.github.chiragji.jacotura;

import lombok.RequiredArgsConstructor;
import org.gradle.api.Project;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A worker class which builds the final properties by extracting it from different sources
 *
 * @author Chirag (chirag-ji)
 * @since 0.0.1
 */
@RequiredArgsConstructor
public class JacoturaPropertyExtractor {

    private final Map<String, ActionBroadcast<JacoturaProperties>> propertiesMap;
    private final Project project;

    public Map<String, Object> extractProperties() {
        Map<String, Object> properties = new LinkedHashMap<>();
        ActionBroadcast<JacoturaProperties> propsBroadcast = propertiesMap.get(project.getPath());
        if (Objects.nonNull(propsBroadcast)) {
            evaluateActionBroadcast(propsBroadcast, properties);
        }
        // extract the other configurations from the extension data
        extractExtensionVariables(properties);

        // return the converted properties
        return convertProperties(properties);
    }

    private void extractExtensionVariables(Map<String, Object> properties) {
        JacoturaExtensions extensions = project.getExtensions().getByType(JacoturaExtensions.class);
        if (Objects.nonNull(extensions.getSourceDirs())) {
            properties.put(JacoturaConstants.KEY_SRC_DIRS, extensions.getSourceDirs());
        }
        if (Objects.nonNull(extensions.getIncludedFileNames())) {
            properties.put(JacoturaConstants.KEY_INCLUDED_FILE_NAMES, extensions.getIncludedFileNames());
        }
        if (extensions.isBeautify()) {
            properties.put(JacoturaConstants.KEY_BEAUTIFY, true);
        }
        if (extensions.isUsePlainFileNames()) {
            properties.put(JacoturaConstants.KEY_USE_PLAIN_FILE_NAME, true);
        }
    }

    private void evaluateActionBroadcast(ActionBroadcast<? super JacoturaProperties> prosBroadcast, Map<String, Object> props) {
        JacoturaProperties properties = new JacoturaProperties(props);
        prosBroadcast.execute(properties);
    }

    private Map<String, Object> convertProperties(Map<String, Object> properties) {
        Map<String, Object> props = new LinkedHashMap<>();
        properties.forEach((k, v) -> props.put(k, convertValue(v)));
        return props;
    }

    private static String convertValue(Object obj) {
        if (Objects.isNull(obj)) return null;
        if (obj instanceof Iterable<?>) {
            Stream<String> stream = StreamSupport.stream(((Iterable<?>) obj).spliterator(), false)
                    .map(JacoturaPropertyExtractor::convertValue);
            String joined = joinStreamContents(stream);
            return joined.isEmpty() ? null : joined;
        } else if (obj instanceof String[]) {
            String joined = joinStreamContents(Arrays.stream((String[]) obj));
            return joined.isEmpty() ? null : joined;
        } else return obj.toString();
    }

    private static String joinStreamContents(Stream<String> stringStream) {
        return stringStream.filter(Objects::nonNull).collect(Collectors.joining(","));
    }
}
