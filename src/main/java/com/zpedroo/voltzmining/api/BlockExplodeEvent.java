package com.zpedroo.voltzmining.api;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class BlockExplodeEvent extends Event implements Cancellable {

    private final Player player;
    private final Block block;
    private final ItemStack item;
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean cancelled = false;

    public BlockExplodeEvent(Player player, Block block, ItemStack item) {
        this.player = player;
        this.block = block;
        this.item = item;
    }

    public Player getPlayer() {
        return player;
    }

    public Block getBlock() {
        return block;
    }

    public ItemStack getItem() {
        return item;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}