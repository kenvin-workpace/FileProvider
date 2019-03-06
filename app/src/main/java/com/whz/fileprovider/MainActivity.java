package com.whz.fileprovider;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

import static android.support.v4.content.FileProvider.getUriForFile;

public class MainActivity extends BaseActivity {

    private ImageView mImageView;
    private File mPicPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    /**
     * 初始化View
     */
    private void initView() {
        mImageView = findViewById(R.id.iv_img);
    }

    /**
     * android6.0以下拍照
     */
    public void click6Down(View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            start6Down();
        } else {
            click6Up(view);
        }
    }

    /**
     * android6.0以上拍照
     */
    public void click6Up(View view) {
        requestPermission(100, new String[]{Manifest.permission.CAMERA}).setPermissionDes("照相机");
    }

    /**
     * android6.0以下拍照
     */
    private void start6Down() {
        String path = getExternalCacheDir().getPath();
        mPicPath = new File(path, "test.png");

        Uri uri = Uri.fromFile(mPicPath);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, 101);
    }

    /**
     * android6.0以上拍照
     */
    private void start6Up() {
        File file = new File(getExternalCacheDir(), "images");
        if (!file.exists()) {
            file.mkdirs();
        }
        mPicPath = new File(file, "test.png");
        Uri uriForFile = getUriForFile(this, "com.whz.fileprovider.Config", mPicPath);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, 200);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                if (hasAllPermissionsGranted(permissions)) {
                    start6Up();
                } else {
                    validPermissions(requestCode, permissions);
                }
                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            mImageView.setImageURI(Uri.fromFile(mPicPath));
        }
    }
}
