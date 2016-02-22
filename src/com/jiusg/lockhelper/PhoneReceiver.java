package com.jiusg.lockhelper;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class PhoneReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
			
			// �����绰
		} else {
			// ��ȥ�缴����
			LockHelperService.isPhone = true;
			TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);     
            tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);  

		}
	}
	
	PhoneStateListener listener=new PhoneStateListener(){  
		   
        @Override  
        public void onCallStateChanged(int state, String incomingNumber) {  
                // TODO Auto-generated method stub  
                //state ��ǰ״̬ incomingNumber,ò��û��ȥ���API  
                super.onCallStateChanged(state, incomingNumber);  
                switch(state){  
                case TelephonyManager.CALL_STATE_IDLE:  
                        LockHelperService.isPhone = false;
                        break;  
                /*case TelephonyManager.CALL_STATE_OFFHOOK:  
                        System.out.println("����");  
                        break;  
                case TelephonyManager.CALL_STATE_RINGING:  
                        System.out.println("����:�������"+incomingNumber);  
                        //����������  
                        break; */ 
                }  
        }   
};  

}
