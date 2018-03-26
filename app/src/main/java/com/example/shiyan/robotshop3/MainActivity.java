package com.example.shiyan.robotshop3;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;


//import static java.lang.System.in;
//import static org.apache.commons.lang3.BooleanUtils.and;


public class MainActivity extends AppCompatActivity {
    private final static long MESSAGE_DELAY = 500;
    private TextView circleButton;
    private TextView box;
    private Button clearButton;
    private Button testButton;
    private TextView totalView;
    private TextView quantityView;
    private TableLayout table;
    java.text.DecimalFormat df=new java.text.DecimalFormat("#.##");

    private LinearLayout.LayoutParams layoutParamsTotal;
    private LinearLayout.LayoutParams layoutParamsName;
    private LinearLayout.LayoutParams layoutParamsQuantity;
    private LinearLayout.LayoutParams layoutParamsUnit;
    private TextView nameTitleView;
    private TextView quantityTitleView;
    private TextView totalTitleView;
    private TextView unitTitleView;
    private View scrollView;
    private ScanGunKeyEventHelper mScanGunKeyEventHelper;
    private StringBuffer mStringBufferResult = new StringBuffer();
    long lastTime = 0;
    private ScanGunKeyEventHelper.OnScanSuccessListener mOnScanSuccessListener;

    private final Runnable mScanningFishedRunnable = new Runnable() {
        @Override
        public void run() {
            performScanSuccess();
        }
    };

