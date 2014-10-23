package com.chuck.relativeschat.service;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.chuck.relativeschat.activity.BaiduMapTestActivity.MyLocationListenner;
import com.chuck.relativeschat.base.RelativesChatApplication;
import com.chuck.relativeschat.entity.PersonBean;

import cn.bmob.im.bean.BmobChatUser;
import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * @Title：SAFEYE@
 * @Description：
 * @date 2014-10-21 上午10:34:12
 * @author chengang
 * @version 1.0
 */
public class GetUserLocationService extends Service{

	private RelativesChatApplication rcApp;
	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	
	@Override
	public IBinder onBind(Intent arg0) {
		
		return null;
	} 
	
	public class MyBinder extends Binder{  
		public GetUserLocationService getService(){  
            return GetUserLocationService.this;  
        }  
    } 
	
	@Override
	public void onCreate() {
		super.onCreate();
		rcApp = (RelativesChatApplication)getApplication();
		getCurrentUserLocation();
	}
	
	@Override
	public void unbindService(ServiceConnection conn) {
		super.unbindService(conn);
	}
	
	public void getCurrentUserLocation(){
		mLocClient = new LocationClient(getApplicationContext());
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000 * 60 * 60);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}
	
	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
				LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
				Log.d("getLatitude", "" + location.getLatitude());
				Log.d("getLongitude", "" + location.getLongitude());
				
				rcApp.setCurrentUserLocation(ll);			
				
                PersonBean personBean = new PersonBean();
                personBean.setAddress(location.getLatitude() + "," + location.getLongitude());
                personBean.setObjectId(rcApp.getCurrentUser().getObjectId());
                personBean.update(getApplicationContext());
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}
	
	@Override
	public void onDestroy() {
		// 退出时销毁定位
		mLocClient.stop();
		super.onDestroy();
	}
}
