package client.scenes; //Ai generated class

import client.config.Config;
import client.config.ConfigManager;
import client.utils.RecipeListCellFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RecipeListCellFactoryTest {

    private TestConfigManager configManager;
    private RecipeListCellFactory factory;

    @BeforeEach
    void setUp() {
        configManager = new TestConfigManager();
        factory = new RecipeListCellFactory(configManager);
    }

    @Test
    void testConstructor() {
        assertNotNull(factory);
    }

    @Test
    void testIsFavoriteWhenTrue() {
        long recipeId = 1L;
        configManager.getConfig().addFavorite(recipeId);

        boolean result = factory.isFavorite(recipeId);

        assertTrue(result);
    }

    @Test
    void testIsFavoriteWhenFalse() {
        long recipeId = 2L;

        boolean result = factory.isFavorite(recipeId);

        assertFalse(result);
    }

    @Test
    void testGetFavoriteIds() {
        Config config = configManager.getConfig();
        config.addFavorite(1L);
        config.addFavorite(2L);
        config.addFavorite(3L);

        Set<Long> result = factory.getFavoriteIds();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains(1L));
        assertTrue(result.contains(2L));
        assertTrue(result.contains(3L));
    }

    @Test
    void testGetFavoriteIdsEmpty() {
        Set<Long> result = factory.getFavoriteIds();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testAddAndRemoveFavorite() {
        Config config = configManager.getConfig();
        long recipeId = 5L;

        assertFalse(factory.isFavorite(recipeId));

        config.addFavorite(recipeId);
        assertTrue(factory.isFavorite(recipeId));

        config.removeFavorite(recipeId);
        assertFalse(factory.isFavorite(recipeId));
    }

    @Test
    void testGetFavoriteIdsReturnsCopy() {
        Config config = configManager.getConfig();
        config.addFavorite(1L);

        Set<Long> result1 = factory.getFavoriteIds();
        Set<Long> result2 = factory.getFavoriteIds();

        assertNotSame(result1, result2);
        assertEquals(result1, result2);
    }

    @Test
    void testMultipleFavoriteOperations() {
        Config config = configManager.getConfig();

        config.addFavorite(10L);
        config.addFavorite(20L);
        config.addFavorite(30L);

        assertTrue(factory.isFavorite(10L));
        assertTrue(factory.isFavorite(20L));
        assertTrue(factory.isFavorite(30L));

        config.removeFavorite(20L);

        assertTrue(factory.isFavorite(10L));
        assertFalse(factory.isFavorite(20L));
        assertTrue(factory.isFavorite(30L));

        Set<Long> ids = factory.getFavoriteIds();
        assertEquals(2, ids.size());
    }

    // Simple test implementation of ConfigManager
    static class TestConfigManager extends ConfigManager {
        private final Config testConfig = Config.defaults();

        @Override
        public Config getConfig() {
            return testConfig;
        }

        @Override
        public void save() {
            // Do nothing for tests
        }
    }
}