    private void performScanSuccess() {
        String barcode = mStringBufferResult.toString();
        if (mOnScanSuccessListener != null)
            mOnScanSuccessListener.onScanSuccess(barcode);
        mStringBufferResult.setLength(0);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
        clearButton = (Button) findViewById(R.id.clearAll);
        testButton = (Button) findViewById(R.id.testButton);

        //右侧付款侧信息：
        circleButton =  (TextView) findViewById(R.id.circle);
        totalView = (TextView) findViewById(R.id.Total);
        quantityView = (TextView) findViewById(R.id.Quantity);

        //商品列表侧信息：
        nameTitleView = (TextView) findViewById(R.id.titleNameNew);
        quantityTitleView = (TextView) findViewById(R.id.titleQuantityNew);
        totalTitleView = (TextView) findViewById(R.id.titleTotalNew);
        unitTitleView = (TextView) findViewById(R.id.titleUnitPriceNew);

        //得到表单
        table = (TableLayout)findViewById(R.id.table);

        //临时滚动
        layoutParamsTotal = new TableRow.LayoutParams(totalTitleView.getLayoutParams());
        layoutParamsName = new TableRow.LayoutParams(nameTitleView.getLayoutParams());
        layoutParamsUnit = new TableRow.LayoutParams(unitTitleView.getLayoutParams());
        layoutParamsQuantity = new TableRow.LayoutParams(quantityTitleView.getLayoutParams());


        circleButton.bringToFront();

        //进入付款页面
        Button btn=(Button)findViewById(R.id.circle);

        scrollView = findViewById(R.id.scrollView);

        btn.setOnClickListener(new View.OnClickListener() {
            //private static final int secondTotal=45;//总进度值
            @Override
            public void onClick(View view) {
                Log.i("指定onClick属性方式","bt1点击事件");
                try {
                    DialogPay.Builder dialogBuild = new DialogPay.Builder(MainActivity.this);
                    String totalPriceStr = totalView.getText().toString();
                    Float totalPrice = Float.valueOf(totalPriceStr);

                    Bitmap qrCodePic = api.getQRCode(totalPrice, null);
                    String qrID = api.qrID;

                    dialogBuild.setImage(qrCodePic);
                    DialogPay dialog = dialogBuild.create(table, totalView, quantityView, qrID);
                    dialog.setCanceledOnTouchOutside(true);// 点击外部区域关闭
                    dialog.show();

                    dialog.check(table,totalView, quantityView, qrID );

                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            table.removeAllViews();
                            totalView.setText(null);
                            quantityView.setText(null);
                            DialogFinish.Builder finish = new DialogFinish.Builder(MainActivity.this);
                            DialogFinish dialogFinish = finish.create(table, totalView, quantityView);
                            dialogFinish.show();
                        }
                    });


                }
                catch (Exception e){
                    Log.v("beyondzzz",e.toString());
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("当前没有商品")
                            .setMessage("请您扫秒商品条形码")
                            .setNegativeButton("确定", null)
                            .show();
                    return;
                }
            }
        });

        //清空商品的按钮
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("即将清空所有商品")
                        .setMessage("确定吗？")
                        .setPositiveButton("清空所有", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                table.removeAllViews();
                                totalView.setText(null);
                                quantityView.setText(null);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }

        });


        //测绘按钮， 手动添加商品的按钮
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productBarCode = "6923450657638";
                addItem(productBarCode);
            }
        });
    }

    private void addItem(String barcode){
        try {
            boolean flag = false;
            int count = table.getChildCount();
            for (int i = 0; i < count; i++) {
                TableRow row = (TableRow) table.getChildAt(i);
                Log.v("beyondzzz", (String) row.getTag());
                if (barcode.equals(row.getTag())) {
                    flag = true;
                    LinearLayout ly = (LinearLayout) row.getChildAt(0);
                    addExsitItem(ly);
                }
            }
            if (flag == false) {
                addNewItem(barcode);
            }
            return;
        }
        catch (Exception e)
        {
            Log.v("beyondzzz",e.toString());
            return;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();

        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            char aChar = api.getInputCode(keyEvent);
            if (aChar != 0) {
                if (lastTime ==0){

                }
                else if ((keyEvent.getDownTime() - lastTime) > 250)
                {
                    //这里，如果两次扫描的时间超过200毫秒，则认为是两次不同的扫描事件。
                    //主要为了避免重复扫码。
                    String barcode = mStringBufferResult.toString();
                    Log.v("beyond333",barcode);
                    addItem(barcode);
                    mStringBufferResult.delete(0, mStringBufferResult.length());
                    //lastTime = keyEvent.getDownTime();
                }
                lastTime = keyEvent.getDownTime();
                mStringBufferResult.append(aChar);
                String barcode = mStringBufferResult.toString();
                Log.v("beyond444",barcode);
                if ((barcode.length() >=13) && (checkBarCode(barcode) == 1))  {
                    addItem(barcode);
                    mStringBufferResult.delete(0, mStringBufferResult.length());
                }

            }
        }
        return false;
    }

    public  int checkBarCode(String barcode){

        String result=  api.getProductInfo(barcode);
        Log.v("beyond555", result);
        if  (result.contains("Traceback")){
            return 0;
        }
        else
        {
            return 1;
        }
    }

        //product item api 请求
    private View.OnClickListener getButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //api.getProductInfo(getResult);
        }
    };


    //添加按钮跟通过扫码都会调用这里
    private  void addExsitItem(LinearLayout ly){
        TextView unit = (TextView) ly.getChildAt(1);
        TextView quantity = (TextView) ly.getChildAt(3);
        TextView total = (TextView) ly.getChildAt(5);
        Integer qValue = Integer.valueOf((String) quantity.getText()).intValue();
        qValue = qValue+1;
        quantity.setText(qValue.toString());
        Float uValue = Float.valueOf((String) unit.getText()).floatValue();
        Float tValue = Float.valueOf((String) total.getText()).floatValue();
        tValue = tValue + uValue;
        //total.setText(tValue.toString());
        total.setText(df.format(tValue));

        updateTotalQuantity();
    }

    //减少的按钮会调用这里
    private  void reduceExsitItem(LinearLayout ly){
        TextView unit = (TextView) ly.getChildAt(1);
        TextView quantity = (TextView) ly.getChildAt(3);
        TextView total = (TextView) ly.getChildAt(5);
        Integer qValue = Integer.valueOf((String) quantity.getText()).intValue();
        if (qValue >0) {
            qValue = qValue - 1;
            quantity.setText(qValue.toString());
            Float uValue = Float.valueOf((String) unit.getText()).floatValue();
            Float tValue = Float.valueOf((String) total.getText()).floatValue();
            tValue = tValue - uValue;
            total.setText(df.format(tValue));
            //total.setText(tValue.toString());
        }
        updateTotalQuantity();
    }

    private  void updateTotalQuantity()
    {
        int count = table.getChildCount();
        double quantity = 0.0;
        double total = 0.0;
        for (int i = 0; i < count; i++) {
            TableRow row = (TableRow) table.getChildAt(i);
            LinearLayout ly = (LinearLayout) row.getChildAt(0);
            TextView  qTV = (TextView) ly.getChildAt(3);
            TextView  tTV = (TextView) ly.getChildAt(5);
            quantity = quantity + Double.valueOf((String)qTV.getText()).doubleValue();
            total = total + Double.valueOf((String)tTV.getText()).doubleValue();
        }
        totalView.setText(df.format(total));
        //totalView.setText(Double.toString(total));
        quantityView.setText(Double.toString(quantity));
    }


    private void addNewItem(String productBarCode){
        String result=  api.getProductInfo(productBarCode);
        String proName = "";
        String proUnit = "";
        String proId = "";
        Button reduce =null;
        Button increase = null;
        try{
            Log.v("beyondcccc",result.toString());
            JSONObject Result = new JSONObject(result);                    //将字符串转换成jsonObject对象
            JSONObject data = Result.getJSONObject("data");                    //获取对应的值
            proName = data.getString("title");
            proUnit = data.getString("price");
            proId = data.getString("item_no");
        }
        catch (JSONException e)
        {
            Log.v("beyondcccc",e.toString());
            return;
        }

        TableRow row = new TableRow(MainActivity.this);
        final LinearLayout ll = new LinearLayout(MainActivity.this);
        row.setTag(proId);
        for (int i=1; i<= 6; i++)
        {
            if(i==1 || i==2 || i==4 || i==6){
                TextView tv = new TextView(MainActivity.this);
                tv.setHeight(80);
                tv.setTextSize(20);
                if (i==1){
                    //layoutParamsName.setMargins(30,10,100,0);
                    tv.setText(proName);
                    //tv.setLayoutParams(new LinearLayout.LayoutParams(300,LinearLayout.LayoutParams.WRAP_CONTENT));
                    tv.setWidth(600);
                    tv.setMinWidth(600);
                    tv.setMinimumWidth(600);
                    //tv.setLayoutParams(new ViewGroup.LayoutParams(300,80));
                    tv.setMaxLines(1);
                    tv.setMaxWidth(600);
                    tv.setPadding(30,0,0,0);
                    ll.addView(tv);
                    //ll.addView(tv,layoutParamsName);
                }
                else if (i==2){
                    //layoutParamsUnit.setMargins(70,10,70,0);
                    tv.setText(proUnit);
                    //tv.setLeft(770);
                    tv.setWidth(340);
                    tv.setMinWidth(340);
                    tv.setMinimumWidth(340);
                    tv.setPadding(100,0,120,0);
                    tv.setGravity(1);
                    ll.addView(tv);
                }else if (i==4){
                    layoutParamsQuantity.setMargins(10,10,10,0);
                    tv.setText("1");
                    tv.setGravity(1);
                    tv.setWidth(40);
                    ll.addView(tv,layoutParamsQuantity);
                }
                else if (i==6){
                    layoutParamsTotal.setMargins(140,10,50,0);
                    tv.setWidth(100);
                    tv.setText(proUnit);
                    ll.addView(tv,layoutParamsTotal);
                }
            }
            else if (i==3){
                reduce = new Button(MainActivity.this);
                reduce.setText("-");
                reduce.setMinWidth(5);
                reduce.setMinimumWidth(5);
                reduce.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });
                ll.addView(reduce);
            }
            else if (i==5){
                increase = new Button(MainActivity.this);
                increase.setText("+");
                increase.setMinWidth(5);
                increase.setMinimumWidth(5);
                ll.addView(increase);
            }
        }
        reduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {reduceExsitItem(ll);}
        });
        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {addExsitItem(ll);}
        });
        row.addView(ll);
        table.addView(row);
        updateTotalQuantity();
    }


    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            final DialogPay dialog = new DialogPay(MainActivity.this,R.style.Dialog);
            int what = msg.what;
            if (what == 0) {    //update
                TextView tv = (TextView) dialog.findViewById(R.id.timerShow);
                tv.setText(DateFormat.format("yyyy-MM-dd hh:mm:ss", System
                        .currentTimeMillis()).toString());
                if(dialog.isShowing()){
                    mHandler.sendEmptyMessageDelayed(0,1000);
                }
            }else {
                dialog.cancel();
            }
        }
    };





}



