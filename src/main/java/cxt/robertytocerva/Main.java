package cxt.robertytocerva;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import cxt.robertytocerva.entidad.Article;
import cxt.robertytocerva.repository.ConexionMongoDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        ConexionMongoDB  mongoDB = new ConexionMongoDB();

        try {
            mongoDB.crearConeccion();
        } catch (MongoException e) {
            logger.error("Error:" + e.getMessage());
            return;
        }
        mongoDB.mostrarInfoCluster();
        mongoDB.mostradDB();

        MongoCollection<Article> articulos;
        try {
            articulos = (MongoCollection<Article>) mongoDB.getCollections("tienda", "articulos", Article.class);

        } catch (Exception e) {
            mongoDB.closeConection();
            logger.error("NO se puedo acceder a la coleccion");
            logger.error("Error:" + e.getMessage());
            return;
        }
        logger.info("Se ha accedio a la coleccion articulos");
        mongoDB.closeConection();

    }
}