package com.thesquadmc.networktools.networking.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.thesquadmc.networktools.networking.mongo.codecs.IPInfoCodec;
import com.thesquadmc.networktools.networking.mongo.codecs.NoteCodec;
import com.thesquadmc.networktools.networking.mongo.codecs.SeasonCodec;
import com.thesquadmc.networktools.player.stats.Stat;
import org.bson.BSON;
import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bson.codecs.Codec;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.UuidCodec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.Binary;

import java.util.Collections;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MongoManager {


    static {
        BSON.addEncodingHook(UUID.class, objectToTransform -> {
            UUID uuid = (UUID) objectToTransform;
            BsonDocument holder = new BsonDocument();

            // Use UUIDCodec to encode the UUID using binary subtype 4
            BsonDocumentWriter writer = new BsonDocumentWriter(holder);
            writer.writeStartDocument();
            writer.writeName("uuid");
            new UuidCodec(UuidRepresentation.JAVA_LEGACY).encode(writer, uuid, EncoderContext.builder().build());
            writer.writeEndDocument();

            BsonBinary bsonBinary = holder.getBinary("uuid");
            return new Binary(bsonBinary.getType(), bsonBinary.getData());
        });

        BSON.addEncodingHook(Stat.class, Object::toString);
    }

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    public MongoManager(String user, String db, String password, String host, int port) {
        Codec<Document> documentCodec = MongoClient.getDefaultCodecRegistry().get(Document.class);
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromCodecs(
                        new NoteCodec(documentCodec),
                        new SeasonCodec(documentCodec),
                        new IPInfoCodec(documentCodec)
                ));

        MongoCredential credential = MongoCredential.createCredential(user, db, password.toCharArray());
        MongoClientOptions options = MongoClientOptions.builder().codecRegistry(codecRegistry).build();

        mongoClient = new MongoClient(new ServerAddress(host, port), Collections.singletonList(credential), options);
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
