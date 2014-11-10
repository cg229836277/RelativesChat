package com.chuck.relativeschat.qrcodescan.activity;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.activity.BaseActivity;
import com.chuck.relativeschat.common.DialogTips;
import com.chuck.relativeschat.common.GetDownloadFileType;
import com.chuck.relativeschat.common.HeadViewLayout;
import com.chuck.relativeschat.tools.StringUtils;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class QrCodeScanActivity extends BaseActivity {

	private final static int SCANNIN_GREQUEST_CODE = 1;

	private TextView mTextView;

	private ImageView mImageView;
	// 扫描获取的文件的网络地址
	private String filePath;
	// 文件通过java的方法判定的大范围的类型
	private String fillType;
	// 文件最终确认的类型
	private String finalType;
	// 文件保存的路径
	private String targetPath;
	
	private HeadViewLayout mHeadViewLayout;

	// List<String> typesList = new ArrayList<String>();

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case 0:
				System.out.println("返回成功");
//				startDownLoadFile();
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qr_code_scan);
		
		mHeadViewLayout = (HeadViewLayout)findViewById(R.id.title_menu_layout);
		mHeadViewLayout.setBackButtonVisiable(View.VISIBLE);
		mHeadViewLayout.setTitleText("扫描和生成二维码");
		
		mTextView = (TextView) findViewById(R.id.result); // 得到扫描的结果
		mImageView = (ImageView) findViewById(R.id.qrcode_bitmap); // 显示扫描的二维码图片

		Button mButton = (Button) findViewById(R.id.button1);
		mButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				// 进入扫描二维码的界面
				intent.setClass(QrCodeScanActivity.this, MipcaCaptureActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
			}
		});

		Button generateBtn = (Button) findViewById(R.id.generateBtn);
		generateBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				// 进入生成二维码的界面
				intent.setClass(QrCodeScanActivity.this,GenerateQRCodeActivity.class);
				startActivity(intent);
			}
		});

		mTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String textStr = mTextView.getText().toString();
				System.out.println(textStr);
				if (!textStr.isEmpty()) {
					targetPath = Environment.getExternalStorageDirectory().toString();

					filePath = textStr;

					new Thread(runnable).start();

					// startDownLoadFile();

				}
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case SCANNIN_GREQUEST_CODE:
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				mTextView.setText(bundle.getString("result"));
				mImageView.setImageBitmap((Bitmap) data.getParcelableExtra("bitmap"));
				
				if(!StringUtils.isEmpty(bundle.getString("result"))){
					openWebsiteFromResult(bundle.getString("result"));
				}
			}
			break;
		}
	}
	
	public void openWebsiteFromResult(final String scanString){
		if(scanString.contains("http") || scanString.contains("https")){
		DialogTips dialog = new DialogTips(QrCodeScanActivity.this, "提示", "是否打开该网址？", "确认", true, true);
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				Intent intent = new Intent();        
		        intent.setAction("android.intent.action.VIEW");    
		        Uri content_url = Uri.parse(scanString);   
		        intent.setData(content_url);  
		        startActivity(intent);
			}				
		});
		
		dialog.show();
		dialog = null;
		}		
	}

	public void getFileType() {
		BufferedInputStream bis = null;
		HttpURLConnection urlconnection = null;
		URL url = null;
		try {
			url = new URL(filePath);
			urlconnection = (HttpURLConnection) url.openConnection();
			urlconnection.connect();
			bis = new BufferedInputStream(urlconnection.getInputStream());
			System.out.println("file type:" + HttpURLConnection.guessContentTypeFromStream(bis));
			fillType = HttpURLConnection.guessContentTypeFromStream(bis).toString();

			if (!fillType.isEmpty())// 判断成功
			{
				GetDownloadFileType myFileType = new GetDownloadFileType(QrCodeScanActivity.this, fillType);

				String fileType = myFileType.getActualFileType();

				String[] chooseType = fileType.split(" ");

				if (chooseType.length > 1) {
					chooseFileTypeDialog(chooseType); // 如果类型不确定，比如图片可能是png，jpeg，jpg格式，需要交给用户选择
					System.out.println(fileType);
				} else {
					System.out.println(fileType);
					finalType = fileType; // 如果单个类型确定的话就返回单个类型
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			//
			// TODO: http request.
			//

			getFileType();

			// startDownLoadFile();
			Message ms = new Message();
			ms.what = 0;

			myHandler.sendMessage(ms);
		}
	};

	public void chooseFileTypeDialog(final String[] str) {
		new AlertDialog.Builder(QrCodeScanActivity.this)
				.setTitle("请选择")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setSingleChoiceItems(str, 0,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int which) {
								dialog.dismiss();
								finalType = str[which];
							}
						}).setNegativeButton("取消", null).show();
	}

//	public void startDownLoadFile() {
//		HttpUtils downLoad = new HttpUtils();
//
//		if (!filePath.isEmpty() && !targetPath.isEmpty()) {
//
//			targetPath = targetPath + "/finalType." + finalType;
//
//			downLoad.download(filePath, targetPath, true, true,
//					new RequestCallBack<File>() {
//						@Override
//						public void onStart() {
//							System.out.println("开始下载");
//						}
//
//						@Override
//						public void onLoading(long total, long current,
//								boolean isUploading) {
//							System.out.println("开始loading");
//						}
//
//						@Override
//						public void onSuccess(ResponseInfo<File> arg0) {
//							// TODO Auto-generated method stub
//							System.out.println("成功");
//						}
//
//						@Override
//						public void onFailure(HttpException arg0, String arg1) {
//							// TODO Auto-generated method stub
//							System.out.println("失败");
//						}
//					});
//		}
//	}
}
