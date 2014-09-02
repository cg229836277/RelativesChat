package com.chuck.relativeschat.Share.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import cn.bmob.im.config.BmobConstant;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.activity.BaseActivity;
import com.chuck.relativeschat.adapter.FriendsBaseListAdapter;
import com.chuck.relativeschat.common.AudioRecorderManager;
import com.chuck.relativeschat.common.BmobConstants;
import com.chuck.relativeschat.common.HeadViewLayout;
import com.chuck.relativeschat.common.ViewHolder;
import com.chuck.relativeschat.entity.ShareFileBean;
import com.chuck.relativeschat.tools.IsListNotNull;
import com.chuck.relativeschat.tools.StringUtils;
import com.chuck.relativeschat.tools.XListView;
import com.chuck.relativeschat.tools.XListView.IXListViewListener;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ShareSoundToFriendsActivity extends BaseActivity implements IXListViewListener{
	private HeadViewLayout mHeadViewLayout;
	private ImageView pressStartRecordImage;
	private ImageView imageLevelImage;
	private XListView mySoundShareListView;
	private List<ShareFileBean> soundFileList;
	private TextView noContentText;
	private SoundShareListAdapter soundListAdapter;
	public static final String SHARE_TO_USER = "shareToUser";
	private String shareToUserName;
	private int PAGE_INDEX = 1;
	private AudioRecorderManager audioManager;
	private String recordSoundFileName = null;
	private File recordSoundFileDir = null;
	private File tempSoundFile;
	private Timer timer;
	private int calNum = 14;
	private ProgressBar recordPregress;
	private TextView numberText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_sound_to_friends);
		
		mHeadViewLayout = (HeadViewLayout)findViewById(R.id.title_menu_layout);
		mHeadViewLayout.setBackButtonVisiable(View.VISIBLE);
		mHeadViewLayout.setTitleText("分享语音");
		shareToUserName = getIntent().getStringExtra(SHARE_TO_USER);
		
		bindEvent();
		
		initDataToList();
	} 
	
	public void bindEvent(){
		pressStartRecordImage = (ImageView)findViewById(R.id.press_start_record_image);
		imageLevelImage = (ImageView)findViewById(R.id.sound_level_image);
		mySoundShareListView = (XListView)findViewById(R.id.my_sound_recorder_list_view);
		noContentText = (TextView)findViewById(R.id.no_share_sound_text);
		recordPregress = (ProgressBar)findViewById(R.id.cal_number_progress);
		numberText = (TextView)findViewById(R.id.number_text);
		recordPregress.setMax(14);
		numberText.setText("15");
		
		pressStartRecordImage.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startRecordSound();
					break;
				case MotionEvent.ACTION_UP:
					stopRecordSound();
					break;
				default:
					break;
				}
				return true;
			}
		});
	}
	
	public void startRecordSound(){
		imageLevelImage.setVisibility(View.VISIBLE);
		mySoundShareListView.setVisibility(View.GONE);
		noContentText.setVisibility(View.GONE);
		String recordSoundDir = BmobConstants.RECORD_SOUND_PATH;
		recordSoundFileDir = new File(recordSoundDir);
		if(!recordSoundFileDir.exists()){
			recordSoundFileDir.mkdir();			
		}
		recordSoundFileName = recordSoundDir + File.separator + UUID.randomUUID() + ".amr";
		tempSoundFile = new File(recordSoundFileName);
		audioManager = new AudioRecorderManager(tempSoundFile, imageLevelImage);
		audioManager.startRecord();
		startClaNum();
	}
	
	public void stopRecordSound(){
		timer.cancel();
		long recordTime = audioManager.stopRecord();
		if(recordTime < 1 * 1000){//如果录音时间少于一秒就提示停止
			mToast.showMyToast("录音时间太短!", Toast.LENGTH_SHORT);
			if(tempSoundFile != null && tempSoundFile.exists()){
				tempSoundFile.delete();
				tempSoundFile = null;
			}
		}else{//在1秒和15秒之间松开就发送
			if(tempSoundFile != null && tempSoundFile.exists()){
				uploadSoundDataToServer(tempSoundFile);
			}
		}		
	}
	
	public void uploadSoundDataToServer(final File soundFile){
		final BmobFile file = new BmobFile(soundFile);
		dialog.show();
		file.upload(getApplicationContext(), new UploadFileListener() {
			
			@Override
			public void onSuccess() {
				ShareFileBean shareBean = new ShareFileBean();
				shareBean.setFileType(ShareFileBean.SOUNG);
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
						soundFile.delete();
						//发送之后立即更新数据
						initDataToList();
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
	
	public void initDataToList(){
		dialog.show();
		BmobQuery<ShareFileBean> query1 = new BmobQuery<ShareFileBean>();
		//找到我分享的，包括普通分享和私密分享
		query1.addWhereEqualTo("shareUser", userManager.getCurrentUserName());			
		BmobQuery<ShareFileBean> query2 = new BmobQuery<ShareFileBean>();
		//找到好友分享给我的
		query2.addWhereEqualTo("shareTo", userManager.getCurrentUserName());
		query1.addWhereEqualTo("isShareToAll", "0");		
		List<BmobQuery<ShareFileBean>> queries = new ArrayList<BmobQuery<ShareFileBean>>();
		queries.add(query1);
		queries.add(query2);
		BmobQuery<ShareFileBean> mainQuery = new BmobQuery<ShareFileBean>();
		mainQuery.addWhereEqualTo("fileType", ShareFileBean.SOUNG);
		mainQuery.or(queries);
		mainQuery.setLimit(PAGE_INDEX * 10);
		mainQuery.findObjects(getApplicationContext(), new FindListener<ShareFileBean>() {
			
			@Override
			public void onSuccess(List<ShareFileBean> arg0) {
				if(IsListNotNull.isListNotNull(arg0)){
					handleExistDataList();
					soundListAdapter = new SoundShareListAdapter(getApplicationContext(), arg0);
					mySoundShareListView.setAdapter(soundListAdapter);
					soundListAdapter.setList(arg0);
					mySoundShareListView.setSelection(soundListAdapter.getCount() - 1);
				}else{
					handleBlankDataList();
				}				
				dialog.dismiss();
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				handleBlankDataList();
				dialog.dismiss();
			}
		});
	}
	
	/**
	 * 处理没有语音分享数据的时候
	 * 
	 * @author chengang
	 * @date 2014-9-2 上午11:03:49
	 */
	public void handleBlankDataList(){
		imageLevelImage.setVisibility(View.GONE);
		mySoundShareListView.setVisibility(View.GONE);
		noContentText.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 处理有语音分享数据的时候
	 * 
	 * @author chengang
	 * @date 2014-9-2 上午11:03:49
	 */
	public void handleExistDataList(){
		imageLevelImage.setVisibility(View.GONE);
		mySoundShareListView.setVisibility(View.VISIBLE);
		noContentText.setVisibility(View.GONE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public class SoundShareListAdapter extends FriendsBaseListAdapter<ShareFileBean>{

		/**
		 * @param context
		 * @param list
		 */
		public SoundShareListAdapter(Context context, List<ShareFileBean> list) {
			super(context, list);
		}

		@Override
		public View bindView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.simple_sound_share_item_layout, null);
			}
			final ShareFileBean data = (ShareFileBean) getList().get(position);
			TextView shareDesc = ViewHolder.get(convertView, R.id.sound_share_desc_text);
			ImageView playImage = ViewHolder.get(convertView, R.id.listen_my_share_sound);
			if(data != null){
				String desc = null;
				if(data.getShareUser().equals(userManager.getCurrentUserName()) && data.getIsShareToAll().equals("0")){
					desc = "我在" + data.getCreatedAt() + "分享了语音给" + data.getShareTo();					
				}else if(data.getShareUser().equals(userManager.getCurrentUserName()) && data.getIsShareToAll().equals("1")){
					desc = "我在" + data.getCreatedAt() + "分享了语音给大家";
				}else if(data.getShareTo().equals(userManager.getCurrentUserName()) && data.getIsShareToAll().equals("1")){
					desc = data.getShareUser() + "在" + data.getCreatedAt() +"给我分享了语音";
				}				
				shareDesc.setText(desc);
			}
			
			playImage.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
				}
			});
			return convertView;
		}		
	}

	@Override
	public void onRefresh() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				PAGE_INDEX++;
				soundListAdapter.notifyDataSetChanged();
				mySoundShareListView.stopRefresh();
			}
		}, 1000);	
	}

	@Override
	public void onLoadMore() {
		
	}
	
	/**
	 * 开始计时
	 * 
	 * @author chengang
	 * @date 2014-9-2 下午3:28:14
	 */
	public void startClaNum(){
		timer = new Timer(true);
		timer.schedule(task,1000, 1000); //延时1000ms后执行，1000ms执行一次
	}
	
	//计时器，录音只能在15秒的时间内
	TimerTask task = new TimerTask(){  
	      public void run() {  
	      Message message = new Message();      
	      message.what = 1;      
	      handler.sendMessage(message);    
	   }  
	};
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.what == 1){
				--calNum;
				if(calNum >= 0){
					numberText.setText("" + calNum);
					recordPregress.setProgress(calNum);
				}else{
					stopRecordSound();
				}
			}else{
				mySoundShareListView.setSelection(soundListAdapter.getCount() - 1);
			}
		}
	};
}
