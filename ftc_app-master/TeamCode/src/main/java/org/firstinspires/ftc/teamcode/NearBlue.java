
/*
Copyright (c) 2016 Robert Atkinson

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


//delay, longer drive before shoot, turn for square after shoot, only shoot one or fix the mortar
@Autonomous(name="NearBlueNoWait", group="Auto")
public class NearBlue extends LinearOpMode {
    mordorHardware robot           = new mordorHardware();
    double turnTolerance;
    double currentHeading;
    double powerFloor;
    double floor = .06;
    double driveGain = .0000;
    int distance = 1200;
    double headingError;
    double driveSteering;
    double leftPower;
    double rightPower;
    double firingSpeed = .9;
    int mortarFreeState = 1440;
    int driveDistance = (433);
    boolean correctColor1 = false;
    boolean correctColor2 = false;
    boolean firstPress =true;
    int counter = 0;



    public void turnLeft(int angle, double power){
        do {

            if (robot.getAdafruitHeading() > 180) {
                currentHeading = robot.getAdafruitHeading() - 360;
            } else {
                currentHeading = robot.getAdafruitHeading();
            }
            robot.setPowerRight(power);
            robot.setPowerLeft(-power);
            robot.waitForTick(10);
            counter++;
        } while(currentHeading>angle&&opModeIsActive());
        robot.stopMotors();
    }
    public void turnRight(int angle, double power){
        do {

            if (robot.getAdafruitHeading() > 180) {
                currentHeading = robot.getAdafruitHeading() - 360;
            } else {
                currentHeading = robot.getAdafruitHeading();
            }
            robot.setPowerRight(-power);
            robot.setPowerLeft(power);
            robot.waitForTick(10);
        } while(currentHeading<angle&&opModeIsActive());
        robot.stopMotors();
    }
    public void resetEncoders(){
        robot.right_drive1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.left_drive1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.right_drive2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.left_drive2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.right_drive1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.left_drive1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.right_drive2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.left_drive2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
    public void resetMaxSpeed(){
        robot.left_drive1.setMaxSpeed(4000);
        robot.left_drive2.setMaxSpeed(4000);
        robot.right_drive1.setMaxSpeed(4000);
        robot.right_drive2.setMaxSpeed(4000);
    }
    public void positionToShoot() {
        resetEncoders();
        robot.magazine_cam.setPosition(robot.camUp);
        while (robot.left_drive1.getCurrentPosition()<driveDistance||robot.left_drive2.getCurrentPosition()<driveDistance||robot.right_drive1.getCurrentPosition()<driveDistance||robot.right_drive2.getCurrentPosition()<driveDistance&&opModeIsActive()) {

            robot.setPowerLeft(.2);
            robot.setPowerRight(.2);
            robot.waitForTick(10);
        }
        robot.stopMotors();
    }
    public void hitBeacon(){
        robot.setPowerLeft(.15);
        robot.setPowerRight(.15);
        sleep(1100);
        robot.stopMotors();
    }
    public void backUp(){
        robot.setPowerLeft(-.15);
        robot.setPowerRight(-.15);
        sleep(500);
        robot.stopMotors();

    }
    public void detectColor(){
        if (robot.frontColor.blue() > robot.frontColor.red()) {
            correctColor1 =true;
        }
    }
    public void shootBall() {
        robot.mortar.setPower(firingSpeed);
        robot.mortar.setTargetPosition(mortarFreeState);
        robot.mortar_gate.setPosition(robot.mortarGateDown);
        sleep(500);
        robot.mortar_gate.setPosition(robot.mortarGateUp);
        sleep(1000);
        robot.mortar_gate.setPosition(robot.mortarGateDown);
        robot.mortar.setPower(firingSpeed);
        robot.mortar.setTargetPosition(mortarFreeState * 2);
    }
    public void capBall() {
        while (robot.right_drive1.getCurrentPosition() < (driveDistance * 2.25)
                && robot.left_drive2.getCurrentPosition() < (driveDistance * 2.25)&&opModeIsActive()) {
            robot.left_drive1.setPower(.1);
            robot.left_drive2.setPower(.1);
            robot.right_drive1.setPower(.2);
            robot.right_drive2.setPower(.2);
        }
        robot.stopMotors();

    }


    public void findWhiteLine() {
        while (robot.floor_seeker.green() < 6&&opModeIsActive()) {
            robot.left_drive1.setPower(0.8);
            robot.left_drive2.setPower(0.8);
            robot.right_drive1.setPower(0.8);
            robot.right_drive2.setPower(0.8);
            robot.left_drive1.setMaxSpeed(500);
            robot.left_drive2.setMaxSpeed(500);
            robot.right_drive1.setMaxSpeed(500);
            robot.right_drive2.setMaxSpeed(500);
        }
        sleep(150);
        robot.stopMotors();
    }

    public void firstBeaconPress() {
        resetMaxSpeed();
        turnRight(75, .17);
        while(!correctColor1&&opModeIsActive()) {
            if(!firstPress){
                sleep(3000);
            }
            hitBeacon();
            sleep(900);
            detectColor();
            backUp();
            firstPress = false;
        }
    }
    public void secondBeaconPress(){
        resetMaxSpeed();
        turnRight(75, .17);
        firstPress =true;
        while(!correctColor2&&opModeIsActive()) {
            if(!firstPress){
                sleep(3000);
            }
            hitBeacon();
            sleep(900);
            detectColor();
            backUp();
            firstPress = false;
        }


    }
    public void driveStraight(){
        resetEncoders();
        while(robot.left_drive1.getCurrentPosition()<distance||robot.left_drive2.getCurrentPosition()<distance||robot.right_drive1.getCurrentPosition()<distance||robot.right_drive2.getCurrentPosition()<distance&&opModeIsActive()){
            robot.setPowerRight(.8);
            robot.setPowerLeft(.8);
            robot.left_drive1.setMaxSpeed(1000);
            robot.left_drive2.setMaxSpeed(1000);
            robot.right_drive1.setMaxSpeed(1000);
            robot.right_drive2.setMaxSpeed(1000);
            robot.waitForTick(10);
        }
        robot.stopMotors();
    }

    public void turnToWall(){
        turnRight(47, .13);
        robot.waitForTick(10);
    }
    public void turnNormal(){
        turnLeft(10,.1);
        robot.waitForTick(10);
    }
    @Override
    public void runOpMode() {
        robot.init(hardwareMap);

        waitForStart();
        resetStartTime();
       // positionToShoot();
        //robot.waitForTick(10);
        //shootBall();

        turnLeft(-47, .13);
        sleep(1000);
        while(opModeIsActive()){
            telemetry.update();
            telemetry.addData("counter", counter);
        }
        //turnToWall();
        //driveStraight();
        //resetMaxSpeed();
        //turnLeft(36, .22);
        //findWhiteLine();
        //firstBeaconPress();
        //backUp();
        //turnLeft(20, .17);
        //findWhiteLine();
        //secondBeaconPress();
        //capBall();
    }
}