package com.zmwl.easypermission;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * EasyPermission来实现Android6.0权限
 */
public class MainActivity extends BaseActivity {

    private static final int CALL_PHONE_REQUEST_CODE = 100; //打电话权限的请求码
    private static final String TAG = "MainActivity";
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        findViewById(R.id.call_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCallPermission();
            }
        });
    }

    //请求拨打电话的权限
    private void requestCallPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //打电话的权限
            String[] mPermissionList = new String[]{Manifest.permission.CALL_PHONE};
            if (EasyPermissions.hasPermissions(mContext, mPermissionList)) {
                //已经同意过
                call();
            } else {
                //未同意过,或者说是拒绝了，再次申请权限
                EasyPermissions.requestPermissions(
                        this,  //上下文
                        "需要拨打电话的权限", //提示文言
                        CALL_PHONE_REQUEST_CODE, //请求码
                        mPermissionList //权限列表
                );
            }
        } else {
            //6.0以下，不需要授权
            call();
        }
    }

    //拨打电话
    private void call() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:10086"));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    //同意授权
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        //跳转到onPermissionsGranted或者onPermissionsDenied去回调授权结果
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Some permissions have been granted
        // ...
        Log.i(TAG, "onPermissionsGranted:" + requestCode + ":" + list.size());
        call();
    }

    //拒绝授权
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        // Some permissions have been denied
        // ...

        Log.i(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            //拒绝授权后，从系统设置了授权后，返回APP进行相应的操作
            Log.i(TAG, "onPermissionsDenied:------>自定义设置授权后返回APP");
            call();
        }
    }
}
