package client.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Config {
    public static final int CURRENT_VERSION = 1;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private int configVersion = CURRENT_VERSION;
    private String prefLocale = "";
    private Set<Long> favoriteRecipeIds = new HashSet<>();

    /**
     * Create a new Config with default values.
     */
    public Config() {
    }

    /**
     * Returns a Config instance populated with default values.
     *
     * @return a new default Config instance
     */
    public static Config defaults() {
        return new Config();
    }

    /**
     * Load config from the provided base directory. If developmentMode is true, a file named
     * `config.json` in the base directory is used. If false, caller should pass an OS-specific
     * folder as baseDir and the file `config.json` will be read there.
     * If the file is missing or invalid, defaults are returned and a default file is written.
     * @param baseDir the directory containing (or to contain) the config file
     * @return the loaded Config instance, or defaults if no valid file exists
     * @throws IOException if an I/O error occurs while reading the config file
     */
    public static Config loadFrom(Path baseDir) throws IOException {
        Path cfg = resolvePath(baseDir);
        try {
            if (Files.exists(cfg)) {
                String json = FileUtils.readFileToString(cfg.toFile(), StandardCharsets.UTF_8);
                Config c = MAPPER.readValue(json, Config.class);
                if (c == null) {
                    throw new IOException("Deserialized config is null");
                }
                return c;
            } else {
                Config d = Config.defaults();
                ensureParent(cfg);
                FileUtils.writeStringToFile(
                        cfg.toFile(),
                        MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(d),
                        StandardCharsets.UTF_8);
                return d;
            }
        } catch (IOException e) {
            // backup corrupt file if possible, then write defaults
            try {
                if (Files.exists(cfg)) {
                    String ts = DateTimeFormatter.ISO_INSTANT.format(
                            Instant.now()
                    ).replaceAll(":", "-");
                    Path backup = cfg.resolveSibling("config.json.corrupt." + ts + ".bak");
                    Files.move(cfg, backup);
                    System.err.println("Backed up corrupt config to " + backup);
                }
            } catch (Exception ex) {
                // ignore backup failures
            }
            Config d = Config.defaults();
            try {
                ensureParent(cfg);
                FileUtils.writeStringToFile(
                        cfg.toFile(),
                        MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(d),
                        StandardCharsets.UTF_8
                );
            } catch (Exception ex) {
                // ignore write failures — caller will see original exception
            }
            throw e;
        }
    }

    /**
     * Save this Config to the provided base directory. The file written is `config.json` under
     * the provided base directory.
     * @param baseDir the directory to write the config file into
     * @throws IOException if an I/O error occurs while writing the config file
     */
    public void saveTo(Path baseDir) throws IOException {
        Path cfg = resolvePath(baseDir);
        ensureParent(cfg);
        FileUtils.writeStringToFile(
                cfg.toFile(),
                MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(this),
                StandardCharsets.UTF_8
        );
    }

    /**
     * Resolve the path to the config file inside the given base directory.
     * The current implementation always resolves to `config.json` directly under baseDir.
     * @param baseDir the base directory where the config file lives
     * @return a Path pointing to the config.json file
     */
    private static Path resolvePath(Path baseDir) {
        return baseDir.resolve("config.json");
    }

    /**
     * Ensure the parent directory of the given path exists by creating missing directories.
     * Any exceptions during directory creation are swallowed.
     * @param cfg the path whose parent directories should be created if missing
     */
    private static void ensureParent(Path cfg) {
        try {
            Path p = cfg.getParent();
            if (p != null) {
                Files.createDirectories(p);
            }
        } catch (Exception ignored) {
        }
    }

    // getters n setters

    /**
     * Get the stored config version number.
     * @return the config version
     */
    public int getConfigVersion() {
        return configVersion;
    }

    /**
     * Set the stored config version number.
     * @param configVersion the version number to set
     */
    public void setConfigVersion(int configVersion) {
        this.configVersion = configVersion;
    }

    /**
     * Get the language filter string.
     * @return the language filter
     */
    public String getPrefLocale() {
        return prefLocale;
    }

    /**
     * Set the language filter string.
     * @param languageFilter the language filter to set
     */
    public void setPrefLocale(String languageFilter) {
        this.prefLocale = languageFilter;
    }

    /**
     * Get the set of favorite recipe IDs.
     * @return the favorite recipe IDs
     */
    public Set<Long> getFavoriteRecipeIds() {
        return favoriteRecipeIds;
    }

    /**
     * Set the set of favorite recipe IDs.
     * @param favoriteRecipeIds the favorite recipe IDs to set
     */
    public void setFavoriteRecipeIds(Set<Long> favoriteRecipeIds) {
        this.favoriteRecipeIds = favoriteRecipeIds;
    }

    /**
     * Add a recipe ID to favorites.
     * @param recipeId the recipe ID to add
     * @return true if the set changed as a result of the call
     */
    public boolean addFavorite(Long recipeId) {
        return favoriteRecipeIds.add(recipeId);
    }

    /**
     * Remove a recipe ID from favorites.
     * @param recipeId the recipe ID to remove
     * @return true if the set contained the specified element
     */
    public boolean removeFavorite(Long recipeId) {
        return favoriteRecipeIds.remove(recipeId);
    }

    /**
     * Check if a recipe ID is in favorites.
     * @param recipeId the recipe ID to check
     * @return true if the recipe is favorited
     */
    public boolean isFavorite(Long recipeId) {
        return favoriteRecipeIds.contains(recipeId);
    }
}
