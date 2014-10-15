package com.chuck.relativeschat.common;

import com.chuck.relativeschat.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ProgressBar;

public class MyDialog extends Dialog {
	
	private Context context;
	private ProgressBar mProgressBar;
	
    public MyDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.mydialog);
        this.setCanceledOnTouchOutside(false);
        mProgressBar = (ProgressBar)findViewById(R.id.dialog_progress);   
        
        setTitle("");
    }
    
    @Override
    public void setTitle(CharSequence title) {
    	super.setTitle("请稍候...");
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
