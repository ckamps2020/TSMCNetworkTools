package com.sgtcaze.nametagedit.api;

import com.sgtcaze.nametagedit.NametagHandler;
import com.sgtcaze.nametagedit.NametagManager;
import com.sgtcaze.nametagedit.api.data.FakeTeam;
import com.sgtcaze.nametagedit.api.data.Nametag;
import org.bukkit.entity.Player;

/**
 * Implements the INametagAPI interface. There only
 * exists one instance of this class.
 */
public final class NametagAPI implements INametagApi {

    private NametagHandler handler;
    private NametagManager manager;

    public NametagAPI(NametagHandler handler, NametagManager manager) {
        this.handler = handler;
        this.manager = manager;
    }

    @Override
    public FakeTeam getFakeTeam(Player player) {
        return manager.getFakeTeam(player.getName());
    }

    @Override
    public Nametag getNametag(Player player) {
        FakeTeam team = manager.getFakeTeam(player.getName());
        boolean nullTeam = team == null;
        return new Nametag(nullTeam ? "" : team.getPrefix(), nullTeam ? "" : team.getSuffix());
    }

    @Override
    public void clearNametag(Player player) {
        manager.reset(player.getName());
    }

    @Override
    public void reloadNametag(Player player) {
        handler.applyTagToPlayer(player, false);
    }

    @Override
    public void clearNametag(String player) {
        manager.reset(player);
    }

    @Override
    public void setPrefix(Player player, String prefix) {
        FakeTeam fakeTeam = manager.getFakeTeam(player.getName());
        setNametagAlt(player, prefix, fakeTeam == null ? null : fakeTeam.getSuffix());
    }

    @Override
    public void setSuffix(Player player, String suffix) {
        FakeTeam fakeTeam = manager.getFakeTeam(player.getName());
        setNametagAlt(player, fakeTeam == null ? null : fakeTeam.getPrefix(), suffix);
    }

    @Override
    public void setPrefix(String player, String prefix) {
        FakeTeam fakeTeam = manager.getFakeTeam(player);
        manager.setNametag(player, prefix, fakeTeam == null ? null : fakeTeam.getSuffix());
    }

    @Override
    public void setSuffix(String player, String suffix) {
        FakeTeam fakeTeam = manager.getFakeTeam(player);
        manager.setNametag(player, fakeTeam == null ? null : fakeTeam.getPrefix(), suffix);
    }

    @Override
    public void setNametag(Player player, String prefix, String suffix) {
        setNametagAlt(player, prefix, suffix);
    }

    @Override
    public void setNametag(String player, String prefix, String suffix) {
        manager.setNametag(player, prefix, suffix);
    }

    /**
     * Private helper function to reduce redundancy
     */
    private void setNametagAlt(Player player, String prefix, String suffix) {
        Nametag nametag = new Nametag(
                handler.formatWithPlaceholders(player, prefix, true),
                handler.formatWithPlaceholders(player, suffix, true)
        );

        manager.setNametag(player.getName(), nametag.getPrefix(), nametag.getSuffix());
    }

}