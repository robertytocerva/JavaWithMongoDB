package cxt.robertytocerva.repository;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.*;
import cxt.robertytocerva.utiles.ConfigFile;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
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

    private boolean getPing(MongoDatabase database) {
        try {
            Bson comando = new BsonDocument("ping", new BsonInt64(1));
            Document resultado = database.runCommand(comando);

            logger.info("Ping: " +resultado.toString());

        } catch (MongoException e) {
            logger.error("Error de Ping:" + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean crearConeccion() throws MongoException {
        if (this.connectionString == null) {
            logger.error("No hay conexion establecida");
            return false;
        }
        try {
            MongoClient mongoClient = MongoClients.create(connectionString);
            MongoDatabase database = mongoClient.getDatabase("admin");

            if (getPing(database)) {
                this.client = mongoClient;
                return true;
            }

        } catch (MongoException e) {
            logger.error("NO se puedo conectar");
            logger.error("Error:" + e.getMessage());
            throw e;
        }
        return false;
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

    public MongoDatabase getDatabaseWhitCodec(String databaseName) throws Exception {
        if (this.client == null) {
            if (!this.crearConeccion()) {
                throw new Exception("No se pudo acceder a la conexion");
            }
        }

        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),CodecRegistries.fromProviders(pojoCodecProvider));

        MongoDatabase database = this.client.getDatabase(databaseName).withCodecRegistry(pojoCodecRegistry);

        if (!getPing(database)) {
            throw new Exception("No se pudo acceder a la base de datos");
        }
        return database;
    }

    public MongoCollection<?> getCollections(String databaseName, String collectionName, Class entidad) throws Exception{
        MongoDatabase database = getDatabaseWhitCodec(databaseName);
        return database.getCollection(collectionName, entidad);
    }

    public  void closeConection() {
        if (this.client != null) {
            this.client.close();
            logger.info("Conexión cerrada");
        }
    }
}
