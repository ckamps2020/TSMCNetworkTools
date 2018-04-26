package me.thesquadmc.networking.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import org.bson.*;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.UuidCodec;
import org.bson.types.Binary;

import java.util.Arrays;
import java.util.UUID;

public final class Mongo {

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

    }

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    public Mongo(String user, String db, String password, String host, int port) {
        MongoCredential credential = MongoCredential.createCredential(user, db, password.toCharArray());
        mongoClient = new MongoClient(new ServerAddress(host, port),
                Arrays.asList(credential));

        mongoDatabase = mongoClient.getDatabase(db);
    }


	public MongoClient getMongoClient() {
		return mongoClient;
	}

	public MongoDatabase getMongoDatabase() {
		return mongoDatabase;
	}

}
