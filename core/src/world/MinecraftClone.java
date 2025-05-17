package world;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import render.Renderer;
import render.TextureManager;
import world.World;

public class MinecraftClone extends ApplicationAdapter {
    private Renderer renderer;
    private TextureManager textureManager;
    private World world;
    private PerspectiveCamera camera;

    @Override
    public void create() {
        textureManager = new TextureManager();
        textureManager.load();

        world = new World(); // Add simple terrain generation here

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(10f, 10f, 10f);
        camera.lookAt(0f, 0f, 0f);
        camera.near = 0.1f;
        camera.far = 100f;
        camera.update();

        renderer = new Renderer(world, textureManager);
    }

    @Override
    public void render() {
        camera.update();

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
