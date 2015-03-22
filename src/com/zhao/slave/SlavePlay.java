package com.zhao.slave;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager.WakeLock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.zhao.audioslave.MainActivity;
import com.zhao.audioslave.R;

/**
 * /**
 * 
 * @author 赵鹏
 * @version 创建时间：2014年9月14日 上午10:37:56 说明 从机类
 * 
 */
public class SlavePlay {
	public static final String standbyIsFalse="a";   //定义命令字
	public static final String standbyIsTrue="b"; 
	public static final String hostCallComing="c";
	public static final String hostCallGoing="d";
	public static final String exiting="e";
	public static final String sleeping="f";
	public static final String waking="g";
	public static final String startPlaying="h";
	public static final String slaveCallComing="i";
	public static final String SlaveCallGoing="j";
	public static final String screenOn ="k";
	public static final String screenOff ="l";
	private static final String TAG = "Slave";
	public Context mContext;
	private WifiAdmin mWifiAdmin;
	private List<ScanResult> wifiResultList;
	private List<String> wifiListString = new ArrayList<String>();
	private StringBuffer strArray = new StringBuffer();
	private String wifiPassword = null;
	String wifiItemSSID = null;

	public SlaveInitThread mInitThread;
	public SlaveUdpRecThread mSlaveUDPThread;
	public SlaveUdpThread mUdpThread;
	
	public static CircleBuffer_new buf;
	public SlavePlayThread_new mPlayThread_new;
	

//	public SlaveTCPThread mSlaveTCPThread;
	public SlaveTCPThread_new mSlaveTCPThread_new;

	public AudioTrack mAudioTrack;
	public AudioManager mAudioManager;

	public int exitType;
	public boolean exitingState;
	public boolean udpFlag;
	public boolean tcpFlag;
	public boolean slavePlayFlag;
	public boolean standbyFlag;
	public boolean socketFlag;
	public String hostIp;
	public String slaveIp;
	public long time;
	public long hostPlayTime;
	Handler mHandler;
	public MainActivity mainActivity;
	
	public SlavePhoneStateListener mHostPhoneStateListener;
	public WakeLock mwakeLock;
	public TelephonyManager manager;
	public ConnectivityManager connmanager;
	public BroadcastReceiver mConnectivityReceiver;
	public BroadcastReceiver mScreenReceiver;
	public IntentFilter filter_screen;
	public IntentFilter filter_wifi;

	

	public SlavePlay(Context context) {
		this.mContext = context;
		this.mainActivity=(MainActivity) context;
		this.mWifiAdmin = new WifiAdmin(context);
		this.mHandler=mainActivity.mHandler;
	}

	// /////////////////////////////////
	// 打开连接WIFI
	// /////////////////////////////////

