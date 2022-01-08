package io.github.chiragji.jacotura;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

import static org.junit.jupiter.api.Assertions.assertFalse;

@Testable
class JacoturaTest {

    Jacotura jacotura = new Jacotura();

    @Test
    void test() {
        String res = jacotura.getUUID();
        Assertions.assertNotNull(res, "Assert result is not null");
    }

    @Test
    void testCondition_1_true_2_false() {
        boolean res = jacotura.cover(true, false);
        assertFalse(res);
    }

    @Test
    void testCondition_1_false_2_true() {
        boolean res = jacotura.cover(false, true);
        assertFalse(res);
    }

    @Test
    void testCondition_1_false_2_false() {
        boolean res = jacotura.cover(false, false);
        assertFalse(res);
    }
}