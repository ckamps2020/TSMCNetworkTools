package me.thesquadmc.listeners.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import me.thesquadmc.Main;
import me.thesquadmc.player.TSMCUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TagAdaptionListener extends PacketAdapter {
	
	private static final int[] UUID_SPLIT_INDICES = { 0, 8, 12, 16, 20, 32 };
	
	private final Map<Integer, UUID> entityIdMap = new HashMap<>();
	private final List<PlayerInfoData> playerInfoDataNew = new ArrayList<>();
	
	public TagAdaptionListener(Main main) {
		super(main, PacketType.Play.Server.PLAYER_INFO);
	}

	@Override
	public void onPacketSending(PacketEvent event) {
		PacketContainer packet = event.getPacket();
		if (packet.getPlayerInfoAction().read(0) != PlayerInfoAction.ADD_PLAYER) return;
		
		for (PlayerInfoData data : packet.getPlayerInfoDataLists().read(0)) {
			Player player = null;
			if (data == null || data.getProfile() == null || (player = Bukkit.getPlayer(data.getProfile().getUUID())) == null) {
				this.playerInfoDataNew.add(data);
				continue;
			}
			
			// TODO: Update the tab list with the nickname
			this.playerInfoDataNew.add(new PlayerInfoData(getUpdatedProfile(player.getEntityId(), data.getProfile()), data.getLatency(), data.getGameMode(), data.getDisplayName()));
		}
		
		packet.getPlayerInfoDataLists().write(0, playerInfoDataNew);
		this.playerInfoDataNew.clear();
	}
	
	public void registerEntityId(Player player) {
		this.entityIdMap.put(player.getEntityId(), player.getUniqueId());
	}
	
	public void unregisterEntityId(Player player) {
		this.entityIdMap.remove(player.getEntityId());
	}
	
	private WrappedGameProfile getUpdatedProfile(int entityId, WrappedGameProfile oldProfile) {
		Player namedPlayer = Bukkit.getPlayer(entityIdMap.get(entityId));
		if (namedPlayer == null) return oldProfile;
		
		String profileId = oldProfile.getId();
		StringBuilder uuid = new StringBuilder();
		if (!profileId.contains("-")) { // If it's a shortened UUID, make it full-length
			for (int i = 0; i < UUID_SPLIT_INDICES.length; i++) {
                uuid.append(oldProfile.getId(), UUID_SPLIT_INDICES[i], UUID_SPLIT_INDICES[i + 1]).append('-');
			}
		} else {
			uuid.append(profileId);
		}
		
		String nickname = TSMCUser.fromPlayer(namedPlayer).getNickname();
		WrappedGameProfile profile = new WrappedGameProfile(UUID.fromString(uuid.toString()), nickname.substring(0, Math.min(nickname.length(), 16)));
		
		// TODO: Update skin to that of the disguised player
		profile.getProperties().clear();
		profile.getProperties().putAll(oldProfile.getProperties()); // Transfer skin data to the new profile
		return profile;
	}
	
}