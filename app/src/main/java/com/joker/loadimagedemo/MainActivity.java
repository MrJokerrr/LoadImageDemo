package com.joker.loadimagedemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 1. 打开手机相册,选择图片,进行裁剪并加载到ImgeView中
 * 2. 打开相机拍照,拍照后进行裁剪并加载到ImgeView上
 * created by Joker
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SELECT_PIC = 0x123;
    private final static int TAKE_PIC = 0x124;
    private final static int CROP_PIC = 0x125;
    private Button mBtnFromGallery;
    private Button mBtnFromCapture;
    private ImageView mImageView;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "test.jpg"));
        initView();

        mBtnFromGallery.setOnClickListener(this);
        mBtnFromCapture.setOnClickListener(this);

    }

    private void initView() {
        mBtnFromGallery = (Button) findViewById(R.id.btn_from_gallery);
        mBtnFromCapture = (Button) findViewById(R.id.btn_from_capture);
        mImageView = (ImageView) findViewById(R.id.iv_image);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_from_gallery:
                getImageViewFromGallery();
                break;

            case R.id.btn_from_capture:
                getImageViewFromCapture();
                break;
        }
    }

    /**
     * 拍照获取图片并裁剪
     */
    private void getImageViewFromCapture() {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PIC);
    }

    /**
     * 从相册获取图片并裁剪
     */
    private void getImageViewFromGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*"); // 从所有图片中进行选择
        // 进行一些裁剪参数配置
        intent.putExtra("crop","true"); // 设置为裁剪
        intent.putExtra("aspectX",1);   // 裁剪的宽比例
        intent.putExtra("aspectY", 1);  // 裁剪的高比例
        intent.putExtra("outputX", 600);// 裁剪的宽度
        intent.putExtra("outputY", 600);// 裁剪的高度
        intent.putExtra("scale", true); // 支持缩放
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // 将裁剪的结果输出到指定的uri
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//裁切成的图片的格式
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, SELECT_PIC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SELECT_PIC:
                if (resultCode == RESULT_OK){
                    try {
                        // 将imageUri对象的图片加载到内存
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        mImageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case TAKE_PIC:
                if (resultCode == RESULT_OK){
                    cropImageUri(imageUri, 600, 600, CROP_PIC);
                }
                break;

            case CROP_PIC:
                if (resultCode == RESULT_OK){
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        mImageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void cropImageUri(Uri imageUri, int outputX, int outputY, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, requestCode);
    }
}
