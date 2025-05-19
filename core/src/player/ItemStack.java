package player;

import world.Block;

public class ItemStack {
    private Block block;
    private int count;
    public static final int MAX_STACK_SIZE = 64;

    public ItemStack(Block block, int count) {
        this.block = block;
        this.count = count;
    }

    public ItemStack(Block block) {
        this(block, 1);
    }

    public Block getBlock() {
        return block;
    }

    public int getCount() {
        return count;
    }

    public void addCount(int amount) {
        count += amount;
    }

    public void removeCount(int amount) {
        count = Math.max(0, count - amount);
    }

    public boolean isEmpty() {
        return count <= 0;
    }

    public boolean isFull() {
        return count >= MAX_STACK_SIZE;
    }

    public int getRemainingSpace() {
        return MAX_STACK_SIZE - count;
    }
} 