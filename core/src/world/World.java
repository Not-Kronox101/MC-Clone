package world;

import utils.Constants;
import java.util.HashMap;
import java.util.Map;

public class World {
    public static final int WIDTH = 16;
    public static final int HEIGHT = 16;
    public static final int DEPTH = 16;

    private final Map<String, Chunk> chunks = new HashMap<>();

    public World() {
        // Initialize or load chunks here if needed
    }

    private String getChunkKey(int chunkX, int chunkZ) {
        return chunkX + "," + chunkZ;
    }

    public Chunk getChunk(int chunkX, int chunkZ) {
        String key = getChunkKey(chunkX, chunkZ);
        if (!chunks.containsKey(key)) {
            Chunk newChunk = new Chunk(chunkX, chunkZ);
            generateChunkTerrain(newChunk);
            chunks.put(key, newChunk);
        }
        return chunks.get(key);
    }

    private void generateChunkTerrain(Chunk chunk) {
        for (int x = 0; x < Constants.CHUNK_SIZE_X; x++) {
            for (int z = 0; z < Constants.CHUNK_SIZE_Z; z++) {
                for (int y = 0; y < Constants.CHUNK_SIZE_Y; y++) {
                    if (y == 0) {
                        chunk.setBlock(x, y, z, Block.STONE);
                    } else if (y < 5) {
                        chunk.setBlock(x, y, z, Block.DIRT);
                    } else if (y == 5) {
                        chunk.setBlock(x, y, z, Block.GRASS);
                    } else {
                        chunk.setBlock(x, y, z, Block.AIR);
                    }
                }
            }
        }
    }

    public Block getBlock(int worldX, int worldY, int worldZ) {
        if (!isInBounds(worldX, worldY, worldZ)) {
            return Block.AIR;
        }

        int chunkX = (int) Math.floor((double) worldX / Constants.CHUNK_SIZE_X);
        int chunkZ = (int) Math.floor((double) worldZ / Constants.CHUNK_SIZE_Z);

        Chunk chunk = getChunk(chunkX, chunkZ);

        int localX = worldX - chunkX * Constants.CHUNK_SIZE_X;
        int localZ = worldZ - chunkZ * Constants.CHUNK_SIZE_Z;

        return chunk.getBlock(localX, worldY, localZ);
    }

    public void setBlock(int worldX, int worldY, int worldZ, Block block) {
        if (!isInBounds(worldX, worldY, worldZ)) {
            return;
        }

        int chunkX = (int) Math.floor((double) worldX / Constants.CHUNK_SIZE_X);
        int chunkZ = (int) Math.floor((double) worldZ / Constants.CHUNK_SIZE_Z);

        Chunk chunk = getChunk(chunkX, chunkZ);

        int localX = worldX - chunkX * Constants.CHUNK_SIZE_X;
        int localZ = worldZ - chunkZ * Constants.CHUNK_SIZE_Z;

        chunk.setBlock(localX, worldY, localZ, block);
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

    public int getDepth() {
        return DEPTH;
    }

    public boolean isInBounds(int x, int y, int z) {
        return x >= 0 && x < getWidth() &&
               y >= 0 && y < getHeight() &&
               z >= 0 && z < getDepth();
    }
}

