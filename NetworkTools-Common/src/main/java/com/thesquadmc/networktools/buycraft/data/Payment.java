package com.thesquadmc.networktools.buycraft.data;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public final class Payment {

    private int id;
    private double amount;
    private Date date;

    private String currency;
    private String currencySymbol;

    private int playerId;
    private String playerName;
    private UUID playerUuid;

    private Map<Integer, String> packages;

    public Payment(int id, double amount, Date date, String currency, String currencySymbol, int playerId, String playerName, UUID playerUuid, Map<Integer, String> packages) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.currency = currency;
        this.currencySymbol = currencySymbol;
        this.playerId = playerId;
        this.playerName = playerName;
        this.playerUuid = playerUuid;
        this.packages = packages;
    }

    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public Date getDate() {
        return date;
    }

    public Map<Integer, String> getPackages() {
        return packages;
    }

}
