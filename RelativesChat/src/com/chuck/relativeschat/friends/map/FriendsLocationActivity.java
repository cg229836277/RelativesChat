package com.chuck.relativeschat.friends.map;

import java.util.List;

import cn.bmob.im.bean.BmobChatUser;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.chuck.relativeschat.R;
import com.chuck.relativeschat.activity.BaseActivity;
import com.chuck.relativeschat.base.RelativesChatApplication;
import com.chuck.relativeschat.entity.PersonBean;
import com.chuck.relativeschat.tools.IsListNotNull;
import com.chuck.relativeschat.tools.PhotoUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class FriendsLocationActivity extends BaseActivity implements OnGetGeoCoderResultListener {

	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;
	private GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private RelativesChatApplication rcApp;
	private List<PersonBean> personBeanDataList;
	private BmobChatUser currentUser;
	private static final String TAG = "FriendsLocationActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends_location);
		
		rcApp = (RelativesChatApplication)getApplication();
		personBeanDataList = rcApp.getMyFriendsDataBean();
		currentUser = userManager.getCurrentUser();		
		
		mMapView = (MapView) findViewById(R.id.friends_location_view);
		mBaiduMap = mMapView.getMap();

		// 初始化搜索模块，注册事件监听
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		
		//113.920054,22.492087
		setMyFriendsData();
	}
	
	public void setMyFriendsData(){
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				dialog.show();
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				if(IsListNotNull.isListNotNull(personBeanDataList)){
					for(PersonBean data : personBeanDataList){	
						String[] location = data.getAddress().split(",");
						if(location.length == 0){
							continue;
						}
						LatLng ptCenter = new LatLng(Double.valueOf(location[0]) , Double.valueOf(location[1]));
						// 反Geo搜索
						mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter));
					}
				}				
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				dialog.dismiss();
			}
			
		}.execute();
	}
	
	public void setUserIconData(String iconUrl){
		ImageLoader.getInstance().loadImage(iconUrl, new ImageLoadingListener() {

			@Override
			public void onLoadingStarted(String imageUri, View view) {
				
			}

			@Override
			public void onLoadingFailed(String imageUri, View view,FailReason failReason) {
				
			}

			@Override
			public void onLoadingComplete(String imageUri, View view,Bitmap loadedImage) {
				Bitmap tempBitmap = PhotoUtil.toRoundCorner(loadedImage, 120);
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				
			}
		});
	}
	
	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		mMapView.onDestroy();
		mSearch.destroy();
		super.onDestroy();
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			mToast.showMyToast("抱歉，未能找到结果", Toast.LENGTH_SHORT);
			return;
		}
//		mBaiduMap.clear();
		mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));
//		mToast.showMyToast(result.getAddress(), Toast.LENGTH_SHORT);
		System.out.println("地址是" + result.getAddress());
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
		
	}
}
