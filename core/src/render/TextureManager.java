package render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import world.Block;

public class TextureManager {
    private Texture atlas;
    private static final int TEXTURE_SIZE = 16;
    private static final int ATLAS_SIZE = 256;

    public void load() {
        atlas = new Texture(Gdx.files.internal("assets/textures/blocks.png"));
    }

    private Texture createDefaultTexture() {
        // Create a 256x16 texture with different colored blocks
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(256, 16, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        
        // Grass (green)
        pixmap.setColor(0.2f, 0.8f, 0.2f, 1);
        pixmap.fillRectangle(0, 0, 16, 16);
        
        // Dirt (brown)
        pixmap.setColor(0.6f, 0.3f, 0.1f, 1);
        pixmap.fillRectangle(16, 0, 16, 16);
        
        // Stone (gray)
        pixmap.setColor(0.5f, 0.5f, 0.5f, 1);
        pixmap.fillRectangle(32, 0, 16, 16);
        
        // Bedrock (dark gray)
        pixmap.setColor(0.2f, 0.2f, 0.2f, 1);
        pixmap.fillRectangle(48, 0, 16, 16);
        
        // Water (blue)
        pixmap.setColor(0.0f, 0.0f, 0.8f, 0.8f);
        pixmap.fillRectangle(64, 0, 16, 16);
        
        // Log (brown)
        pixmap.setColor(0.4f, 0.2f, 0.1f, 1);
        pixmap.fillRectangle(80, 0, 16, 16);
        
        // Leaves (dark green)
        pixmap.setColor(0.0f, 0.6f, 0.0f, 1);
        pixmap.fillRectangle(96, 0, 16, 16);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    public TextureRegion getBlockTexture(Block block) {
        int x = 0, y = 0;
        switch (block) {
            case GRASS:
                x = 0;
                y = 0;
                break;
            case DIRT:
                x = 1;
                y = 0;
                break;
            case STONE:
                x = 2;
                y = 0;
                break;
            case BEDROCK:
                x = 3;
                y = 0;
                break;
            case WATER:
                x = 4;
                y = 0;
                break;
            case LOG:
                x = 5;
                y = 0;
                break;
            case LEAVES:
                x = 6;
                y = 0;
                break;
            default:
                return null;
        }
        return new TextureRegion(atlas, x * TEXTURE_SIZE, y * TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE);
    }

    public Texture getAtlasTexture() {
        return atlas;
    }

    public void dispose() {
        if (atlas != null) {
            atlas.dispose();
        }
    }
}
