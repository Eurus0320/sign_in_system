package com.example.cameraalbumtest;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.drm.DrmInfoStatus.STATUS_ERROR;

public class loginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mTvLoginactivityRegister;
    private RelativeLayout mRlLoginactivityTop;
    private EditText mEtLoginactivityUsername;
    private EditText mEtLoginactivityPassword;
    private LinearLayout mLlLoginactivityTwo;
    private Button mBtLoginactivityLogin;

    static String usr_name;
    String password;
    int status;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        // 初始化控件
        mBtLoginactivityLogin = findViewById(R.id.bt_loginactivity_login);
        mRlLoginactivityTop = findViewById(R.id.rl_loginactivity_top);
        mEtLoginactivityUsername = findViewById(R.id.et_loginactivity_username);
        mEtLoginactivityPassword = findViewById(R.id.et_loginactivity_password);
        mLlLoginactivityTwo = findViewById(R.id.ll_loginactivity_two);

        // 设置点击事件监听器
        mBtLoginactivityLogin.setOnClickListener(this);
        mTvLoginactivityRegister.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.bt_loginactivity_login:
                usr_name = mEtLoginactivityUsername.getText().toString().trim();
                password = mEtLoginactivityPassword.getText().toString().trim();
                if (!TextUtils.isEmpty(usr_name) && !TextUtils.isEmpty(password)) {
                    send_login_info();
                    get_login_result();
                    if (status == 1) {
                        Toast.makeText(this, name+"登录成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();//销毁此Activity
                    } else {
                        Toast.makeText(this, "ID或密码不正确，请重新输入", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "请输入你的ID或密码", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void send_login_info() {
        try {
            String urlPath = "xxxxxx";  	//URL
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");	//请求方式为POST
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/json");     //设置发送的数据为 json 类型，会被添加到http body当中
            String json = "{\"user_name\":\""+ usr_name +"\"," + "\"password\":" + "\"" + password + "\"}";
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


    public void get_login_result() {
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
                String loginInfo = StreamUtils.readStream(is);
                //3.解析json数据（这里是list形式）  并显示数据
                JSONArray loginJsonArray = new JSONArray(loginInfo);
                // 这里的数组长度其实是1
                for (int i = 0; i < loginJsonArray.length(); i++) {
                    JSONObject loginJsonObject = loginJsonArray.getJSONObject(i);
                    status = loginJsonObject.getInt("status");
                    name = loginJsonObject.getString("name");
                }
            } else {
                System.out.println("请求失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
