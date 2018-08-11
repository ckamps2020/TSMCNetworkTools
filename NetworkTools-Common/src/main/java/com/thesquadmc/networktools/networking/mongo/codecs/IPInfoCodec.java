package com.thesquadmc.networktools.networking.mongo.codecs;

import com.thesquadmc.networktools.networking.mongo.UserDatabase;
import com.thesquadmc.networktools.objects.logging.IPInfo;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

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
                document.getDate(UserDatabase.FIRST_JOINED),
                document.getDate(UserDatabase.LAST_JOINED),
                document.getInteger(UserDatabase.COUNT)
        );
    }

    @Override
    public void encode(BsonWriter bsonWriter, IPInfo info, EncoderContext encoderContext) {
        Document document = new Document("_id", info.getIP())
                .append(UserDatabase.FIRST_JOINED, info.getFirstJoined())
                .append(UserDatabase.LAST_JOINED, info.getLastJoined())
                .append(UserDatabase.COUNT, info.getCount());

        documentCodec.encode(bsonWriter, document, encoderContext);
    }

    @Override
    public Class<IPInfo> getEncoderClass() {
        return IPInfo.class;
    }
}
