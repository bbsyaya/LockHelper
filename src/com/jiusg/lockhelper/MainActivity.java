package com.jiusg.lockhelper;

import java.util.Calendar;

import com.jiusg.Tools.Mydialog;
import com.jiusg.Tools.SmartBarUtils;
import com.jiusg.lockhelper.R;
import com.meizu.mstore.license.ILicensingService;
import com.meizu.mstore.license.LicenseCheckHelper;
import com.meizu.mstore.license.LicenseResult;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;

public class MainActivity extends Activity {

	private ILicensingService mLicensingService = null;
	private final String APKPublic = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCL1JrmG/y+pHE67dj99Myr+ZVVX7QgRUIuTWcQvdSmM8o57UEA214tzy9IZkDpAk7KWE9s4h2c3a4JwecCXIwbiT4K5X+7YNqPkAh1EIQ3MR7l3+WSqyAISzOf9XUMv7mzZ3QtKiAZmKH7SEs4M4VpFp+g5/DeBvIzjrKM47pYAQIDAQAB";
	private ServiceConnection mLicenseConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {

			mLicensingService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {

			mLicensingService = ILicensingService.Stub.asInterface(service);
		}
	};
	private Handler hd;
	private SharedPreferences sp;
	public static boolean IsChooseApp = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final ActionBar bar = getActionBar();
		bar.addTab(bar
				.newTab()
				.setIcon(R.drawable.ic_tab_home)
				.setTabListener(
						new MyTabListener<HomeFragment>(this, "��������",
								HomeFragment.class)));
		bar.addTab(bar
				.newTab()
				.setIcon(R.drawable.ic_tab_setting)
				.setTabListener(
						new MyTabListener<SettingFragment>(this, "����",
								SettingFragment.class)));

		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		SmartBarUtils.setActionBarTabsShowAtBottom(bar, true); // ���������ṩ�Ľӿڣ�ʹ����ʾ��SmartBar��

		// ��һ��ִ�г���ʱ�������LockHelper��Ĭ��λ��
		// �����һ��ִ��ʱ�����
		sp = getSharedPreferences("Screen", 0); // Screen,���˺ö��Ҫ����Ϣ������ȡ����
		SharedPreferences sp_setting = PreferenceManager
				.getDefaultSharedPreferences(getApplication());
		// һ��Ҫ����aaaaaa
		// sp.edit().putString("UserVersionInfo", "OfficialVersionISTRUE")
		// .commit();

		// �ж���汾���汾��Ϣ���ԣ����û����ܽ����˸���
		if (!sp.getString("Version", "").equals("1.1.5")) {

			sp.edit().putString("Version", "1.1.5").commit();
			final Mydialog dl = new Mydialog(this, R.style.dialog);
			LayoutInflater inflater = LayoutInflater.from(this);
			View layout = inflater.inflate(R.layout.lesson, null, false);
			dl.setContentView(layout);
			dl.setCancelable(true);
			dl.show();
		}
		// �ж�λ�ñ༭�Ƿ���ֵ��û�еĻ��û����ǵ�һ�ΰ�װ����ȡ�豸����Ϣ������λ�õ�Ĭ��ֵ������Ҫ������û�������
		if (sp_setting.getString("Lockposition", "").equals("")) {
			sp_setting.edit().putString("Lockposition", "0").commit();

			// �����û��ֻ�����Ļ��С��Ϣ
			sp.edit().putInt("ScreenHeight", GetScreenHeight()).commit();
			sp.edit().putInt("ScreenWidth", GetScreenWidth()).commit();

			// ����һЩ��ʼ����
			sp_setting.edit().putInt("Alpha", 15).commit();
			sp_setting.edit().putInt("Size", 300 + 10 * 15).commit();
			sp_setting.edit()
					.putInt("LockpositionY", GetScreenHeight() / 30 * 10)
					.commit();
		}

