package io.github.chiragji.jacotura.maven;

import io.github.chiragji.jacotura.JacoturaConfig;
import io.github.chiragji.jacotura.JacoturaConversion;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class JacoturaMojo extends AbstractMojo {

    private File jacocoReport;
    private File coberturaReport;
    private List<String> sourceDirs;
    private List<String> includedFileNames;
    private boolean beautify;
    private boolean usePlainFileNames;
    private boolean skip;

    @Override
    public void execute() throws MojoExecutionException {
        if (skip) {
            getLog().info("Skipping jacotura conversion");
            return;
        }
        if (jacocoReport == null) {
            throw new MojoExecutionException("Missing jacocoReport configuration");
        }
        if (coberturaReport == null) {
            throw new MojoExecutionException("Missing coberturaReport configuration");
        }
        if (!jacocoReport.exists()) {
            getLog().warn("Jacoco report does not exist. Skipping conversion");
            return;
        }

        JacoturaConfig config = new JacoturaConfig();
        config.setJacoturaFile(jacocoReport);
        config.setCoberturaFile(coberturaReport);
        config.setSrcDirs(toSet(sourceDirs));
        config.setIncludeFileNames(toSet(includedFileNames));
        config.setBeautify(beautify);
        config.setUsePlainFileName(usePlainFileNames);

        try {
            new JacoturaConversion(config).start();
            getLog().info("Converted JaCoCo report to Cobertura report");
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to convert JaCoCo report to Cobertura report", e);
        }
    }

    public void setJacocoReport(File jacocoReport) {
        this.jacocoReport = jacocoReport;
    }

    public void setCoberturaReport(File coberturaReport) {
        this.coberturaReport = coberturaReport;
    }

    public void setSourceDirs(String[] sourceDirs) {
        this.sourceDirs = sourceDirs == null ? Collections.emptyList() : Arrays.asList(sourceDirs);
    }

    public void setIncludedFileNames(String[] includedFileNames) {
        this.includedFileNames = includedFileNames == null ? Collections.emptyList() : Arrays.asList(includedFileNames);
    }

    public void setBeautify(boolean beautify) {
        this.beautify = beautify;
    }

    public void setUsePlainFileNames(boolean usePlainFileNames) {
        this.usePlainFileNames = usePlainFileNames;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    private Set<String> toSet(List<String> values) {
        if (values == null || values.isEmpty()) {
            return Collections.emptySet();
        }
        return new LinkedHashSet<>(values);
    }
}
