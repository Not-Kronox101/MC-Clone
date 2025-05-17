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
        float forwardX = (float) Math.sin(yawRad);
        float forwardZ = (float) Math.cos(yawRad);
        float strafeX = (float) Math.cos(yawRad);
        float strafeZ = (float) -Math.sin(yawRad);

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            player.move(forwardX * moveSpeed, 0, -forwardZ * moveSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            player.move(-forwardX * moveSpeed, 0, forwardZ * moveSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.move(strafeX * moveSpeed, 0, strafeZ * moveSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.move(-strafeX * moveSpeed, 0, -strafeZ * moveSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            player.move(0, moveSpeed, 0); // Jump or fly
        }
    }

    private void handleMouseInput() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            // Break block
            RayCaster.RaycastResult hit = RayCaster.cast(world, player);
            if (hit != null) {
                world.setBlock(hit.x, hit.y, hit.z, Block.AIR);
            }
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            // Place block
            RayCaster.RaycastResult hit = RayCaster.cast(world, player);
            if (hit != null) {
                world.setBlock(hit.x + hit.faceX, hit.y + hit.faceY, hit.z + hit.faceZ, Block.DIRT);
            }
        }
    }
}
