package world;

import utils.Constants;

public class Chunk {
    private final Block[][][] blocks;
    private final int chunkX;
    private final int chunkZ;

    public Chunk(int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.blocks = new Block[Constants.CHUNK_SIZE][Constants.CHUNK_HEIGHT][Constants.CHUNK_SIZE];
        
        // Initialize all blocks to AIR
        for (int x = 0; x < Constants.CHUNK_SIZE; x++) {
            for (int y = 0; y < Constants.CHUNK_HEIGHT; y++) {
                for (int z = 0; z < Constants.CHUNK_SIZE; z++) {
                    blocks[x][y][z] = Block.AIR;
                }
            }
        }
    }

    public Block getBlock(int x, int y, int z) {
        if (x < 0 || x >= Constants.CHUNK_SIZE || 
            y < 0 || y >= Constants.CHUNK_HEIGHT || 
            z < 0 || z >= Constants.CHUNK_SIZE) {
            return Block.AIR;
        }
        return blocks[x][y][z];
    }

    public void setBlock(int x, int y, int z, Block block) {
        if (x < 0 || x >= Constants.CHUNK_SIZE || 
            y < 0 || y >= Constants.CHUNK_HEIGHT || 
            z < 0 || z >= Constants.CHUNK_SIZE) {
            return;
        }
        blocks[x][y][z] = block;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }
}
