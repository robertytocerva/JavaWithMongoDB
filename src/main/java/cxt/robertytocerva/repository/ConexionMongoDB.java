package cxt.robertytocerva.repository;

import com.mongodb.ConnectionString;
import com.mongodb.MongoException;
import com.mongodb.client.ListDatabasesIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import cxt.robertytocerva.utiles.ConfigFile;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ConexionMongoDB {
    private final Logger logger = LoggerFactory.getLogger(ConexionMongoDB.class);
    private final ConnectionString connectionString;
    private MongoClient client = null;

    public ConexionMongoDB() {
        Properties properties = null;

        try {
            ConfigFile configFile = new ConfigFile("db/mongodb.properties");
            properties = configFile.readPropiertiesFIle();
            properties = configFile.readPropiertiesFIle();
        }catch (Exception e) {
            logger.error("Error recuperando conexion:" + e.getMessage());
            this.connectionString = null;
            return;
        }
        String host = properties.getProperty("host");
        String port = properties.getProperty("port");
        String usuario = properties.getProperty("username");
        String clave = properties.getProperty("password");

        String uri = String.format("mongodb://%s:%s@%s:%s/", usuario, clave, host, port);
        this.connectionString = new ConnectionString(uri);
    }

    public boolean crearConeccion() throws MongoException {
        if (this.connectionString == null) {
            logger.error("No hay conexion establecida");
            return false;
        }
        try {
            MongoClient mongoClient = MongoClients.create(connectionString);
            MongoDatabase database = mongoClient.getDatabase("admin");
            Bson comando = new BsonDocument("ping", new BsonInt64(1));
            Document resultado = database.runCommand(comando);

            logger.info("Conexion establecida. Ping: " +resultado.toString());

            this.client = mongoClient;

            return true;
        } catch (MongoException e) {
            logger.error("NO se puedo conectar");
            logger.error("Error:" + e.getMessage());
            throw e;
        }
    }

    public void mostrarInfoCluster() {
        if (this.client != null) {
            logger.info("Información del cluster");
            logger.info(this.client.getClusterDescription().toString());
        } else {
            logger.error("No hay conexion establecida");
        }
    }

    public void mostradDB() {
        if (this.client != null) {
            logger.info("Listado de DB existentes");
            ListDatabasesIterable<Document> databases = this.client.listDatabases();
            databases.forEach(db -> logger.info(db.toJson()));
        }else {
            logger.error("No hay conexion establecida");
        }
    }

    public  void closeConection() {
        if (this.client != null) {
            this.client.close();
            logger.info("Conexión cerrada");
        }
    }
}
