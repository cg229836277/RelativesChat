package com.chuck.relativeschat.common;

import java.io.IOException;
import java.util.UUID;

import android.media.MediaRecorder;
import android.view.SurfaceHolder;

public class VideoRecorderManager extends MediaRecorder{
	
	public final String videoFilePath = BmobConstants.RECORD_VIDEO_PATH + UUID.randomUUID() + "mp4";
	
	private SurfaceHolder mSurfaceHolder;
	
	public VideoRecorderManager(SurfaceHolder surfaceHolder){
		this.mSurfaceHolder = surfaceHolder;
	}
	
	public void startTakeVideo(){
        // 设置录制视频源为Camera(相机)  
        setVideoSource(MediaRecorder.VideoSource.CAMERA);  
        // 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4  
        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);  
        // 设置录制的视频编码h263 h264  
        setVideoEncoder(MediaRecorder.VideoEncoder.H264);  
        // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错  
        setVideoSize(176, 144);  
        // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错  
        setVideoFrameRate(20);  
        setPreviewDisplay(mSurfaceHolder.getSurface());  
        // 设置视频文件输出的路径  
        setOutputFile(videoFilePath);  
        try {  
            // 准备录制  
            prepare();  
            // 开始录制  
            start();  
        } catch (IllegalStateException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } 
	}
	
	public void stopTakeVideo(){
		 // 停止录制  
        stop();  
        // 释放资源  
        release();  
	}

}
