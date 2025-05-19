package render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g3d.attributes.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import player.Player;
import player.ItemStack;
import world.*;
import utils.RayCaster;
import utils.RayCaster.RaycastResult;
import java.util.EnumMap;

public class Renderer {
    private final ModelBatch batch;
    private final Environment environment;
    private final ModelBuilder modelBuilder;
    private final ModelInstance[][][] blockInstances;
    private final World world;
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch spriteBatch;
    private final BitmapFont font;
    private Vector3 highlightedBlockPos;
    private final TextureManager textureManager;
    private final EnumMap<Block, Model> blockModels;
    private final Player player;
    private boolean inventoryVisible = false;

    // Crosshair properties
    private static final float CROSSHAIR_SIZE = 10f;
    private static final float CROSSHAIR_THICKNESS = 2f;
    private static final Color CROSSHAIR_COLOR = new Color(1f, 1f, 1f, 0.8f);

    // Inventory properties
    private static final int INVENTORY_ROWS = 3;
    private static final int INVENTORY_COLS = 9;
    private static final float SLOT_SIZE = 50f;
    private static final float INVENTORY_PADDING = 10f;
    private static final Color INVENTORY_BG_COLOR = new Color(0.2f, 0.2f, 0.2f, 0.8f);
    private static final Color SLOT_COLOR = new Color(0.3f, 0.3f, 0.3f, 0.8f);
    private static final Color SELECTED_SLOT_COLOR = new Color(0.4f, 0.4f, 0.4f, 0.8f);
    private static final Color SELECTED_SLOT_BORDER = new Color(1f, 1f, 1f, 0.8f);
    private static final Color HOVER_SLOT_COLOR = new Color(0.5f, 0.5f, 0.5f, 0.8f);
    private static final Color HOVER_SLOT_BORDER = new Color(1f, 1f, 1f, 0.5f);

