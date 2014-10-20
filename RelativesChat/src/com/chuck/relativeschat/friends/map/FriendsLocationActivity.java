package com.chuck.relativeschat.friends.map;

import java.util.ArrayList;
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
import com.chuck.relativeschat.adapter.MyFriendsListViewAdapter;
import com.chuck.relativeschat.base.RelativesChatApplication;
import com.chuck.relativeschat.bean.UserInfoBean;
import com.chuck.relativeschat.entity.PersonBean;
import com.chuck.relativeschat.tools.IsListNotNull;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

public class FriendsLocationActivity extends BaseActivity implements OnGetGeoCoderResultListener {

	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;
	private GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private RelativesChatApplication rcApp;
	private List<PersonBean> personBeanDataList;
	private BmobChatUser currentUser;
	
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
		LatLng ptCenter = new LatLng(22.492087 , 113.920054);
		// 反Geo搜索
		mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter));
	}
	
	public void getMyFriendsData(){
		new AsyncTask<Void, List<UserInfoBean>, List<UserInfoBean>>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				dialog.show();
			}
			
			@Override
			protected List<UserInfoBean> doInBackground(Void... params) {
				
				if(IsListNotNull.isListNotNull(personBeanDataList)){
					List<UserInfoBean> infoBeanList = new ArrayList<UserInfoBean>();
					for(PersonBean data : personBeanDataList){
						if(!data.getObjectId().equals(currentUser.getObjectId())){									
							UserInfoBean infoBean = new UserInfoBean();
							infoBean.setAvatorUrl(data.getAvatar());
							infoBean.setUserName(data.getUsername());
							infoBean.setUserState(data.getUserState());
							infoBean.setNickName(data.getNickName());
							infoBean.setUserId(data.getObjectId());
							infoBeanList.add(infoBean);							
						}
					}
					return infoBeanList;
				}	
				return null;
			}
			
			@Override
			protected void onPostExecute(List<UserInfoBean> result) {
				super.onPostExecute(result);
				if(IsListNotNull.isListNotNull(result)){
					
				}
				dialog.dismiss();
			}
			
		}.execute();
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
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			mToast.showMyToast("抱歉，未能找到结果", Toast.LENGTH_SHORT);
			return;
		}
		mBaiduMap.clear();
		mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));
		String strInfo = String.format("纬度：%f 经度：%f",result.getLocation().latitude, result.getLocation().longitude);
		mToast.showMyToast(strInfo, Toast.LENGTH_SHORT);
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			mToast.showMyToast("抱歉，未能找到结果", Toast.LENGTH_SHORT);
			return;
		}
		mBaiduMap.clear();
		mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));
		mToast.showMyToast(result.getAddress(), Toast.LENGTH_SHORT);
		System.out.println("地址是" + result.getAddress());
	}
}
