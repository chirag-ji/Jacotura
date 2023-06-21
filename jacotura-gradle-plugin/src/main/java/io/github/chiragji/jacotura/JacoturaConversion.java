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

import io.github.chiragji.jacotura.report.cobertura.*;
import io.github.chiragji.jacotura.report.jacoco.*;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.gradle.internal.impldep.org.jetbrains.annotations.Nullable;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A final task which converts the {@code Jacoco} report to {@code Cobertura} report
 *
 * @author Chirag (chirag-ji)
 * @since 0.0.1
 */
@RequiredArgsConstructor
public class JacoturaConversion {
    private final JacoturaConfig jacoturaConfig;
    private final Counter emptyCounter = new Counter();

    @SneakyThrows
    public void start() {
        JacocoReport jacocoReport = getJacocoReport();
        CoberturaReport coberturaReport = new CoberturaReport();
        if (Objects.nonNull(jacocoReport.getSessionInfo())) {
            SessionInfo sessionInfo = jacocoReport.getSessionInfo();
            coberturaReport.setTimestamp(sessionInfo.getStart() / 1000);
        }
        if (Objects.nonNull(jacoturaConfig.getSrcDirs()))
            jacoturaConfig.getSrcDirs().forEach(coberturaReport::createNewSource);
        computeRates(coberturaReport, jacocoReport);
        buildCoverage(jacocoReport, coberturaReport);
        writeCoberturaReport(coberturaReport);
    }

    private void buildCoverage(JacocoReport jacocoReport, CoberturaReport coberturaReport) {
        jacocoReport.getPackages().forEach(jPkg -> {
            CoberturaPackage cPkg = coberturaReport.createNewPackage();
            computeRates(cPkg, jPkg);
            cPkg.setName(getCoberturaFriendlyName(jPkg.getName()));
            jPkg.getClasses().forEach(jCls -> {
                Set<String> includedNames = jacoturaConfig.getIncludeFileNames();
                // check user defined only files to convert. if defined check for exact name
                if (!Util.isEmptyCollection(includedNames) && !includedNames.contains(jCls.getSourceFileName())) {
                    return;
                }
                CoberturaClass cCls = cPkg.createNewClass();
                cCls.setName(getCoberturaFriendlyName(jCls.getName()));
                cCls.setFileName(getClassName(jPkg, jCls));
                computeRates(cCls, jCls);
                List<JacocoLine> lines = getSourceFileLines(jPkg, jCls.getSourceFileName());
                jCls.getMethods().forEach(jMth -> {
                    CoberturaMethod cMth = cCls.createNewMethod();
                    cMth.setName(getCoberturaFriendlyName(jMth.getName()));
                    cMth.setSignature(jMth.getDescription());
                    computeRates(cMth, jMth);
                    List<JacocoLine> methodLines = getMethodLines(jCls.getMethods(), jMth, lines);
                    methodLines.forEach(jLine -> {
                        CoberturaLine cLine = cMth.createNewLine();
                        buildLineCoverage(cLine, jLine);
                    });
                    lines.forEach(jLine -> {
                        CoberturaLine cLine = cCls.createNewLine();
                        buildLineCoverage(cLine, jLine);
                    });
                });
            });
        });
    }

    private String getClassName(JacocoPackage pkg, JacocoClass cls) {
        if (jacoturaConfig.isUsePlainFileName()) {
            return cls.getSourceFileName();
        } else {
            return String.format("%s/%s", pkg.getName(), cls.getSourceFileName());
        }
    }

