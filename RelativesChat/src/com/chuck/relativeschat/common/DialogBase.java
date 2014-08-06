package com.chuck.relativeschat.common;

import com.chuck.relativeschat.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class DialogBase extends Dialog {
	protected OnClickListener onSuccessListener;
	protected Context mainContext;
	protected OnClickListener onCancelListener;
	protected OnDismissListener onDismissListener;
	
	protected View view;
	protected Button positiveButton, negativeButton;
	private boolean isFullScreen = false;
	
	private boolean hasTitle = true;
	
	private int width = 0, height = 0, x = 0, y = 0;
	private int iconTitle = 0;
	private String message, title;
	private String namePositiveButton, nameNegativeButton;
	private final int MATCH_PARENT = android.view.ViewGroup.LayoutParams.MATCH_PARENT;

	private boolean isCancel = true;
	
	
	public boolean isCancel() {
		return isCancel;
	}

	public void setCancel(boolean isCancel) {
		this.isCancel = isCancel;
	}

	/**
	 *
	 * @param context
	 */
	public DialogBase(Context context) {
		super(context, R.style.alert);
		this.mainContext = context;
	}
	
	/** 
	 * �����¼�
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_my_dialog);
		this.onBuilding();
		//
//		TextView dialog_top = (TextView)findViewById(R.id.dialog_title_text);
		View title_red_line = (View)findViewById(R.id.title_red_line);
		
		//
//		if(hasTitle){
//			dialog_top.setVisibility(View.VISIBLE);
////			title_red_line.setVisibility(View.VISIBLE);
//		}else{
//			dialog_top.setVisibility(View.GONE);
////			title_red_line.setVisibility(View.GONE);
//		}
		TextView titleTextView = (TextView)findViewById(R.id.dialog_title_text);
		titleTextView.setText(this.getTitle());
		TextView messageTextView = (TextView)findViewById(R.id.dialog_content_text);
		messageTextView.setText(this.getMessage());
		
//		if (view != null) {
//			FrameLayout custom = (FrameLayout) findViewById(R.id.dialog_custom);
//			custom.addView(view, new LayoutParams(MATCH_PARENT, MATCH_PARENT));
//			findViewById(R.id.dialog_contentPanel).setVisibility(View.GONE);
//		} else {
//			findViewById(R.id.dialog_customPanel).setVisibility(View.GONE);
//		}

		//
		positiveButton = (Button)findViewById(R.id.commit_dialog_button);
		negativeButton = (Button)findViewById(R.id.cancle_dialog_button);
		if(namePositiveButton != null && namePositiveButton.length()>0){
			positiveButton.setText(namePositiveButton);
			positiveButton.setVisibility(View.VISIBLE);
			positiveButton.setOnClickListener(GetPositiveButtonOnClickListener());
		} else {
			positiveButton.setVisibility(View.GONE);
			findViewById(R.id.dialog_leftspacer).setVisibility(View.VISIBLE);
			findViewById(R.id.dialog_rightspacer).setVisibility(View.VISIBLE);
		}
		if(nameNegativeButton != null && nameNegativeButton.length()>0){
			negativeButton.setText(nameNegativeButton);
			negativeButton.setVisibility(View.VISIBLE);
			negativeButton.setOnClickListener(GetNegativeButtonOnClickListener());
		} else {
			negativeButton.setVisibility(View.GONE);
		}
		
		//
//		LayoutParams params = this.getWindow().getAttributes();  
//		if(this.getWidth()>0)
//			params.width = this.getWidth();  
//		if(this.getHeight()>0)
//			params.height = this.getHeight();  
//		if(this.getX()>0)
//			params.width = this.getX();  
//		if(this.getY()>0)
//			params.height = this.getY();  
//		
//		//
//		if(isFullScreen) {
//			params.width = WindowManager.LayoutParams.MATCH_PARENT;
//			params.height = WindowManager.LayoutParams.MATCH_PARENT;
//		}
		
		//
		if(isCancel){
			setCanceledOnTouchOutside(true);
			setCancelable(true);
		}else{
			setCanceledOnTouchOutside(false);
			setCancelable(false);
		}
//	    getWindow().setAttributes(params);  
		this.setOnDismissListener(GetOnDismissListener());
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}

	/**
	 *
	 * @return OnDismiss
	 */
	protected OnDismissListener GetOnDismissListener() {
		return new OnDismissListener(){
			public void onDismiss(DialogInterface arg0) {
				DialogBase.this.onDismiss();
				DialogBase.this.setOnDismissListener(null);
				view = null;
				mainContext = null;
				positiveButton = null;
				negativeButton = null;
				if(onDismissListener != null){
					onDismissListener.onDismiss(null);
				}
			}			
		};
	}

	/**
	 *
	 * @return
	 */
	protected View.OnClickListener GetPositiveButtonOnClickListener() {
		return new View.OnClickListener() {
			public void onClick(View v) {
				if(OnClickPositiveButton())
					DialogBase.this.dismiss();
			}
		};
	}
	
	/**
	 *
	 * @return 
	 */
	protected View.OnClickListener GetNegativeButtonOnClickListener() {
		return new View.OnClickListener() {
			public void onClick(View v) {
				OnClickNegativeButton();
				DialogBase.this.dismiss();
			}
		};
	}
	
	/**
	 *
	 * @return
	 */
	protected OnFocusChangeListener GetOnFocusChangeListener() {
		return new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus && v instanceof EditText) {
					((EditText) v).setSelection(0, ((EditText) v).getText().length());
				}
			}
		};
	}
	
	/**
	 *
	 * @param listener
	 */
	public void SetOnSuccessListener(OnClickListener listener){
		onSuccessListener = listener;
	}
	
	/**
	 * 
	 * @param listener
	 */
	public void SetOnDismissListener(OnDismissListener listener){
		onDismissListener = listener;
	}

	/**
	 * @param listener
	 */
	public void SetOnCancelListener(OnClickListener listener){
		onCancelListener = listener;
	}
	
	/**
	 *
	 */
	protected abstract void onBuilding();

	/**
	 *
	 */
	protected abstract boolean OnClickPositiveButton();

	/**
	 *
	 */
	protected abstract void OnClickNegativeButton();

	/**
	 * 
	 */
	protected abstract void onDismiss();

	/**
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * @param iconTitle
	 */
	public void setIconTitle(int iconTitle) {
		this.iconTitle = iconTitle;
	}

	/**
	 * @return
	 */
	public int getIconTitle() {
		return iconTitle;
	}

	/**
	 * @return
	 */
	protected String getMessage() {
		return message;
	}

	/**
	 * @param message
	 */
	protected void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return
	 */
	protected View getView() {
		return view;
	}

	/**
	 * @param view
	 */
	protected void setView(View view) {
		this.view = view;
	}

	/**
	 * @return
	 */
	public boolean getIsFullScreen() {
		return isFullScreen;
	}

	/**
	 * @param isFullScreen
	 */
	public void setIsFullScreen(boolean isFullScreen) {
		this.isFullScreen = isFullScreen;
	}

	public boolean isHasTitle() {
		return hasTitle;
	}


	public void setHasTitle(boolean hasTitle) {
		this.hasTitle = hasTitle;
	}

	
	/**
	 * @return
	 */
	protected int getWidth() {
		return width;
	}

	/**
	 * @param width
	 */
	protected void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return
	 */
	protected int getHeight() {
		return height;
	}

	/**
	 * @param height
	 */
	protected void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @return
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return 
	 */
	public int getY() {
		return y;
	}

	/**
	 * @paramy
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * @return
	 */
	protected String getNamePositiveButton() {
		return namePositiveButton;
	}

	/**
	 * @param namePositiveButton
	 */
	protected void setNamePositiveButton(String namePositiveButton) {
		this.namePositiveButton = namePositiveButton;
	}

	/**
	 * @return 
	 */
	protected String getNameNegativeButton() {
		return nameNegativeButton;
	}

	/**
	 * @param nameNegativeButton
	 */
	protected void setNameNegativeButton(String nameNegativeButton) {
		this.nameNegativeButton = nameNegativeButton;
	}
}