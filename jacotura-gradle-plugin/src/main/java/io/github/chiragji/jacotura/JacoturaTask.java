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

import org.gradle.api.internal.ConventionTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A gradle script facing task which constructs the running configuration from the input properties
 *
 * @author Chirag (chirag-ji)
 * @since 0.0.1
 */
public class JacoturaTask extends ConventionTask {
    private static final Logger LOGGER = Logging.getLogger(JacoturaTask.class);

    private Map<String, String> props;

    @Input
    public Map<String, String> getProperties() {
        if (Objects.isNull(props))
            props = new LinkedHashMap<>();
        return props;
    }

    @TaskAction
    public void run() {
        // check first for required properties
        if (!hasRequiredProperties()) return;

        // if it has all properties, start build config
        JacoturaConfig config = buildJacoturaConfig();
        if (!config.getJacoturaFile().exists()) {
            // if jacoco report does not yet born to codebase, leave happily without being angry
            LOGGER.warn("Jacoco report not exists. Skipping conversion");
            return;
        }

        LOGGER.info("Starting jacoco report conversion to cobertura");
        new JacoturaConversion(config).start();
        LOGGER.info("finished jacoco report conversion to cobertura");
    }

    private boolean hasRequiredProperties() {
        Map<String, String> props = getProperties();
        boolean hasProperties = true;
        if (!props.containsKey(JacoturaConstants.KEY_JACOCO_PATH)) {
            reportMissingProperty(JacoturaConstants.KEY_JACOCO_PATH);
            hasProperties = false;
        }
        if (!props.containsKey(JacoturaConstants.KEY_COBERTURA_PATH)) {
            reportMissingProperty(JacoturaConstants.KEY_COBERTURA_PATH);
            hasProperties = false;
        }
        return hasProperties;
    }

    private void reportMissingProperty(String propertyName) {
        LOGGER.warn("Does not have required property '{}'; Skipping report conversion", propertyName);
    }

    private JacoturaConfig buildJacoturaConfig() {
        Map<String, String> props = getProperties();
        JacoturaConfig config = new JacoturaConfig();
        config.setJacoturaReport(props.get(JacoturaConstants.KEY_JACOCO_PATH));
        config.setCoberturaReport(props.get(JacoturaConstants.KEY_COBERTURA_PATH));
        config.setSrcDirs(getArrayProperty(JacoturaConstants.KEY_SRC_DIRS));
        config.setIncludeFileNames(getArrayProperty(JacoturaConstants.KEY_INCLUDED_FILE_NAMES));
        config.setBeautify(Boolean.parseBoolean(props.get(JacoturaConstants.KEY_BEAUTIFY)));
        return config;
    }

    private Set<String> getArrayProperty(String propertyName) {
        String prop = getProperties().get(propertyName);
        if (Objects.isNull(prop)) return Collections.emptySet();
        return Arrays.stream(prop.split(",")).collect(Collectors.toSet());
    }
}
