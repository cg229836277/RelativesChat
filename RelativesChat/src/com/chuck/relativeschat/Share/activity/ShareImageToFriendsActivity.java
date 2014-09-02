package com.chuck.relativeschat.Share.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.activity.BaseActivity;
import com.chuck.relativeschat.common.BmobConstants;
import com.chuck.relativeschat.common.HeadViewLayout;
import com.chuck.relativeschat.common.MyColorPickerDialog;
import com.chuck.relativeschat.entity.ShareFileBean;
import com.chuck.relativeschat.tools.BitmapUtils;
import com.chuck.relativeschat.tools.IsListNotNull;
import com.chuck.relativeschat.tools.PhotoUtil;
import com.chuck.relativeschat.tools.StringUtils;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ShareImageToFriendsActivity extends BaseActivity implements OnClickListener , OnTouchListener{
	
    private Bitmap baseBitmap;
    private Bitmap alterBitmap;
    private Canvas canvas;
    private Paint paint;
	private MyHandler mHandler;
	private ImageView drawCanvasImage;
	int colorValue = Color.RED;
	private int[] textIds = {R.id.pen_color_text ,R.id.re_draw_text ,
			R.id.save_to_local_text , R.id.send_text,
			R.id.select_from_local_text, R.id.take_photo_text};
	private HeadViewLayout mHeadViewLayout;
	private float penDrawWidth = 0;
	private String photoPath;//拍摄照片的路径
	private String takePhotoName;//拍摄照片的名字
    // 定义手指开始触摸的坐标
    float startX = 0;
    float startY = 0;
    
    // 定义手指结束触摸的坐标
    float stopX = 0;
    float stopY = 0;
    
    float moveX = 0;
    float moveY = 0;
    float suofangX = 0;
    float suofangY = 0;
    public boolean isDrawOnImage = false;
    public boolean isUpdateSuccessed = false;
    public static final String SHARE_TO_USER = "shareToUser";
    private String shareToUserName = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_image_to_friends);
		mHandler = new MyHandler();
		colorPickerDialog = new MyColorPickerDialog(this , mHandler);
		
		mHeadViewLayout = (HeadViewLayout)findViewById(R.id.title_menu_layout);
		mHeadViewLayout.setBackButtonVisiable(View.VISIBLE);
		mHeadViewLayout.setTitleText("关于图片");
		shareToUserName = getIntent().getStringExtra(SHARE_TO_USER);
		bindEvent();
		initDrawPen();
	}
	
	public void bindEvent(){
		for(int i = 0 ; i < textIds.length ; i++){
			TextView text = (TextView)findViewById(textIds[i]);
			text.setOnClickListener(this);
		}
		drawCanvasImage = (ImageView)findViewById(R.id.draw_canvas_image);
		drawCanvasImage.setOnTouchListener(this);
	}
	
	/**
	 * 初始化画笔
	 * 
	 * @author chengang
	 * @date 2014-8-19 下午3:15:40
	 */
	public void initDrawPen(){
		paint = new Paint();
        paint.setStrokeWidth(5);
        penDrawWidth = paint.getStrokeWidth();
        paint.setColor(colorValue);
	}
	
	private class MyHandler extends Handler{ 
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what > 100){
				colorValue = msg.what;
				String color = ColorPickerPreference.convertToRGB(colorValue);
				paint.setColor(Color.parseColor(color));
				System.out.println("选中好的颜色是" + color);
			}else if(msg.what == 1){
				Bundle data = msg.getData();
				String path = data.getString("path");
				uploadFileDataToServer(path);
			}else{
				handleUploadResult(false);
			}			
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pen_color_text :
			colorPickerDialog.show();
			break;
		case R.id.re_draw_text:
			clearDrawCanvas();
			break;
		case R.id.save_to_local_text:
			if(baseBitmap != null){
				saveDrawPicture("local");
			}else{
				mToast.showMyToast("请先画画或者加载照片!", Toast.LENGTH_SHORT);
			}
			break;
		case R.id.send_text:
			if(baseBitmap != null){
				saveDrawPicture("upload");
			}else{
				mToast.showMyToast("请先画画或者加载照片!", Toast.LENGTH_SHORT);
			}
			break;
		case R.id.select_from_local_text:
			selectImageFromLocal();
			break;
		case R.id.take_photo_text:
			selectImageByTakePhoto();
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		List<Float> imageList = null;
        switch (event.getAction()) {
	        // 用户按下动作
	        case MotionEvent.ACTION_DOWN:
	            // 第一次绘图初始化内存图片，指定背景为白色
	            if (baseBitmap == null) {
	                baseBitmap = Bitmap.createBitmap(drawCanvasImage.getWidth(),drawCanvasImage.getHeight(), Bitmap.Config.ARGB_8888);
	                drawCanvasImage.setImageBitmap(baseBitmap);
	                canvas = new Canvas(baseBitmap);
	                canvas.drawColor(Color.WHITE);
	            }
	            // 记录开始触摸的点的坐标
	            startX = event.getX();
	            startY = event.getY();
	            
	            imageList = getImageViewIneerSize(drawCanvasImage);
	            if(IsListNotNull.isListNotNull(imageList)){
	            	suofangX = imageList.get(0);
	            	suofangY = imageList.get(1);
	            	moveX = imageList.get(2);
	            	moveY = imageList.get(3);
	            	startX = (startX-moveX) * suofangX;	
	            	startY = (startY-moveY) * suofangY;
	            }
	            
	            break;
	        // 用户手指在屏幕上移动的动作
	        case MotionEvent.ACTION_MOVE:
	            // 记录移动位置的点的坐标
	            stopX = event.getX();
	            stopY = event.getY();
	            
	            imageList = getImageViewIneerSize(drawCanvasImage);
	            if(IsListNotNull.isListNotNull(imageList)){
	            	suofangX = imageList.get(0);
	            	suofangY = imageList.get(1);
	            	moveX = imageList.get(2);
	            	moveY = imageList.get(3);
//	            	System.out.println("起点X" + startX + " 起点Y" + startY + "终点X" + stopX + " 终点Y" + 
//	            			stopY + " 缩放X" + suofangX+ " 缩放Y" + suofangY + " 偏移X" + moveX+ " 偏移Y" + moveY);
//	            	if(suofangX > 0){
//	            		startX = (startX-moveX) * suofangX;	            		
	            		stopX = (stopX-moveX) * suofangX;	            		
//	            	}
//	            	if(suofangY > 0){
//	            		startY = (startY-moveY) * suofangY;
	            		stopY = (stopY-moveY) * suofangY;
//	            	}
	            }	        
	            
	            //根据两点坐标，绘制连线
	            canvas.drawLine(startX, startY, stopX, stopY, paint);
//	            System.out.println("起点X" + startX + " 起点Y" + startY + " 终点X" + stopX+ " 终点Y" + stopY);
	            drawCanvasImage.invalidate();
	            // 更新开始点的位置
	            startX = stopX;
	            startY = stopY;
	            
	            // 把图片展示到ImageView中
//	            drawCanvasImage.setImageBitmap(baseBitmap);
	            break;
	        case MotionEvent.ACTION_UP:	        	
	            stopX = event.getX();
	            stopY = event.getY();
	            
	            imageList = getImageViewIneerSize(drawCanvasImage);
	            if(IsListNotNull.isListNotNull(imageList)){
	            	suofangX = imageList.get(0);
	            	suofangY = imageList.get(1);
	            	moveX = imageList.get(2);
	            	moveY = imageList.get(3);
//	            	System.out.println("起点X" + startX + " 起点Y" + startY + "终点X" + stopX + " 终点Y" + 
//	            			stopY + " 缩放X" + suofangX+ " 缩放Y" + suofangY + " 偏移X" + moveX+ " 偏移Y" + moveY);
//	            	if(suofangX > 0){
//	            		startX = (startX-moveX) * suofangX;	            		
	            		stopX = (stopX-moveX) * suofangX;	            		
//	            	}
//	            	if(suofangY > 0){
//	            		startY = (startY-moveY) * suofangY;
	            		stopY = (stopY-moveY) * suofangY;
//	            	}
	            }
	            
	            canvas.drawLine(startX, startY, stopX, stopY, paint);
	            drawCanvasImage.invalidate();// 刷新
	            break;
	        default:
	            break;
        }
        return true;
	}
	
	/**
	 * 清除画布
	 * 
	 * @author chengang
	 * @date 2014-8-19 下午3:30:34
	 */
	public void clearDrawCanvas(){
		// 手动清除画板的绘图，重新创建一个画板
        if (baseBitmap != null) {
        	isDrawOnImage = false;
        	takePhotoName = null;
        	alterBitmap = null;
            baseBitmap = Bitmap.createBitmap(drawCanvasImage.getWidth(),drawCanvasImage.getHeight(), Bitmap.Config.ARGB_8888);
            canvas = new Canvas(baseBitmap);
            canvas.drawColor(Color.WHITE);
            drawCanvasImage.setImageBitmap(baseBitmap);
//            mToast.showMyToast("清除画布成功！", Toast.LENGTH_SHORT);
        }
	}
	
	/**
	 * 保存已经画好的图片到本地
	 * 
	 * @author chengang
	 * @date 2014-8-19 下午4:21:31
	 */
	public void saveDrawPicture(final String operateType){
		new AsyncTask<Void, Boolean, Boolean>() {
			String serverFilePath;
			
			@Override
			protected void onPreExecute() {
				dialog.show();
				super.onPreExecute();
			}
			
			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					if(operateType.equals("local")){
						return saveImageToLocal();
					}else{
						serverFilePath = uploadImageToServer();
						if(!StringUtils.isEmpty(serverFilePath)){
							return true;
						}else{
							return false;
						}
					}
		            
		        } catch (Exception e) {
		            e.printStackTrace();
		            return false;
		        }
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
//				if(!operateType.equals("local") && result){
//					result = uploadFileDataToServer(serverFilePath);
//				}
//				Looper.prepare();
				Message msg = new Message();
				if(result){
					msg.what = 1;
					Bundle dle = new Bundle();
					dle.putString("path", serverFilePath);
					msg.setData(dle);
					mHandler.sendMessage(msg);
//					mToast.showMyToast("操作成功！", Toast.LENGTH_SHORT);
//					clearDrawCanvas();
				}else{
					msg.what = 0;
					mHandler.sendMessage(msg);
//					mToast.showMyToast("操作失败！", Toast.LENGTH_SHORT);
				}
//				Looper.loop();
			}
		}.execute();		
	}
	
	public void selectImageFromLocal(){
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setType("image/*"); 
		intent.setAction(Intent.ACTION_GET_CONTENT);   
		startActivityForResult(intent,BmobConstants.REQUESTCODE_UPLOADAVATAR_LOCATION);
	}
	
	public void selectImageByTakePhoto(){
		File dir = new File(BmobConstants.DRAW_PICTURE_PATH);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		//定义照片名字
		takePhotoName = new SimpleDateFormat("yyMMddHHmmss").format(new Date()) + ".png";
		File file = new File(dir, takePhotoName);
		photoPath = file.getAbsolutePath();//获取照片路径
		Uri imageUri = Uri.fromFile(file);

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(intent,BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA);
	}
	
	/**
	 * 保存图片到本地
	 * 
	 * @author chengang
	 * @date 2014-8-22 上午10:56:25
	 * @return
	 */
	public boolean saveImageToLocal(){
		boolean isSavedSuccessed = true;
		// 保存图片到SD卡上
		String fileName = System.currentTimeMillis() + ".png";
		if(alterBitmap != null){
			if(!StringUtils.isEmpty(takePhotoName)){
				isSavedSuccessed = PhotoUtil.saveBitmap(BmobConstants.DRAW_PICTURE_PATH, takePhotoName,alterBitmap, true);
			}
		}else{
			isSavedSuccessed = PhotoUtil.saveBitmap(BmobConstants.DRAW_PICTURE_PATH, fileName,baseBitmap, true);
		}
        // Android设备Gallery应用只会在启动的时候扫描系统文件夹
        // 这里模拟一个媒体装载的广播，用于使保存的图片可以在Gallery中查看
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MEDIA_MOUNTED);
        intent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()));
        sendBroadcast(intent);	
		return isSavedSuccessed;		
	}
	
	/**
	 * 上传图片到服务器
	 * 
	 * @author chengang
	 * @date 2014-8-22 上午10:57:54
	 * @return
	 */
	public String uploadImageToServer(){
		boolean isSavedSuccessed = false;
		String totalFilePath = null;
		// 保存图片到SD卡上
		if(alterBitmap != null){
			Bitmap compressedBitmap = BitmapUtils.comp(alterBitmap);
			if(compressedBitmap != null){
				if(!StringUtils.isEmpty(takePhotoName)){
					isSavedSuccessed = PhotoUtil.saveBitmap(BmobConstants.DRAW_PICTURE_PATH, takePhotoName,compressedBitmap, true);
					totalFilePath = BmobConstants.DRAW_PICTURE_PATH + takePhotoName;
				}
			}else{
				isSavedSuccessed = false;
//				return isSavedSuccessed;
			}
		}else{
			String fileName = System.currentTimeMillis() + ".png";
			isSavedSuccessed = PhotoUtil.saveBitmap(BmobConstants.DRAW_PICTURE_PATH, fileName,baseBitmap, true);
			totalFilePath = BmobConstants.DRAW_PICTURE_PATH + fileName;
		}
		if(isSavedSuccessed){
//			isUpdateSuccessed = uploadFileDataToServer(totalFilePath);
			return totalFilePath;
		}else{
			return null;
		}
//		return isUpdateSuccessed;
	}
	
	/**
	 * 保存相关的数据到服务器
	 * 
	 * @author chengang
	 * @date 2014-8-22 下午1:28:44
	 * @param uploadFile
	 */
	public void uploadFileDataToServer(String totalFilePath){
//		totalFilePath = "/mnt/sdcard/onlylove.mp3";
		File file = new File(totalFilePath);
		if(file!= null && file.exists()){
			final BmobFile uploadFile = new BmobFile(file);
			uploadFile.upload(this, new UploadFileListener() {
				
				@Override
				public void onSuccess() {
					setFileData(uploadFile);
				}
				
				@Override
				public void onProgress(Integer arg0) {
					
				}
				
				@Override
				public void onFailure(int arg0, String arg1) {
					handleUploadResult(false);
					System.out.println("上传文件失败的原因是" + arg1);
				}
			});
		}		
	}
	
	public void setFileData(BmobFile tempFile){
		ShareFileBean fileDataBean = new ShareFileBean();
		fileDataBean.setFileName(tempFile.getFilename());
		fileDataBean.setFilePath(tempFile.getFileUrl());
		fileDataBean.setFileType(ShareFileBean.PHOTO);
		fileDataBean.setShareUser(userManager.getCurrentUserName());		
		if(!StringUtils.isEmpty(shareToUserName)){
			fileDataBean.setShareTo(shareToUserName);
			fileDataBean.setIsShareToAll("0");
		}else{
			fileDataBean.setShareTo(null);
			fileDataBean.setIsShareToAll("1");
		}
		fileDataBean.save(this, new SaveListener() {
			
			@Override
			public void onSuccess() {
				handleUploadResult(true);
				shareToUserName = null;
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				handleUploadResult(false);
				System.out.println("设置上传数据失败 " + arg1);
			}
		});		
	}
	
	/*根据两点坐标，绘制连线
	 *startX、stopX 为触摸事件开始、结束的地方
	 *offsetX，为图片在X轴的位移值
	 *scaleX，为图片在X轴的缩放值的倒数
	 */ 
	public List<Float> getImageViewIneerSize(ImageView iv){
		
		if(!isDrawOnImage){
			return null;
		}
		
		List<Float> size=new ArrayList<Float>();
	    //获得ImageView中Image的变换矩阵 
	    Matrix m = iv.getImageMatrix(); 
	    float[] values = new float[10]; 
	    m.getValues(values); 
	 
	    //Image在绘制过程中的变换矩阵，从中获得x和y方向的缩放系数 
	    float sx = values[0]; 
	    float sy = values[4]; 

	    //计算Image在屏幕上实际绘制的宽高 
	   size.add(1/sx); //scaleX
	   size.add(1/sy); //scaleY
	   size.add(values[2]); //X轴的translate的值
	   size.add(values[5]); //Y轴的translate的值
	   
	   paint.setStrokeWidth(penDrawWidth * size.get(0));

	   return size;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA:// �����޸�ͷ��
				if (resultCode == RESULT_OK) {
					if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
						return;
					}
					if(!StringUtils.isEmpty(photoPath)){
						File file = new File(photoPath);
						if(file != null && file.exists()){
//			            	BitmapFactory.Options options = new BitmapFactory.Options();
//			                options.inJustDecodeBounds = true;
//							Bitmap photoBitmap = BitmapFactory.decodeFile(photoPath);
//							getBitmapOptions(options);																               
//							photoBitmap = BitmapFactory.decodeFile(photoPath, options);
							Bitmap photoBitmap = BitmapUtils.getSmallBitmap(photoPath);
			                alterBitmap = Bitmap.createBitmap(photoBitmap.getWidth(),photoBitmap.getHeight(), photoBitmap.getConfig());
			                
			                isDrawOnImage = true;
			                
			                baseBitmap = alterBitmap;
			                canvas = new Canvas(alterBitmap);
			                Matrix matrix = new Matrix();
			                canvas.drawBitmap(photoBitmap, matrix, paint);
			                /* 将Bitmap设定到ImageView */  
			                drawCanvasImage.setImageBitmap(alterBitmap);  
						}
					}					
				}
			break;
			case BmobConstants.REQUESTCODE_UPLOADAVATAR_LOCATION://从本地选取图片
				if(data == null){
					return;
				}
	            Uri uri = data.getData(); 
	            if(uri == null){
	            	return;
	            }
	            takePhotoName = new SimpleDateFormat("yyMMddHHmmss").format(new Date()) + ".png";
	            Log.e("uri", takePhotoName);  	        
	            ContentResolver cr = this.getContentResolver();  
	            try {  
	            	BitmapFactory.Options options = new BitmapFactory.Options();
	                options.inJustDecodeBounds = true;
	                
	                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));/*.copy(Bitmap.Config.ARGB_8888, true);*/  
	                getBitmapOptions(options);
	               
	                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
	                alterBitmap = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), bitmap.getConfig());
	                
	                isDrawOnImage = true;
	                
	                baseBitmap = alterBitmap;
	                canvas = new Canvas(alterBitmap);
	                Matrix matrix = new Matrix();
	                canvas.drawBitmap(bitmap, matrix, paint);
	                /* 将Bitmap设定到ImageView */  
	                drawCanvasImage.setImageBitmap(alterBitmap);  
	            } catch (FileNotFoundException e) {  
	                Log.e("Exception", e.getMessage(),e);  
	            }  
			break;
		}
	}
	
	protected String getAbsoluteImagePath(Uri uri) {
		// can post image
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, proj, // Which columns to return
		null, // WHERE clause; which rows to return (all rows)
		null, // WHERE clause selection arguments (none)
		null); // Order-by clause (ascending by name)
		if (cursor != null) {
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} else {
			//如果游标为空说明获取的已经是绝对路径了
			return uri.getPath();
		}
	}
	
	public void getBitmapOptions(BitmapFactory.Options options){
		
        Display display = getWindowManager().getDefaultDisplay();
        float dw = display.getWidth();
        float dh = display.getHeight();
        
        int heightRatio = (int) Math.ceil(options.outHeight / dh);
        int widthRatio = (int) Math.ceil(options.outWidth / dw);
        if (heightRatio > 1 && widthRatio > 1) {
            if (heightRatio > widthRatio) {
                options.inSampleSize = heightRatio;
            } else {
                options.inSampleSize = widthRatio;
            }
        }
        options.inJustDecodeBounds = false;
	}
	
	public void handleUploadResult(boolean result){
		dialog.dismiss();
		if(result){
			mToast.showMyToast("操作成功！", Toast.LENGTH_SHORT);
			//文件上传成功之后从本地删除
			File file = new File(BmobConstants.DRAW_PICTURE_PATH + takePhotoName);
			if (file != null && file.exists()) {
				file.delete();
			}
			clearDrawCanvas();
		}else{
			mToast.showMyToast("操作失败！", Toast.LENGTH_SHORT);
		}
	}
	
	@Override
	protected void onDestroy() {
		System.gc();
		super.onDestroy();
	}
}
