package com.thesquadmc.networktools.abstraction.v1_8_R3;

import com.mojang.authlib.properties.Property;
import com.thesquadmc.networktools.abstraction.ProfileProperty;

public class ProfileProperty1_8_R3 implements ProfileProperty {

	private final Property property;

	public ProfileProperty1_8_R3(String name, String value, String signature) {
		this.property = new Property(name, value, signature);
	}

	public ProfileProperty1_8_R3(Property property) {
		this.property = property;
	}

	@Override
	public Object getHandle() {
		return property;
	}

	@Override
	public String getName() {
		return property.getName();
	}

	@Override
	public String getValue() {
		return property.getValue();
	}

	@Override
	public String getSignature() {
		return property.getSignature();
	}

}