package com.slwy.lwq.lrcplayer;

import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LrcUtil {

    private int mTheLastTime;
    private int curLocation = 0;  //当前位置
    private int maxRecordNum = 0;  //最大记录个数
    private List<LrcRecord> recordList = new ArrayList<>();

    LrcUtil(File file){
        File mp3File = new File(Environment.getExternalStorageDirectory(), "205.mp3");
        openMp3File(mp3File);

        openLrcFile(file);
    }

//    public void openFile(String filename) {
//        openLrcFile(filename);
//    }

    public LrcRecord reLocation(int inc){
        if(inc == 0){
            curLocation = 0;
        }
        if (inc == -1){
            curLocation = curLocation -1;
            if (curLocation < 0) curLocation = maxRecordNum -1 ;
        }
        if (inc == 1){
            curLocation = curLocation + 1;
            if (curLocation > maxRecordNum -1) curLocation = 0;
        }
        return seekTo(curLocation);
    }

    private void openMp3File(File file){
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(file.getPath());
//            mediaPlayer.prepare();
            mTheLastTime = mediaPlayer.getDuration();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //以特定格式打开文件
    private void openLrcFile(File lrcFile){  //读取lrc文件
        try {
            FileInputStream fileInputStream = new FileInputStream(lrcFile);  //将文件导入为文件输入流
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream,"utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);  //转成字节流

            readLrcFile(bufferedReader);

            bufferedReader.close();
            inputStreamReader.close();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //对文件逐行扫描
    private void readLrcFile(BufferedReader bufferedReader) {  //将文件内容导入到一个数据列表中
        String regex = "\\[\\d\\d:\\d\\d.\\d\\d]"; // 正则表达式
        Pattern pattern = Pattern.compile(regex);
        String lineStr;  //用于存储行数据
        int timeTemp = mTheLastTime;
        Stack<Map<Integer, String>> mapStack = new Stack<>();  //用于保存lrc存入的数据
        Stack<LrcRecord> listStack = new Stack<>();  //用于重新构造新的数据
        try {
            while ((lineStr = bufferedReader.readLine()) != null) {  //将lrc文件内容推送到一个栈中
                Matcher matcher = pattern.matcher(lineStr);
                if(matcher.find()){
                    int timeInt = timeToInt(matcher.group().substring(1, 9));
                    String textStr = lineStr.substring(matcher.end());
                    Map<Integer, String> map = new HashMap<>();
                    map.put(timeInt, textStr);
                    mapStack.push(map);
                }
            }
            while(!mapStack.empty()){  //重新构造一个新栈，中间层，后进先出
                for (Map.Entry<Integer, String> entry : mapStack.pop().entrySet()) {
//                    boolean display = true;
                    int startTime = entry.getKey();
                    int stopTime = timeTemp;
                    String lrcText = entry.getValue();
                    LrcRecord lrcRecord = new LrcRecord(true, startTime, stopTime, lrcText);
                    listStack.push(lrcRecord);
                    timeTemp = entry.getKey();
                }
            }
            while(!listStack.empty()){  //使用中间层来构造一个数据列表，全局的，目的是便于修改数据
                recordList.add(listStack.pop());
            }
            maxRecordNum = recordList.size();
        } catch (IOException e) {  //异常处理
            e.printStackTrace();
        }
    }

    //字符串时间 --> 数字时间
    private int timeToInt(String string) {  //将00:00.00的时间表示方式转换为毫秒单位的时间
        string = string.replace(".", ":");  //转换成00:00:00
        String timeData[] = string.split(":");  //分离成三个数的一个数组
        int minute = Integer.parseInt(timeData[0]);  //提取分钟
        int second = Integer.parseInt(timeData[1]);  //提取秒钟
        int millisecond = Integer.parseInt(timeData[2]) * 10;  //提取毫秒，这里只显示两位
        return (minute * 60 + second) * 1000 + millisecond;  //返回毫秒为单位的时间
    }

    private LrcRecord seekTo(int index){
        return recordList.get(index);
    }

    //这段程序留着备用
//    Uri convertUri(Uri uri) {  //将"content://"类型的Uri转换为"file://"的Uri
//        if(uri.toString().substring(0, 7).equals("content")) {  //如果是以"content"开头
//            String[] colName = { MediaStore.MediaColumns.DATA };    //声明要查询的字段
//            Cursor cursor = getContentResolver().query(uri, colName,  //以uri进行查询
//                    null, null, null);
//            if (cursor != null){
//                cursor.moveToFirst();      //移到查询结果的第一个记录
//                uri = Uri.parse("file://" + cursor.getString(0)); //将路径转为 Uri
//                cursor.close();
//            }
//        }
//        return uri;
//    }

    //数字时间 --> 字符串时间  例如：mm:ss.ms
    public String timeFromIntToString(int intTime) {
//        int minute = intTime/1000/60;
//        int second = (intTime - minute * 60 * 1000)/1000;
//        int millisecond = intTime - minute * 60 * 1000 - second * 1000;
//        String strMin = String.format(Locale.CHINA,"%02d", min);
//        String strSecond = String.format(Locale.CHINA,"%02d", second);
//        String strMillisecond = String.format(Locale.CHINA,"%02d", millisecond).substring(0, 2);
//        return strMin + ":" + strSecond + "." + strMillisecond;   // 返回 mm:ss.ms
        int millisecond = intTime % 1000;
        intTime = intTime / 1000;
        int second = intTime % 60;
        intTime = intTime /60;
        int minute = intTime % 60;
        return String.format(Locale.CHINA,"%02d:%02d.%02d", minute, second, millisecond/10);
    }

    public List<LrcRecord> getRecordList(){
        return recordList;
    }
}
