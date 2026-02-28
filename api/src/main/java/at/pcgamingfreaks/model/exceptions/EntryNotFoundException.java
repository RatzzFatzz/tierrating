package at.pcgamingfreaks.model.exceptions;

import at.pcgamingfreaks.model.ContentType;

public class EntryNotFoundException extends RuntimeException {
    public EntryNotFoundException(String message) {
        super(message);
    }

    public EntryNotFoundException(ContentType type, long id) {
        super(String.format("%s entry with id %d not found", type, id));
    }
}
