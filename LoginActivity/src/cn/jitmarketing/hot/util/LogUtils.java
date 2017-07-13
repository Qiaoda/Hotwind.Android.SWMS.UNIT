package cn.jitmarketing.hot.util;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;

/**
 * Created by hotwind on 2016/8/1.
 */
public class LogUtils {

    /*路径*/
    private static final String PATH = "//sdcard//hotlog.txt";
    // 内存中日志文件最大值，10M
    private static final int MEMORY_LOG_FILE_MAX_SIZE = 1024 * 1024 * 3;

    //在SD卡上用txt记录
    public static void logOnFile(String logs) {
        if (checkSDCardStatus()) {
            File logFile = new File(PATH);
            if (logFile.exists() && logFile.length() > MEMORY_LOG_FILE_MAX_SIZE) {
                logFile.delete();
            }
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss:SSS"); 
            String date = sDateFormat.format(new java.util.Date());
            logs = "\r\n"+date + "-->>" + logs;
            FileOutputStream fout;
//            DataOutputStream dataout;
            OutputStreamWriter dataout;
            try {
                fout = new FileOutputStream(PATH, true);
                dataout=new OutputStreamWriter(fout,"UTF-8");
                dataout.write(logs);  
                dataout.flush();
                fout.flush();
                dataout.close();
                fout.close(); 
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    // SD卡是否存在
    private static boolean checkSDCardStatus() {
        boolean SDCardStatus = false;
        String sDStateString = android.os.Environment.getExternalStorageState();
        if (sDStateString.equals(android.os.Environment.MEDIA_MOUNTED)) {
            SDCardStatus = true;
        } else {
            // SD卡不可用
        }
        return SDCardStatus;
    }
}
