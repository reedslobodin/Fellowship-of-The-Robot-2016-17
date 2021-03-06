package org.firstinspires.ftc.robotcontroller.external.samples;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchImpl;
import com.qualcomm.robotcore.util.ElapsedTime;


@TeleOp(name = "Range Sensors", group = "MRI")
//@Disabled
public class MRI_Range_Sensors extends OpMode {

    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();

    byte[] rangeAcache;
    byte[] rangeCcache;
    byte[] rangeDcache;

    I2cDevice rangeA;
    I2cDevice rangeC;
    I2cDevice rangeD;
    I2cDeviceSynch rangeAreader;
    I2cDeviceSynch rangeCreader;
    I2cDeviceSynch rangeDreader;

    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");

        rangeA = hardwareMap.i2cDevice.get("range28");
        rangeC = hardwareMap.i2cDevice.get("range2a");
        rangeD = hardwareMap.i2cDevice.get("range30");

        rangeAreader = new I2cDeviceSynchImpl(rangeA, I2cAddr.create8bit(0x28), false);
        rangeCreader = new I2cDeviceSynchImpl(rangeC, I2cAddr.create8bit(0x2a), false);
        rangeDreader = new I2cDeviceSynchImpl(rangeD, I2cAddr.create8bit(0x30), false);

        rangeAreader.engage();
        rangeCreader.engage();
        rangeDreader.engage();
    }

    @Override
    public void loop() {
        telemetry.addData("Status", "Running: " + runtime.toString());

        rangeAcache = rangeAreader.read(0x04, 2);  //Read 2 bytes starting at 0x04
        rangeCcache = rangeCreader.read(0x04, 2);
        rangeDcache = rangeDreader.read(0x04, 2);

        int RUS = rangeCcache[0] & 0xFF;   //Ultrasonic value is at index 0. & 0xFF creates a value between 0 and 255 instead of -127 to 128
        int LUS = rangeAcache[0] & 0xFF;
        int CUS = rangeDcache[0] & 0xFF;
        int RODS = rangeCcache[1] & 0xFF;
        int LODS = rangeAcache[1] & 0xFF;
        int CODS = rangeDcache[1] & 0xFF;

        //display values
        telemetry.addData("1 A US", LUS);
        telemetry.addData("2 A ODS", LODS);
        telemetry.addData("3 C US", RUS);
        telemetry.addData("4 C ODS", RODS);
        telemetry.addData("5 D CUS", CUS);
        telemetry.addData("6 D CODS", CODS);
    }
}