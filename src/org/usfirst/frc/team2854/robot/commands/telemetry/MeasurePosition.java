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
		Robot.position = Vec2f.add(Robot.position, new Vec2f(positionMagnitude, Robot.tm.gyro.getAngle()));
	}

	@Override
	protected boolean isFinished() {
		return true;
	}

}
