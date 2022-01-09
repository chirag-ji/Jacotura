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

/**
 * A class that defines the constants that will be used widely in many pieces of the codebase
 *
 * @author Chirag (chirag-ji)
 * @since 0.0.1
 */
public abstract class JacoturaConstants {
    public static final String JACOTURA_TASK_NAME = "jacotura";
    public static final String JACOTURA_EXTENSION_NAME = "jacotura";

    public static final String PROJECT_NAME = "Gradle Jacotura plugin";
    public static final String DESCRIPTION = "Gradle plugin to convert JaCoCo coverage reports to Cobertura coverage reports";

    public static final String KEY_JACOCO_PATH = "jacotura.jacoco.path";
    public static final String KEY_COBERTURA_PATH = "jacotura.cobertura.path";
    public static final String KEY_SRC_DIRS = "jacotura.source.dirs";
    public static final String KEY_INCLUDED_FILE_NAMES = "jacotura.files.includedNames";
    public static final String KEY_BEAUTIFY = "jacotura.cobertura.beautify";
}
