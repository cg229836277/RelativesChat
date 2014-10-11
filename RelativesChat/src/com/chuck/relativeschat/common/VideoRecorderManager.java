package com.chuck.relativeschat.common;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Build.VERSION;
import android.view.SurfaceHolder;
import android.widget.MediaController;

public class VideoRecorderManager extends MediaRecorder implements OnErrorListener , OnInfoListener{
	
	public final String videoFileDir = BmobConstants.RECORD_VIDEO_PATH;
	
	public final String videoFilePath = videoFileDir + UUID.randomUUID() + ".mp4";
	
	private SurfaceHolder mSurfaceHolder;
	private final int MAX_TIME = 15000;
	
	private Camera camera;
	private Camera.Parameters cameraParas;
	
	public VideoRecorderManager(SurfaceHolder surfaceHolder){
		this.mSurfaceHolder = surfaceHolder;
		
		File  file = new File(videoFileDir);
		if(!file.exists()){
			file.mkdirs();
		}
		
		initCamera();
	}
	
	public void startTakeVideo(){
		if(camera != null){
			camera.stopPreview();
			camera.unlock();			
		}else{
			return;
		}
		
		setOnErrorListener(this);
		setCamera(camera);
		setOnInfoListener(this);
		setPreviewDisplay(mSurfaceHolder.getSurface());
		setAudioSource(AudioSource.MIC);// 音麦
        // 设置录制视频源为Camera(相机)  
        setVideoSource(VideoSource.CAMERA);  
        // 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4  
        setOutputFormat(OutputFormat.MPEG_4);  
        // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错  
        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);
//		setVideoSize(320, 240);
		setMaxDuration(MAX_TIME);
        // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错  
        setVideoFrameRate(profile.videoFrameRate);  
        setAudioEncoder(AudioEncoder.AAC);// 声音源码
        // 设置录制的视频编码h263 h264  
        setVideoEncoder(VideoEncoder.H264);  
  
        try {       	
    		File file = new File(videoFilePath);
    		if(file.exists()){
    			file.delete();
    		}else{
    			file.createNewFile();
    		}
    		
            // 设置视频文件输出的路径  
            setOutputFile(file.getAbsolutePath());
        	
            // 准备录制  
            prepare();  
            // 开始录制  
            start();  
        } catch (IllegalStateException e) {  
            e.printStackTrace();  
            releaseMediaRecorder();
        } catch (IOException e) {  
            e.printStackTrace(); 
            releaseMediaRecorder();
        } 
	}
	
	public void stopTakeVideo(){
		 // 停止录制  
        stop();  
        //重置
        reset();        
        // 释放资源  
        release();  
        
        releaseMediaRecorder();
	}
	
	public void initCamera(){
		camera = Camera.open();
		cameraParas = camera.getParameters();
	}
	
	public void startPreView(){		
		try {
			if(camera == null){
				initCamera();
			}
			if (VERSION.SDK_INT >= 14) {// 自定对焦
				cameraParas.setFocusMode("auto");
			} else {
				cameraParas.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			}
			camera.setDisplayOrientation(90);
			camera.setParameters(cameraParas);
			camera.startPreview();
			camera.setPreviewDisplay(mSurfaceHolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
     * 销毁
     */
    public void releaseMediaRecorder(){
		if (camera != null) {
			camera.lock();
			camera.release();
			camera=null;
		}
    }

	@Override
	public void onInfo(MediaRecorder arg0, int arg1, int arg2) {
		System.out.println("输出录制视频的详细信息是" + arg1);
	}

	@Override
	public void onError(MediaRecorder arg0, int arg1, int arg2) {
		System.out.println("输出录制视频出错的详细信息是" + arg1);
	}
	
	public String getRecordedVideoUrl(){
		return videoFilePath;
	}
}
