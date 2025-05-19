package player;

import world.Block;
import java.util.Arrays;

public class Inventory {
    public static final int HOTBAR_SIZE = 9;
    public static final int INVENTORY_ROWS = 3;
    public static final int INVENTORY_COLS = 9;
    public static final int INVENTORY_SIZE = INVENTORY_ROWS * INVENTORY_COLS;
    public static final int TOTAL_SIZE = HOTBAR_SIZE * (INVENTORY_ROWS + 1); // +1 for hotbar

    private final ItemStack[] items;
    private int selectedSlot;

    public Inventory() {
        items = new ItemStack[TOTAL_SIZE];
        selectedSlot = 0;
        // Initialize all slots with null (empty)
        Arrays.fill(items, null);
    }

    public ItemStack getStack(int slot) {
        return items[slot];
    }

    public void setStack(int slot, ItemStack stack) {
        items[slot] = stack;
    }

    public void addItem(Block block, int count) {
        // First try to stack with existing items
        for (int i = 0; i < TOTAL_SIZE; i++) {
            if (items[i] != null && items[i].getBlock() == block) {
                items[i].addCount(count);
                return;
            }
        }

        // If no stack found, find first empty slot
        for (int i = 0; i < TOTAL_SIZE; i++) {
            if (items[i] == null) {
                items[i] = new ItemStack(block, count);
                return;
            }
        }
    }

    public void removeItem(Block block, int count) {
        for (int i = 0; i < TOTAL_SIZE; i++) {
            if (items[i] != null && items[i].getBlock() == block) {
                items[i].removeCount(count);
                if (items[i].getCount() <= 0) {
                    items[i] = null;
                }
                return;
            }
        }
    }

    public Block getSelectedBlock() {
        ItemStack stack = items[selectedSlot];
        return stack != null ? stack.getBlock() : Block.AIR;
    }

    public int getSelectedSlot() {
        return selectedSlot;
    }

    public void setSelectedSlot(int slot) {
        if (slot >= 0 && slot < HOTBAR_SIZE) {
            selectedSlot = slot;
        }
    }

    public void selectNextSlot() {
        selectedSlot = (selectedSlot + 1) % HOTBAR_SIZE;
    }

    public void selectPreviousSlot() {
        selectedSlot = (selectedSlot - 1 + HOTBAR_SIZE) % HOTBAR_SIZE;
    }

    public void swapSlots(int slot1, int slot2) {
        if (slot1 >= 0 && slot1 < TOTAL_SIZE && slot2 >= 0 && slot2 < TOTAL_SIZE) {
            ItemStack temp = items[slot1];
            items[slot1] = items[slot2];
            items[slot2] = temp;
        }
    }

    public int getCount(Block block) {
        int count = 0;
        for (ItemStack stack : items) {
            if (stack != null && stack.getBlock() == block) {
                count += stack.getCount();
            }
        }
        return count;
    }
}
