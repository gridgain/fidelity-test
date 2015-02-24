package fidelity.test;

import org.gridgain.grid.*;
import org.gridgain.grid.cache.*;
import org.gridgain.grid.lang.*;
import org.gridgain.grid.messaging.*;
import org.gridgain.grid.resources.*;

import java.util.*;

public class MQueueSimulator {
    private static final Random RND = new Random();

    private static final String TOPIC = "CACHE_INVALIDATE";

    public void start(String cfgPath) throws GridException {
        try (Grid grid = GridGain.start(cfgPath)) {
            GridMessaging messaging = grid.forRemotes().message();

            messaging.remoteListen(TOPIC, new Listener()).get();

            while (true)
                messaging.sendOrdered(TOPIC, RND.nextInt(1000), 0);
        }
    }

    private static class Listener implements GridBiPredicate<UUID, Integer> {
        @GridInstanceResource
        private Grid grid;

        @Override public boolean apply(UUID uuid, Integer key) {
            GridCache<Integer, Position> cache = grid.cache("partitioned");

            cache.clear(key);

            return true;
        }
    }
}
