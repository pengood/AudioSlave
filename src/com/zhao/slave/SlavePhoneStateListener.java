package com.zhao.slave;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SlavePhoneStateListener extends PhoneStateListener{
	private static final String TAG = "Slave";
	public SlavePlay mSlavePlay;
	
	public SlavePhoneStateListener(Object object){
		this.mSlavePlay = (SlavePlay) object;
	}
	public void onCallStateChanged(int state, String incomingNumber)     
    {    
        switch(state)    
        {    
        case TelephonyManager.CALL_STATE_IDLE:    
         //   Toast.makeText(context, "call not answer", Toast.LENGTH_LONG).show();  
        	new SlaveUdpThread(mSlavePlay.hostIp, mSlavePlay.SlaveCallGoing).start();
        	Log.d(TAG, "slave no call");
            break;    
                
        case TelephonyManager.CALL_STATE_RINGING:    
        //    Toast.makeText(context, "incoming", Toast.LENGTH_LONG).show();    
        	new SlaveUdpThread(mSlavePlay.hostIp, mSlavePlay.slaveCallComing).start();
        	Log.d(TAG, "slave ring");
            break;    
                
        case TelephonyManager.CALL_STATE_OFFHOOK:    
         //   Toast.makeText(context, "in a call", Toast.LENGTH_LONG).show();
        	Log.d(TAG, "slave off hook");
            break;    
        }    
    }    
	
	
	

}
