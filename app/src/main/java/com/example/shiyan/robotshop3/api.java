package com.example.shiyan.robotshop3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by shiyan on 2017/10/17.
 */

public class api {
    private static boolean mCaps=false;
    //39.107.24.82
    //private EditText getResult = (EditText) findViewById(R.id.editTextUrl);
    private static String productAPI = "http://39.107.24.82:8081/productInfo/";
    private static String productAPIByNo = "http://39.107.24.82:8081/productInfoByNo/";

    private static String qrcodeAPI = "http://39.107.24.82:8081/payReq/";
    private static String qrStatusAPI = "http://39.107.24.82:8081/qrInfoReq/";
    public static String qrID = "";
    //得到付款二维码
    public static String getQRCodeAndID(float price){

        price = price * 100;
        HttpGet httpGet = new HttpGet(qrcodeAPI + "?priceFen=" + price);
        HttpClient httpClient = new DefaultHttpClient();
        String result = "";
        // 发送请求
        try {
            HttpResponse response = httpClient.execute(httpGet);
            // 显示响应
            result = showResponseResult(response);// 一个私有方法，将响应结果显示出来
        } catch (Exception e) {
            Log.v("winner",e.toString());
            e.printStackTrace();
        }
        return result;
    }

    public static boolean getQRStatus(String qrID){
        HttpGet httpGet = new HttpGet(qrStatusAPI + "?qr_id=" + qrID);
        HttpClient httpClient = new DefaultHttpClient();
        //String result = "";
        int status = -1;
        // 发送请求
        try {
            HttpResponse response = httpClient.execute(httpGet);
            String result = showResponseResult(response);
            //Log.v("beyond", "here" + result);
            JSONObject data = new JSONObject(result);                    //将字符串转换成jsonObject对象
            JSONObject Result = data.getJSONObject("data");                    //将字符串转换成jsonObject对象
            status = Result.getInt("payment_status");
            qrID = Result.getString("qr_id");
            if (status==1){
                return true;
            }
        } catch (Exception e){
            Log.v("winner",e.toString());
            e.printStackTrace();
        }
        return false;
    }


    public static String showResponseResult(HttpResponse response)
    {
        if (null == response)
        {return "";}
        String result = "";
        HttpEntity httpEntity = response.getEntity();
        try
        {
            InputStream inputStream = httpEntity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream));
            String line = "";
            while (null != (line = reader.readLine()))
            {
                result += line;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }

    //拿到商品请求数据
    public static String getProductInfo(String productID)
    {
        // 生成请求对象

        //HttpGet httpGet = new HttpGet(productAPI + "?item_id=" + productID);
        HttpGet httpGet = new HttpGet(productAPIByNo + "?item_no=" + productID);

        HttpClient httpClient = new DefaultHttpClient();
        String result = "";
        // 发送请求
        try {
            HttpResponse response = httpClient.execute(httpGet);
            // 显示响应
            result = showResponseResult(response);// 一个私有方法，将响应结果显示出来
        } catch (Exception e) {
            //Log.v("winner",e.toString());
            e.printStackTrace();
        }
        return result;
    }

    //生成付款二维码的函数
    public static Bitmap getQRCode(float price, JSONObject purchaseDetail){
        String qrCodeString = "";
        try {
            String result = api.getQRCodeAndID(price);
            //Log.v("beyond", result);
            JSONObject data = new JSONObject(result);                    //将字符串转换成jsonObject对象
            JSONObject Result = data.getJSONObject("data");                    //将字符串转换成jsonObject对象
            qrCodeString = Result.getString("qr_code");
            qrID = Result.getString("qr_id");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            Log.v("beyond",e.toString());
        }

        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(qrCodeString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);

        } catch (Exception e) {
            Log.v("beyondaaaa",e.toString());
            e.printStackTrace();
        }
        return null;
    }

    /////////////
    //*API
    //Log.v("Error","1111111111111111");
    //YZClient client = new DefaultYZClient(new Token("78ee09ce32e831bcb45d23d85c846d80")); //new Sign(appKey, appSecret)
        /*
        try {
            YZClient client = new DefaultYZClient(new Sign("1b2e181830de327d4f", "c0643b3393fdcb9d581d90576d032f18")); //new Sign(appKey, appSecret)
            Log.v("Beyond", "2222222222222222");}
        catch (Exception e){
            Log.v("Error", "start");
            Log.v("Error", e.toString());
            Log.v("Error", "end");
            //e.printStackTrace();        }*/
    //YouzanItemGetParams youzanItemGetParams = new YouzanItemGetParams();
    //youzanItemGetParams.setItemId(351202017L);
    //YouzanItemGet youzanItemGet = new YouzanItemGet();
    //youzanItemGet.setAPIParams(youzanItemGetParams);
    //YouzanItemGetResult result = client.invoke(youzanItemGet);
    //String str = result.toString();
    //System.out.println(str);
    //API
    /////////////
    //YouzanItemGetParams youzanItemGetParams = new YouzanItemGetParams();
    //youzanItemGetParams.setItemId(1775641L);
    //YouzanItemGet youzanItemGet = new YouzanItemGet();
    //youzanItemGet.setAPIParams(youzanItemGetParams);
    //YouzanItemGetResult result = client.invoke(youzanItemGet);
            /*
            HttpCall result = Http.attach(this)
                .put("item_id", "1")
                .with(new GoodsItemQuery() {
                    private double price;
                    @Override
                    protected void onSuccess(@NonNull GoodsDetailModel data) {
                         price  =  data.getPrice();
                    }
                    @Override
                    protected void onFailure(@NonNull YouzanException error) {
                     //请求失败, error.getMessage()可获取失败信息
                    }
                    public double getPrice(){
                        return price;
                    }
                });
            */


    public static char getInputCode(KeyEvent event) {

        int keyCode = event.getKeyCode();
        char aChar;

        if (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z) {
            //字母
            aChar = (char) ((mCaps ? 'A' : 'a') + keyCode - KeyEvent.KEYCODE_A);
        } else if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
            //数字
            aChar = (char) ('0' + keyCode - KeyEvent.KEYCODE_0);
        } else {
            //其他符号
            switch (keyCode) {
                case KeyEvent.KEYCODE_PERIOD:
                    aChar = '.';
                    break;
                case KeyEvent.KEYCODE_MINUS:
                    aChar = mCaps ? '_' : '-';
                    break;
                case KeyEvent.KEYCODE_SLASH:
                    aChar = '/';
                    break;
                case KeyEvent.KEYCODE_BACKSLASH:
                    aChar = mCaps ? '|' : '\\';
                    break;
                default:
                    aChar = 0;
                    break;
            }
        }
        return aChar;

    }

}

/*
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="立即支付"
        android:textColor="#000000"
        android:visibility="visible"
        app:layout_constraintBottom_toBottomOf="@+id/circle2"
        app:layout_constraintLeft_toLeftOf="@+id/circle2"
        app:layout_constraintRight_toRightOf="@+id/circle2"
        app:layout_constraintTop_toTopOf="@+id/circle2" />
 */

