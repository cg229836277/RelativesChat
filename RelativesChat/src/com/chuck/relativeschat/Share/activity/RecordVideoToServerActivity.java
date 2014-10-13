package com.chuck.relativeschat.Share.activity;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.activity.BaseActivity;
import com.chuck.relativeschat.common.VideoRecorderManager;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.VideoView;

public class RecordVideoToServerActivity extends BaseActivity implements SurfaceHolder.Callback{

	static final int REQUEST_VIDEO_CAPTURE = 1;
	public boolean isRecording = false;
	private Button startTakeVideo;// 开始录制按钮  
    private VideoRecorderManager mediarecorder;// 录制视频的类  
    private SurfaceView surfaceview;// 显示视频的控件  
    // 用来显示视频的一个接口，我靠不用还不行，也就是说用mediarecorder录制视频还得给个界面看  
    // 想偷偷录视频的同学可以考虑别的办法。。嗯需要实现这个接口的Callback接口  
    private SurfaceHolder surfaceHolder;  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_video_to_server);
		bindEvent();
	}
	
	@SuppressWarnings("deprecation")
	public void bindEvent(){
		startTakeVideo = (Button)findViewById(R.id.start_or_stop_btn);
		surfaceview = (SurfaceView)findViewById(R.id.take_video_view);
		
		surfaceHolder = surfaceview.getHolder();// 取得holder  
		surfaceHolder.addCallback(this); // holder加入回调接口  
        // setType必须设置，要不出错.  
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 
        
        startTakeVideo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(mediarecorder == null){
					mediarecorder = new VideoRecorderManager(surfaceHolder);
				}
				
				if(isRecording){
					finishRecordVideo();
				}else{
					mediarecorder.startTakeVideo();
					startTakeVideo.setBackgroundResource(R.drawable.take_video_pressed);
					isRecording = true;
				}
			}
		});
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		 surfaceHolder = arg0;  
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		 surfaceHolder = arg0;  
		 if(mediarecorder == null){
			mediarecorder = new VideoRecorderManager(surfaceHolder);
		 }
		 mediarecorder.startPreView();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		surfaceview = null;  
		surfaceHolder = null;  
		mediarecorder = null;  
		
		if(mediarecorder!=null){
			mediarecorder.releaseMediaRecorder();
			mediarecorder.release();
			mediarecorder=null;
       }
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
			finishRecordVideo();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void finishRecordVideo(){
		String videoUrl = mediarecorder.getRecordedVideoUrl();
		if(isRecording){			
			mediarecorder.stopTakeVideo();
			mediarecorder = null;
			isRecording = false;	
		}
		
		Intent intent = new Intent();
		intent.putExtra(ShareVideoToFriendsActivity.VIDEO_URL, videoUrl);
		setResult(RESULT_OK , intent);
		finish();
	}
}
