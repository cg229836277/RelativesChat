package com.chuck.relativeschat.friends.map;

import java.util.List;

import cn.bmob.im.bean.BmobChatUser;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.chuck.relativeschat.R;
import com.chuck.relativeschat.activity.BaseActivity;
import com.chuck.relativeschat.base.RelativesChatApplication;
import com.chuck.relativeschat.entity.PersonBean;
import com.chuck.relativeschat.tools.IsListNotNull;
import com.chuck.relativeschat.tools.PhotoUtil;
import com.chuck.relativeschat.tools.StringUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FriendsLocationActivity extends BaseActivity{

	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;
	private RelativesChatApplication rcApp;
	private List<PersonBean> personBeanDataList;
	private BmobChatUser currentUser;
	private Marker mMarker;
	private InfoWindow mInfoWindow;
	private static final String TAG = "FriendsLocationActivity"; 
	private BitmapDescriptor bd = null;
	BitmapDescriptor bdGround = BitmapDescriptorFactory.fromResource(R.drawable.ground_overlay);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends_location);
		
		rcApp = (RelativesChatApplication)getApplication();
		personBeanDataList = rcApp.getMyFriendsDataBean();
		currentUser = userManager.getCurrentUser();		
		
		mMapView = (MapView) findViewById(R.id.friends_location_view);
		mBaiduMap = mMapView.getMap();
		
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(16.0f);
		mBaiduMap.setMapStatus(msu);
		
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(rcApp.getCurrentUserLocation());
		mBaiduMap.animateMapStatus(u);
		
		setUserLocationData();
		
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			public boolean onMarkerClick(final Marker marker) {
				Button button = new Button(getApplicationContext());
				button.setBackgroundResource(R.drawable.popup);
				OnInfoWindowClickListener listener = null;
				if (marker == mMarker) {
					listener = new OnInfoWindowClickListener() {
						public void onInfoWindowClick() {
							LatLng ll = marker.getPosition();
							LatLng llNew = new LatLng(ll.latitude + 0.005,ll.longitude + 0.005);
							marker.setPosition(llNew);
							mBaiduMap.hideInfoWindow();
						}
					};
					LatLng ll = marker.getPosition();
					mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), ll, -47, listener);
					mBaiduMap.showInfoWindow(mInfoWindow);
				}
				return true;
			}
		});		
	}
	
	public void setUserLocationData(){
		if(IsListNotNull.isListNotNull(personBeanDataList)){
			for(PersonBean beanData : personBeanDataList){
				if(beanData != null){
					initOverlay(beanData);
				}
			}
		}
	}
	
	public void initOverlay(PersonBean beanData) {
		if(StringUtils.isEmpty(beanData.getAddress())){
			return;
		}
		String[] locationStr = beanData.getAddress().split(",");
		// add marker overlay
		final LatLng ll = new LatLng(Double.valueOf(locationStr[0]) , Double.valueOf(locationStr[1]));
		
		ImageLoader.getInstance().loadImage(beanData.getAvatar(), new ImageLoadingListener() {

			@Override
			public void onLoadingStarted(String imageUri, View view) {
				
			}

			@Override
			public void onLoadingFailed(String imageUri, View view,FailReason failReason) {
				
			}

			@Override
			public void onLoadingComplete(String imageUri, View view,Bitmap loadedImage) {
				Bitmap tempBitmap = PhotoUtil.toRoundCorner(loadedImage, 180);
				
				// 定义矩阵对象  
		        Matrix matrix = new Matrix();  
		        // 缩放原图  
		        matrix.postScale(0.5f, 0.5f);
		        
		        Bitmap dstbmp = Bitmap.createBitmap(tempBitmap, 0, 0, tempBitmap.getWidth(), tempBitmap.getHeight(), matrix, true);  
				
				bd = BitmapDescriptorFactory.fromBitmap(dstbmp);

				OverlayOptions oo = new MarkerOptions().position(ll).icon(bd).zIndex(100).draggable(true);
				mMarker = (Marker) (mBaiduMap.addOverlay(oo));
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				
			}
		});
	}
	
	@Override
	protected void onPause() {
		// MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
		mMapView.onDestroy();
		super.onDestroy();
		// 回收 bitmap 资源
		bd.recycle();
		bdGround.recycle();
	}
}
