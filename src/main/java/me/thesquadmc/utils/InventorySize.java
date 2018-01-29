package me.thesquadmc.utils;

public enum InventorySize {

	ONE_LINE(9),
	TWO_LINE(18),
	THREE_LINE(27),
	FOUR_LINE(36),
	FIVE_LINE(45),
	SIX_LINE(54),
	;

	private int size;

	InventorySize(int size) {
		this.size = size;
	}

	public int getSize() { return size; }

}
