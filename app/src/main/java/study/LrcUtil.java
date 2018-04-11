package com.ysbing.lrcshow;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LrcUtil {

    private List<Integer> mTimeList = new ArrayList<>();
    private List<String> mWords = new ArrayList<>();
    private Iterator<Integer> mTimeIterator;
    private Iterator<String> mWordIterator;

    public LrcUtil(File lrcFile) throws FileNotFoundException {
        readLrcFile(lrcFile);
    }

    public boolean hasTime() {
        return mTimeIterator.hasNext();
    }

    public boolean hasWord() {
        return mWordIterator.hasNext();
    }

    public int getTime() {
        return mTimeIterator.next();
    }

    public String getWord() {
        return mWordIterator.next();
    }

    //文件打开函数
    private void readLrcFile(File file) throws FileNotFoundException {  //读取lrc文件
        FileInputStream fileInputStream = new FileInputStream(file);  //将文件导入为文件输入流
        InputStreamReader inputStreamReader;
        try {
            inputStreamReader = new InputStreamReader(fileInputStream, "utf-8");
            //按utf-8的编码格式打开流
            readLrcStream(inputStreamReader);  //读取lrc流，下面的方法
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    //文件处理函数
    private void readLrcStream(InputStreamReader inputStreamReader) {
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);  //转成字节流
        String s;  //用于存储行数据
        try {
            while ((s = bufferedReader.readLine()) != null) {  //当前行非空
                addTimeToList(s);  //将时间添加到mTimeList
                if ((s.contains("[ar:")) || (s.contains("[ti:")) || (s.contains("[by:"))
                        || (s.contains("[al:"))) {
                    continue;
                } else {
                    if (TextUtils.isEmpty(s))
                        continue;
                    int startIndex = s.indexOf("[");
                    int endIndex = s.indexOf("]");
                    if (startIndex >= 0 && endIndex >= 0) {
                        String ss = s.substring(startIndex, endIndex + 1);  //ss为时间 s为行串
                        s = s.replace(ss, "");  //s为行文字
                    } else continue;
                }
                mWords.add(s);  //将行文字加入mWords
            }
            mTimeIterator = mTimeList.iterator();  //时间迭代
            mWordIterator = mWords.iterator();  //文字迭代
            bufferedReader.close();  //关闭缓存
            inputStreamReader.close();  //关闭输入流
        } catch (IOException e) {  //异常处理
            e.printStackTrace();
            mWords.add("没有读取到歌词");
        }
    }

    private void addTimeToList(String string) {  //在字符串中提取出时间 存入mTimeList
        Matcher matcher = Pattern.compile(  //正则表达式 进行匹配
                "\\[\\d{1,2}:\\d{1,2}([\\.:]\\d{1,2})?\\]").matcher(string);
        //String regex = "\\[(\\d{1,2}):(\\d{1,2}).(\\d{1,2})\\]"; // 正则表达式
        if (matcher.find()) {  //如果有匹配
            String str = matcher.group();
            mTimeList.add(timeHandler(str.substring(1, str.length() - 1)));
        }
    }

    //时间处理函数
    private int timeHandler(String string) {  //将00:00.00的时间表示方式转换为毫秒单位的时间
        string = string.replace(".", ":");  //转换成00:00:00
        String timeData[] = string.split(":");  //分离成一个三位的数组
        int minute = Integer.parseInt(timeData[0]);  //提取分钟
        int second = Integer.parseInt(timeData[1]);  //提取秒
        int millisecond = Integer.parseInt(timeData[2]);  //提取毫秒
        if (timeData[2].length() == 1)  //针对00:00.0这种情况处理
            millisecond *= 100;
        else if (timeData[2].length() == 2)  //针对00:00.00这种情况处理
            millisecond *= 10;

        return (minute * 60 + second) * 1000 + millisecond;  //返回毫秒为单位的时间
    }
}
