package com.zpedroo.voltzmining.objects.player;

import java.math.BigInteger;
import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    private BigInteger availableBlocks;
    private BigInteger brokenBlocks;
    private boolean update;

    public PlayerData(UUID uuid, BigInteger availableBlocks, BigInteger brokenBlocks) {
        this.uuid = uuid;
        this.availableBlocks = availableBlocks;
        this.brokenBlocks = brokenBlocks;
        this.update = false;
    }

    public UUID getUUID() {
        return uuid;
    }

    public BigInteger getAvailableBlocks() {
        return availableBlocks;
    }

    public BigInteger getBrokenBlocks() {
        return brokenBlocks;
    }

    public boolean isQueueUpdate() {
        return update;
    }

    public void addBlocks(BigInteger amount) {
        this.availableBlocks = availableBlocks.add(amount);
        this.brokenBlocks = brokenBlocks.add(amount);
        this.update = true;
    }

    public void removeAvailableBlocks(BigInteger amount) {
        this.availableBlocks = availableBlocks.subtract(amount);
        this.update = true;
    }

    public void setQueueUpdate(boolean update) {
        this.update = update;
    }
}