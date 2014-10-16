package com.chuck.relativeschat.common;

import com.chuck.relativeschat.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ProgressBar;

/**
 * @Title：SAFEYE@
 * @Description：
 * @date 2014-10-16 上午8:54:40
 * @author chengang
 * @version 1.0
 */
public class MyProgressDialog extends Dialog {
	private Context context;
	private ProgressBar mProgressBar;
	
    public MyProgressDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.myprogress_dialog);
        this.setCanceledOnTouchOutside(false);
        mProgressBar = (ProgressBar)findViewById(R.id.dialog_progress);   
        setTitle(0);
    }
    
//    @Override
//    public void setTitle(CharSequence title) {
//    	super.setTitle("正在加载...");
//    }
    
    public void setTitle(int progress){
    	setTitle("正在加载..." + progress + "%");
    	mProgressBar.setProgress(progress);
    }
    
    @Override
    public void show() {
    	if(!isShowing()){
    		super.show();
    	}   	
    }
    
    public void setProgress(int progress){
    	mProgressBar.setProgress(progress);
    }
    
    @Override
    public void dismiss() {
    	if(isShowing()){
    		super.dismiss();
    	}      	
    }
}
