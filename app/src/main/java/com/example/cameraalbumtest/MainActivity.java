package com.example.cameraalbumtest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.FrameLayout;


import org.json.JSONArray;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {


    Handler handlerThread;
    public int code = 0;
    CameraPreview mPreview;
    String key = " ";

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    Mat imageMat = new Mat();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                String[] places = getResources().getStringArray(R.array.places);
                Toast.makeText(MainActivity.this, "你选择的是:"+places[pos], Toast.LENGTH_SHORT).show();
                code = pos;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
        Button buttonStartPreview = (Button) findViewById(R.id.button_start_preview);
        buttonStartPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                TextInputLayout til_id = (TextInputLayout) findViewById(R.id.til_id);
//                usr_id = til_id.getEditText().getText().toString();
//                System.out.println("usr_id:"+usr_id);

                startPreview();
            }
        });
        Button buttonStopPreview = (Button) findViewById(R.id.button_stop_preview);
        buttonStopPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPreview();
            }
        });
    }

    public void startPreview() {
        mPreview = new CameraPreview(this, code);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }

    public void stopPreview() {
        Bundle bundle = new Bundle();
        bundle.putInt("width", mPreview.width);
        bundle.putInt("height", mPreview.height);
        bundle.putInt("scanFrq", mPreview.scanFrq);
        bundle.putInt("LEDfrq", (Integer)1000);
        bundle.putInt("code", code);
        bundle.putByteArray("img", mPreview.img);
        Log.i("Main", "create Async");
        //new ProcessWithAsyncTask().execute(bundle);

        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.removeAllViews();
        int width = bundle.getInt("width");
        int height = bundle.getInt("height");
        int scanFrq = bundle.getInt("scanFrq");
        int LEDfrq = bundle.getInt("LEDfrq");
        int code = bundle.getInt("code");

        send_pos_id();
//        switch (code){
//            case 0: key = "01"; break;
//            case 1: key = "0011"; break;
//            case 2: key = "000111"; break;
//            case 3: key = "00001111"; break;
//        }
        get_key();
        Log.i("Main", Integer.toString(LEDfrq));
        ImgProcess imgPrc = new ImgProcess(bundle.getByteArray("img"), width, height, scanFrq, LEDfrq);
        if(imgPrc.judge(key))
        {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setIcon(R.mipmap.ic_launcher);
            dialog.setTitle("Sign in Success");
            dialog.setMessage("提示信息： 签到成功");
            dialog.setCancelable(false);
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this, "确定", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.show();
            send_file();
        }
        else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setIcon(R.mipmap.ic_launcher);
            dialog.setTitle("Sign in Failed");
            dialog.setMessage("提示信息： 签到失败");
            dialog.setCancelable(false);
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this, "确定", Toast.LENGTH_SHORT).show();                    }
            });
            dialog.show();
        }
    }

    public void send_pos_id(){
        try {
            String urlPath = "xxxxxx";  	//URL
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");	//请求方式为POST
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/json");     //设置发送的数据为 json 类型，会被添加到http body当中
            String json = "{\"pos_id\":"+ code +"}";
            conn.setRequestProperty("Content-Length", String.valueOf(json.length()));

            //post请求把数据以流的方式写给服务器，指定请求的输出模式
            conn.setDoOutput(true);
            conn.getOutputStream().write(json.getBytes());

            int code = conn.getResponseCode();
            if (code != 200) {
                System.out.println("请求失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("请求失败");
        }
    }

    public void get_key(){
        try {
            String urlPath = "xxxxxx";
            URL url = new URL(urlPath);
            HttpURLConnection coon = (HttpURLConnection) url.openConnection();
            coon.setRequestMethod("GET");
            coon.setConnectTimeout(5000);

            int code = coon.getResponseCode();
            if (code == 200) {
                //1.得到输入流
                InputStream is = coon.getInputStream();
                //2.将流用自己写的StreamUtils转化为字符串  改字符串为json格式
                String info = StreamUtils.readStream(is);
                //3.解析json数据（这里是list形式）  并显示数据
                JSONArray myJsonArray = new JSONArray(info);
                // 这里的数组长度其实是1
                for (int i = 0; i < myJsonArray.length(); i++) {
                    JSONObject myJsonObject = myJsonArray.getJSONObject(i);
                    key = myJsonObject.getString("key");
                }
            } else {
                System.out.println("请求失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void send_file(){
        try {
            long totalMilliSeconds = System.currentTimeMillis();
            long totalSeconds = totalMilliSeconds / 1000;
            String time = Long.toString(totalSeconds);


            String urlPath = "xxxxxx";  	//URL
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");	//请求方式为POST
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/json");     //设置发送的数据为 json 类型，会被添加到http body当中
            String json = "{\"user_name\":\""+ loginActivity.usr_name +"\", \"time\":\"" + time + "\", \"pos_id\":" + code + "}";
            conn.setRequestProperty("Content-Length", String.valueOf(json.length()));

            //post请求把数据以流的方式写给服务器，指定请求的输出模式
            conn.setDoOutput(true);
            conn.getOutputStream().write(json.getBytes());

            int code = conn.getResponseCode();
            if (code != 200) {
                System.out.println("请求失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("请求失败");
        }

    }

    public void onPause() {
        finish();
        super.onPause();
    }

    public byte[] encode(byte[] byteArray){
        for (int i = 0; i < byteArray.length; ++i){
            byteArray[i] = (byte) (byteArray[i] ^ 96);
        }
        return byteArray;

    }

    public void writeFile(){
        File file1 = new File(Environment.getExternalStorageDirectory(), "sign_in.doc");
        if (file1.exists())
            file1.delete();
        try {
            file1.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file1);
            // 获取BufferedOutputStream对象
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            // 往文件所在的缓冲输出流中写byte数据
            byte[] usrId = loginActivity.usr_name.getBytes();

            String key = "";
            switch (code){
                case 0: key = "01"; break;
                case 1: key = "0011"; break;
                case 2: key = "000111"; break;
                case 3: key = "00001111"; break;
            }
            byte[] Key = key.getBytes();
            long totalMilliSeconds = System.currentTimeMillis();
            long totalSeconds = totalMilliSeconds / 1000;
            String time = Long.toString(totalSeconds);
            byte[] Time = time.getBytes();
            String c = Integer.toString(code);
            byte[] Code = c.getBytes();
            bufferedOutputStream.write(encode(usrId));
            bufferedOutputStream.write(encode(Code));
            bufferedOutputStream.write(encode(Key));
            bufferedOutputStream.write(encode(Time));
            // 刷出缓冲输出流，该步很关键，要是不执行flush()方法，那么文件的内容是空的。
            bufferedOutputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*@SuppressLint("HandlerLeak")
    Handler handlerMain = new Handler() {
        public void handleMessage(Message msg) {
            if ((boolean)msg.obj)
                Toast.makeText(MainActivity.this, "Sign-in Success!", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(MainActivity.this, "Sign-in Failure!", Toast.LENGTH_SHORT).show();
        }
    };

    class ImageProcessThread extends Thread {

        Boolean isValid = false;

        @SuppressLint("HandlerLeak")
        public void run() {
            Looper.prepare();
            Handler handlerThread = new Handler() {
                public void handleMessage(Message msg) {
                    Bundle bundle = msg.getData();
                    int width = bundle.getInt("width");
                    int height = bundle.getInt("height");
                    int scanFrq = bundle.getInt("scanFrq");
                    int LEDfrq = bundle.getInt("LEDfrq");
                    String key = " ";
                    switch (code){
                        case 0: key = "0011"; break;
                        case 1: key = "0010"; break;
                        case 2: key = "0001"; break;
                        case 3: key = "0000"; break;
                    }
                    ImgProcess imgPrc = new ImgProcess(bundle.getByteArray("img"), height, width, scanFrq, LEDfrq);
                    isValid = imgPrc.judge(key);
                }
            };
            handlerMain.obtainMessage(1, isValid).sendToTarget();
            Looper.loop();
        }
    }*/

    /*private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    //Log.i(TAG, "OpenCV loaded successfully");
//                    mOpenCvCameraView.enableView();
//                    mOpenCvCameraView.setOnTouchListener(ColorBlobDetectionActivity.this);
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };*/
}


