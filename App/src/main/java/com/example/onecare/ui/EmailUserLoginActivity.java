package com.example.onecare.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import us.zoom.sdk.ZoomApiError;
import us.zoom.sdk.ZoomAuthenticationError;
import com.example.onecare.R;
import com.example.onecare.startjoinmeeting.UserLoginCallback;
import com.example.onecare.startjoinmeeting.emailloginuser.EmailUserLoginHelper;

public class EmailUserLoginActivity extends Activity implements UserLoginCallback.ZoomDemoAuthenticationListener, View.OnClickListener {

	private final static String TAG = "ZoomSDKExample";
	
	private EditText mEdtUserName;
	private EditText mEdtPassord;
	private Button mBtnLogin;
	private View mProgressPanel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.email_login_activity);
		
		mEdtUserName = (EditText)findViewById(R.id.userName);
		mEdtPassord = (EditText)findViewById(R.id.password);

		mBtnLogin = (Button)findViewById(R.id.btnLogin);
		mBtnLogin.setOnClickListener(this);

		mProgressPanel = (View)findViewById(R.id.progressPanel);
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		UserLoginCallback.getInstance().addListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		UserLoginCallback.getInstance().removeListener(this);
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btnLogin) {
			onClickBtnLogin();
		}
	}
	
	private void onClickBtnLogin() {
		String userName = mEdtUserName.getText().toString().trim();
		String password = mEdtPassord.getText().toString().trim();
		if(userName.length() == 0 || password.length() == 0) {
			Toast.makeText(this, "You need to enter user name and password.", Toast.LENGTH_LONG).show();
			return;
		}

		int ret=EmailUserLoginHelper.getInstance().login(userName, password);
		if(!(ret== ZoomApiError.ZOOM_API_ERROR_SUCCESS)) {
			if (ret == ZoomApiError.ZOOM_API_ERROR_EMAIL_LOGIN_IS_DISABLED) {
				Toast.makeText(this, "Account had disable email login ", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, "ZoomSDK has not been initialized successfully or sdk is logging in.", Toast.LENGTH_LONG).show();
			}
		} else {
			mBtnLogin.setVisibility(View.GONE);
			mProgressPanel.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onZoomSDKLoginResult(long result) {
		if(result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
			Toast.makeText(this, "Login successfully", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(this, LoginUserStartJoinMeetingActivity.class);
			startActivity(intent);
			UserLoginCallback.getInstance().removeListener(this);
			finish();
		} else {
			Toast.makeText(this, "Login failed result code = " + result, Toast.LENGTH_SHORT).show();
		}
		mBtnLogin.setVisibility(View.VISIBLE);
		mProgressPanel.setVisibility(View.GONE);
	}

	@Override
	public void onZoomSDKLogoutResult(long result) {
		if(result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
			Toast.makeText(this, "Logout successfully", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "Logout failed result code = " + result, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onZoomIdentityExpired() {
		//Zoom identity expired, please re-login;
	}

	@Override
	public void onZoomAuthIdentityExpired() {

	}
}
