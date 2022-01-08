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
package io.github.chiragji.jacotura.report.cobertura;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * A class which holds the {@code Cobertura Report} data
 *
 * @author Chirag (chirag-ji)
 * @since 0.0.1
 */
@Data
@XmlRootElement(name = "coverage")
@EqualsAndHashCode(callSuper = true)
public class CoberturaReport extends CoberturaAttributes {
    private long timestamp;
    private List<CoberturaSourceFile> sources;
    private List<CoberturaPackage> packages;

    @XmlAttribute
    public long getTimestamp() {
        return timestamp;
    }

    @XmlElementWrapper(name = "sources")
    @XmlElement(name = "source")
    public List<CoberturaSourceFile> getSources() {
        if (Objects.isNull(sources))
            this.sources = new LinkedList<>();
        return sources;
    }

    @XmlElementWrapper(name = "packages")
    @XmlElement(name = "package")
    public List<CoberturaPackage> getPackages() {
        if (Objects.isNull(packages))
            packages = new LinkedList<>();
        return packages;
    }

    public void createNewSource(@NonNull String value) {
        CoberturaSourceFile src = new CoberturaSourceFile(value);
        getSources().add(src);
    }

    public CoberturaPackage createNewPackage() {
        CoberturaPackage pkg = new CoberturaPackage();
        getPackages().add(pkg);
        return pkg;
    }
}
