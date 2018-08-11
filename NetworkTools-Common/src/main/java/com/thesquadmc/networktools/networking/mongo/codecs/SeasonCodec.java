package com.thesquadmc.networktools.networking.mongo.codecs;

import com.thesquadmc.networktools.player.stats.Season;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class SeasonCodec implements Codec<Season> {

    private final Codec<Document> documentCodec;

    public SeasonCodec(Codec<Document> documentCodec) {
        this.documentCodec = documentCodec;
    }

    @Override
    public Season decode(BsonReader bsonReader, DecoderContext decoderContext) {
        Document document = documentCodec.decode(bsonReader, decoderContext);
        return Season.fromDocument(document);
    }

    @Override
    public void encode(BsonWriter bsonWriter, Season season, EncoderContext encoderContext) {
        Document document = season.toDocument();
        documentCodec.encode(bsonWriter, document, encoderContext);
    }

    @Override
    public Class<Season> getEncoderClass() {
        return Season.class;
    }
}
