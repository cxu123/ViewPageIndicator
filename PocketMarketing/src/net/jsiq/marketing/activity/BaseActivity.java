package net.jsiq.marketing.activity;

import net.jsiq.marketing.R;
import net.jsiq.marketing.fragment.LeftMenuFragment;
import net.jsiq.marketing.fragment.RightMenuFragment;
import net.jsiq.marketing.util.MessageToast;
import net.jsiq.marketing.util.NetworkUtils;
import net.jsiq.marketing.util.ViewHelper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import cn.sharesdk.framework.ShareSDK;

import com.actionbarsherlock.app.ActionBar;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class BaseActivity extends SlidingFragmentActivity implements
		OnClickListener {

	protected SlidingMenu sm;
	private boolean currentNetworkConnected;

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			boolean connected = NetworkUtils.isNetworkConnected(context);
			if (currentNetworkConnected) {
				if (!connected) {
					MessageToast.showText(context, R.string.networkInterupt);
				}
			} else {
				if (connected) {
					MessageToast.showText(context, R.string.networkConnected);
				}
			}
			currentNetworkConnected = connected;
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		ShareSDK.initSDK(this);
		currentNetworkConnected = NetworkUtils.isNetworkConnected(this);
		registerReceiver();
		initActionBar();
		initSlidingMenu();
	}

	private void initSlidingMenu() {
		sm = getSlidingMenu();
		initSlidingMenuAttr();
		loadContentForSlidingMenu();
	}

	private void initSlidingMenuAttr() {
		sm.setMode(SlidingMenu.LEFT_RIGHT);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setBehindScrollScale(0.0f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	}

	public void loadContentForSlidingMenu() {
		setBehindView();
		setSecondaryMenu();
	}

	private void setBehindView() {
		setBehindContentView(R.layout.menu_frame);
		sm.setTouchModeBehind(SlidingMenu.TOUCHMODE_MARGIN);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new LeftMenuFragment()).commit();
	}

	private void setSecondaryMenu() {
		sm.setSecondaryMenu(R.layout.menu_frame_two);
		sm.setSecondaryShadowDrawable(R.drawable.shadowright);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame_two, new RightMenuFragment()).commit();
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		// Get custom view
		View customerView = loadCustomerView();
		// Now set custom view
		ViewHelper.initActionBarAndSetCustomerView(actionBar, customerView);
	}

	private View loadCustomerView() {
		View actionbarView = LayoutInflater.from(this).inflate(
				R.layout.actionbar_main, null);
		ImageButton menuBtn = (ImageButton) actionbarView
				.findViewById(R.id.menu_btn);
		menuBtn.setOnClickListener(this);
		ImageButton personalBtn = (ImageButton) actionbarView
				.findViewById(R.id.personal_btn);
		personalBtn.setOnClickListener(this);
		return actionbarView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.menu_btn:
			if (sm.isMenuShowing()) {
				showContent();
			} else {
				showMenu();
			}
			break;
		case R.id.personal_btn:
			if (sm.isSecondaryMenuShowing()) {
				showContent();
			} else {
				showSecondaryMenu();
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		unregisterReceiver();
		super.onDestroy();
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		if (null != receiver) {
			registerReceiver(receiver, filter);
		}
	}

	private void unregisterReceiver() {
		if (null != receiver) {
			unregisterReceiver(receiver);
		}
	}

}
