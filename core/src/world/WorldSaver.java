package world;

import utils.Constants;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class WorldSaver {
    private final Path worldDir;

    public WorldSaver(Path worldDir) {
        this.worldDir = worldDir;
        try {
            if (!Files.exists(worldDir)) {
                Files.createDirectories(worldDir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save chunk data to file (chunk_x_z.dat)
    public void saveChunk(Chunk chunk) {
        String filename = "chunk_" + chunk.getChunkX() + "_" + chunk.getChunkZ() + ".dat";
        Path file = worldDir.resolve(filename);

        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(file)))) {
            for (int x = 0; x < Constants.CHUNK_SIZE_X; x++) {
                for (int y = 0; y < Constants.CHUNK_SIZE_Y; y++) {
                    for (int z = 0; z < Constants.CHUNK_SIZE_Z; z++) {
                        dos.writeInt(chunk.getBlock(x, y, z).getId());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load chunk data from file; returns null if missing
    public Chunk loadChunk(int chunkX, int chunkZ) {
        String filename = "chunk_" + chunkX + "_" + chunkZ + ".dat";
        Path file = worldDir.resolve(filename);

        if (!Files.exists(file)) {
            return null;
        }

        Chunk chunk = new Chunk(chunkX, chunkZ);

        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(Files.newInputStream(file)))) {
            for (int x = 0; x < Constants.CHUNK_SIZE_X; x++) {
                for (int y = 0; y < Constants.CHUNK_SIZE_Y; y++) {
                    for (int z = 0; z < Constants.CHUNK_SIZE_Z; z++) {
                        int id = dis.readInt();
                        Block block = getBlockById(id);
                        chunk.setBlock(x, y, z, block);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return chunk;
    }

    // Save world metadata (seed, version, etc.)
    public void saveWorldMetadata(WorldMetadata metadata) {
        Path metaFile = worldDir.resolve("world.meta");
        Properties props = new Properties();
        props.setProperty("name", metadata.getName());
        props.setProperty("seed", Long.toString(metadata.getSeed()));
        props.setProperty("version", metadata.getVersion());

        try (OutputStream out = Files.newOutputStream(metaFile)) {
            props.store(out, "World Metadata");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load world metadata
    public WorldMetadata loadWorldMetadata() {
        Path metaFile = worldDir.resolve("world.meta");
        if (!Files.exists(metaFile)) return null;

        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(metaFile)) {
            props.load(in);
            String name = props.getProperty("name", "MyWorld");
            long seed = Long.parseLong(props.getProperty("seed", "0"));
            String version = props.getProperty("version", "1.0");
            return new WorldMetadata(name, seed, version);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Helper: find Block enum by id
    private Block getBlockById(int id) {
        for (Block b : Block.values()) {
            if (b.getId() == id) return b;
        }
        return Block.AIR; // Default fallback
    }
}
