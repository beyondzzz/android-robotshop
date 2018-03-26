package com.example.shiyan.robotshop3;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.shiyan.robotshop3.R.id.table;


/**
 * 该自定义Dialog应用在：弹出框居中显示图片，点击其他区域自动关闭Dialog
 *
 * @author SHANHY(365384722@QQ.COM)
 * @date   2015年12月4日
 */
public class DialogPay extends Dialog {

    public DialogPay(Context context) {
        super(context);
    }

    public DialogPay(Context context, int theme) {
        super(context, theme);
    }

    public void check(TableLayout table, TextView t, TextView q, String qrID){
        if (api.getQRStatus(qrID)){

            table.removeAllViews();
            t.setText(null);
            q.setText(null);
        }
    }

    public static class Builder {
        private Context context;
        private Bitmap image;
        private String qrID;

        public Builder(Context context) {
            this.context = context;
        }

        public Bitmap getImage() {
            return image;
        }

        public void setImage(Bitmap image) {
            this.image = image;
        }

        public DialogPay create(final TableLayout table, final TextView tt, final TextView q, String qr_id) {
            qrID = qr_id;
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final DialogPay dialog = new DialogPay(context,R.style.Dialog);
            dialog.setTitle("小日子便利店");
            View layout = inflater.inflate(R.layout.paymentdialog, null);
            dialog.addContentView(layout, new LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                    , android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

            dialog.setContentView(layout);

            TextView desc = (TextView) layout.findViewById(R.id.context);
            desc.setText("请使用微信二维码扫描，并付款");

            ImageView img = (ImageView)layout.findViewById(R.id.img_qrcode);
            img.setImageBitmap(getImage());

            ////////
            //45秒后关闭
            final Timer t = new Timer();
            t.schedule(new TimerTask() {
                public void run() {
                    dialog.dismiss();
                    t.cancel();
                }
            }, 60000);
            //45秒后关闭
            ////////

            ////////
            //45秒后关闭
            final Timer time = new Timer();
            time.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(dialog.isShowing()){
                        Log.v("beyond",qrID);
                        Log.v("beyond","we are here."+qrID);
                        if(api.getQRStatus(qrID)){
                            dialog.dismiss();
                            time.cancel();
                            //table.removeAllViews();
                            //tt.setText(null);
                            //q.setText(null);

                        }
                    }
                }
            }, 0, 1000);
            //45秒后关闭
            ////////
            return dialog;
        }
    }
}