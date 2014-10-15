package com.chuck.relativeschat.Share.activity;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.activity.BaseActivity;
import com.chuck.relativeschat.common.BmobConstants;
import com.chuck.relativeschat.entity.ShareFileBean;
import com.chuck.relativeschat.tools.MediaFileCacheUtil;
import com.chuck.relativeschat.tools.StringUtils;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class ReviewRecordedVideoActivity extends BaseActivity {

	private Button cancelBtn;
	private Button confirmBtn;
	private VideoView recordedVideoView;
	private String recordedVideoUrl;
	private MediaFileCacheUtil fileCacheUtil = null;
	private String shareToUserName = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_review_recorded_video);
		
		fileCacheUtil = new MediaFileCacheUtil();
		
		recordedVideoUrl = getIntent().getStringExtra(ShareVideoToFriendsActivity.VIDEO_URL);
		shareToUserName = getIntent().getStringExtra(ShareVideoToFriendsActivity.SHARE_TO_USER);
		bindEvent();
	}
	
	public void bindEvent(){
		cancelBtn = (Button)findViewById(R.id.cancel_btn);
		confirmBtn = (Button)findViewById(R.id.confirm_btn);
		confirmBtn.setText("分享");
		cancelBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		confirmBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				uploadRecordedVideoToServer();
			}
		});
		
		recordedVideoView = (VideoView)findViewById(R.id.review_video_view);		
		//定义MediaController对象
		MediaController mediaController = new MediaController(this);
		//把MediaController对象绑定到VideoView上
		mediaController.setAnchorView(recordedVideoView);
		//设置VideoView的控制器是mediaController
		recordedVideoView.setMediaController(mediaController);
		
		//这两种方法都可以 videoView.setVideoPath("file:///sdcard/love_480320.mp4");
		recordedVideoView.setVideoURI(Uri.parse(recordedVideoUrl));
		//启动后就播放
		recordedVideoView.start();
	}
	
	public void uploadRecordedVideoToServer(){
		final File videoFile = new File(recordedVideoUrl);
		final BmobFile file = new BmobFile(videoFile);
		dialog.show();
		file.upload(getApplicationContext(), new UploadFileListener() {
			
			@Override
			public void onSuccess() {
				ShareFileBean shareBean = new ShareFileBean();
				shareBean.setFileType(ShareFileBean.VIDEO);
				shareBean.setFilePath(file.getFileUrl());
				shareBean.setFileName(file.getFilename());
				shareBean.setShareUser(userManager.getCurrentUserName());
				shareBean.setIsGoodNumber("0");
				if(!StringUtils.isEmpty(shareToUserName)){
					shareBean.setShareTo(shareToUserName);
					shareBean.setIsShareToAll("0");
				}else{
					shareBean.setIsShareToAll("1");
				}
				
				shareBean.save(getApplicationContext(), new SaveListener() {
					
					@Override
					public void onSuccess() {
						dialog.dismiss();
						//在本地文件上传成功之后，将其添加到缓存，避免再次播放的时候从网络下载						
						fileCacheUtil.addMediaFileToMemoryCache(file.getFileUrl(), videoFile);
						videoFile.renameTo(new File(BmobConstants.RECORD_VIDEO_CACHE_PATH));
						videoFile.delete();
						//发送之后立即更新数据
						Intent intent = new Intent();
						setResult(RESULT_OK);
						mToast.showMyToast("分享成功！", Toast.LENGTH_SHORT);
						finish();												
					}
					
					@Override
					public void onFailure(int arg0, String arg1) {
						mToast.showMyToast("数据上传失败！", Toast.LENGTH_SHORT);
						System.out.println("数据上传失败的原因的是" + arg1);
						dialog.dismiss();
					}
				});
			}
			
			@Override
			public void onProgress(Integer arg0) {
				
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				mToast.showMyToast("文件上传失败！", Toast.LENGTH_SHORT);
				System.out.println("文件上传失败的原因的是" + arg1);
				dialog.dismiss();
			}
		});
	}
}
