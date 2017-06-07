package org.usfirst.frc.team2854.robot.commands.rc;

import org.usfirst.frc.team2854.robot.*;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.command.Command;

public class Drive extends Command {

	public Drive() {
	}

	protected void execute() {
		Robot.myRobot.tankDrive(Robot.oi.stick[0].getY(),Robot.oi.stick[1].getY());
	}

	protected boolean isFinished() {
		return true;
	}
}
