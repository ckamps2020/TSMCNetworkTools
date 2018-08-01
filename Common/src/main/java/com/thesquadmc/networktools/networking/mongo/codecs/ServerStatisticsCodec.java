package com.thesquadmc.networktools.networking.mongo.codecs;

import com.thesquadmc.networktools.player.ServerStatistics;
import com.thesquadmc.networktools.utils.server.ServerType;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class ServerStatisticsCodec implements Codec<ServerStatistics> {

    private final Codec<Document> documentCodec;

    public ServerStatisticsCodec(Codec<Document> documentCodec) {
        this.documentCodec = documentCodec;
    }

    @Override
    public ServerStatistics decode(BsonReader bsonReader, DecoderContext decoderContext) {
        Document document = documentCodec.decode(bsonReader, decoderContext);

        return new ServerStatistics(
                document.getString("_id"),
                document.get("server_type", ServerType.class),
                document.getLong("play_time"),
                document.getInteger("logins"),
                document.getInteger("blocks_broken")
        );
    }

    @Override
    public void encode(BsonWriter bsonWriter, ServerStatistics stats, EncoderContext encoderContext) {
        Document document = new Document("_id", stats.getServerName())
                .append("server_type", stats.getType())
                .append("play_time", stats.getPlaytime())
                .append("logins", stats.getLogins())
                .append("blocks_broken", stats.getBlocksBroken());

        documentCodec.encode(bsonWriter, document, encoderContext);
    }

    @Override
    public Class<ServerStatistics> getEncoderClass() {
        return ServerStatistics.class;
    }
}
