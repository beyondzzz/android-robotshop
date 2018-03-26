package com.example.shiyan.robotshop3;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
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
 * Created by shiyan on 2018/3/25.
 */

public class DialogFinish extends AlertDialog {



/**
 * 该自定义Dialog应用在：弹出框居中显示图片，点击其他区域自动关闭Dialog
 *
 * @author SHANHY(365384722@QQ.COM)
 * @date   2015年12月4日
 */

    public DialogFinish(Context context) {
        super(context);
    }

    public DialogFinish(Context context, int theme) {
        super(context, theme);
    }


    public static class Builder {
        private Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public DialogFinish create(final TableLayout table, final TextView tt, final TextView q) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final DialogFinish dialog = new DialogFinish(context,R.style.Dialog);
            dialog.setTitle("小日子便利店");
            dialog.setMessage("付款完成，谢谢惠顾～！");


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
                            try {
                                Thread.sleep(1000);                 //1000 毫秒，也就是1秒.
                            } catch(InterruptedException ex) {
                                Thread.currentThread().interrupt();
                            }
                            dialog.dismiss();
                            time.cancel();


                    }
                }
            }, 0, 1000);
            //45秒后关闭
            ////////
            return dialog;
        }
    }
}