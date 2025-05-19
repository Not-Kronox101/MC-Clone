package player;

import com.badlogic.gdx.math.Vector3;
import world.Block;
import world.World;
import player.Inventory;


public class Player {
    private float x, y, z;        // Position
    private float yaw, pitch;

    private static final float PLAYER_WIDTH = 0.6f;
    private static final float PLAYER_HEIGHT = 1.8f;
    private static final float PLAYER_DEPTH = 0.6f;
    private static final float STEP_SIZE = 0.1f;  // Smaller steps for more precise collision
    private final Inventory inventory;
    private final World world;

    private Block selectedBlock = Block.GRASS; // Default selected block

    public Player(World world, float startX, float startY, float startZ) {
        this.world = world;
        this.x = startX;
        this.y = startY;
        this.z = startZ;
        this.yaw = 0f;
        this.pitch = 0f;
        this.inventory = new Inventory();
    }

    public void moveWithCollision(float dx, float dy, float dz, World world) {
        // Move in smaller steps for more precise collision detection
        float remainingX = dx;
        float remainingY = dy;
        float remainingZ = dz;

        // Handle Y movement first (vertical)
        while (Math.abs(remainingY) > 0) {
            float stepY = Math.signum(remainingY) * Math.min(Math.abs(remainingY), STEP_SIZE);
            if (!wouldCollide(x, y + stepY, z)) {
                y += stepY;
            }
            remainingY -= stepY;
        }

        // Then handle X movement
        while (Math.abs(remainingX) > 0) {
            float stepX = Math.signum(remainingX) * Math.min(Math.abs(remainingX), STEP_SIZE);
            if (!wouldCollide(x + stepX, y, z)) {
                x += stepX;
            }
            remainingX -= stepX;
        }

        // Finally handle Z movement
        while (Math.abs(remainingZ) > 0) {
            float stepZ = Math.signum(remainingZ) * Math.min(Math.abs(remainingZ), STEP_SIZE);
            if (!wouldCollide(x, y, z + stepZ)) {
                z += stepZ;
            }
            remainingZ -= stepZ;
        }
    }

    public boolean wouldCollide(float newX, float newY, float newZ) {
        // Check more points around the player's bounding box
        float minX = newX - PLAYER_WIDTH/2;
        float maxX = newX + PLAYER_WIDTH/2;
        float minY = newY;
        float maxY = newY + PLAYER_HEIGHT;
        float minZ = newZ - PLAYER_DEPTH/2;
        float maxZ = newZ + PLAYER_DEPTH/2;

        // Check all corners
        if (world.isBlockSolid((int)minX, (int)minY, (int)minZ) ||
            world.isBlockSolid((int)maxX, (int)minY, (int)minZ) ||
            world.isBlockSolid((int)minX, (int)maxY, (int)minZ) ||
            world.isBlockSolid((int)maxX, (int)maxY, (int)minZ) ||
            world.isBlockSolid((int)minX, (int)minY, (int)maxZ) ||
            world.isBlockSolid((int)maxX, (int)minY, (int)maxZ) ||
            world.isBlockSolid((int)minX, (int)maxY, (int)maxZ) ||
            world.isBlockSolid((int)maxX, (int)maxY, (int)maxZ)) {
            return true;
        }

        // Check middle points of each face
        if (world.isBlockSolid((int)newX, (int)minY, (int)newZ) ||
            world.isBlockSolid((int)newX, (int)maxY, (int)newZ) ||
            world.isBlockSolid((int)minX, (int)newY, (int)newZ) ||
            world.isBlockSolid((int)maxX, (int)newY, (int)newZ) ||
            world.isBlockSolid((int)newX, (int)newY, (int)minZ) ||
            world.isBlockSolid((int)newX, (int)newY, (int)maxZ)) {
            return true;
        }

        // Check center of the player
        return world.isBlockSolid((int)newX, (int)newY, (int)newZ);
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

    public void jump() {
        // Simple jump implementation - just move up by a fixed amount
        if (!wouldCollide(x, y + 0.1f, z)) {
            y += 0.5f;
        }
    }

    public void addToInventory(Block block) {
        // For now, just set the selected block
        selectedBlock = block;
    }

    public void removeFromInventory(Block block) {
        // For now, just clear the selected block
        selectedBlock = null;
    }

    public Block getSelectedBlock() {
        return selectedBlock;
    }
}