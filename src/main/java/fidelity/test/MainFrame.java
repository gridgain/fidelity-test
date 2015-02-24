package fidelity.test;

import java.util.*;

public class MainFrame {
    private static final Random RND = new Random();

    public Position position() {
        return new Position(RND.nextDouble());
    }
}
