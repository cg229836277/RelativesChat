package com.chuck.relativeschat.activity;

import java.util.List;

import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.db.BmobDB;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.adapter.FriendsInvitationMessageAdapter;
import com.chuck.relativeschat.common.DialogTips;
import com.chuck.relativeschat.common.HeadViewLayout;

import android.os.Bundle;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemLongClickListener;

public class FriendsInvitionMessageActivity extends BaseActivity implements OnItemLongClickListener {

	private HeadViewLayout mHeadViewLayout;
	private ListView listview;
	private FriendsInvitationMessageAdapter adapter;
	private  List<BmobInvitation> inviteList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends_invition_message);
		
		mHeadViewLayout = (HeadViewLayout)findViewById(R.id.title_menu_layout);
		mHeadViewLayout.setBackButtonVisiable(View.VISIBLE);
		mHeadViewLayout.setTitleText("好友验证");
		
		initView();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2 , long arg3) {
		BmobInvitation invite = (BmobInvitation) adapter.getItem(arg2);
		showDeleteDialog(arg2,invite);
		return true;
	}
	
	public void initView(){
		listview = (ListView)findViewById(R.id.list_newfriend);
		listview.setOnItemLongClickListener(this);
		inviteList = BmobDB.create(this).queryBmobInviteList();
		adapter = new FriendsInvitationMessageAdapter(this,inviteList);
		listview.setAdapter(adapter);
		if(adapter != null && adapter.getCount() > 0){
			listview.setSelection(adapter.getCount());
		}
	}
	
	public void showDeleteDialog(final int position,final BmobInvitation invite) {
		DialogTips dialog = new DialogTips(this,invite.getFromname(),"删除好友？", "确认",true,true);
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				deleteInvite(position,invite);
			}
		});
		
		dialog.show();
		dialog = null;
	}
	
	private void deleteInvite(int position, BmobInvitation invite){
		adapter.remove(position);
		BmobDB.create(this).deleteInviteMsg(invite.getFromid(), Long.toString(invite.getTime()));
	}
}
