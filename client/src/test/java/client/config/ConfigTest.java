package client.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigTest {
    @Test
    public void saveAndLoadLanguagePersists(@TempDir Path tmp) throws Exception {
        // create and save config using the new Config.saveTo
        Config c = Config.defaults();
        c.setPrefLocale("nl");
        c.saveTo(tmp);

        // load using new static loadFrom
        Config loaded = Config.loadFrom(tmp);
        assertEquals("nl", loaded.getPrefLocale());
    }

    @Test
    public void serializeDeserializePreservesFields() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Config c = Config.defaults();
        c.setPrefLocale("en");

        String json = mapper.writeValueAsString(c);
        Config d = mapper.readValue(json, Config.class);

        assertNotNull(d);
        assertEquals(Config.CURRENT_VERSION, d.getConfigVersion());
        assertEquals("en", d.getPrefLocale());
    }
}
