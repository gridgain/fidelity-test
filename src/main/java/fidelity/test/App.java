package fidelity.test;

import org.gridgain.grid.*;
import org.gridgain.grid.cache.*;

import java.util.*;

public class App {
    private static final Random RND = new Random();

    public static void main(String[] args) throws GridException {
        MainFrame mainFrame = new MainFrame();

        try (Grid grid = GridGain.start(args[0])) {
            GridCache<Integer, Position> cache = grid.cache("partitioned");

            while (true) {
                int key = RND.nextInt(1000);

                Position position = cache.get(key);

                if (position == null) {
                    position = mainFrame.position();

                    cache.putx(key, position);
                }
            }
        }
    }
}
