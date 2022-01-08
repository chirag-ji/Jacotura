package io.github.chiragji.jacotura;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

@Testable
class JacoturaConversionTest {

    @Test
    void testConversion() {
        JacoturaConfig config = getConfig();
        JacoturaConversion jacoturaConversion = new JacoturaConversion(config);
        long start = System.currentTimeMillis();
        jacoturaConversion.start();
        printCompletionTime(start);
    }

    private JacoturaConfig getConfig() {
        JacoturaConfig config = new JacoturaConfig();
        config.setJacoturaReport("src/test/resources/jacoco.xml");
        config.setCoberturaReport("src/test/resources/cobertura.xml");
        return config;
    }

    private void printCompletionTime(long startMills) {
        long currentMills = System.currentTimeMillis();
        System.out.println("JacoturaConversionTest.printCompletionTime: complete in " + (currentMills - startMills) + "ms");
    }
}