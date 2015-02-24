package fidelity.test;

import org.gridgain.grid.*;
import org.gridgain.grid.cache.*;

import java.util.*;

public class App {
    private static final Random RND = new Random();

    public static void main(String[] args) throws GridException {
        if (args.length > 0 && "--mqueue".equals(args[0])) {
            new MQueueSimulator().start("example-cache-client.xml");
        }
        else {
            MainFrame mainFrame = new MainFrame();

            try (Grid grid = GridGain.start("example-cache-client.xml")) {
                GridCache<Integer, Position> cache = grid.cache("partitioned");

                long totalDur = 0;
                long cnt = 0;

                while (true) {
                    int key = RND.nextInt(1000);

                    long s = System.nanoTime();

                    Position position = cache.get(key);

                    if (position == null) {
                        position = mainFrame.position();

                        cache.putx(key, position);
                    }

                    long dur = System.nanoTime() - s;

                    totalDur += dur;
                    cnt++;

                    if (cnt == 10000) {
                        double avg = totalDur / cnt;

                        System.out.println(">>> Average latency: " + avg + " nanos.");

                        totalDur = 0;
                        cnt = 0;
                    }
                }
            }
        }
    }
}
