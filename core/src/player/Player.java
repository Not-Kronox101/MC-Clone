package player;

import world.Block;
import world.World;
import player.Inventory;


public class Player {
    private float x, y, z;        // Position
    private float yaw, pitch;

    private static final float WIDTH = 0.6f;
    private static final float HEIGHT = 1.8f;
    private final Inventory inventory = new Inventory();
    private final World world;

    public Player(World world, float startX, float startY, float startZ) {
        this.world = world;
        this.x = startX;
        this.y = startY;
        this.z = startZ;
        this.yaw = 0f;
        this.pitch = 0f;
    }

    // Move with collision (Minecraft-style, axis-aligned)
    public void moveWithCollision(float dx, float dy, float dz, World world) {
        float radius = WIDTH / 2;

        // X axis
        if (!collidesAt(x + dx, y, z, radius)) {
            x += dx;
        }
        // Y axis
        if (!collidesAt(x, y + dy, z, radius)) {
            y += dy;
        }
        // Z axis
        if (!collidesAt(x, y, z + dz, radius)) {
            z += dz;
        }

        // Prevent falling below the world
        if (y < 0) y = 0;
    }

    private boolean collidesAt(float px, float py, float pz, float radius) {
        int minX = (int) Math.floor(px - radius);
        int maxX = (int) Math.floor(px + radius);
        int minY = (int) Math.floor(py);
        int maxY = (int) Math.floor(py + HEIGHT - 0.1f); // Y fix for first layer
        int minZ = (int) Math.floor(pz - radius);
        int maxZ = (int) Math.floor(pz + radius);

        for (int xi = minX; xi <= maxX; xi++) {
            for (int yi = minY; yi <= maxY; yi++) {
                for (int zi = minZ; zi <= maxZ; zi++) {
                    if (world.getBlock(xi, yi, zi) != Block.AIR) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Getters
    public float getX() { return x; }
    public float getY() { return y; }
    public float getZ() { return z; }
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
    public Inventory getInventory() { return inventory; }

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