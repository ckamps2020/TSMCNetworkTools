package com.thesquadmc.buycraft.data;

public final class PlayerQueue {

    private boolean executeOffline;
    private int nextCheck;
    private boolean more;

    public PlayerQueue(boolean executeOffline, int nextCheck, boolean more) {
        this.executeOffline = executeOffline;
        this.nextCheck = nextCheck;
        this.more = more;
    }

    public boolean isExecuteOffline() {
        return executeOffline;
    }

    public int getNextCheck() {
        return nextCheck;
    }

    public boolean isMore() {
        return more;
    }

}
