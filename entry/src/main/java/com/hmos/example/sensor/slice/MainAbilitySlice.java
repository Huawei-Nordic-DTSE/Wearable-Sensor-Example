package com.hmos.example.sensor.slice;

import com.hmos.example.sensor.ResourceTable;
import com.hmos.example.sensor.utils.LogUtil;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Image;
import ohos.sensor.agent.CategoryOrientationAgent;
import ohos.sensor.bean.CategoryOrientation;
import ohos.sensor.data.CategoryOrientationData;
import ohos.sensor.listener.ICategoryOrientationDataCallback;

public class MainAbilitySlice extends AbilitySlice {
    private static final long INTERVAL = 100000000L; //nano sec, 100000000 nano seconds = 100 milliseconds
    private static final int SCREEN_SIZE = 466;
    private static final int STEP_PIXEL = 3;
    private static final int SCALE = 20;

    private final CategoryOrientationAgent categoryOrientationAgent = new CategoryOrientationAgent();
    private ICategoryOrientationDataCallback orientationDataCallback;
    private CategoryOrientation orientationSensor;

    private static final int matrix_length = 9;
    private static final int rotationVectorLength = 9;

    private int posX=0, posY=0;
    private Image mIcon;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);

        mIcon = (Image)findComponentById(ResourceTable.Id_text);
        mIcon.setPosition(SCREEN_SIZE/2, SCREEN_SIZE/2);

        // Create a sensor callback object.
        orientationDataCallback = new ICategoryOrientationDataCallback() {
            @Override
            public void onSensorDataModified(CategoryOrientationData categoryOrientationData) {
                // Obtain the rotation matrix based on data of the rotation vector sensor.
                float[] rotationMatrix = new float[matrix_length];
                CategoryOrientationData.getDeviceRotationMatrix(rotationMatrix, categoryOrientationData.values);

                // Obtain the device orientation based on the rotation matrix.
                float[] rotationAngle = new float[rotationVectorLength];
                rotationAngle = CategoryOrientationData.getDeviceOrientation(rotationMatrix, rotationAngle);

                int x = calX(rotationAngle[2]*180/3.1415926f);
                int y = calY(rotationAngle[1]*180/3.1415926f);

                LogUtil.debug("MainAbilitySlice_Log", "anglex/angley: "
                        + rotationAngle[2]*180/3.1415926f
                        + "/" + rotationAngle[1]*180/3.1415926f
                        + " x/y: " + x + "/" + y);
                if(Math.abs(x-posX) > STEP_PIXEL || Math.abs(y-posY) > STEP_PIXEL) {
                    posX = x;
                    posY = y;
                    getUITaskDispatcher().asyncDispatch(() -> mIcon.setPosition(x,y));
                }
            }

            @Override
            public void onAccuracyDataModified(CategoryOrientation categoryOrientation, int index) {
                // Use the changed accuracy data.
            }

            @Override
            public void onCommandCompleted(CategoryOrientation categoryOrientation) {
                // The sensor executes the command callback.
            }
        };

        // Obtain the sensor object and subscribe to sensor data.
        orientationSensor = categoryOrientationAgent.getSingleSensor(
                CategoryOrientation.SENSOR_TYPE_GAME_ROTATION_VECTOR);
        if (orientationSensor != null) {
            categoryOrientationAgent.setSensorDataCallback(
                    orientationDataCallback, orientationSensor, INTERVAL);
        }
    }

    int calX(float zDegree) {
        int deg = Math.round(zDegree);

        if (deg  > SCALE)
            deg = SCALE;
        else if (deg < -1*SCALE)
            deg = -1*SCALE;

        return (deg + SCALE)*(SCREEN_SIZE/(2*SCALE));
    }

    int calY(float yDegree) {
        int deg = Math.round(yDegree);

        if (deg  > SCALE)
            deg = SCALE;
        else if (deg < -1*SCALE)
            deg = -1*SCALE;

        return Math.abs(deg -SCALE)*(SCREEN_SIZE/(2*SCALE));
    }


    @Override
    public void onActive() {
        super.onActive();

        if (orientationSensor != null) {
            categoryOrientationAgent.setSensorDataCallback(
                    orientationDataCallback, orientationSensor, INTERVAL);
        }
    }

    @Override
    protected void onInactive() {
        super.onInactive();

        if (orientationSensor != null) {
            categoryOrientationAgent.releaseSensorDataCallback(
                    orientationDataCallback, orientationSensor);
        }
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
