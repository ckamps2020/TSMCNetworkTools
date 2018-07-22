package com.thesquadmc.networktools.networking.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.thesquadmc.networktools.networking.mongo.codecs.IPInfoCodec;
import com.thesquadmc.networktools.networking.mongo.codecs.NoteCodec;
import org.bson.BSON;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MongoManager {


    static {
        BSON.addEncodingHook(UUID.class, String::valueOf);
    }

    private final CodecRegistry codecRegistry;

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    public MongoManager(String user, String db, String password, String host, int port) {
        Codec<Document> defaultDocumentCodec = MongoClient.getDefaultCodecRegistry().get(Document.class);
        codecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromCodecs(
                        new NoteCodec(defaultDocumentCodec),
                        new IPInfoCodec(defaultDocumentCodec)
                ));

        MongoCredential credential = MongoCredential.createCredential(user, db, password.toCharArray());
        MongoClientOptions options = MongoClientOptions.builder().codecRegistry(codecRegistry).build();

        mongoClient = new MongoClient(new ServerAddress(host, port), Arrays.asList(credential), options);
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

    public MongoCollection<Document> getCollection(String name) {
        return mongoDatabase.getCollection(name);
    }
}
