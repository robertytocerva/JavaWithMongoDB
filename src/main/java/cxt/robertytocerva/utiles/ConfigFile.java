package cxt.robertytocerva.utiles;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigFile {
    private final String filename;

    public ConfigFile(String filename) {
        this.filename = filename;
    }

    public Properties readPropiertiesFIle() throws IOException {
        Properties prop = new Properties();
        try (
                InputStream inputStream = ConfigFile.class.getClassLoader().getResourceAsStream(this.filename)
        ){
            prop = new Properties();
            prop.load(inputStream);
        }

        return prop;
    }

}