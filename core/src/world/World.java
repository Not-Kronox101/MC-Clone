package world;

import utils.Constants;
import java.util.HashMap;
import java.util.Map;

public class World {
    public static final int WIDTH = 256;  // example value
    public static final int HEIGHT = 256; // add this line if missing
    public static final int DEPTH = 256;  // example value

    private final Map<String, Chunk> chunks = new HashMap<>();

    public World() {
        // Initialize or load chunks here if needed
    }

    // Generate chunk key from coordinates
    private String getChunkKey(int chunkX, int chunkZ) {
        return chunkX + "," + chunkZ;
    }

    // Get chunk; if missing, generate a new one
    public Chunk getChunk(int chunkX, int chunkZ) {
        String key = getChunkKey(chunkX, chunkZ);
        if (!chunks.containsKey(key)) {
            Chunk newChunk = new Chunk(chunkX, chunkZ);
            generateChunkTerrain(newChunk);
            chunks.put(key, newChunk);
        }
        return chunks.get(key);
    }

    // Simple flat terrain generation example
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

    // Access block by world coordinates
    public Block getBlock(int worldX, int worldY, int worldZ) {
        int chunkX = worldX / Constants.CHUNK_SIZE_X;
        int chunkZ = worldZ / Constants.CHUNK_SIZE_Z;

        Chunk chunk = getChunk(chunkX, chunkZ);

        int localX = worldX % Constants.CHUNK_SIZE_X;
        int localZ = worldZ % Constants.CHUNK_SIZE_Z;

        if (localX < 0) localX += Constants.CHUNK_SIZE_X;
        if (localZ < 0) localZ += Constants.CHUNK_SIZE_Z;

        return chunk.getBlock(localX, worldY, localZ);
    }

    // Set block by world coordinates
    public void setBlock(int worldX, int worldY, int worldZ, Block block) {
        int chunkX = worldX / Constants.CHUNK_SIZE_X;
        int chunkZ = worldZ / Constants.CHUNK_SIZE_Z;

        Chunk chunk = getChunk(chunkX, chunkZ);

        int localX = worldX % Constants.CHUNK_SIZE_X;
        int localZ = worldZ % Constants.CHUNK_SIZE_Z;

        if (localX < 0) localX += Constants.CHUNK_SIZE_X;
        if (localZ < 0) localZ += Constants.CHUNK_SIZE_Z;

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

    //Checks inbound
    public boolean isInBounds(int x, int y, int z) {

    int minX = 1000, minY = 1000, minZ = 1000;
    int maxX = getWidth() - 1;
    int maxY = getHeight() - 1;
    int maxZ = getDepth() - 1;
    return x >= minX && x <= maxX &&
           y >= minY && y <= maxY &&
           z >= minZ && z <= maxZ;
}
}
