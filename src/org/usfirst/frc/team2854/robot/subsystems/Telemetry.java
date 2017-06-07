package org.usfirst.frc.team2854.robot.subsystems;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.AnalogGyro;

public class Telemetry extends Subsystem {
	public Encoder[] encoders;
	public AnalogGyro gyro;

	public Telemetry(int[][] encoderChannels, int gyroChannel) {
		gyro = new AnalogGyro(gyroChannel);
		for (int i = 0; i < encoderChannels[0].length; i++) {
			encoders[i] = new Encoder(encoderChannels[0][i], encoderChannels[1][i]);
		}
	}

	public double avgEncoder() {
		int sum = 0;
		for (int i = 0; i < encoders.length; i++) {
			sum = sum + encoders[i].get();
		}
		return sum / encoders.length;
	}

	public void initDefaultCommand() {

	}
}
