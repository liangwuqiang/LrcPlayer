package com.slwy.lwq.lrcplayer;

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

    //处理歌词文件
    private void readLrcFile(File file) throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(file);
        InputStreamReader inputStreamReader;
        try {
            inputStreamReader = new InputStreamReader(
                    fileInputStream, "utf-8");
            readLrcStream(inputStreamReader);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void readLrcStream(InputStreamReader inputStreamReader) {
        BufferedReader bufferedReader = new BufferedReader(
                inputStreamReader);
        String s;
        try {
            while ((s = bufferedReader.readLine()) != null) {
                addTimeToList(s);
                if ((s.contains("[ar:")) || (s.contains("[ti:")) || (s.contains("[by:"))
                        || (s.contains("[al:"))) {
                    continue;
                } else {
                    if (TextUtils.isEmpty(s))
                        continue;
                    int startIndex = s.indexOf("[");
                    int endIndex = s.indexOf("]");
                    if (startIndex >= 0 && endIndex >= 0) {
                        String ss = s.substring(startIndex, endIndex + 1);
                        s = s.replace(ss, "");
                    } else continue;
                }
                mWords.add(s);
            }
            mTimeIterator = mTimeList.iterator();
            mWordIterator = mWords.iterator();
            bufferedReader.close();
            inputStreamReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            mWords.add("没有读取到歌词");
        }
    }

    private void addTimeToList(String string) {
        Matcher matcher = Pattern.compile(
                "\\[\\d{1,2}:\\d{1,2}([\\.:]\\d{1,2})?\\]").matcher(string);
        if (matcher.find()) {
            String str = matcher.group();
            mTimeList.add(timeHandler(str.substring(1,
                    str.length() - 1)));
        }
    }

    // 分离出时间
    private int timeHandler(String string) {
        string = string.replace(".", ":");
        String timeData[] = string.split(":");
        // 分离出分、秒并转换为整型
        int minute = Integer.parseInt(timeData[0]);
        int second = Integer.parseInt(timeData[1]);
        int millisecond = Integer.parseInt(timeData[2]);
        if (timeData[2].length() == 1)
            millisecond *= 100;
        else if (timeData[2].length() == 2)
            millisecond *= 10;

        // 计算上一行与下一行的时间转换为毫秒数
        return (minute * 60 + second) * 1000 + millisecond;
    }
}
