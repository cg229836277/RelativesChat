/*
 * Copyright (C) 2014 gujicheng
 * 未经作者许可，禁止将该程序用于商业用途
 * 
 * 该声波通信程序在前一个开源版本（SinVoice）的基础上，做了许多优化：
 * 优化如下：
 * 1. 识别效率更高，几乎达到100%，完全可以达到商业用途标准，比chirp，支付宝，茄子快传等软件的识别效率更高。
 * 2. 能支持更多复杂场景的识别，在有嘈杂大声的背景音乐，嘈杂的会议室，食堂，公交车，马路，施工场地，
 *     小汽车，KTV等一些复杂的环境下，依然能保持很高的识别率。
 * 3. 能支持更多token的识别，通过编码可以传送所有字符。
 * 4. 通过定制可以实现相同字符的连续传递,比如“234456”。
 * 5. 支持自动纠错功能，在有3个以内字符解码出错的情况下可以自动纠正。
 * 6. 程序运行效率非常高，可以用于智能手机，功能手机，嵌入式设备，PC，平板等嵌入式系统上。
 * 7. 声波的频率声音和音量可定制。
 * 
 * 此demo程序属于试用性质程序，仅具备部分功能，其限制如下：
 * 1. 仅支持部分字符识别。
 * 2. 识别若干次后，程序会自动停止识别。若想继续使用，请停止该程序，然后重新启动程序。
 * 3. 不支持连续字符传递。
 * 4. 不支持自动纠错功能。
 * 5. 禁止用于商业用途。
 * 
 * 若您对完整的声波通信程序感兴趣，请联系作者获取商业授权版本（仅收取苦逼的加班费）。
 *************************************************************************
 **                   作者信息                                                            **
 *************************************************************************
 ** Email: gujicheng197@126.com                                        **
 ** QQ   : 29600731                                                                 **
 ** Weibo: http://weibo.com/gujicheng197                          **
 *************************************************************************
 */
package com.example.sinvoicedemo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.libra.sinvoice.Common;
import com.libra.sinvoice.LogHelper;
import com.libra.sinvoice.SinVoicePlayer;
import com.libra.sinvoice.SinVoiceRecognition;

public class MainActivity extends Activity implements SinVoiceRecognition.Listener, SinVoicePlayer.Listener {
    private final static String TAG = "MainActivity";

    private final static int MSG_SET_RECG_TEXT = 1;
    private final static int MSG_RECG_START = 2;
    private final static int MSG_RECG_END = 3;
    private final static int MSG_PLAY_TEXT = 4;

    private final static int TOKEN_LEN = 6;
    private final static String BAKCUP_LOG_PATH = "/sinvoice_backup";

    private final static int TYPE_NONE = 0;
    private final static int TYPE_PLAY = 1;
    private final static int TYPE_RECOGNITION = 2;

    private Handler mHanlder;
    private SinVoicePlayer mSinVoicePlayer;
    private SinVoiceRecognition mRecognition;
    private boolean mIsReadFromFile;
    private String mSdcardPath;
    private PowerManager.WakeLock mWakeLock;
    private TextView mPlayTextView;
    private TextView mRecognisedTextView;
    private TextView mRegState;
    private String mPlayText;
    private TextView mState;
    private int mSinVoiceType;

    static {
        System.loadLibrary("zjnicall");
        LogHelper.d(TAG, "jnicall loadlibrary");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSinVoiceType = TYPE_NONE;
        mState = (TextView)this.findViewById(R.id.state);

        mIsReadFromFile = false;

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);

        mSdcardPath = Environment.getExternalStorageDirectory().getPath();

        mSinVoicePlayer = new SinVoicePlayer();
        mSinVoicePlayer.init();
        mSinVoicePlayer.setListener(this);

        mRecognition = new SinVoiceRecognition();
        mRecognition.init(this);
        mRecognition.setListener(this);

        mPlayTextView = (TextView) findViewById(R.id.playtext);
        mRecognisedTextView = (TextView) findViewById(R.id.regtext);
        mRegState = (TextView)findViewById(R.id.regstate);
        mHanlder = new RegHandler(this);

