package utils;

import com.badlogic.gdx.math.Vector3;
import player.Player;
import world.Block;
import world.World;

public class RayCaster {
    public static final float MAX_DISTANCE = 6f;
    public static final float STEP = 0.1f;

    public static class RaycastResult {
        public int x, y, z;
        public int faceX, faceY, faceZ; // Face normal (used for placing blocks)

        public RaycastResult(int x, int y, int z, int faceX, int faceY, int faceZ) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.faceX = faceX;
            this.faceY = faceY;
            this.faceZ = faceZ;
        }
    }

    public static RaycastResult cast(World world, Player player) {
        Vector3 origin = new Vector3(player.getX(), player.getY(), player.getZ());
        Vector3 direction = new Vector3().setFromSpherical(player.getYaw(), player.getPitch()).nor();

        int lastX = (int) origin.x;
        int lastY = (int) origin.y;
        int lastZ = (int) origin.z;

        for (float d = 0; d < MAX_DISTANCE; d += STEP) {
            Vector3 pos = origin.cpy().mulAdd(direction, d);

            int x = (int) Math.floor(pos.x);
            int y = (int) Math.floor(pos.y);
            int z = (int) Math.floor(pos.z);

            if (!world.isInBounds(x, y, z)) break;

            Block block = world.getBlock(x, y, z);
            if (block != Block.AIR) {
                return new RaycastResult(x, y, z, x - lastX, y - lastY, z - lastZ);
            }

            lastX = x;
            lastY = y;
            lastZ = z;
        }

        return null;
    }
}
