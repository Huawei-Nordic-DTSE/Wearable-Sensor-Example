package com.hmos.example.sensor;

import com.hmos.example.sensor.slice.MainAbilitySlice;
import com.hmos.example.sensor.utils.LogUtil;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.bundle.IBundleManager;

import java.util.ArrayList;
import java.util.List;

public class MainAbility extends Ability {
    private final int PERMISSION_REQUEST_CODE = 1;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());
        setSwipeToDismiss(true);

        String [] permissions = {"ohos.permission.ACCELEROMETER",
                "ohos.permission.GYROSCOPE"};

        List<String> notGrantedPermissionLists = new ArrayList<>();

        for(String permission : permissions) {
            if(verifySelfPermission(permission) != IBundleManager.PERMISSION_GRANTED){
                LogUtil.debug("MainAbility_Log", permission + " is not granted.");
                if(canRequestPermission(permission))
                    notGrantedPermissionLists.add(permission);
            } else {
                LogUtil.debug("MainAbility_Log", permission + " is already granted.");
            }
        }

        if(notGrantedPermissionLists.size() > 0)
            requestPermissionsFromUser(notGrantedPermissionLists.toArray(new String[0]), PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsFromUserResult(int requestCode, String[] permissions,
                                                   int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == IBundleManager.PERMISSION_GRANTED) {
                LogUtil.debug("MainAbility_Log", "User granted " + permissions);
            } else {
                LogUtil.debug("MainAbility_Log", "User rejected " + permissions);
            }
        }
    }
}
