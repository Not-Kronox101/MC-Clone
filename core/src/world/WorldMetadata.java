package world;

public class WorldMetadata {
    private String name;
    private long seed;
    private String version;

    public WorldMetadata(String name, long seed, String version) {
        this.name = name;
        this.seed = seed;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public long getSeed() {
        return seed;
    }

    public String getVersion() {
        return version;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
