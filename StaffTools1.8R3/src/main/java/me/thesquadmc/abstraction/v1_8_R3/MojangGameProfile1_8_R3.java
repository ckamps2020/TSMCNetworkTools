package me.thesquadmc.abstraction.v1_8_R3;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.UUID;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import me.thesquadmc.abstraction.MojangGameProfile;
import me.thesquadmc.abstraction.ProfileProperty;

public class MojangGameProfile1_8_R3 implements MojangGameProfile {

	private final GameProfile handle;

	public MojangGameProfile1_8_R3(GameProfile handle) {
		this.handle = handle;
	}

	@Override
	public UUID getID() {
		return handle.getId();
	}

	@Override
	public void setName(String name) {
		try {
			Field fieldName = handle.getClass().getDeclaredField("name");
			fieldName.setAccessible(true);
			fieldName.set(handle, name);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return handle.getName();
	}

	@Override
	public void addProperty(String propertyName, ProfileProperty property) {
		this.handle.getProperties().put(propertyName, new Property(property.getName(), property.getValue(), property.getValue()));
	}

	@Override
	public void removeProperty(String property) {
		this.handle.getProperties().removeAll(property);
	}

	@Override
	public Multimap<String, ProfileProperty> getPropertyMap() {
		Multimap<String, ProfileProperty> propertyMap = LinkedHashMultimap.create();

		for (Entry<String, Collection<Property>> propertyEntry : handle.getProperties().asMap().entrySet()) {
			String key = propertyEntry.getKey();
			for (Property property : propertyEntry.getValue()) {
				propertyMap.put(key, new ProfileProperty1_8_R3(property));
			}
		}

		return propertyMap;
	}

	@Override
	public boolean isLegacy() {
		return false;
	}

}