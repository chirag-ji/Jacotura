package io.github.chiragji.jacotura;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNull;

@Testable
class JacoturaConversionTest {

    @Test
    void testConversion() {
        Exception ex = null;
        try {
            JacoturaConfig config = getConfig();
            JacoturaConversion jacoturaConversion = new JacoturaConversion(config);
            long start = System.currentTimeMillis();
            jacoturaConversion.start();
            printCompletionTime(start);
        } catch (Exception e) {
            ex = e;
        }
        if (Objects.nonNull(ex))
            ex.printStackTrace();
        assertNull(ex, "Assert no exceptions are thrown");
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