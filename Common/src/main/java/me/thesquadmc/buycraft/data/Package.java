package me.thesquadmc.buycraft.data;

public final class Package {

	private int id;
	private int order;
	private String name;
	private double price;

	private boolean inSale;
	private double discount;

	public Package(int id, int order, String name, double price, boolean inSale, Double discount) {
		this.id = id;
		this.order = order;
		this.name = name;
		this.price = price;
		this.inSale = inSale;
		if (inSale){
			this.discount = discount;
		}
	}

	public int getId() {
		return id;
	}

	public int getOrder() {
		return order;
	}

	public String getName() {
		return name;
	}

	public double getPrice() {
		return price;
	}

	public boolean isInSale() {
		return inSale;
	}

	public double getDiscount() {
		return discount;
	}

}
