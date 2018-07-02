package me.thesquadmc.abstraction.v1_8_R3;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import me.thesquadmc.utils.json.JSONUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_8_R3.EntityHorse;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import net.minecraft.server.v1_8_R3.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldBorder.EnumWorldBorderAction;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.WorldBorder;

import me.thesquadmc.Main;
import me.thesquadmc.abstraction.BossBarManager;
import me.thesquadmc.abstraction.HumanNPC;
import me.thesquadmc.abstraction.MobNPC;
import me.thesquadmc.abstraction.MojangGameProfile;
import me.thesquadmc.abstraction.NMSAbstract;
import me.thesquadmc.abstraction.ProfileProperty;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.server.ServerProperty;

public class NMSAbstract1_8_R3 implements NMSAbstract {

    private final BossBarManager bossBarManager = new BossBarManager1_8_R3();
    private final Map<UUID, Integer> horses = new HashMap<>();

    @Override
    public String getVersionMin() {
        return "1.8.4";
    }

    @Override
    public String getVersionMax() {
        return "1.8.8";
    }

    @Override
    public int getPing(Player player) {
        return ((CraftPlayer) player).getHandle().ping;
    }

    @Override
    public MojangGameProfile getGameProfile(OfflinePlayer player) {
        return new MojangGameProfile1_8_R3(((CraftPlayer) player).getProfile());
    }

    @Override
    public ProfileProperty getSkinProperty(OfflinePlayer player) {
        return (player != null ? getSkinProperty(player.getName()) : null);
    }

