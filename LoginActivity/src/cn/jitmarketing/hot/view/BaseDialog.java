package cn.jitmarketing.hot.view;

import cn.jitmarketing.hot.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;



/**
 * Created by Administrator on 2016/5/18.
 */
public class BaseDialog extends Dialog {

    /*上下文*/
    private Context context;
    /*弹窗视图*/
    private View dialogView;

    public BaseDialog(Context context, View dialogView) {
        super(context, R.style.share_dialog);
        this.context=context;
        this.dialogView=dialogView;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(dialogView);
        /*设置窗体大小*/
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (getScreenWidth(context) * 0.95);
        lp.height=LayoutParams.WRAP_CONTENT;
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);
//        /*点击其它位置不消失*/
//        this.setCancelable(false);
    }
    
    /**
     * 获得屏幕宽度
     * @return
     */
    public static int getScreenWidth(Context context)
    {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

}
