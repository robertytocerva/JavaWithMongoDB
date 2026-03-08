package cxt.robertytocerva;

import com.mongodb.MongoException;
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
        mongoDB.closeConection();

    }
}