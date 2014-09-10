package com.chuck.relativeschat.common;

import java.io.File;
import java.io.IOException;
import com.chuck.relativeschat.R;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

/**
 * 
 * @Title：SAFEYE@
 * @Description：
 * @date 2014-9-1 下午4:53:48
 * @author chengang
 * @version 1.0
 */
public class AudioRecorderManager implements MediaPlayer.OnPreparedListener , OnCompletionListener{
	private final String TAG = "RecordManager";
	private MediaRecorder mMediaRecorder = null;
	private MediaPlayer mPlayer = null;
	public static final int MAX_LENGTH = 1000 * 60 * 15;// 最大录音时长1000*60*10;
	private File file;
	private String mFileUrl;
	private Handler myActivityHandler;

	/**
	 * 初始化录音数据
	 * @param file
	 * @param view
	 */
	public AudioRecorderManager(File file,ImageView view) {
		this.file = file;
		this.view=view;
	}
	
	/**
	 * 初始化播放录音的数据
	 * @param fileUrl
	 * @param handler
	 */
	public AudioRecorderManager(String fileUrl , Handler handler) {
		this.mFileUrl = fileUrl;
		this.myActivityHandler = handler;
	}

	private long startTime;
	private long endTime;

	/**
	 * 开始录音 使用amr格式
	 * 
	 * @param mRecAudioFile
	 *            录音文件
	 * @return
	 */
	public void startRecord() {
		// 开始录音
		/* ①Initial：实例化MediaRecorder对象 */
		mMediaRecorder = new MediaRecorder();
		/* ②setAudioSource/setVedioSource */
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
		/*
		 * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
		 * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
		 */
		mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
		/* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
		mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		/* ③准备 */
		mMediaRecorder.setOutputFile(file.getAbsolutePath());
		mMediaRecorder.setMaxDuration(MAX_LENGTH);
		try {
			mMediaRecorder.prepare();
		} catch (IOException e) {
			Log.e(TAG, "prepare() failed");
		}
		/* ④开始 */
		mMediaRecorder.start();
		// AudioRecord audioRecord.
		/* 获取开始时间* */
		startTime = System.currentTimeMillis();
		updateMicStatus();
		Log.i(TAG, "startTime" + startTime);

	}

	/**
	 * 停止录音
	 * 
	 * @param mMediaRecorder
	 */
	public long stopRecord() {
		if (mMediaRecorder == null){
			return 0L;
		}
		endTime = System.currentTimeMillis();
		Log.i(TAG, "endTime" + endTime);
		mMediaRecorder.stop();
		mMediaRecorder.release();
		mMediaRecorder = null;
		Log.i(TAG, "Time" + (endTime - startTime));
		return endTime - startTime;
	}

	private final Handler mHandler = new Handler();
	private Runnable mUpdateMicStatusTimer = new Runnable() {
		public void run() {
			updateMicStatus();
		}
	};

	/**
	 * 更新话筒状态 分贝是也就是相对响度 分贝的计算公式K=20lg(Vo/Vi) Vo当前振幅值 Vi基准值为600：我是怎么制定基准值的呢？ 当20
	 * * Math.log10(mMediaRecorder.getMaxAmplitude() / Vi)==0的时候vi就是我所需要的基准值
	 * 当我不对着麦克风说任何话的时候，测试获得的mMediaRecorder.getMaxAmplitude()值即为基准值。
	 * Log.i("mic_", "麦克风的基准值：" + mMediaRecorder.getMaxAmplitude());前提时不对麦克风说任何话
	 */
	private int BASE = 600;
	private int SPACE = 200;// 间隔取样时间
	private ImageView view;

	private void updateMicStatus() {
		if (mMediaRecorder != null && view != null) {
			// int vuSize = 10 * mMediaRecorder.getMaxAmplitude() / 32768;
			int ratio = mMediaRecorder.getMaxAmplitude() / BASE;
			int db = 0;// 分贝
			if (ratio > 1)
				db = (int) (20 * Math.log10(ratio));
			System.out.println("分贝值："+db+"     "+Math.log10(ratio));
			switch (db / 4) {
			case 0:
				view.setImageResource(R.drawable.mic_default);
				break;
			case 1:
				view.setImageResource(R.drawable.mic_1);
				break;
			case 2:
				view.setImageResource(R.drawable.mic_2);
				break;
			case 3:
				view.setImageResource(R.drawable.mic_3);
				break;
			case 4:
				view.setImageResource(R.drawable.mic_4);
				break;
			case 5:
				view.setImageResource(R.drawable.mic_5);
				break;
			default:
				view.setImageResource(R.drawable.mic_default);
				break;
			}
			mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
			/*
			 * if (db > 1) { vuSize = (int) (20 * Math.log10(db)); Log.i("mic_",
			 * "麦克风的音量的大小：" + vuSize); } else Log.i("mic_", "麦克风的音量的大小：" + 0);
			 */
		}
	}
	
    public void startPlaying() {
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnPreparedListener(this);
        try {
        	System.out.println("录音地址是" + mFileUrl);
            mPlayer.setDataSource(mFileUrl);
            mPlayer.prepare();          
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    public void stopPlaying() {
    	if (mPlayer != null && mPlayer.isPlaying()) {
    		mPlayer.stop();
    		mPlayer.release();
    		mPlayer = null;
		}
    }
    
	@Override
	public void onPrepared(MediaPlayer mp) {		
		mPlayer.start();
	}
	
	@Override
	public void onCompletion(MediaPlayer arg0) {
		Log.i("mediaPlayer", "onCompletion");
		Message msg = new Message();
		msg.what = 2;
		myActivityHandler.sendMessage(msg);
		stopPlaying();
		
		System.gc();
	}
	
	public void destoryPlayer(){
		if (mMediaRecorder != null) {
			mMediaRecorder.release();
			mMediaRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
	}
}

