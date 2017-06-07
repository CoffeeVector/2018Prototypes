package org.usfirst.frc.team2854.robot.commands.telemetry;

public class Vec2f {
	public float x, y;

	public Vec2f(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vec2f(float m, double theta) {
		x = (float) (m * Math.cos(theta));// Radians
		y = (float) (m * Math.sin(theta));
	}
	
	public static Vec2f add(Vec2f v,Vec2f v2){
		Vec2f out = new Vec2f(0,0);
		out.x = v.x + v2.x;
		out.y = v.y + v2.y;
		return out; 
	}

}
