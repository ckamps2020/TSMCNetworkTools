package com.thesquadmc.networktools.utils.handlers;

import com.thesquadmc.networktools.utils.enums.UpdateType;

public class UpdateEvent extends BaseEvent {

    private final UpdateType type;

    public UpdateEvent(UpdateType type) {
        this.type = type;
    }

    public UpdateType getUpdateType() {
        return this.type;
    }

}
