package render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import world.Block;

import java.util.EnumMap;

public class TextureManager {
    private static final int TILE_SIZE = 16;  // pixels per tile in the atlas
    private static final int ATLAS_WIDTH = 256;  // width of the full atlas in pixels
    private static final int ATLAS_HEIGHT = 256;

    private Texture atlasTexture;
    private EnumMap<Block, TextureRegion> blockTextures;

    public void load() {
        atlasTexture = new Texture(Gdx.files.internal("assets/texture_atlas.png"));
        atlasTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        blockTextures = new EnumMap<>(Block.class);
        generateRegions();
    }

    private void generateRegions() {
        int tilesX = ATLAS_WIDTH / TILE_SIZE;
        int tilesY = ATLAS_HEIGHT / TILE_SIZE;

        for (Block block : Block.values()) {
            int index = block.getId(); // e.g., grass = 0, dirt = 1, etc.
            int x = index % tilesX;
            int y = tilesY - 1 - (index / tilesX);

            TextureRegion region = new TextureRegion(atlasTexture, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            blockTextures.put(block, region);
        }
    }

    public TextureRegion getBlockTexture(Block block) {
        return blockTextures.get(block);
    }

    public Texture getAtlasTexture() {
        return atlasTexture;
    }

    public void dispose() {
        atlasTexture.dispose();
    }
}
