package at.pcgamingfreaks.exceptions;

import at.pcgamingfreaks.model.enums.MediaSource;
import at.pcgamingfreaks.model.enums.MediaType;
import at.pcgamingfreaks.model.enums.SyncType;

public class MediaSyncConflictException extends RuntimeException {
	public MediaSyncConflictException(String username, MediaSource source, MediaType type, SyncType syncType) {
		super(String.format("Conflict queuing sync for %s %s %s because %s is already running", username, source, type, syncType));
	}
}