    private void buildLineCoverage(@NonNull CoberturaLine cLine, @NonNull JacocoLine jLine) {
        int nr = jLine.getLineNumber();
        int mb = jLine.getMissedBranches();
        int cb = jLine.getCoveredBranches();
        int ci = jLine.getCoveredInstructions();

        int hits = ci > 0 ? 1 : 0;
        int mcb = mb + cb;

        if (mcb > 0) {
            int perc = (100 * cb) / mcb;
            cLine.setBranch(true);
            cLine.setNumber(nr);
            cLine.setHits(hits);
            cLine.setConditionCoverage(perc, cb, mcb);
            CoberturaCondition condition = cLine.createNewCondition();
            condition.setNumber(0);
            condition.setType("jump");
            condition.setCoverage(String.format("%d%%", perc));
        } else {
            cLine.setBranch(false);
            cLine.setHits(hits);
            cLine.setNumber(nr);
        }
    }

    private List<JacocoLine> getSourceFileLines(JacocoPackage pkg, String fileName) {
        return pkg.getSourceFiles().stream().filter(f -> f.getName().equals(fileName)).findAny()
                .map(JacocoSourceFile::getLines).orElse(Collections.emptyList());
    }

    private List<JacocoLine> getMethodLines(List<JacocoMethod> methods, JacocoMethod currentMethod, List<JacocoLine> lines) {
        int startLine = currentMethod.getLine();
        if (startLine <= 0) return Collections.emptyList();
        JacocoMethod highestMethod = methods.stream().filter(m -> m.getLine() > startLine).findAny().orElse(null);
        int endLine = Objects.isNull(highestMethod) ? Integer.MAX_VALUE : highestMethod.getLine();
        return lines.stream().filter(l -> startLine <= l.getLineNumber() && l.getLineNumber() < endLine)
                .collect(Collectors.toList());
    }

    private String getCoberturaFriendlyName(@NonNull String name) {
        return Util.isEmptyString(name) ? "" : name.replace('/', '.');
    }

    private void computeRates(@NonNull CoberturaAttributes attributes, @NonNull JacocoAttributes jacocoAttributes) {
        attributes.setBranchRate(computeFraction(jacocoAttributes, CounterType.BRANCH));
        attributes.setLineRate(computeFraction(jacocoAttributes, CounterType.LINE));
        attributes.setComplexity(computeSum(jacocoAttributes, CounterType.COMPLEXITY));
    }

    private double computeSum(@NonNull JacocoAttributes attributes, @NonNull CounterType counterType) {
        Counter counter = getCounter(attributes.getCounters(), counterType);
        return (counter.getCovered() + counter.getMissed()) + 0D;
    }

    private double computeFraction(@NonNull JacocoAttributes attributes, @NonNull CounterType counterType) {
        Counter counter = getCounter(attributes.getCounters(), counterType);
        return counter.getCovered() / computeSum(attributes, counterType);
    }

    private Counter getCounter(@Nullable Set<Counter> counters, @NonNull CounterType counterType) {
        if (Util.isEmptyCollection(counters)) return emptyCounter;
        return counters.stream().filter(c -> c.getCounterType() == counterType).findAny().orElse(emptyCounter);
    }

    private JacocoReport getJacocoReport() throws JAXBException, IOException, SAXException, ParserConfigurationException {
        JAXBContext ctx = JAXBContext.newInstance(JacocoReport.class);
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setFeature("http://xml.org/sax/features/validation", false);
        spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        XMLReader xmlReader = spf.newSAXParser().getXMLReader();
        try (FileReader reader = new FileReader(jacoturaConfig.getJacoturaFile())) {
            InputSource inputSource = new InputSource(reader);
            SAXSource source = new SAXSource(xmlReader, inputSource);
            return (JacocoReport) ctx.createUnmarshaller().unmarshal(source);
        }
    }

    private void writeCoberturaReport(CoberturaReport coberturaReport) throws JAXBException {
        File coberturaFile = jacoturaConfig.getCoberturaFile();
        coberturaFile.getParentFile().mkdirs();
        JAXBContext ctx = JAXBContext.newInstance(CoberturaReport.class);
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, jacoturaConfig.isBeautify());
        marshaller.marshal(coberturaReport, jacoturaConfig.getCoberturaFile());
    }
}
