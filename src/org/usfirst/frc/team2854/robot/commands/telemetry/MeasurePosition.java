package org.usfirst.frc.team2854.robot.commands.telemetry;

import org.usfirst.frc.team2854.robot.Robot;

import org.usfirst.frc.team2854.robot.subsystems.Telemetry;

import edu.wpi.first.wpilibj.command.Command;

public class MeasurePosition extends Command {
	public float positionMagnitude;

	public MeasurePosition() {

	}

	protected void execute() {
		positionMagnitude = (float) Robot.tm.avgEncoder() - positionMagnitude;
		Robot.position = Vec2f.add(Robot.position, new Vec2f(positionMagnitude, Robot.tm.gyro.getAngle()));//This is primarily an approximation. 
		//TODO Check if the gyro has an output of degress or radians, and adjust accordingly
	}

	@Override
	protected boolean isFinished() {
		return true;
	}

}