	public int openWifi() {
		// //
		// TODO Auto-generated method stub
		
		wifiListString.clear();
		
		mWifiAdmin.WifiOpen();
		mWifiAdmin.WifiStartScan();

		while (mWifiAdmin.WifiCheckState() != WifiManager.WIFI_STATE_ENABLED) { 
		//	Log.i("WifiState", String.valueOf(mWifiAdmin.WifiCheckState()));
			
		}
		try {
			Toast.makeText(mContext, "正在打开WIFI....", Toast.LENGTH_SHORT).show();
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		wifiResultList = mWifiAdmin.getScanResults();
		mWifiAdmin.getConfiguration();
		if (wifiListString != null) {
			Log.i("WIFIButtonListener", "dataChange");
			scanResultToString(wifiResultList, strArray);
			final String[] str = strArray.toString().split(";");

			Dialog alertDialog = new AlertDialog.Builder(mContext)
					.setTitle("请选择WIFI热点：")
					.setIcon(R.drawable.ic_launcher)
					.setItems(str, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							String[] ItemValue = str[which].split("--");
							wifiItemSSID = ItemValue[0];
							int wifiItemId = mWifiAdmin.IsConfiguration("\""
									+ wifiItemSSID + "\"");
							if (wifiItemId != -1) {
								if (mWifiAdmin.ConnectWifi(wifiItemId)) {
									Toast.makeText(mContext,
											"连接" + str[which] + "成功！",
											Toast.LENGTH_SHORT).show();
								}
							} else {
								LayoutInflater factory = LayoutInflater
										.from(mContext);
								final View textEntryView = factory.inflate(
										R.layout.login_dialog, null);
								final EditText passWord = (EditText) textEntryView
										.findViewById(R.id.editText1);
								new AlertDialog.Builder(mContext)
										.setTitle("请输入WIFI密码：")
										.setView(textEntryView)
										.setPositiveButton(
												"确定",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int whichButton) {
														wifiPassword = passWord
																.getText()
																.toString()
																.trim();
														if (wifiPassword != null) {
															int netId = mWifiAdmin
																	.AddWifiConfig(
																			wifiResultList,
																			wifiItemSSID,
																			wifiPassword);
															if (netId != -1) {
																mWifiAdmin
																		.getConfiguration();
																if (mWifiAdmin
																		.ConnectWifi(netId)) {
																	// selectedItem.setBackgroundResource(R.color.green);
																	int intIP = mWifiAdmin
																			.getConnectedIPAddr();
																	Log.d("IP",
																			""
																					+ intIP);
																}
															} else {
																Toast.makeText(
																		mContext,
																		"网络连接错误",
																		Toast.LENGTH_SHORT)
																		.show();
																// selectedItem.setBackgroundResource(R.color.burlywood);

															}
														}

													}

												})
										.setNegativeButton(
												"取消",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int whichButton) {

													}

												}).show();

							}

						}
					})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
								}
							}).create();
			alertDialog.show();

		}

		return 0;
	}

	public void scanResultToString(List<ScanResult> listScan, StringBuffer sb) {
		for (int i = 0; i < listScan.size(); i++) {
			ScanResult strScan = listScan.get(i);
			sb.append(strScan.SSID + "--" + strScan.BSSID);
			sb.append(";"); // 网络的名字,BSSID
		}
	}

	// //////////////////////////////
	// 初始化
	// ///////////////////////////////
	public int init() {
		Log.d(TAG, "init()");
		slaveIp=mWifiAdmin.getLocalAdress();
		hostIp=mWifiAdmin.getSeverAdress();
		mInitThread = new SlaveInitThread(this);
	//	mInitThread.setFlag(true);
		mInitThread.start();
		try {
			Thread.sleep(40);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d(TAG, "SlavePlay time: " + time + " hostIp: " + hostIp);
	//	mInitThread.setFlag(false);
		
	
		
		int Buffersize=AudioTrack.getMinBufferSize(48000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		Log.d(TAG, "bufferSize: "+Buffersize);
		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 48000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, Buffersize, AudioTrack.MODE_STREAM);

		mSlaveUDPThread = new SlaveUdpRecThread(this);
		mSlaveUDPThread.start();
		buf = new CircleBuffer_new(61440);
		socketFlag=false;
		standbyFlag=false;
		tcpFlag=true;
		slavePlayFlag=true;
		mSlaveTCPThread_new = new SlaveTCPThread_new(this);
		mPlayThread_new=new SlavePlayThread_new(this);
	
		mSlaveTCPThread_new.start();
		mPlayThread_new.start();
	
		
		//异常处理
		
		mAudioManager=(AudioManager)mContext.getSystemService(mContext.AUDIO_SERVICE);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 14, AudioManager.FLAG_SHOW_UI);
		mAudioTrack.setStereoVolume(1.0f, 1.0f);
		manager = (TelephonyManager)mContext.getSystemService(mContext.TELEPHONY_SERVICE);
		mHostPhoneStateListener=new SlavePhoneStateListener(this);
		manager.listen(mHostPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		filter_screen = new IntentFilter();
		filter_screen.addAction(Intent.ACTION_SCREEN_ON);
		filter_screen.addAction(Intent.ACTION_SCREEN_OFF);
		mScreenReceiver = new BroadcastReceiver(){

			@Override
			public void onReceive(Context arg0, Intent arg1) {
				// TODO Auto-generated method stub
				String action = arg1.getAction();
				if(action.equals(Intent.ACTION_SCREEN_ON)){
				Log.d(TAG, "screen_on");
				}
				else if(action.equals(Intent.ACTION_SCREEN_OFF)){
					Log.d(TAG, "screen_off");
				}
			}
			
		};
		mContext.registerReceiver(mScreenReceiver, filter_screen);
		
		filter_wifi = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION); 
		mConnectivityReceiver=new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub       
		        connmanager = (ConnectivityManager)mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);
				NetworkInfo wifiInfo = connmanager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				if(wifiInfo.isConnected() == false){   //WiFi鏂紑
					Log.d(TAG, "wifi lost");
				}
				else 
					Log.d(TAG, "wifi connect");				
			}			
		};
		mContext.registerReceiver(mConnectivityReceiver, filter_wifi);	
		//
		return 0;

	}
	
	public void exit(){
		mContext.unregisterReceiver(mConnectivityReceiver);
		mContext.unregisterReceiver(mScreenReceiver);
		manager.listen(mHostPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		tcpFlag=false;
		udpFlag=false;
		slavePlayFlag=false;
		
		
//		if(mSlaveTCPThread_new!=null){
//			try {
//			//	mSlaveTCPThread_new.socket.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		
		
		//--------------------------------------test
//		Message msg=new Message();
//		msg.what=88;						
//		mainActivity.mHandler.sendMessage(msg);
		//----------------------------------------test
		
		//关闭socket
		//----------------
		
	
		
	}
	
	
	

}
