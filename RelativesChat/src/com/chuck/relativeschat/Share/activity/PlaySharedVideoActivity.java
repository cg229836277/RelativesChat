package com.chuck.relativeschat.Share.activity;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.activity.BaseActivity;
import com.chuck.relativeschat.tools.StringUtils;

public class PlaySharedVideoActivity extends BaseActivity implements OnErrorListener{
	
	public static final String VIDEO_URL = "video_url";
	private String videoUrl = null;
	private VideoView playVideoView;
	private MediaController videoController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_shared_video);
		videoUrl = getIntent().getStringExtra(VIDEO_URL);
		if(!StringUtils.isEmpty(videoUrl)){
			dialog.show();
			bindEvent();
			playVideo();
		}
	}
	
	public void bindEvent(){
		
		playVideoView = (VideoView)findViewById(R.id.play_video_view);
	}
	
	public void playVideo(){
		videoController = new MediaController(this);
		videoController.setAnchorView(playVideoView);
		playVideoView.setVideoURI(Uri.parse(videoUrl));		
		playVideoView.setMediaController(videoController);
		playVideoView.requestFocus();
		playVideoView.setOnErrorListener(this);
		try{
			getWindow().setFormat(PixelFormat.TRANSLUCENT);
			playVideoView.setOnPreparedListener(new OnPreparedListener() {			
				@Override
				public void onPrepared(MediaPlayer arg0) {
			        if (playVideoView != null && videoUrl != null) {   
			        	dialog.dismiss();
			        	playVideoView.start();
			        } else {
			            mToast.showMyToast("发生错误", Toast.LENGTH_SHORT);
			            finish();
			        }
				}
			});
		}catch(Exception e){
			mToast.showMyToast("播放出错！", Toast.LENGTH_SHORT);
			finish();
		}
	}
	
	@Override
	public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
		System.out.println("出现错误的原因是" + arg1);
		return false;
	}
  
    public void onPause() {
        playVideoView.stopPlayback();       
        super.onPause();
    }
}
