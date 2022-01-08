package io.github.chiragji.jacotura;

import java.util.UUID;

/**
 * A sample class to generate some coverage
 *
 * @author Chirag
 */
public class Jacotura {

    String getUUID() {
        return UUID.randomUUID().toString();
    }

    boolean cover(boolean c1, boolean c2) {
        if (c1 && !c2)
            System.out.println("Condition 1 is true & Condition 2 is false");
        if (!c1 && c2)
            System.out.println("Condition 1 is false & Condition 2 is true");
        if (c1 && c2)
            System.out.println("Both conditions are ture");
        if (!c1 && !c2)
            System.out.println("Both conditions are false");
        return c1 && c2;
    }
}
