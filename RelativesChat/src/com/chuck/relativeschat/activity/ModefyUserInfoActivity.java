package com.chuck.relativeschat.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.bean.PersonBean;
import com.chuck.relativeschat.common.BmobConstants;
import com.chuck.relativeschat.common.HeadViewLayout;
import com.chuck.relativeschat.tools.ImageLoadOptions;
import com.chuck.relativeschat.tools.PhotoUtil;
import com.chuck.relativeschat.tools.StringUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ModefyUserInfoActivity extends BaseActivity {

	private HeadViewLayout mHeadViewLayout;
	private EditText userStateEdit;
	private EditText userNickEdit;
	private Button commitModifyBtn;
	private String stateStr , nickStr;
	private RelativeLayout layout_choose;
	private RelativeLayout layout_photo;
	private PopupWindow avatorPop;
	private ImageView userIconImage;
	public String filePath = "";
	private RelativeLayout parentLayout;
	
	private Bitmap newBitmap;
	private boolean isFromCamera = false;//
	private int degree = 0;
	private PersonBean userDataBean;
	private String avatarPath;//用户图像
	private String avatarFilePath;//用户图像所在的地址
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modefy_user_info);
		
		mHeadViewLayout = (HeadViewLayout)findViewById(R.id.title_menu_layout);
		mHeadViewLayout.setBackButtonVisiable(View.VISIBLE);
		mHeadViewLayout.setTitleText("修改用户信息");
		
		bindEvent();
	}
	
	public void bindEvent(){	
		userStateEdit = (EditText)findViewById(R.id.modefy_user_state_edit);
		userNickEdit = (EditText)findViewById(R.id.modefy_user_nick_edit);
		
		parentLayout = (RelativeLayout)findViewById(R.id.modify_info_parent_layout);
		
		userDataBean = rcApp.getPersonDetailData();
		
		userIconImage = (ImageView)findViewById(R.id.user_icon_image);
		userIconImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				updateUserIcon();
			}
		});
		
		if(!StringUtils.isEmpty(userDataBean.getNickName())){
			userNickEdit.setText(userDataBean.getNickName());
		}
		if(!StringUtils.isEmpty(userDataBean.getUserState())){
			userStateEdit.setText(userDataBean.getUserState());
		}
		if(!StringUtils.isEmpty(userDataBean.getAvatar())){
			avatarPath = userDataBean.getAvatar();
			refreshAvatar(userDataBean.getAvatar());
		}else{
			userIconImage.setImageResource(R.drawable.default_head);
		}

		commitModifyBtn = (Button)findViewById(R.id.modefy_user_info_ok_btn);
		commitModifyBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				stateStr = userStateEdit.getText().toString();
				nickStr = userNickEdit.getText().toString();
				if(StringUtils.isEmpty(nickStr) || StringUtils.isEmpty(stateStr)){
					mToast.showMyToast("个人信息填写不完整", Toast.LENGTH_SHORT);
				}else{
					updateUserInfo();
				}
			}
		});
	}
	
	/**
	 * 开始更新用户的信息
	 * 
	 * @author chengang
	 * @date 2014-8-5 下午3:58:07
	 */
	public void updateUserInfo(){
		new AsyncTask<Void, Void, Void>() {
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				dialog.show();
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				userDataBean.setNickName(nickStr);
				userDataBean.setUserState(stateStr);
				updateUserAvatar(avatarPath);
				userDataBean.update(getApplicationContext(), new UpdateListener(){

					@Override
					public void onFailure(int arg0, String arg1) {
						dialog.dismiss();
						mToast.showMyToast("个人信息更新失败！", Toast.LENGTH_SHORT);
						setResult(Activity.RESULT_OK);
						finish();
					}

					@Override
					public void onSuccess() {	
						dialog.dismiss();
						rcApp.setPersonDetailData(userDataBean);	
						mToast.showMyToast("个人信息更新成功！", Toast.LENGTH_SHORT);
						finish();
					}		
				});
				return null;
			}
		}.execute();

	}
	
	public void updateUserIcon(){
		View view = LayoutInflater.from(this).inflate(R.layout.pop_showavator, null);
		layout_choose = (RelativeLayout) view.findViewById(R.id.layout_choose);
		layout_photo = (RelativeLayout) view.findViewById(R.id.layout_photo);
		layout_photo.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				layout_choose.setBackgroundColor(getResources().getColor(R.color.white));
				layout_photo.setBackground(getResources().getDrawable(R.drawable.pop_bg_press));
				File dir = new File(BmobConstants.MyAvatarDir);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				//定义照片名字
				File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss").format(new Date()));
				filePath = file.getAbsolutePath();//获取照片路径
				Uri imageUri = Uri.fromFile(file);

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(intent,BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA);
			}
		});
		layout_choose.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				layout_photo.setBackgroundColor(getResources().getColor(R.color.white));
				layout_choose.setBackground(getResources().getDrawable(R.drawable.pop_bg_press));
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intent,BmobConstants.REQUESTCODE_UPLOADAVATAR_LOCATION);
			}
		});

		avatorPop = new PopupWindow(view, mScreenWidth, 600);
		avatorPop.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					avatorPop.dismiss();
					return true;
				}
				return false;
			}
		});

		avatorPop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		avatorPop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		avatorPop.setTouchable(true);
		avatorPop.setFocusable(true);
		avatorPop.setOutsideTouchable(true);
		avatorPop.setBackgroundDrawable(new BitmapDrawable());
		//设置pop出现的动画和位置
		avatorPop.setAnimationStyle(R.style.Animations_GrowFromBottom);
		avatorPop.showAtLocation(parentLayout, Gravity.BOTTOM, 0, 0);
	}
	
	private void startImageAction(Uri uri, int outputX, int outputY,int requestCode, boolean isCrop) {
		Intent intent = null;
		if (isCrop) {
			intent = new Intent("com.android.camera.action.CROP");
		} else {
			intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		}
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, requestCode);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA:// �����޸�ͷ��
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					return;
				}
				isFromCamera = true;
				File file = new File(filePath);
				degree = PhotoUtil.readPictureDegree(file.getAbsolutePath());
				startImageAction(Uri.fromFile(file), 200, 200,BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
			}
			break;
		case BmobConstants.REQUESTCODE_UPLOADAVATAR_LOCATION:// �����޸�ͷ��
			if (avatorPop != null) {
				avatorPop.dismiss();
			}
			Uri uri = null;
			if (data == null) {
				return;
			}
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					return;
				}
				isFromCamera = false;
				uri = data.getData();
				startImageAction(uri, 200, 200,BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
			} else {
			}

			break;
		case BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP:// �ü�ͷ�񷵻�
			// TODO sent to crop
			if (avatorPop != null) {
				avatorPop.dismiss();
			}
			if (data == null) {
				return;
			} else {
				saveCropAvator(data);
			}
			filePath = "";
			uploadAvatar();
			break;
		default:
			break;

		}
	}
	
	private void uploadAvatar(){
		final BmobFile bmobFile = new BmobFile(new File(avatarFilePath));
		bmobFile.upload(this, new UploadFileListener() {
			
			@Override
			public void onSuccess() {
				avatarPath = bmobFile.getFileUrl();
			}
			
			@Override
			public void onProgress(Integer arg0) {
				
			}
			
			@Override
			public void onFailure(int arg0, String msg) {
			}
		});
	}
	
	private void updateUserAvatar(final String url){
		userDataBean.setAvatar(url);
		userDataBean.update(this, new UpdateListener() {
		    @Override
		    public void onSuccess() {
				refreshAvatar(url);
		    }
		    @Override
		    public void onFailure(int code, String msg) {
		    }
		});
	}

	private void saveCropAvator(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap bitmap = extras.getParcelable("data");
			if (bitmap != null) {
				bitmap = PhotoUtil.toRoundCorner(bitmap, 10);
				if (isFromCamera && degree != 0) {
					bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
				}
				userIconImage.setImageBitmap(bitmap);
				String filename = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
				avatarFilePath = BmobConstants.MyAvatarDir + filename;
				PhotoUtil.saveBitmap(BmobConstants.MyAvatarDir, filename,bitmap, true);
				if (bitmap != null && bitmap.isRecycled()) {
					bitmap.recycle();
				}
			}
		}
	}
	
	private void refreshAvatar(String avatar){
		if (!StringUtils.isEmpty(avatar)) {
			ImageLoader.getInstance().displayImage(avatar, userIconImage, ImageLoadOptions.getOptions());
		} else {
			userIconImage.setImageResource(R.drawable.default_head);
		}
	}
}
