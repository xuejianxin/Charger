package com.androxue.electronic.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.androxue.electronic.R;
import com.androxue.electronic.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by JimCharles on 2016/12/9.
 */

public class UserActivity extends Activity implements View.OnClickListener{

    private CheckBox remember_passwd, logibySelf;
    private TextView account, password, nickName, cardid, money, current, voltage;
    private String user_name, user_account, user_password, user_money, user_current, user_voltage, user_cardid, result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState) ;
        setContentView(R.layout.activity_user);

        initViews();
    }

    private void initViews(){

        account = (TextView) findViewById(R.id.et_account);
        account.setOnClickListener(this);

        password = (TextView) findViewById(R.id.et_password);
        password.setOnClickListener(this);

        nickName = (TextView) findViewById(R.id.et_nickname);
        nickName.setOnClickListener(this);

        cardid = (TextView) findViewById(R.id.et_cardid);
        cardid.setOnClickListener(this);

        money = (TextView) findViewById(R.id.et_money);
        money.setOnClickListener(this);

        current = (TextView) findViewById(R.id.et_current);
        current.setOnClickListener(this);

        voltage = (TextView) findViewById(R.id.et_voltage);
        voltage.setOnClickListener(this);

        remember_passwd = (CheckBox) findViewById(R.id.remember_password);
        logibySelf = (CheckBox) findViewById(R.id.login_by_self);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMessage();
            }
        });
    }

    public void sendHttpPostRequest() throws Exception {
        HttpURLConnection connection = null;
        try {
            String httpUrl="http://192.168.2.110/cdz/user_login.php";
            URL url = new URL(httpUrl);//创建一个URL
            connection  = (HttpURLConnection) url.openConnection();//通过该url获得与服务器的连接
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");//设置请求方式为post
            connection.setConnectTimeout(8000);//设置超时为3秒
            connection.setReadTimeout(8000);
            //设置传送类型
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Charset", "utf-8");
            //提交数据
            String data = "&passwd=" + URLEncoder.encode("hhhhhh", "UTF-8")+ "&number=" + URLEncoder.encode("13710675396", "UTF-8");//传递的数据
            connection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
            ToastUtils.showShort(this,
                    "数据提交成功......");
            //获取输出流
            OutputStream os = connection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            //获取响应输入流对象
            InputStreamReader is = new InputStreamReader(connection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(is);
            StringBuilder strBuilder = new StringBuilder();
            String line;
            //读取服务器返回信息
            while ((line = bufferedReader.readLine()) != null){
                strBuilder.append(line);
            }
            result = strBuilder.toString();
            is.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void set() {

        account.setText("账号 ：" + user_account);
        password.setText("密码 ：" + user_password);
        nickName.setText("昵称 ：" + user_name);
        cardid.setText("卡号 ：" + user_cardid );
        money.setText("余额 ：" + user_money + " 元");
        current.setText("电流 ：" + user_current + " A");
        voltage.setText("电压 ：" + user_voltage + " V");
    }

    public void setMessage() {
        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {    //提示读取结果
                    try {
                        parseJson();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    set();
                    Toast.makeText(UserActivity.this, result, Toast.LENGTH_SHORT).show();
                }
            }
        };
        new Thread() {
            public void run() {     //请求网络
                try {
                    sendHttpPostRequest();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Message m = new Message();
                m.what = 1;
                // 发送消息到Handler
                handler.sendMessage(m);
            }
        }.start();
    }

    protected void parseJson() throws JSONException {

        String string = "{\"t_id\":\"64\",\"user_name\":\"建新\",\"user_number\":\"13710675396\",\"user_cardid\":\"639039431\",\"user_passwd\":\"hhhhhh\",\"user_money\":\"9999999999\",\"dianya\":\"11100\",\"dianliu\":\"83274.11\"}";
        JSONObject jsonObject = new JSONObject(string);
        user_account = jsonObject.getString("user_number");
        user_password = jsonObject.getString("user_passwd");
        user_name = jsonObject.getString("user_name");
        user_money = jsonObject.getString("user_money");
        user_cardid = jsonObject.getString("user_cardid");
        user_current = jsonObject.getString("dianliu");
        user_voltage = jsonObject.getString("dianya");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cancel:
                finish();
                break;
            default:
                break;
        }
    }
}