		// ����Ѿ���֤���û�Ϊ��ʽ�棬��ôÿ������Ӧ�þ�û��Ҫ��֤��
		if (!sp.getString("UserVersionInfo", "")
				.equals("OfficialVersionISTRUE")) {
			// �󶨷��� ��Щ����������֤�Ƿ���ʽ�汾��
			if (mLicensingService == null) {

				Intent intent = new Intent();
				intent.setAction(ILicensingService.class.getName());
				bindService(intent, mLicenseConnection,
						Context.BIND_AUTO_CREATE);

			}

			hd = new LicenseHandler();
			Message msg1 = hd.obtainMessage();
			msg1.obj = "License";
			hd.sendMessageDelayed(msg1, 3000);
		}
	}

	public static class MyTabListener<T extends Fragment> implements
			ActionBar.TabListener {

		private final Activity mActivity;
		private final String mTag;
		private final Class<T> mClass;
		private final Bundle mArgs;
		private Fragment mFragment;

		public MyTabListener(Activity activity, String tag, Class<T> clz) {
			this(activity, tag, clz, null);
		}

		public MyTabListener(Activity activity, String tag, Class<T> clz,
				Bundle args) {
			mActivity = activity;
			mTag = tag;
			mClass = clz;
			mArgs = args;

			// Check to see if we already have a fragment for this tab, probably
			// from a previously saved state. If so, deactivate it, because our
			// initial state is that a tab isn't shown.
			mFragment = mActivity.getFragmentManager().findFragmentByTag(mTag);
			if (mFragment != null && !mFragment.isDetached()) {
				FragmentTransaction ft = mActivity.getFragmentManager()
						.beginTransaction();
				ft.detach(mFragment);
				ft.commit();
			}
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {

			// Toast.makeText(mActivity, "Reselected!",
			// Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {

			if (mFragment == null) {
				mFragment = Fragment.instantiate(mActivity, mClass.getName(),
						mArgs);
				ft.add(android.R.id.content, mFragment, mTag);
			} else {
				ft.attach(mFragment);
			}

			mActivity.getActionBar().setTitle(mTag);
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {

			if (mFragment != null) {
				ft.detach(mFragment);
			}
		}

	}

	class LicenseHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			// super.handleMessage(msg);

			if (msg.obj.toString().equals("License")) {
				// ���÷�����License�������ؼ����
				LicenseResult result = null;
				try {

					result = mLicensingService.checkLicense(getApplication()
							.getPackageName());

				} catch (RemoteException e) {
					e.printStackTrace();
					Toast.makeText(getApplication(), "����:4 ��֤License�������ش���",
							Toast.LENGTH_SHORT).show();
				}
				// �����ж�
				if (result.getResponseCode() == LicenseResult.RESPONSE_CODE_SUCCESS) {

					boolean bSuccess = LicenseCheckHelper.checkResult(
							APKPublic, result);

					if (bSuccess
							&& result.getPurchaseType() == LicenseResult.PURCHASE_TYPE_NORMAL) {
						// ��ǰ��֤Ϊ��ʽ��
						sp.edit()
								.putString("UserVersionInfo",
										"OfficialVersionISTRUE").commit();
					} else {

						if (bSuccess
								&& result.getPurchaseType() == LicenseResult.PURCHASE_TYPE_TRIAL) {
							// ��ǰ��֤Ϊ���ð�
							Calendar cal = result.getStartDate();
							cal.add(Calendar.DAY_OF_MONTH, 3);
							Calendar cNow = Calendar.getInstance(); // ��ȡ��ǰϵͳʱ��
							if (cNow.after(cal)) {

								Toast.makeText(getApplication(),
										"����ʱ���Ѿ��������������ֽ������ṩ����",
										Toast.LENGTH_SHORT).show();
								Intent it = new Intent();
								it.setClass(MainActivity.this,
										LockHelperService.class);
								stopService(it);
								sp.edit()
										.putString("UserVersionInfo",
												"TrialVersionOver").commit();

							} else {

								Toast.makeText(getApplication(), "��ǰΪ���ð棡",
										Toast.LENGTH_SHORT).show();
								sp.edit()
										.putString("UserVersionInfo",
												"TrialVersion").commit();
							}
						} else {
							Intent it = new Intent();
							it.setClass(MainActivity.this,
									LockHelperService.class);
							stopService(it);
							Toast.makeText(getApplication(), "����:1 ��֤ʧ�ܣ�������",
									Toast.LENGTH_SHORT).show();
							sp.edit().putString("UserVersionInfo", "Error")
									.commit();
						}
					}
				} else {
					if (result.getResponseCode() == LicenseResult.RESPONSE_CODE_NO_LICENSE_FILE) {

						Intent it = new Intent();
						it.setClass(MainActivity.this, LockHelperService.class);
						stopService(it);
						Toast.makeText(getApplication(), "����:2 ��֤ʧ�ܣ������ԣ�",
								Toast.LENGTH_SHORT).show();
						sp.edit().putString("UserVersionInfo", "Error")
								.commit();
					} else {

						Intent it = new Intent();
						it.setClass(MainActivity.this, LockHelperService.class);
						stopService(it);
						Toast.makeText(getApplication(), "����:3 ��֤ʧ�ܣ�������",
								Toast.LENGTH_SHORT).show();
						sp.edit().putString("UserVersionInfo", "Error")
								.commit();
					}
				}
				// �����
				if (mLicensingService != null) {
					unbindService(mLicenseConnection);
				}
			}

		}
	}

	/**
	 * ��ȡ��ǰ��Ļ�Ŀ�
	 * 
	 * @param activity
	 * @return
	 */
	public int GetScreenWidth() {

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	/**
	 * ��ȡ��ǰ��Ļ�ĸ߶�
	 * 
	 * @param activity
	 * @return
	 */
	public int GetScreenHeight() {

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels - getStatusBarHeight();
	}

	/**
	 * ��ȡ״̬���ĸ߶�
	 * 
	 * @param activity
	 * @return
	 */
	public int getStatusBarHeight() {
		Class<?> c = null;
		Object obj = null;
		java.lang.reflect.Field field = null;
		int x = 0;
		int statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = getResources().getDimensionPixelSize(x);
			return statusBarHeight;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusBarHeight;
	}
}
