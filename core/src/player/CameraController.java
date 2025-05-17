package player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;

public class CameraController {
    private final PerspectiveCamera camera;
    private final Player player;

    private float sensitivity = 0.2f;

    public CameraController(PerspectiveCamera camera, Player player) {
        this.camera = camera;
        this.player = player;
        Gdx.input.setCursorCatched(true); // Lock cursor
    }

    public void update() {
        float deltaX = -Gdx.input.getDeltaX() * sensitivity;
        float deltaY = -Gdx.input.getDeltaY() * sensitivity;

        float newYaw = player.getYaw() + deltaX;
        float newPitch = player.getPitch() + deltaY;

        // Clamp pitch to avoid flipping
        newPitch = Math.max(-89f, Math.min(89f, newPitch));

        player.setRotation(newYaw, newPitch);

        // Update camera direction
        camera.direction.set(
            (float) Math.cos(Math.toRadians(newPitch)) * (float) Math.cos(Math.toRadians(newYaw)),
            (float) Math.sin(Math.toRadians(newPitch)),
            (float) Math.cos(Math.toRadians(newPitch)) * (float) Math.sin(Math.toRadians(newYaw))
        ).nor();

        // Sync camera position to player
        camera.position.set(player.getX(), player.getY(), player.getZ());

        camera.update();
    }
}
