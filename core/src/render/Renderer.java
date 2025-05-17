package render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.attributes.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import world.*;
import world.Block;

import java.util.EnumMap;

public class Renderer {
    private final ModelBatch batch;
    private final Environment environment;
    private final ModelBuilder modelBuilder;
    private final ModelInstance[][][] blockInstances;
    private final World world;

    private final TextureManager textureManager;
    private final EnumMap<Block, Model> blockModels;

    public Renderer(World world, TextureManager textureManager) {
        this.world = world;
        this.textureManager = textureManager;

        batch = new ModelBatch();
        modelBuilder = new ModelBuilder();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1f));
        environment.add(new DirectionalLight().set(1f, 1f, 1f, -1f, -0.8f, -0.2f));

        blockModels = new EnumMap<>(Block.class);
        createBlockModels();

        int sizeX = World.WIDTH;
        int sizeY = World.HEIGHT;
        int sizeZ = World.DEPTH;

        blockInstances = new ModelInstance[sizeX][sizeY][sizeZ];
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                for (int z = 0; z < sizeZ; z++) {
                    Block block = world.getBlock(x, y, z);
                    if (block != Block.AIR) {
                        Vector3 pos = new Vector3(x + 0.5f, y + 0.5f, z + 0.5f);
                        Model model = blockModels.get(block);
                        blockInstances[x][y][z] = new ModelInstance(model, pos);
                    }
                }
            }
        }
    }

    private void createBlockModels() {
        Texture atlas = textureManager.getAtlasTexture();
        for (Block block : Block.values()) {
            if (block == Block.AIR) continue;

            TextureRegion region = textureManager.getBlockTexture(block);
            Material material = new Material(
                    TextureAttribute.createDiffuse(region),
                    ColorAttribute.createSpecular(Color.WHITE),
                    FloatAttribute.createShininess(16f)
            );

            Model model = modelBuilder.createBox(
                    1f, 1f, 1f,
                    material,
                    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates
            );
            blockModels.put(block, model);
        }
    }

    public void render(PerspectiveCamera camera) {
        Gdx.gl.glClearColor(0.5f, 0.7f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        batch.begin(camera);
        for (ModelInstance[][] layer : blockInstances) {
            for (ModelInstance[] row : layer) {
                for (ModelInstance instance : row) {
                    if (instance != null) {
                        batch.render(instance, environment);
                    }
                }
            }
        }
        batch.end();
    }

    public void dispose() {
        batch.dispose();
        for (Model model : blockModels.values()) {
            model.dispose();
        }
    }
}
