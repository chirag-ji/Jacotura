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

import lombok.Data;
import lombok.NonNull;

import java.io.File;
import java.util.Set;

/**
 * A configuration object which holds the data that needed to run the report conversion
 *
 * @author Chirag (chirag-ji)
 * @since 0.0.1
 */
@Data
public class JacoturaConfig {
    private File jacoturaFile;
    private File coberturaFile;
    private Set<String> srcDirs;
    private Set<String> includeFileNames;
    private boolean beautify;
    private boolean usePlainFileName;

    public void setJacoturaReport(@NonNull String jacoturaFilePath) {
        this.jacoturaFile = new File(jacoturaFilePath);
    }

    public void setCoberturaReport(@NonNull String jacoturaFilePath) {
        this.coberturaFile = new File(jacoturaFilePath);
    }
}
