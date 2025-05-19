package world;

import utils.Constants;
import java.util.Map;
import java.util.HashMap;

public class World {
    private final Map<String, Chunk> chunks;
    private static final int BASE_LEVEL = 10;  // Start terrain higher up
    private static final int GRASS_LAYER = BASE_LEVEL + 4;  // Top layer
    private static final int DIRT_LAYERS = 2;  // Middle layers
    private static final int STONE_LAYERS = 2; // Bottom layers
    private static final int WORLD_SIZE = 2;   // Number of chunks in each direction

    public World() {
        chunks = new HashMap<>();
        generateTerrain();
    }

    private void generateTerrain() {
        // Generate a simple flat terrain
        for (int x = -WORLD_SIZE; x < WORLD_SIZE; x++) {
            for (int z = -WORLD_SIZE; z < WORLD_SIZE; z++) {
                Chunk chunk = new Chunk(x, z);
                
                // Generate flat terrain
                for (int cx = 0; cx < Constants.CHUNK_SIZE; cx++) {
                    for (int cz = 0; cz < Constants.CHUNK_SIZE; cz++) {
                        // Add grass layer (top)
                        chunk.setBlock(cx, GRASS_LAYER, cz, Block.GRASS);
                        
                        // Add dirt layers (middle)
                        for (int y = 1; y <= DIRT_LAYERS; y++) {
                            chunk.setBlock(cx, GRASS_LAYER - y, cz, Block.DIRT);
                        }
                        
                        // Add stone layers (bottom)
                        for (int y = DIRT_LAYERS + 1; y <= DIRT_LAYERS + STONE_LAYERS; y++) {
                            chunk.setBlock(cx, GRASS_LAYER - y, cz, Block.STONE);
                        }
                    }
                }
                
                chunks.put(getChunkKey(x, z), chunk);
            }
        }
    }

    private String getChunkKey(int x, int z) {
        return x + "," + z;
    }

    public Block getBlock(int x, int y, int z) {
        int chunkX = x >> Constants.CHUNK_SIZE_BITS;
        int chunkZ = z >> Constants.CHUNK_SIZE_BITS;
        Chunk chunk = chunks.get(getChunkKey(chunkX, chunkZ));
        
        if (chunk == null) {
            return Block.AIR;
        }
        
        int localX = x & (Constants.CHUNK_SIZE - 1);
        int localZ = z & (Constants.CHUNK_SIZE - 1);
        return chunk.getBlock(localX, y, localZ);
    }

    public void setBlock(int x, int y, int z, Block block) {
        int chunkX = x >> Constants.CHUNK_SIZE_BITS;
        int chunkZ = z >> Constants.CHUNK_SIZE_BITS;
        Chunk chunk = chunks.get(getChunkKey(chunkX, chunkZ));
        
        if (chunk != null) {
            int localX = x & (Constants.CHUNK_SIZE - 1);
            int localZ = z & (Constants.CHUNK_SIZE - 1);
            chunk.setBlock(localX, y, localZ, block);
        }
    }

    public boolean isInBounds(int x, int y, int z) {
        return y >= 0 && y < Constants.CHUNK_HEIGHT;
    }

    public boolean isBlockSolid(int x, int y, int z) {
        Block block = getBlock(x, y, z);
        return block != Block.AIR;
    }
}

