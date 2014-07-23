package com.chuck.relativeschat.common;

import java.io.IOException;
import com.chuck.relativeschat.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @Title：SAFEYE@
 * @Description：
 * @date 2014-7-22 上午9:41:41
 * @author chengang
 * @version 1.0
 */
public class HeadViewLayout extends LinearLayout {
	private LinearLayout rootViewGroup; // 标题最外层的ViewGroup
	private LayoutInflater mInflater;
	private ImageButton backButton; // 后退按钮
	private TextView tvTitleText; // 标题文本
//	private RelativesChatApplication rcApp;

	public HeadViewLayout(Context context) {
		super(context);
	}

	public HeadViewLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
//		rcApp = (RelativesChatApplication)((Activity)context).getApplication();
		initView();
	}
	
	public void initView(){
		mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rootViewGroup = (LinearLayout) mInflater.inflate(R.layout.friends_view_head_layout, null);
		rootViewGroup.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		addView(rootViewGroup);
		backButton = (ImageButton) rootViewGroup.findViewById(R.id.back_image_btn);
		tvTitleText = (TextView) rootViewGroup.findViewById(R.id.title_text);
		
		backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Runtime runtime = Runtime.getRuntime();
				try {
					runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void setTitleText(String title) {
		tvTitleText.setText(title);
	}
	
	public void setBackButtonVisiable(int viewFlag){
		backButton.setVisibility(viewFlag);
	}
}
