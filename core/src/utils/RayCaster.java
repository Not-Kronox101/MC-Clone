package utils;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import player.Player;
import world.Block;
import world.World;

public class RayCaster {
    public static final float MAX_DISTANCE = 10f;
    private static final float EPSILON = 0.0001f;

    public static class RaycastResult {
        public int x, y, z;
        public int faceX, faceY, faceZ; // Face normal (used for placing blocks)
        public float distance; // Distance to the hit point
        public Vector3 hitPoint; // Exact point where the ray hit the block

        public RaycastResult(int x, int y, int z, int faceX, int faceY, int faceZ, float distance, Vector3 hitPoint) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.faceX = faceX;
            this.faceY = faceY;
            this.faceZ = faceZ;
            this.distance = distance;
            this.hitPoint = hitPoint;
        }
    }

    public static RaycastResult cast(World world, Player player) {
        // Start ray from player's eye position
        Vector3 origin = new Vector3(player.getX(), player.getY() + 1.6f, player.getZ());
        
        // Calculate direction vector from yaw and pitch (Minecraft style)
        float yawRad = (float) Math.toRadians(player.getYaw());
        float pitchRad = (float) Math.toRadians(player.getPitch());
        
        Vector3 direction = new Vector3(
            (float) (Math.cos(pitchRad) * Math.sin(yawRad)),
            (float) Math.sin(pitchRad),
            (float) (Math.cos(pitchRad) * Math.cos(yawRad))
        ).nor();
        
        Ray ray = new Ray(origin, direction);

        // Variables to track the closest hit
        float closestDistance = MAX_DISTANCE;
        RaycastResult closestHit = null;

        // Check blocks in a reasonable range
        int checkRadius = (int) Math.ceil(MAX_DISTANCE);
        int playerX = (int) Math.floor(player.getX());
        int playerY = (int) Math.floor(player.getY());
        int playerZ = (int) Math.floor(player.getZ());

        // First check blocks in a smaller radius for better performance
        for (int x = playerX - checkRadius; x <= playerX + checkRadius; x++) {
            for (int y = playerY - checkRadius; y <= playerY + checkRadius; y++) {
                for (int z = playerZ - checkRadius; z <= playerZ + checkRadius; z++) {
                    if (!world.isInBounds(x, y, z)) continue;

                    Block block = world.getBlock(x, y, z);
                    if (block == Block.AIR || !block.isSolid()) continue;

                    // Skip blocks that are completely surrounded by solid blocks
                    if (!hasExposedFace(world, x, y, z)) continue;

                    // Create bounding box for the block
                    Vector3 min = new Vector3(x, y, z);
                    Vector3 max = new Vector3(x + 1, y + 1, z + 1);

                    // Check ray-box intersection
                    Vector3 hitPoint = new Vector3();
                    float distance = intersectRayBox(ray, min, max, hitPoint);
                    
                    if (distance > 0 && distance < closestDistance) {
                        // Determine which face was hit
                        int faceX = 0, faceY = 0, faceZ = 0;
                        
                        // Calculate the distance from hit point to each face
                        float dx = Math.min(Math.abs(hitPoint.x - x), Math.abs(hitPoint.x - (x + 1)));
                        float dy = Math.min(Math.abs(hitPoint.y - y), Math.abs(hitPoint.y - (y + 1)));
                        float dz = Math.min(Math.abs(hitPoint.z - z), Math.abs(hitPoint.z - (z + 1)));
                        
                        // Find the closest face
                        if (dx < dy && dx < dz) {
                            faceX = hitPoint.x < x + 0.5f ? -1 : 1;
                        } else if (dy < dz) {
                            faceY = hitPoint.y < y + 0.5f ? -1 : 1;
                        } else {
                            faceZ = hitPoint.z < z + 0.5f ? -1 : 1;
                        }

                        closestDistance = distance;
                        closestHit = new RaycastResult(x, y, z, faceX, faceY, faceZ, distance, hitPoint);
                    }
                }
            }
        }

        return closestHit;
    }

    private static float intersectRayBox(Ray ray, Vector3 min, Vector3 max, Vector3 hitPoint) {
        float tmin = Float.NEGATIVE_INFINITY;
        float tmax = Float.POSITIVE_INFINITY;

        // X axis
        if (Math.abs(ray.direction.x) > EPSILON) {
            float tx1 = (min.x - ray.origin.x) / ray.direction.x;
            float tx2 = (max.x - ray.origin.x) / ray.direction.x;
            tmin = Math.max(tmin, Math.min(tx1, tx2));
            tmax = Math.min(tmax, Math.max(tx1, tx2));
        } else if (ray.origin.x < min.x || ray.origin.x > max.x) {
            return -1; // Ray is parallel to x-axis and outside the box
        }

        // Y axis
        if (Math.abs(ray.direction.y) > EPSILON) {
            float ty1 = (min.y - ray.origin.y) / ray.direction.y;
            float ty2 = (max.y - ray.origin.y) / ray.direction.y;
            tmin = Math.max(tmin, Math.min(ty1, ty2));
            tmax = Math.min(tmax, Math.max(ty1, ty2));
        } else if (ray.origin.y < min.y || ray.origin.y > max.y) {
            return -1; // Ray is parallel to y-axis and outside the box
        }

        // Z axis
        if (Math.abs(ray.direction.z) > EPSILON) {
            float tz1 = (min.z - ray.origin.z) / ray.direction.z;
            float tz2 = (max.z - ray.origin.z) / ray.direction.z;
            tmin = Math.max(tmin, Math.min(tz1, tz2));
            tmax = Math.min(tmax, Math.max(tz1, tz2));
        } else if (ray.origin.z < min.z || ray.origin.z > max.z) {
            return -1; // Ray is parallel to z-axis and outside the box
        }

        // Check if the ray intersects the box
        if (tmax < tmin || tmax < 0 || tmin > MAX_DISTANCE) {
            return -1;
        }

        // Use tmin if it's positive, otherwise use tmax
        float t = tmin > 0 ? tmin : tmax;
        if (t < 0 || t > MAX_DISTANCE) {
            return -1;
        }

        // Calculate hit point
        hitPoint.set(ray.origin).mulAdd(ray.direction, t);
        return t;
    }

    private static boolean hasExposedFace(World world, int x, int y, int z) {
        // Check all six faces of the block
        // If any adjacent block is air or non-solid, this face is exposed
        return isExposed(world, x+1, y, z) || // right
               isExposed(world, x-1, y, z) || // left
               isExposed(world, x, y+1, z) || // top
               isExposed(world, x, y-1, z) || // bottom
               isExposed(world, x, y, z+1) || // front
               isExposed(world, x, y, z-1);   // back
    }

    private static boolean isExposed(World world, int x, int y, int z) {
        if (!world.isInBounds(x, y, z)) return true; // Consider world bounds as exposed
        Block block = world.getBlock(x, y, z);
        return block == Block.AIR || !block.isSolid();
    }
}
