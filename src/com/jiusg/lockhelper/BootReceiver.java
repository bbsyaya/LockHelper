package com.jiusg.lockhelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * ����ϵͳ�㲥��������������
 * 
 * @author Administrator
 * 
 */
public class BootReceiver extends BroadcastReceiver {

	private SharedPreferences Lock_setting;
	private SharedPreferences sp_ver;

	@Override
	public void onReceive(Context context, Intent intent) {

		Lock_setting = PreferenceManager.getDefaultSharedPreferences(context);
		sp_ver = context.getSharedPreferences("Screen", 0);

		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

			// ���û������˿���������������ʽ�棬����δ���ڵ����ð���ܽ��п�������
			if (Lock_setting.getBoolean("PowerBoot", false)
					& (sp_ver.getString("UserVersionInfo", "").equals(
							"OfficialVersionISTRUE") || sp_ver.getString(
							"UserVersionInfo", "").equals("TrialVersion")))
				context.startService(new Intent(context,
						LockHelperService.class));

		}

	}

}
