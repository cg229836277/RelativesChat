package com.chuck.relativeschat.service;

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
	
	@Override
	public void onDestroy() {
		super.onDestroy();		
	}
	
	public void getCurrentUserLocation(){
		//获取到LocationManager对象
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //创建一个Criteria对象
        Criteria criteria = new Criteria();
        //设置粗略精确度
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        //设置是否需要返回海拔信息
        criteria.setAltitudeRequired(false);
        //设置是否需要返回方位信息
        criteria.setBearingRequired(false);
        //设置是否允许付费服务
        criteria.setCostAllowed(true);
        //设置电量消耗等级
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        //设置是否需要返回速度信息
        criteria.setSpeedRequired(false);
 
        //根据设置的Criteria对象，获取最符合此标准的provider对象
        String currentProvider = locationManager.getBestProvider(criteria, true);
        Log.d("Location", "currentProvider: " + currentProvider);
        //根据当前provider对象获取最后一次位置信息
        Location currentLocation = locationManager.getLastKnownLocation(currentProvider);
        //如果位置信息为null，则请求更新位置信息
        if(currentLocation == null){
            locationManager.requestLocationUpdates(currentProvider, 0, 0, locationListener);
        }
        //直到获得最后一次位置信息为止，如果未获得最后一次位置信息，则显示默认经纬度
        //每隔10秒获取一次位置信息
        while(true){
            currentLocation = locationManager.getLastKnownLocation(currentProvider);
            if(currentLocation != null){
                Log.d("Location", "Latitude: " + currentLocation.getLatitude());
                Log.d("Location", "location: " + currentLocation.getLongitude()); 
                
                PersonBean personBean = new PersonBean();
                personBean.setAddress(currentLocation.getLatitude() + "," + currentLocation.getLongitude());
                personBean.setObjectId(rcApp.getCurrentUser().getObjectId());
                personBean.update(getApplicationContext());
                break;
            }else{
                Log.d("Location", "Latitude: " + 0);
                Log.d("Location", "location: " + 0);
            }
        }
	}
	
	//创建位置监听器
    private LocationListener locationListener = new LocationListener(){
        //位置发生改变时调用
        @Override
        public void onLocationChanged(Location location) {
            Log.d("Location", "onLocationChanged");
            Log.d("Location", "onLocationChanged Latitude" + location.getLatitude());
            Log.d("Location", "onLocationChanged location" + location.getLongitude());
        }

        //provider失效时调用
        @Override
        public void onProviderDisabled(String provider) {
            Log.d("Location", "onProviderDisabled");
        }

        //provider启用时调用
        @Override
        public void onProviderEnabled(String provider) {
            Log.d("Location", "onProviderEnabled");
        }

        //状态改变时调用
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("Location", "onStatusChanged");
        }
    };
}
