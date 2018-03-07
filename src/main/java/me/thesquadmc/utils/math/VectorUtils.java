package me.thesquadmc.utils.math;

import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public final class VectorUtils {

	public static final Vector rotateAroundAxisX(Vector v, double angle) {
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		double y = v.getY() * cos - v.getZ() * sin;
		double z = v.getY() * sin + v.getZ() * cos;
		return v.setY(y).setZ(z);
	}

	public static final Vector rotateAroundAxisY(Vector v, double angle) {
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		double x = v.getX() * cos + v.getZ() * sin;
		double z = v.getX() * -sin + v.getZ() * cos;
		return v.setX(x).setZ(z);
	}

	public static final Vector rotateAroundAxisZ(Vector v, double angle) {
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		double x = v.getX() * cos - v.getY() * sin;
		double y = v.getX() * sin + v.getY() * cos;
		return v.setX(x).setY(y);
	}

	public static final Vector rotateVector(Vector v, double angleX, double angleY, double angleZ) {
		rotateAroundAxisX(v, angleX);
		rotateAroundAxisY(v, angleY);
		rotateAroundAxisZ(v, angleZ);
		return v;
	}

	public static final double angleToXAxis(Vector vector) {
		return Math.atan2(vector.getX(), vector.getY());
	}

	public static Vector getRandomVector() {
		double x = ThreadLocalRandom.current().nextDouble() * 2.0D - 1.0D;
		double y = ThreadLocalRandom.current().nextDouble() * 2.0D - 1.0D;
		double z = ThreadLocalRandom.current().nextDouble() * 2.0D - 1.0D;

		return new Vector(x, y, z).normalize();
	}

	public static Vector getRandomCircleVector() {
		double rnd = ThreadLocalRandom.current().nextDouble() * 2.0D * Math.PI;
		double x = Math.cos(rnd);
		double z = Math.sin(rnd);

		return new Vector(x, 0.0D, z);
	}

	public static double getRandomAngle() {
		return ThreadLocalRandom.current().nextDouble() * 2.0D * Math.PI;
	}

}