        Button playStart = (Button) findViewById(R.id.start_play);
        playStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if ( TYPE_NONE == mSinVoiceType ) {
                    mSinVoiceType = TYPE_PLAY;
                    mSinVoicePlayer.play(TOKEN_LEN, true, 2000);
                    mState.setText("state:played");
                } else if ( TYPE_PLAY == mSinVoiceType ) {
                    Toast.makeText(getApplicationContext(), "start player already",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Please stop recognition",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button playStop = (Button) findViewById(R.id.stop_play);
        playStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if ( TYPE_PLAY == mSinVoiceType ) {
                    mSinVoiceType = TYPE_NONE;
                    mSinVoicePlayer.stop();
                    mState.setText("state:none");
                }
            }
        });

        Button recognitionStart = (Button) findViewById(R.id.start_reg);
        recognitionStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if ( TYPE_NONE == mSinVoiceType ) {
                    mSinVoiceType = TYPE_RECOGNITION;
                    mRecognition.start(TOKEN_LEN, mIsReadFromFile);
                    mState.setText("state:recognized");
                } else if ( TYPE_RECOGNITION == mSinVoiceType ) {
                    Toast.makeText(getApplicationContext(), "start recognition already",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "please stop play",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button recognitionStop = (Button) findViewById(R.id.stop_reg);
        recognitionStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if ( TYPE_RECOGNITION == mSinVoiceType ) {
                    mSinVoiceType = TYPE_NONE;
                    mRecognition.stop();
                    mState.setText("state:none");
                }
            }
        });

        CheckBox cbReadFromFile = (CheckBox) findViewById(R.id.fread_from_file);
        cbReadFromFile.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                mIsReadFromFile = isChecked;
            }
        });

        Button btBackup = (Button) findViewById(R.id.back_debug_info);
        btBackup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                backup();
            }
        });

        Button btClearBackup = (Button) findViewById(R.id.clear_debug_info);
        btClearBackup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                new AlertDialog.Builder(MainActivity.this).setTitle("information").setMessage("Sure to clear?").setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        clearBackup();
                    }
                }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        mWakeLock.acquire();
    }

    @Override
    public void onPause() {
        super.onPause();

        mWakeLock.release();

        mSinVoicePlayer.stop();
        mRecognition.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mRecognition.uninit();
        mSinVoicePlayer.uninit();
    }

    private void clearBackup() {
        delete(new File(mSdcardPath + BAKCUP_LOG_PATH));

        Toast.makeText(this, "clear backup log info successful", Toast.LENGTH_SHORT).show();
    }

    private static void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            file.delete();
        }
    }

    private void backup() {
        mRecognition.stop();

        String timestamp = getTimestamp();
        String destPath = mSdcardPath + BAKCUP_LOG_PATH + "/back_" + timestamp;
        try {
            copyDirectiory(destPath, mSdcardPath + "/sinvoice");
            copyDirectiory(destPath, mSdcardPath + "/sinvoice_log");

            FileOutputStream fout = new FileOutputStream(destPath + "/text.txt");

            String str = mPlayTextView.getText().toString();
            byte[] bytes = str.getBytes();
            fout.write(bytes);

            str = mRecognisedTextView.getText().toString();
            bytes = str.getBytes();
            fout.write(bytes);

            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "backup log info successful", Toast.LENGTH_SHORT).show();
    }

    private static class RegHandler extends Handler {
        private StringBuilder mTextBuilder = new StringBuilder();
        private MainActivity mAct;
        public RegHandler(MainActivity act) {
            mAct = act;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_SET_RECG_TEXT:
                char ch = (char) msg.arg1;
                mTextBuilder.append(ch);
                break;

            case MSG_RECG_START:
                mTextBuilder.delete(0, mTextBuilder.length());
                break;

            case MSG_RECG_END:
                LogHelper.d(TAG, "recognition end gIsError:" + msg.arg1);
                if ( msg.arg1 >= 0) {
                    if (null != mAct ) {
                        mAct.mRecognisedTextView.setText(mTextBuilder.toString());
                        mAct.mRegState.setText("reg ok(" + msg.arg1 + ")");
                    }
                } else {
/*                    mAct.mRecognisedTextView.setText(mTextBuilder.toString());
                    mAct.mRegState.setText("reg err(" + msg.arg1 + ")");
*///                    mAct.mRegState.setText("reg err");
                }
                break;

            case MSG_PLAY_TEXT:
                mAct.mPlayTextView.setText(mAct.mPlayText);
                break;
            }
            super.handleMessage(msg);
        }
    }

    @Override
    public void onSinVoiceRecognitionStart() {
        mHanlder.sendEmptyMessage(MSG_RECG_START);
    }

    @Override
    public void onSinVoiceRecognition(char ch) {
        mHanlder.sendMessage(mHanlder.obtainMessage(MSG_SET_RECG_TEXT, ch, 0));
    }

    @Override
    public void onSinVoiceRecognitionEnd(int result) {
        mHanlder.sendMessage(mHanlder.obtainMessage(MSG_RECG_END, result, 0));
    }

    @Override
    public void onSinVoicePlayStart() {
        LogHelper.d(TAG, "start play");
    }

    @Override
    public void onSinVoicePlayEnd() {
        LogHelper.d(TAG, "stop play");
    }

    private static String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        return sdf.format(new Date());
    }

    private static void copyFile(File targetFile, File sourceFile) throws IOException {
        FileInputStream input = new FileInputStream(sourceFile);
        BufferedInputStream inBuff = new BufferedInputStream(input);

        FileOutputStream output = new FileOutputStream(targetFile);
        BufferedOutputStream outBuff = new BufferedOutputStream(output);

        byte[] b = new byte[1024 * 5];
        int len;
        while ((len = inBuff.read(b)) != -1) {
            outBuff.write(b, 0, len);
        }
        outBuff.flush();

        inBuff.close();
        outBuff.close();
        output.close();
        input.close();
    }

    private static void copyDirectiory(String targetDir, String sourceDir) throws IOException {
        (new File(targetDir)).mkdirs();
        File[] file = (new File(sourceDir)).listFiles();
        if ( null != file ) {
            for (int i = 0; i < file.length; i++) {
                if (file[i].isFile()) {
                    File sourceFile = file[i];
                    File targetFile = new File(new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());
                    copyFile(targetFile, sourceFile);
                }
                if (file[i].isDirectory()) {
                    String srcPath = sourceDir + "/" + file[i].getName();
                    String targetPath = targetDir + "/" + file[i].getName();
                    copyDirectiory(targetPath, srcPath);
                }
            }
        }
    }

    @Override
    public void onSinToken(int[] tokens) {
        if ( null != tokens ) {
            StringBuilder sb = new StringBuilder();
            for ( int i = 0; i < tokens.length; ++i ) {
                sb.append(Common.DEFAULT_CODE_BOOK.charAt(tokens[i]));
            }

            mPlayText = sb.toString();
            LogHelper.d(TAG, "onSinToken " + mPlayText);
            mHanlder.sendEmptyMessage(MSG_PLAY_TEXT);
        }
    }

}
