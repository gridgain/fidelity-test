package fidelity.test;

import org.gridgain.grid.*;
import org.gridgain.grid.cache.*;
import org.jdk8.backport.*;

import java.util.*;
import java.util.concurrent.*;

public class App {
    private static final int DFLT_THREAD_CNT = 8;

    private static final Random RND = new Random();

    public static void main(String[] args) throws GridException, InterruptedException {
        if (args.length > 0 && "--mqueue".equals(args[0])) {
            new MQueueSimulator().start("example-cache-client.xml");
        }
        else {
            int threadCnt = DFLT_THREAD_CNT;

            if (args.length > 0)
                threadCnt = Integer.parseInt(args[0]);

            final MainFrame mainFrame = new MainFrame();

            try (Grid grid = GridGain.start("example-cache-client.xml")) {
                final GridCache<Integer, Position> cache = grid.cache("partitioned");

                System.out.println();
                System.out.println(">>> Number of threads: " + threadCnt);

                ExecutorService exec = Executors.newFixedThreadPool(threadCnt + 1);

                final LongAdder totalDur = new LongAdder();
                final LongAdder cnt = new LongAdder();

                for (int i = 0; i < threadCnt; i++) {
                    exec.submit(new Callable<Object>() {
                        @Override public Object call() throws Exception {
                            while (!Thread.currentThread().isInterrupted()) {
                                int key = RND.nextInt(1000);

                                long s = System.nanoTime();

                                Position position = cache.get(key);

                                if (position == null) {
                                    position = mainFrame.position();

                                    cache.putx(key, position);
                                }

                                long dur = System.nanoTime() - s;

                                totalDur.add(dur);
                                cnt.increment();
                            }

                            return null;
                        }
                    });
                }

                exec.submit(new Callable<Object>() {
                    @Override public Object call() throws Exception {
                        while (!Thread.currentThread().isInterrupted()) {
                            Thread.sleep(1000);

                            long totalDur0 = totalDur.sumThenReset();
                            long cnt0 = cnt.sumThenReset();

                            System.out.println();
                            System.out.println(">>> Throughput: " + cnt0 + " ops/sec.");
                            System.out.println(">>> Average latency: " + (((double)totalDur0 / 1000000) / cnt0) + " ms.");
                        }

                        return null;
                    }
                });

                exec.shutdown();
                exec.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            }
        }
    }
}
