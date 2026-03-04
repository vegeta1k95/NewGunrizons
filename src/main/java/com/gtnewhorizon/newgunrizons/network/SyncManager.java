package com.gtnewhorizon.newgunrizons.network;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gtnewhorizon.newgunrizons.state.ManagedState;
import com.gtnewhorizon.newgunrizons.weapon.PlayerItemInstance;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class SyncManager<S extends ManagedState<S>> {

    private static final Logger logger = LogManager.getLogger(SyncManager.class);
    private final SimpleNetworkWrapper channel;
    private final Map<PlayerItemInstance<?>, Long> watchables = new LinkedHashMap<>();
    private final long syncTimeout = 10000L;

    public SyncManager(SimpleNetworkWrapper channel) {
        this.channel = channel;
    }

    public void watch(PlayerItemInstance<?> watchableInstance) {
        this.watchables.put(watchableInstance, watchableInstance.getUpdateId());
    }

    public void unwatch(PlayerItemInstance<?> watchableInstance) {
        this.watchables.remove(watchableInstance);
    }

    public void run() {
        List<PlayerItemInstance<?>> instancesToUpdate = (List) this.watchables.entrySet()
            .stream()
            .filter((e) -> {
                return e.getKey()
                    .getUpdateId() != e.getValue()
                    && e.getKey()
                        .getSyncStartTimestamp() + this.syncTimeout < System.currentTimeMillis();
            })
            .map((e) -> { return (PlayerItemInstance) e.getKey(); })
            .collect(Collectors.toList());
        instancesToUpdate.forEach(this::sync);
    }

    private void sync(PlayerItemInstance<?> watchable) {
        logger.debug(
            "Syncing {} in state {} with update id {} to server",
            watchable,
            watchable.getState(),
            watchable.getUpdateId());
        long updateId = watchable.getUpdateId();
        watchable.setSyncStartTimestamp(System.currentTimeMillis());
        this.channel.sendToServer(new SyncMessage(watchable));
        this.watchables.put(watchable, updateId);
        watchable.setSyncStartTimestamp(0L);
        logger.debug("Sent sync for {} with update id {}", watchable, updateId);
    }
}
