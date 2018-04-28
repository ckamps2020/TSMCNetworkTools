package me.thesquadmc.networking.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import me.thesquadmc.Main;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Mongo {

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    public Mongo(String user, String db, String password, String host, int port) {
        MongoCredential credential = MongoCredential.createCredential(user, db, password.toCharArray());
        mongoClient = new MongoClient(new ServerAddress(host, port),
                Arrays.asList(credential));
        mongoDatabase = mongoClient.getDatabase(db);

        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

}
