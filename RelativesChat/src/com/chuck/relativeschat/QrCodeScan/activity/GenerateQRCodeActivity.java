package com.chuck.relativeschat.QrCodeScan.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import cn.bmob.v3.datatype.BmobFile;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.activity.BaseActivity;
import com.chuck.relativeschat.base.RelativesChatApplication;
import com.chuck.relativeschat.common.HeadViewLayout;
import com.chuck.relativeschat.tools.HttpDownloader;
import com.chuck.relativeschat.tools.StringUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class GenerateQRCodeActivity extends BaseActivity {

	private EditText qr_text;
	private Button qr_generate;
	private ImageView qr_image;
	private int QR_WIDTH = 0;
	private int QR_HEIGHT = 0;
	private HeadViewLayout mHeadViewLayout;
	private RelativesChatApplication rcApp;

	// List<Bitmap> imageList = new ArrayList<Bitmap>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.generate_code);

		mHeadViewLayout = (HeadViewLayout) findViewById(R.id.title_menu_layout);
		mHeadViewLayout.setBackButtonVisiable(View.VISIBLE);
		mHeadViewLayout.setTitleText("生成二维码");
		
		rcApp = (RelativesChatApplication)getApplication();

		qr_text = (EditText) findViewById(R.id.qr_text);
		qr_generate = (Button) findViewById(R.id.generate);
		qr_image = (ImageView) findViewById(R.id.qr_image);

		if (qr_text.getText().toString() != null) {
			qr_generate.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// 生成二维码图片
					createImage();
				}
			});
		} else {
			Toast.makeText(this, "请输入你的名字", Toast.LENGTH_SHORT).show();
		}
		
	}

	// 生成二维码图
	private void createImage() {
		try {
			QR_WIDTH = qr_image.getWidth();
			QR_HEIGHT = qr_image.getHeight();

			// 需要引入core包
			QRCodeWriter writer = new QRCodeWriter();

			String text = qr_text.getText().toString();

			System.out.println("生成的文本：" + text);
			if (text == null || "".equals(text) || text.length() < 1
					&& QR_WIDTH == 0 && QR_HEIGHT == 0) {
				return;
			}

			// 把输入的文本转为二维码
			BitMatrix martix = writer.encode(text, BarcodeFormat.QR_CODE,QR_WIDTH, QR_HEIGHT);

			System.out.println("w:" + martix.getWidth() + "h:"+ martix.getHeight());

			Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			BitMatrix bitMatrix = new QRCodeWriter().encode(text,BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			for (int y = 0; y < QR_HEIGHT; y++) {
				for (int x = 0; x < QR_WIDTH; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * QR_WIDTH + x] = 0xff000000;
					} else {
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}

				}
			}

			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,Bitmap.Config.ARGB_8888);

			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
			qr_image.setImageBitmap(bitmap);

			if (bitmap != null && text != null) {
				// 保存二维码图片
//				saveImage(bitmap, text);
				begainGenerateQrCode(bitmap);
			}

		} catch (WriterException e) {
			e.printStackTrace();
		}
	}
	
	public void begainGenerateQrCode(final Bitmap sourceBitmap){
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				dialog.show();
			}
			
			@Override
			protected Void doInBackground(Void... arg0) {
				
				getCurrentUserIcon(sourceBitmap);
				
				return null;
			}	
			
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				dialog.dismiss();
				mToast.showMyToast("二维码生成成功!", Toast.LENGTH_SHORT);
			}
		}.execute();
	}
	
	public Bitmap getCurrentUserIcon(Bitmap sourceBitmap){
		if(rcApp.getCurrentUser() != null){
			String iconUrl = rcApp.getCurrentUser().getAvatar();
			if(!StringUtils.isEmpty(iconUrl)){
				String fileName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
				Bitmap iconBitmap = HttpDownloader.downfile(iconUrl,fileName);
				if(iconBitmap != null){
					generateQrImageWithImage(sourceBitmap , iconBitmap,HttpDownloader.filePath + fileName);
				}else{
					mToast.showMyToast("获取用户图像时出错", Toast.LENGTH_SHORT);
				}
			}
		}		
		return null;
	}
	
	/**
	 * 生成带有水印的二维码
	 * 
	 * @author chengang
	 * @date 2014-8-8 下午3:41:28
	 * @param qrSourceBitmap  已经生成的二维码图片
	 * @param userIconBitmap  用户的图像
	 */
	private void generateQrImageWithImage(Bitmap qrSourceBitmap , Bitmap userIconBitmap , String fileName){
		// 头像图片的大小
		int portrait_W = userIconBitmap.getWidth();
		int portrait_H = userIconBitmap.getHeight();
		
		int QRCODE_SIZE = qrSourceBitmap.getHeight();

		// 设置头像要显示的位置，即居中显示
		int left = (QRCODE_SIZE - portrait_W) / 2;
		int top = (QRCODE_SIZE - portrait_H) / 2;
		int right = left + portrait_W;
		int bottom = top + portrait_H;
		Rect rect1 = new Rect(left, top, right, bottom);

		// 取得qr二维码图片上的画笔，即要在二维码图片上绘制我们的头像
		Canvas canvas = new Canvas(qrSourceBitmap);

		// 设置我们要绘制的范围大小，也就是头像的大小范围
		Rect rect2 = new Rect(0, 0, portrait_W, portrait_H);
		// 开始绘制
		canvas.drawBitmap(userIconBitmap, rect2, rect1, null);
		
		saveImage(qrSourceBitmap, fileName);
	}

	// 保存二维码图片
	private void saveImage(Bitmap bitmap, String imageName) {
		// imageList.add(bitmap);

//		String savePath = getSDPath(); // 获取图片的存储路径
		// String imageName = Integer.toString(imageList.size()) + ".png";
		// //获取图片的名称就是该图片的序号的名称
		String imageEntity = imageName + ".png";
		File f = new File(imageEntity);
		try {
			f.createNewFile();
		} catch (IOException e) {
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}
//
//	// 获取图片的保存路径
//	public static String getSDPath() {
//		boolean hasSDCard = Environment.getExternalStorageState().equals(
//				Environment.MEDIA_MOUNTED);
//		if (hasSDCard) {
//			return Environment.getExternalStorageDirectory().toString();
//		} else {
//			return "/data/data/com.example.qr_codescan";
//		}
//	}
}
