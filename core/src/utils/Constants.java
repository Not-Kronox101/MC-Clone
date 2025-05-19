package utils;

public class Constants {
    public static final int BLOCK_SIZE = 1;            // 1 unit per block (for rendering and logic)
    public static final int CHUNK_SIZE_X = 16;         // Width of a chunk in blocks
    public static final int CHUNK_SIZE_Y = 256;        // Height (Y-axis) of a chunk (Minecraft's new build height)
    public static final int CHUNK_SIZE_Z = 16;         // Depth of a chunk in blocks
    public static final int CHUNK_SIZE = 16;
    public static final int CHUNK_SIZE_BITS = 4; // log2(CHUNK_SIZE)
    public static final int CHUNK_HEIGHT = 256;
    public static final int SEA_LEVEL = 64;

    public static final float GRAVITY = -9.8f;         // Gravity for player physics
    public static final int VIEW_DISTANCE = 8;         // Number of chunks to load around the player

    // Terrain generation constants
    public static final int BEDROCK_LAYER = 0;         // Y-level of bedrock
    public static final int STONE_LAYER = 1;          // Y-level where stone starts
    public static final int DIRT_LAYER = 4;           // Y-level where dirt starts
    public static final int GRASS_LAYER = 5;          // Y-level where grass starts

    // Noise generation constants
    public static final float TERRAIN_SCALE = 0.01f;   // Scale of terrain features
    public static final float MOUNTAIN_SCALE = 0.005f; // Scale of mountain features
    public static final float TREE_SCALE = 0.1f;      // Scale of tree placement

    private Constants() {
        // Prevent instantiation
    }
}