    public Renderer(World world, TextureManager textureManager, Player player) {
        this.player = player;
        this.world = world;
        this.textureManager = textureManager;
        batch = new ModelBatch();
        modelBuilder = new ModelBuilder();
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1f));
        environment.add(new DirectionalLight().set(1f, 1f, 1f, -1f, -0.8f, -0.2f));

        blockModels = new EnumMap<>(Block.class);
        createBlockModels();

        // Match the world size (4 chunks * 16 blocks = 64 blocks)
        int sizeX = 64;
        int sizeY = 32;  // Height for terrain
        int sizeZ = 64;

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

        // Run raycast each frame
        RaycastResult result = RayCaster.cast(world, player);
        if (result != null) {
            highlightedBlockPos = new Vector3(result.x, result.y, result.z);
        } else {
            highlightedBlockPos = null;
        }

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

        // Draw highlight box
        if (highlightedBlockPos != null) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.YELLOW);
            shapeRenderer.box(
                    highlightedBlockPos.x,
                    highlightedBlockPos.y,
                    highlightedBlockPos.z,
                    1f, 1f, 1f
            );
            shapeRenderer.end();
        }

        // Render UI elements
        renderCrosshair();
        if (inventoryVisible) {
            renderInventory();
        } else {
            renderHotbar();
        }
    }

    private void renderCrosshair() {
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;

        shapeRenderer.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(CROSSHAIR_COLOR);

        // Draw horizontal line
        shapeRenderer.rect(centerX - CROSSHAIR_SIZE, centerY - CROSSHAIR_THICKNESS/2,
                         CROSSHAIR_SIZE * 2, CROSSHAIR_THICKNESS);
        // Draw vertical line
        shapeRenderer.rect(centerX - CROSSHAIR_THICKNESS/2, centerY - CROSSHAIR_SIZE,
                         CROSSHAIR_THICKNESS, CROSSHAIR_SIZE * 2);

        shapeRenderer.end();
    }

    private void renderInventory() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        
        // Calculate inventory position (centered)
        float inventoryWidth = INVENTORY_COLS * SLOT_SIZE + (INVENTORY_COLS + 1) * INVENTORY_PADDING;
        float inventoryHeight = INVENTORY_ROWS * SLOT_SIZE + (INVENTORY_ROWS + 1) * INVENTORY_PADDING;
        float startX = (screenWidth - inventoryWidth) / 2;
        float startY = (screenHeight - inventoryHeight) / 2;

        shapeRenderer.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, screenWidth, screenHeight));
        
        // Draw inventory background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(INVENTORY_BG_COLOR);
        shapeRenderer.rect(startX, startY, inventoryWidth, inventoryHeight);
        shapeRenderer.end();

        // Get mouse position
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        int hoverSlot = getSlotUnderMouse();

        // Draw slots
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int row = 0; row < INVENTORY_ROWS; row++) {
            for (int col = 0; col < INVENTORY_COLS; col++) {
                float x = startX + INVENTORY_PADDING + col * (SLOT_SIZE + INVENTORY_PADDING);
                float y = startY + INVENTORY_PADDING + row * (SLOT_SIZE + INVENTORY_PADDING);
                
                // Draw slot background
                shapeRenderer.setColor(SLOT_COLOR);
                shapeRenderer.rect(x, y, SLOT_SIZE, SLOT_SIZE);
                
                int slot = row * INVENTORY_COLS + col;
                
                // Draw selected slot highlight
                if (slot == player.getInventory().getSelectedSlot()) {
                    shapeRenderer.setColor(SELECTED_SLOT_COLOR);
                    shapeRenderer.rect(x + 2, y + 2, SLOT_SIZE - 4, SLOT_SIZE - 4);
                }
                
                // Draw hover highlight
                if (slot == hoverSlot) {
                    shapeRenderer.setColor(HOVER_SLOT_COLOR);
                    shapeRenderer.rect(x + 2, y + 2, SLOT_SIZE - 4, SLOT_SIZE - 4);
                    
                    // Draw hover border
                    shapeRenderer.setColor(HOVER_SLOT_BORDER);
                    shapeRenderer.rect(x, y, SLOT_SIZE, 2); // Top
                    shapeRenderer.rect(x, y + SLOT_SIZE - 2, SLOT_SIZE, 2); // Bottom
                    shapeRenderer.rect(x, y, 2, SLOT_SIZE); // Left
                    shapeRenderer.rect(x + SLOT_SIZE - 2, y, 2, SLOT_SIZE); // Right
                }
            }
        }
        shapeRenderer.end();

        // Draw items and counts
        spriteBatch.begin();
        for (int row = 0; row < INVENTORY_ROWS; row++) {
            for (int col = 0; col < INVENTORY_COLS; col++) {
                int slot = row * INVENTORY_COLS + col;
                ItemStack stack = player.getInventory().getStack(slot);
                if (stack != null && !stack.isEmpty()) {
                    float x = startX + INVENTORY_PADDING + col * (SLOT_SIZE + INVENTORY_PADDING);
                    float y = startY + INVENTORY_PADDING + row * (SLOT_SIZE + INVENTORY_PADDING);
                    
                    // Draw block texture
                    TextureRegion texture = textureManager.getBlockTexture(stack.getBlock());
                    spriteBatch.draw(texture, x + 5, y + 5, SLOT_SIZE - 10, SLOT_SIZE - 10);
                    
                    // Draw count
                    String count = String.valueOf(stack.getCount());
                    font.draw(spriteBatch, count, x + SLOT_SIZE - 20, y + 20);
                }
            }
        }
        spriteBatch.end();
    }

    private int getSlotUnderMouse() {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        
        float slotSize = SLOT_SIZE;
        float inventoryWidth = slotSize * INVENTORY_COLS + (INVENTORY_COLS + 1) * INVENTORY_PADDING;
        float inventoryHeight = slotSize * INVENTORY_ROWS + (INVENTORY_ROWS + 1) * INVENTORY_PADDING;
        float startX = (Gdx.graphics.getWidth() - inventoryWidth) / 2;
        float startY = (Gdx.graphics.getHeight() - inventoryHeight) / 2;

        if (mouseX >= startX && mouseX < startX + inventoryWidth &&
            mouseY >= startY && mouseY < startY + inventoryHeight) {
            int col = (int)((mouseX - startX - INVENTORY_PADDING) / (slotSize + INVENTORY_PADDING));
            int row = (int)((mouseY - startY - INVENTORY_PADDING) / (slotSize + INVENTORY_PADDING));
            
            // Check if mouse is actually over a slot (not in padding)
            if (col >= 0 && col < INVENTORY_COLS && row >= 0 && row < INVENTORY_ROWS) {
                return row * INVENTORY_COLS + col;
            }
        }
        return -1;
    }

    private void renderHotbar() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        
        // Calculate hotbar position (bottom center)
        float hotbarWidth = INVENTORY_COLS * SLOT_SIZE + (INVENTORY_COLS + 1) * INVENTORY_PADDING;
        float startX = (screenWidth - hotbarWidth) / 2;
        float startY = INVENTORY_PADDING; // Bottom padding

        shapeRenderer.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, screenWidth, screenHeight));
        
        // Draw hotbar background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(INVENTORY_BG_COLOR);
        shapeRenderer.rect(startX, startY, hotbarWidth, SLOT_SIZE + 2 * INVENTORY_PADDING);
        shapeRenderer.end();

        // Draw slots
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int col = 0; col < INVENTORY_COLS; col++) {
            float x = startX + INVENTORY_PADDING + col * (SLOT_SIZE + INVENTORY_PADDING);
            float y = startY + INVENTORY_PADDING;
            
            // Draw slot background
            shapeRenderer.setColor(SLOT_COLOR);
            shapeRenderer.rect(x, y, SLOT_SIZE, SLOT_SIZE);
            
            // Draw selected slot highlight
            if (col == player.getInventory().getSelectedSlot()) {
                shapeRenderer.setColor(SELECTED_SLOT_COLOR);
                shapeRenderer.rect(x + 2, y + 2, SLOT_SIZE - 4, SLOT_SIZE - 4);
                
                // Draw white border around selected slot
                shapeRenderer.setColor(SELECTED_SLOT_BORDER);
                shapeRenderer.rect(x, y, SLOT_SIZE, 2); // Top
                shapeRenderer.rect(x, y + SLOT_SIZE - 2, SLOT_SIZE, 2); // Bottom
                shapeRenderer.rect(x, y, 2, SLOT_SIZE); // Left
                shapeRenderer.rect(x + SLOT_SIZE - 2, y, 2, SLOT_SIZE); // Right
            }
        }
        shapeRenderer.end();

        // Draw items and counts
        spriteBatch.begin();
        for (int col = 0; col < INVENTORY_COLS; col++) {
            int slot = col;
            ItemStack stack = player.getInventory().getStack(slot);
            if (stack != null && !stack.isEmpty()) {
                float x = startX + INVENTORY_PADDING + col * (SLOT_SIZE + INVENTORY_PADDING);
                float y = startY + INVENTORY_PADDING;
                
                // Draw block texture
                TextureRegion texture = textureManager.getBlockTexture(stack.getBlock());
                spriteBatch.draw(texture, x + 5, y + 5, SLOT_SIZE - 10, SLOT_SIZE - 10);
                
                // Draw count
                String count = String.valueOf(stack.getCount());
                font.draw(spriteBatch, count, x + SLOT_SIZE - 20, y + 20);
            }
        }
        spriteBatch.end();
    }

    public void setInventoryVisible(boolean visible) {
        inventoryVisible = visible;
    }

    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        spriteBatch.dispose();
        font.dispose();
        for (Model model : blockModels.values()) {
            model.dispose();
        }
    }

    public void updateBlock(int x, int y, int z, Block block) {
        if (x >= 0 && x < blockInstances.length &&
            y >= 0 && y < blockInstances[0].length &&
            z >= 0 && z < blockInstances[0][0].length) {
            
            // Remove old block instance
            blockInstances[x][y][z] = null;
            
            // Create new block instance if not air
            if (block != Block.AIR) {
                Vector3 pos = new Vector3(x + 0.5f, y + 0.5f, z + 0.5f);
                Model model = blockModels.get(block);
                blockInstances[x][y][z] = new ModelInstance(model, pos);
            }
        }
    }
}