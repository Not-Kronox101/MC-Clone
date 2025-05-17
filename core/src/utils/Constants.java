package utils;

public class Constants {
    public static final int BLOCK_SIZE = 1;            // 1 unit per block (for rendering and logic)
    public static final int CHUNK_SIZE_X = 16;         // Width of a chunk in blocks
    public static final int CHUNK_SIZE_Y = 128;        // Height (Y-axis) of a chunk (like Minecraft’s build height)
    public static final int CHUNK_SIZE_Z = 16;         // Depth of a chunk in blocks

    public static final float GRAVITY = -9.8f;         // Gravity for player physics
    public static final int VIEW_DISTANCE = 4;         // Number of chunks to load around the player

    private Constants() {
        // Prevent instantiation
    }
}
