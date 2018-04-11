package com.ysbing.lrcshow;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;

import java.io.FileNotFoundException;

public class MainActivity extends FragmentActivity {
    private LRCFragment lrcFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //歌词路径，一定要提前检查好这个文件要存在
                //我放了一个歌词文件“演员.lrc”在assets，可复制去测试
                String lrcPath = "/storage/sdcard0/205.lrc";
                // 音乐的总时间，如没的话，传0即可
                int musicDuration = 0;
                try {
                    lrcFragment.start(lrcPath, musicDuration);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "歌词文件不存在", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initView() {
        lrcFragment = new LRCFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_music_lrc, lrcFragment);
        transaction.commit();
    }
}
