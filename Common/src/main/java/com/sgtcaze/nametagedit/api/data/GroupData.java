package com.sgtcaze.nametagedit.api.data;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.Objects;

/**
 * This class represents a group nametag. There
 * are several properties available.
 */
public class GroupData implements INametag {

    private String groupName;
    private String prefix;
    private String suffix;
    private String permission;
    private Permission bukkitPermission;
    private int sortPriority;

    public GroupData() {

    }

    public GroupData(String groupName, String prefix, String suffix, String permission, Permission bukkitPermission, int sortPriority) {
        this.groupName = groupName;
        this.prefix = prefix;
        this.suffix = suffix;
        this.permission = permission;
        this.bukkitPermission = bukkitPermission;
        this.sortPriority = sortPriority;
    }

    @Override
    public boolean isPlayerTag() {
        return false;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
        bukkitPermission = new Permission(permission, PermissionDefault.FALSE);
    }

    public Permission getBukkitPermission() {
        return bukkitPermission;
    }

    public void setBukkitPermission(Permission bukkitPermission) {
        this.bukkitPermission = bukkitPermission;
    }

    @Override
    public int getSortPriority() {
        return sortPriority;
    }

    public void setSortPriority(int sortPriority) {
        this.sortPriority = sortPriority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupData groupData = (GroupData) o;
        return sortPriority == groupData.sortPriority &&
                Objects.equals(groupName, groupData.groupName) &&
                Objects.equals(prefix, groupData.prefix) &&
                Objects.equals(suffix, groupData.suffix) &&
                Objects.equals(permission, groupData.permission) &&
                Objects.equals(bukkitPermission, groupData.bukkitPermission);
    }

    @Override
    public int hashCode() {

        return Objects.hash(groupName, prefix, suffix, permission, bukkitPermission, sortPriority);
    }
}