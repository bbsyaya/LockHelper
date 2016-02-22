package com.jiusg.lockhelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.jiusg.lockhelper.R;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MyApplication extends ListActivity {

	private ArrayList<HashMap<String, Object>> ApplicationList;
	private ProgressBar pb;
	private TextView tv;
	private Handler hd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myapplication);
		pb = (ProgressBar) findViewById(R.id.progressBar2);
		tv = (TextView) findViewById(R.id.LoadApp);

		hd = new MyAppHandler();
		Message msg = hd.obtainMessage();
		msg.obj = "load";
		hd.sendMessageDelayed(msg, 500);

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		SharedPreferences Lock_info = getSharedPreferences("Lock_info", 0);
		int lock_position = getIntent().getExtras().getInt("lock_position");
		if (position == 0) {
			Lock_info.edit().putString("Name" + lock_position, "����").commit();

		} else {
			Lock_info
					.edit()
					.putString("Name" + lock_position,
							(String) ApplicationList.get(position).get("Name"))
					.commit();
			Lock_info
					.edit()
					.putString(
							"Packagename" + lock_position,
							(String) ApplicationList.get(position).get(
									"PackageName")).commit();
			Lock_info
					.edit()
					.putString(
							"MainActivity" + lock_position,
							(String) ApplicationList.get(position).get(
									"MainActivity")).commit();
		}
		HomeFragment.IsChooseApp = true;
		// ���û�ѡ��������Ӧ�ú�ִ��Service��onstartcommand()���������¼���һЩ���ݣ���Ϊ��Ҫ���¼��ز����ļ���
		if (lock_position == 4) {

			Intent it = new Intent();
			it.setClass(MyApplication.this, LockHelperService.class);
			startService(it);
		}
		finish();

	}

	private class baseAdapter extends BaseAdapter {

		LayoutInflater inflater = LayoutInflater.from(MyApplication.this);

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return ApplicationList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {

			ViewHolder holder;
			if (convertView == null) {
				// ʹ��View�Ķ���itemView��R.layout.item����
				convertView = inflater.inflate(R.layout.applicationlist, null);
				holder = new ViewHolder();

				holder.label_no = (TextView) convertView
						.findViewById(R.id.ApplicationNameId_no);
				holder.icon = (ImageView) convertView
						.findViewById(R.id.ApplicationImageId);
				holder.label = (TextView) convertView
						.findViewById(R.id.ApplicationNameId);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.label_no.setText(ApplicationList.get(position)
					.get("Name_no").toString());
			holder.icon.setImageDrawable((Drawable) ApplicationList.get(
					position).get("Image"));
			holder.label.setText(ApplicationList.get(position).get("Name")
					.toString());

			return convertView;
		}
	}

	private class ViewHolder {
		private ImageView icon;
		private TextView label;
		private TextView label_no;
	}

	// �����������Activity����Ϣ��������Launch����
	public void queryAppInfo() {
		PackageManager pm = this.getPackageManager(); // ���PackageManager����
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		// ͨ����ѯ���������ResolveInfo����.
		List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent,
				PackageManager.GET_RESOLVED_FILTER);
		// ����ϵͳ���� �� ����name����
		// ���������Ҫ������ֻ����ʾϵͳӦ�ã��������г�������Ӧ�ó���
		Collections.sort(resolveInfos,
				new ResolveInfo.DisplayNameComparator(pm));

		for (ResolveInfo reInfo : resolveInfos) {
			String activityName = reInfo.activityInfo.name; // ��ø�Ӧ�ó��������Activity��name
			String pkgName = reInfo.activityInfo.packageName; // ���Ӧ�ó���İ���
			String appLabel = (String) reInfo.loadLabel(pm); // ���Ӧ�ó����Label
			Drawable icon = reInfo.loadIcon(pm); // ���Ӧ�ó���ͼ��

			HashMap<String, Object> hm = new HashMap<String, Object>();
			if (!(appLabel.equals("ͼ��") | appLabel.equals("�����"))) {
				hm.put("Image", icon);
				hm.put("Name", appLabel);
				hm.put("PackageName", pkgName);
				hm.put("Name_no", "");
				hm.put("MainActivity", activityName);
				ApplicationList.add(hm);
			}

		}
	}

	class MyAppHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			ApplicationList = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> hm = new HashMap<String, Object>();
			hm.put("Name_no", "  ��ѡ���κ�Ӧ��");
			hm.put("Name", "");
			ApplicationList.add(hm);
			queryAppInfo();
			pb.setVisibility(View.GONE);
			tv.setVisibility(View.GONE);
			setListAdapter(new baseAdapter());
		}

	}

}
