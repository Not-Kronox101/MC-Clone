package player;

import world.Block;
import world.World;

public class Player {
    private float x, y, z;      // Player position in world coordinates
    private float yaw, pitch;   // Camera rotation (horizontal, vertical)

    private World world;

    public Player(World world, float startX, float startY, float startZ) {
        this.world = world;
        this.x = startX;
        this.y = startY;
        this.z = startZ;
        this.yaw = 0f;
        this.pitch = 0f;
    }

    // Basic movement with collision (very simplified)
    public void move(float dx, float dy, float dz) {
        float newX = x + dx;
        float newY = y + dy;
        float newZ = z + dz;

        if (!isBlocked(newX, y, z)) x = newX;
        if (!isBlocked(x, newY, z)) y = newY;
        if (!isBlocked(x, y, newZ)) z = newZ;
    }

    // Check if position collides with solid block
    private boolean isBlocked(float px, float py, float pz) {
        int bx = (int)Math.floor(px);
        int by = (int)Math.floor(py);
        int bz = (int)Math.floor(pz);

        Block block = world.getBlock(bx, by, bz);
        return block != Block.AIR;
    }

    // Getters and setters
    public float getX() { return x; }
    public float getY() { return y; }
    public float getZ() { return z; }
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }

    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setRotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }
}
