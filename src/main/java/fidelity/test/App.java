package fidelity.test;

import org.gridgain.grid.*;
import org.gridgain.grid.cache.*;

import java.util.*;

public class App {
    private static final Random RND = new Random();

    public static void main(String[] args) throws GridException, InterruptedException {
        MainFrame mainFrame = new MainFrame();

        if (args.length > 0 && "--mqueue".equals(args[0])) {
            new MQueueSimulator().start("example-cache-client.xml");
        }
        else {
            try (Grid grid = GridGain.start("example-cache-client.xml")) {
                GridCache<Integer, Position> cache = grid.cache("partitioned");

                while (true) {
                    Thread.sleep(100);

                    int key = RND.nextInt(1000);

                    Position position = cache.get(key);

                    if (position == null) {
                        position = mainFrame.position();

                        cache.putx(key, position);

                        System.out.println(">>> Got from MainFrame [key=" + key + ", position=" + position + ']');
                    }
                    else
                        System.out.println(">>> Got from Cache [key=" + key + ", position=" + position + ']');
                }
            }
        }
    }
}
