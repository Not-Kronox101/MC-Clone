package input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import player.Player;
import utils.RayCaster;
import world.Block;
import world.World;

public class InputHandler {
    private final Player player;
    private final World world;
    private float moveSpeed = 0.1f;

    public InputHandler(Player player, World world) {
        this.player = player;
        this.world = world;
    }

    public void update() {
        handleMovement();
        handleMouseInput();
    }

    private void handleMovement() {
        float yawRad = (float) Math.toRadians(player.getYaw());

        // Forward vector (direction player is facing)
        float forwardX = (float) Math.sin(yawRad);
        float forwardZ = (float) Math.cos(yawRad);

        // Strafe vector (perpendicular to forward)
        float strafeX = (float) Math.cos(yawRad);
        float strafeZ = (float) -Math.sin(yawRad);

        float dx = 0, dy = 0, dz = 0;

        // W - Forward
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            dx += forwardX * moveSpeed;
            dz += forwardZ * moveSpeed;
        }
        // S - Backward
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            dx -= forwardX * moveSpeed;
            dz -= forwardZ * moveSpeed;
        }
        // A - Strafe Left
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            dx += strafeX * moveSpeed;
            dz += strafeZ * moveSpeed;
        }
        // D - Strafe Right
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            dx -= strafeX * moveSpeed;
            dz -= strafeZ * moveSpeed;
        }
        // SPACE - Fly Up
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            dy += moveSpeed;
        }
        // Z - Fly Down
        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            dy -= moveSpeed;
        }

        // Move with Minecraft-style collision
        player.moveWithCollision(dx, dy, dz, world);
    }

    private void handleMouseInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.L)) {
            // Break block
            RayCaster.RaycastResult hit = RayCaster.cast(world, player);
            if (hit != null) {
                world.setBlock(hit.x, hit.y, hit.z, Block.AIR);
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.K)) {
            // Place block
            RayCaster.RaycastResult hit = RayCaster.cast(world, player);
            if (hit != null) {
                world.setBlock(hit.x + hit.faceX, hit.y + hit.faceY, hit.z + hit.faceZ, Block.DIRT);
            }
        }
    }
}
