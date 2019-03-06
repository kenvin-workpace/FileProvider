package com.whz.fileprovider;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevin on 2018/4/27
 */
public class BaseActivity extends AppCompatActivity {

    private String permissionDes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public BaseActivity setPermissionDes(String permissionDes) {
        this.permissionDes = permissionDes;
        return this;
    }

    /**
     * 主入口
     */
    public BaseActivity requestPermission(int requestCode, String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, requestCode);
        return this;
    }

    /**
     * 请求权限
     * <p>
     * 1、如果app之前请求过该权限,被用户拒绝, 这个方法就会返回true.
     * 2、如果设备策略禁止应用拥有这条权限, 这个方法也返回false.
     * 3、如果用户之前拒绝权限的时候勾选了对话框中”Don’t ask again”的选项,那么这个方法会返回false.
     */
    public void validPermissions(int requestCode, String[] permissions) {
        String[] allDeniedPermissions = getAllDeniedPermissions(permissions);
        for (String permission : allDeniedPermissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                showPermissionDialog(true, requestCode, allDeniedPermissions);
            } else {
                showPermissionDialog(false, requestCode, allDeniedPermissions);
            }
        }
    }

    /**
     * 权限提示框
     */
    private void showPermissionDialog(final boolean aBoolean, final int requestCode, final String[] allDeniedPermissions) {
        String tip;
        if (aBoolean) {
            tip = "需要%1$s的权限";
        } else {
            tip = "需要%1$s的权限, 但此权限已被禁止, 你可以到设置中更改";
        }
        tip = String.format(tip, TextUtils.isEmpty(permissionDes) ? "必须有" : permissionDes);
        Snackbar.make(getWindow().getDecorView(), tip, Snackbar.LENGTH_LONG).setAction("去设置", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (aBoolean) {
                    ActivityCompat.requestPermissions(BaseActivity.this, allDeniedPermissions, requestCode);
                }else {
                    startSetting();
                }
            }
        }).show();
    }

    /**
     * 进入手机应用设置界面
     */
    private void startSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * 拥有请求的全部权限
     */
    public boolean hasAllPermissionsGranted(@NonNull String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    /**
     * 被拒绝的所有权限
     */
    private String[] getAllDeniedPermissions(String[] permissions) {
        List<String> lists = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                lists.add(permission);
            }
        }
        String[] result = new String[lists.size()];
        lists.toArray(result);
        return result;
    }
}
