package player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import input.InputHandler;

public class CameraController {
    private final PerspectiveCamera camera;
    private final Player player;
    private final InputHandler inputHandler;
    private float sensitivity = 0.2f;

    public CameraController(PerspectiveCamera camera, Player player, InputHandler inputHandler) {
        this.camera = camera;
        this.player = player;
        this.inputHandler = inputHandler;
    }

    public void update() {
        // Only update camera if inventory is not open
        if (!inputHandler.isInventoryOpen()) {
            float deltaX = -Gdx.input.getDeltaX() * sensitivity;      // right = increase yaw
            float deltaY = -Gdx.input.getDeltaY() * sensitivity;     // up = decrease pitch

            float newYaw = player.getYaw() + deltaX;
            float newPitch = player.getPitch() + deltaY;

            // Clamp pitch to avoid flipping
            newPitch = Math.max(-89f, Math.min(89f, newPitch));
            player.setRotation(newYaw, newPitch);

            // Calculate direction vector from yaw and pitch (Minecraft style)
            float yawRad = (float) Math.toRadians(newYaw);
            float pitchRad = (float) Math.toRadians(newPitch);

            camera.direction.set(
                (float) (Math.cos(pitchRad) * Math.sin(yawRad)),
                (float) Math.sin(pitchRad),
                (float) (Math.cos(pitchRad) * Math.cos(yawRad))
            ).nor();
        }

        // Sync camera position to player
        camera.position.set(player.getX(), player.getY(), player.getZ());
        camera.update();
    }
}