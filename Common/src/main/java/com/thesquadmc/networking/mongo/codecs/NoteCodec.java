package com.thesquadmc.networking.mongo.codecs;

import com.thesquadmc.objects.logging.Note;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.UUID;

import static com.thesquadmc.networking.mongo.UserDatabase.CREATOR;
import static com.thesquadmc.networking.mongo.UserDatabase.CREATOR_NAME;
import static com.thesquadmc.networking.mongo.UserDatabase.MESSAGE;
import static com.thesquadmc.networking.mongo.UserDatabase.TIMESTAMP;

public class NoteCodec implements Codec<Note> {

    private final Codec<Document> documentCodec;

    public NoteCodec(Codec<Document> documentCodec) {
        this.documentCodec = documentCodec;
    }

    @Override
    public Note decode(BsonReader bsonReader, DecoderContext decoderContext) {
        Document document = documentCodec.decode(bsonReader, decoderContext);

        return new Note(
                document.getString("_id"),
                document.get(CREATOR, UUID.class),
                document.getString(CREATOR_NAME),
                document.getDate(TIMESTAMP),
                document.getString(MESSAGE)
        );
    }

    @Override
    public void encode(BsonWriter bsonWriter, Note note, EncoderContext encoderContext) {
        Document document = new Document("_id", note.getIdentifier())
                .append(CREATOR, note.getCreator())
                .append(CREATOR_NAME, note.getCreatorName())
                .append(TIMESTAMP, note.getTimestamp())
                .append(MESSAGE, note.getNote());

        documentCodec.encode(bsonWriter, document, encoderContext);
    }

    @Override
    public Class<Note> getEncoderClass() {
        return Note.class;
    }
}