    @Override
    public ProfileProperty getSkinProperty(String name) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("https://use.gameapis.net/mc/player/profile/" + name).openStream()))) {
            Gson gson = JSONUtils.getGson();

            JsonObject root = gson.fromJson(reader, JsonObject.class);
            if (!root.has("properties")) return null;

            JsonObject properties = root.getAsJsonObject("properties");
            String propertyName = properties.get("name").getAsString();
            String propertyValue = properties.get("value").getAsString();
            String propertySignature = properties.get("signature").getAsString();

            return new ProfileProperty1_8_R3(propertyName, propertyValue, propertySignature);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ProfileProperty createNewProperty(String name, String value, String signature) {
        return new ProfileProperty1_8_R3(name, value, signature);
    }

    @Override
    public HumanNPC createHumanNPC(Location location, String name, String skin) {
        return new HumanNPC1_8_R3(location, name, skin);
    }

    @Override
    public MobNPC createMobNPC(String name, String displayName, EntityType type, boolean ai) {
        return new MobNPC1_8_R3(name, displayName, type, ai);
    }

    @Override
    public void savePropertiesFile() {
        ((CraftServer) Bukkit.getServer()).getServer().getPropertyManager().savePropertiesFile();
    }

    @Override
    public <T> void setServerProperty(ServerProperty<T> property, T value) {
        ((CraftServer) Bukkit.getServer()).getServer().getPropertyManager().setProperty(property.getPropertyName(), value);
    }

    @Override
    public void setAI(LivingEntity entity, boolean ai) {
        EntityLiving nmsEntity = ((CraftLivingEntity) entity).getHandle();

        NBTTagCompound tag = new NBTTagCompound();
        nmsEntity.c(tag);
        tag.setInt("NoAI", ai ? 0 : 1);
        nmsEntity.a(tag);
    }

    @Override
    public BossBarManager getBossBarManager() {
        return bossBarManager;
    }

    @Override
    public void setLookDirection(Entity entity, float pitch, float yaw) {
        if (entity == null) return;

        net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        yaw = toYawThatMakesNoSense(yaw);
        nmsEntity.yaw = yaw;
        nmsEntity.pitch = pitch;

        if (entity instanceof LivingEntity) {
            this.setHeadYaw((LivingEntity) entity, yaw);
        }
    }

    @Override
    public void setHeadYaw(LivingEntity entity, float yaw) {
        if (entity == null) return;

        EntityLiving nmsEntity = ((CraftLivingEntity) entity).getHandle();
        yaw = toYawThatMakesNoSense(yaw);

        nmsEntity.aK = yaw;
        if (!(nmsEntity instanceof EntityHuman)) {
            nmsEntity.aI = yaw;
        }

        nmsEntity.aL = yaw;
    }

    @Override
    public void sendTitle(Player player, String title, String subtitle, int in, int stay, int out) {
        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\": \"\"}").a(CC.translate(title)));
        PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\": \"\"}").a(CC.translate(subtitle)));
        PacketPlayOutTitle timingsPacket = new PacketPlayOutTitle(in, stay, out);

        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(titlePacket);
        connection.sendPacket(subtitlePacket);
        connection.sendPacket(timingsPacket);
    }

    @Override
    public void broadcastTitle(String title, String subtitle, int in, int stay, int out) {
        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\": \"\"}").a(CC.translate(title)));
        PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\": \"\"}").a(CC.translate(subtitle)));
        PacketPlayOutTitle timingsPacket = new PacketPlayOutTitle(in, stay, out);

        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            connection.sendPacket(titlePacket);
            connection.sendPacket(subtitlePacket);
            connection.sendPacket(timingsPacket);
        }
    }

    @Override
    public void sendActionBar(Player player, String text) {
        PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + CC.translate(text) + "\"}"), (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public void broadcastActionBar(String text) {
        PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + CC.translate(text) + "\"}"), (byte) 2);

        for (Player player : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    @Override
    public void sendTabList(Player player, String header, String footer) {
        PacketPlayOutPlayerListHeaderFooter packet = preparePlayerListPacket(header, footer);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public void broadcastTabList(String header, String footer) {
        PacketPlayOutPlayerListHeaderFooter packet = preparePlayerListPacket(header, footer);

        for (Player player : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    @Override
    public void sit(Player player) {
        Location location = player.getLocation();
        EntityHorse horse = new EntityHorse(((CraftWorld) player.getWorld()).getHandle());

        horse.setLocation(location.getX(), location.getY(), location.getZ(), 0, 0);
        horse.setInvisible(true);

        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        PlayerConnection connection = nmsPlayer.playerConnection;

        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(horse);
        connection.sendPacket(packet);
        PacketPlayOutAttachEntity sit = new PacketPlayOutAttachEntity(0, nmsPlayer, horse);
        connection.sendPacket(sit);

        this.horses.put(player.getUniqueId(), horse.getId());
    }

    @Override
    public void stand(Player player) {
        int horseId = horses.getOrDefault(player.getUniqueId(), -1);
        if (horseId == -1) return;

        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(horseId);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public Set<Player> getSitting() {
        return horses.keySet().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public void sendWorldBorder(Player player, int warningBlocks) {
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        WorldBorder border = nmsPlayer.world.getWorldBorder();
        PacketPlayOutWorldBorder packetWorldBorder = new PacketPlayOutWorldBorder(border, EnumWorldBorderAction.SET_WARNING_BLOCKS);

        try {
            Field fieldI = border.getClass().getDeclaredField("i");
            fieldI.setAccessible(true);
            fieldI.setInt(border, warningBlocks);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        nmsPlayer.playerConnection.sendPacket(packetWorldBorder);
    }

    @Override
    public String toBase64(ItemStack item) {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             DataOutputStream dataOutput = new DataOutputStream(byteStream)) {
            NBTTagList tagList = new NBTTagList();
            NBTTagCompound tagCompound = new NBTTagCompound();

            net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
            nmsItem.save(tagCompound);
            tagList.add(tagCompound);

            NBTCompressedStreamTools.a(tagCompound, (DataOutput) dataOutput);
            return new BigInteger(1, byteStream.toByteArray()).toString(32);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ItemStack fromBase64(String data) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(new BigInteger(data, 32).toByteArray())) {
            NBTTagCompound tagCompound = NBTCompressedStreamTools.a(new DataInputStream(inputStream));
            return CraftItemStack.asBukkitCopy(net.minecraft.server.v1_8_R3.ItemStack.createStack(tagCompound));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemStack setMonsterEggType(ItemStack item, EntityType type) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);

        NBTTagCompound idTag = new NBTTagCompound();
        idTag.setString("id", type.getName());
        NBTTagCompound tag = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
        tag.set("EntityTag", idTag);
        nmsItem.setTag(tag);

        return CraftItemStack.asBukkitCopy(nmsItem);
    }


    private float toYawThatMakesNoSense(float yaw) {
        while (yaw < -180.0F) {
            yaw += 360.0F;
        }
        while (yaw >= 180.0F) {
            yaw -= 360.0F;
        }
        return yaw;
    }

    private PacketPlayOutPlayerListHeaderFooter preparePlayerListPacket(String header, String footer) {
        IChatBaseComponent headerComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + CC.translate(header) + "\"}");
        IChatBaseComponent footerComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + CC.translate(footer) + "\"}");
        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();

        try {
            Field headerField = packet.getClass().getDeclaredField("a");
            headerField.setAccessible(true);
            headerField.set(packet, headerComponent);

            Field footerField = packet.getClass().getDeclaredField("b");
            footerField.setAccessible(true);
            footerField.set(packet, footerComponent);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        return packet;
    }

}