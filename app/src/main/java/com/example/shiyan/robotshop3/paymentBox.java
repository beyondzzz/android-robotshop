package com.example.shiyan.robotshop3;

import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shiyan on 2017/10/22.
 */

public class paymentBox extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paymentwindow);

        String totalPriceStr = getIntent().getStringExtra("totalPrice");
        Float totalPrice = Float.valueOf( totalPriceStr);
        Bitmap qrCodePic = getQRCode(totalPrice, null);

        ImageView iv = (ImageView) findViewById(R.id.iv);
        ContentResolver cr = this.getContentResolver();
        iv.setImageBitmap(qrCodePic);
    }

    public Bitmap getQRCode(float price, JSONObject purchaseDetail){
        String qrCodeString = "";
        try {
            String result = api.getQRCodeAndID(price);
            Log.v("beyond", result);
            JSONObject data = new JSONObject(result);                    //将字符串转换成jsonObject对象
            JSONObject Result = data.getJSONObject("data");                    //将字符串转换成jsonObject对象
            qrCodeString = Result.getString("qr_code");
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
            Log.v("beyond",e.toString());
            e.printStackTrace();
        }
        return null;
    }
}
