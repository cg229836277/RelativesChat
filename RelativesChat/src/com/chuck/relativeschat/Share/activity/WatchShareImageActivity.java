package com.chuck.relativeschat.Share.activity;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.R.layout;
import com.chuck.relativeschat.activity.BaseActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ImageView;

public class WatchShareImageActivity extends BaseActivity {

	private ImageView imageView;
	private String imageUrl;
	private static final String IMAGE_URL = "url";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_watch_share_image);
		imageUrl = getIntent().getStringExtra(IMAGE_URL);
		imageView = (ImageView)findViewById(R.id.friends_share_image);
	}
	
	public void downloadImageFromServer(){
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				dialog.show();
			}
			
			@Override
			protected Void doInBackground(Void... params) {

				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				dialog.dismiss();
			}
		}.execute();
	}
}
