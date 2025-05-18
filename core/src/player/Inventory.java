package player;

import world.Block;
import java.util.EnumMap;

public class Inventory {
    private final EnumMap<Block, Integer> items = new EnumMap<>(Block.class);
    private Block selectedBlock = Block.DIRT; // default selected

    public void addBlock(Block block, int amount) {
        if (block == Block.AIR) return;
        items.put(block, items.getOrDefault(block, 0) + amount);
    }

    public boolean removeBlock(Block block) {
        int count = items.getOrDefault(block, 0);
        if (count > 0) {
            items.put(block, count - 1);
            return true;
        }
        return false;
    }

    public Block getSelectedBlock() {
        return selectedBlock;
    }

    public void selectNextBlock() {
        Block[] values = Block.values();
        int index = selectedBlock.ordinal();
        do {
            index = (index + 1) % values.length;
        } while (values[index] == Block.AIR);
        selectedBlock = values[index];
    }

    public void selectPreviousBlock() {
        Block[] values = Block.values();
        int index = selectedBlock.ordinal();
        do {
            index = (index - 1 + values.length) % values.length;
        } while (values[index] == Block.AIR);
        selectedBlock = values[index];
    }

    public int getCount(Block block) {
        return items.getOrDefault(block, 0);
    }
}
