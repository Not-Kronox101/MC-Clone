package world;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import player.CameraController;
import input.InputHandler;
import player.Player;
import render.Renderer;
import render.TextureManager;
import utils.Constants;

public class MinecraftClone extends ApplicationAdapter {
    private TextureManager textureManager;
    private Renderer renderer;
    private World world;
    private PerspectiveCamera camera;
    private Player player;
    private CameraController cameraController;
    private InputHandler inputHandler;

    @Override
    public void create() {
        textureManager = new TextureManager();
        textureManager.load();

        world = new World();

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 0.01f;
        camera.far = 1000f;

        // Start player above the grass layer (y=14)
        player = new Player(world, 8f, 16f, 8f);
        renderer = new Renderer(world, textureManager, player);
        inputHandler = new InputHandler(player, world, renderer, this);
        cameraController = new CameraController(camera, player, inputHandler);

        // Set initial cursor state
        Gdx.input.setCursorCatched(true);
    }

    @Override
    public void render() {
        inputHandler.update();
        cameraController.update();

        Gdx.gl.glClearColor(0.5f, 0.7f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        renderer.render(camera);
    }

    @Override
    public void dispose() {
        textureManager.dispose();
        renderer.dispose();
    }
}
