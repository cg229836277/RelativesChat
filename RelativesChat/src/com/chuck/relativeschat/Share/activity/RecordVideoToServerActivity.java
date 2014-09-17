package com.chuck.relativeschat.Share.activity;

import com.chuck.relativeschat.R;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.widget.VideoView;

public class RecordVideoToServerActivity extends Activity {

	static final int REQUEST_VIDEO_CAPTURE = 1;
	private VideoView mVideoView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_video_to_server);
		
		mVideoView = (VideoView)findViewById(R.id.video_view);
		
		dispatchTakeVideoIntent();
	}
	
	private void dispatchTakeVideoIntent() {
	    Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
	    if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
	        startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
	    }
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
	        Uri videoUri = data.getData();
	        mVideoView.setVideoURI(videoUri);
	    }
	}
}
