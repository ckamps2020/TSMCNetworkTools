package me.thesquadmc.utils.handlers;

public class UpdateEvent extends ToolsHandler {

	private final UpdateType type;

	public UpdateEvent(UpdateType type) {
		this.type = type;
	}

	public UpdateType getUpdateType() {
		return this.type;
	}

}
