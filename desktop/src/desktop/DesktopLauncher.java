package desktop;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import world.MinecraftClone; // or your correct package

public class DesktopLauncher {
    public static void main (String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Minecraft Clone");
        config.setWindowedMode(800, 600);
        new Lwjgl3Application(new MinecraftClone(), config);
    }
}