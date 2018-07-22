package me.thesquadmc.networking.mongo.codecs;

import me.thesquadmc.objects.logging.IPInfo;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import static me.thesquadmc.networking.mongo.UserDatabase.COUNT;
import static me.thesquadmc.networking.mongo.UserDatabase.FIRST_JOINED;
import static me.thesquadmc.networking.mongo.UserDatabase.LAST_JOINED;

public class IPInfoCodec implements Codec<IPInfo> {

    private final Codec<Document> documentCodec;

    public IPInfoCodec(Codec<Document> documentCodec) {
        this.documentCodec = documentCodec;
    }

    @Override
    public IPInfo decode(BsonReader bsonReader, DecoderContext decoderContext) {
        Document document = documentCodec.decode(bsonReader, decoderContext);

        return new IPInfo(
                document.getString("_id"),
                document.getDate(FIRST_JOINED),
                document.getDate(LAST_JOINED),
                document.getInteger(COUNT)
        );
    }

    @Override
    public void encode(BsonWriter bsonWriter, IPInfo info, EncoderContext encoderContext) {
        Document document = new Document("_id", info.getIP())
                .append(FIRST_JOINED, info.getFirstJoined())
                .append(LAST_JOINED, info.getLastJoined())
                .append(COUNT, info.getCount());

        documentCodec.encode(bsonWriter, document, encoderContext);
    }

    @Override
    public Class<IPInfo> getEncoderClass() {
        return IPInfo.class;
    }
}
