package world;

import utils.Constants;

public class Chunk {
    private final int chunkX, chunkZ; // chunk position in the world grid
    private final Block[][][] blocks;

    public Chunk(int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        blocks = new Block[Constants.CHUNK_SIZE_X][Constants.CHUNK_SIZE_Y][Constants.CHUNK_SIZE_Z];

        // Initialize all blocks as AIR by default
        for (int x = 0; x < Constants.CHUNK_SIZE_X; x++) {
            for (int y = 0; y < Constants.CHUNK_SIZE_Y; y++) {
                for (int z = 0; z < Constants.CHUNK_SIZE_Z; z++) {
                    blocks[x][y][z] = Block.AIR;
                }
            }
        }
    }

    public Block getBlock(int x, int y, int z) {
        if (isValidPosition(x, y, z)) {
            return blocks[x][y][z];
        }
        return Block.AIR; // Outside chunk bounds returns AIR by default
    }

    public void setBlock(int x, int y, int z, Block block) {
        if (isValidPosition(x, y, z)) {
            blocks[x][y][z] = block;
        }
    }

    private boolean isValidPosition(int x, int y, int z) {
        return x >= 0 && x < Constants.CHUNK_SIZE_X &&
               y >= 0 && y < Constants.CHUNK_SIZE_Y &&
               z >= 0 && z < Constants.CHUNK_SIZE_Z;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }
}
