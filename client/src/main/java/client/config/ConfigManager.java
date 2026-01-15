package client.config;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigManager {

    private volatile Config config;
    private Path baseDir;
    private boolean initialized = false;
    private boolean developmentMode = false;

    /**
     * no-arg constructor is used by Guice
     */
    public ConfigManager() {
    }

    /**
     * Initialize and load the config from disk. Safe to call multiple times; the
     * first call performs the load.
     * @param developmentMode if true, use the working directory as base; else use OS app data dir
     */
    public synchronized void init(boolean developmentMode) {
        if (initialized) {
            return;
        }
        this.developmentMode = developmentMode;
        this.baseDir = developmentMode ? Paths.get("") : getDefaultAppDataDir();
        try {
            this.config = Config.loadFrom(baseDir);
        } catch (IOException e) {
            System.err.println("Failed to load config, using defaults: " + e.getMessage());
            this.config = Config.defaults();
        }
        initialized = true;
    }

    private Path getDefaultAppDataDir() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            if (appData != null && !appData.isBlank()) {
                return Paths.get(appData, "csep", "client");
            }
        }
        // fallback to user home
        return Paths.get(System.getProperty("user.home"), ".csep", "client");
    }

    /**
     * Get the loaded config. If manager wasn't initialized this will initialize
     * it using developmentMode=false.
     * @return the in-memory Config instance
     */
    public Config getConfig() {
        if (!initialized) {
            init(false);
        }
        return config;
    }

    /**
     * Persist the current config to disk. Safe to call multiple times.
     */
    public synchronized void save() {
        if (!initialized || config == null) {
            return;
        }
        try {
            config.saveTo(baseDir);
        } catch (IOException e) {
            System.err.println("Failed to save config: " + e.getMessage());
        }
    }

    /**
     * Returns whether the application is running in development mode.
     * @return whether the application is running in development mode.
     */
    public boolean isDevelopmentMode() {
        return developmentMode;
    }

    /**
     * Sets the app as running in dev or prod.
     * @param developmentMode True for development mode, false for production mode.
     */
    public void setDevelopmentMode(boolean developmentMode) {
        this.developmentMode = developmentMode;
    }
}
