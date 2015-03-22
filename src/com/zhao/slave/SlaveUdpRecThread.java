package com.zhao.slave;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import android.media.AudioManager;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.zhao.audioslave.LockAndUnlockScreen;

/**
 * /**
 * 
 * @author 赵鹏
 * @version 创建时间：2014年9月15日 下午8:55:40 说明 UDP接收
 * 
 */
public class SlaveUdpRecThread extends Thread {
	private static final String TAG = "SlaveUdpReceive";
	public static final int DEFAULT_PORT = 43708;
	public String rmsg = "NOT OK";
	static DatagramSocket udpSocket = null;
	static DatagramPacket udpPacket = null;
	public boolean isRunning = true;
	public boolean standby=false;

	public SlavePlay mSlavePlay;
	public LockAndUnlockScreen laus;
	public SlaveUdpRecThread(Object object) {
		this.mSlavePlay = (SlavePlay) object;

	}

	public void setFlag(boolean flag) {
		isRunning = flag;
	}

	@Override
	public void run() {
		byte[] data = new byte[10];
		try {
			if (udpSocket == null) {
				Log.d(TAG, "New udp Socket");
				udpSocket = new DatagramSocket(null);
				udpSocket.setReuseAddress(true);
				udpSocket.bind(new InetSocketAddress(DEFAULT_PORT));
			}
			udpPacket = new DatagramPacket(data, data.length);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		while (true) {

			try {
				udpSocket.receive(udpPacket);
			} catch (Exception e) {
			}
			if (null != udpPacket.getAddress()) {
		//		final String quest_ip = udpPacket.getAddress().toString();
				final String result = new String(udpPacket.getData(),
						udpPacket.getOffset(), udpPacket.getLength());
				if (result.equals("S")) {
				//	mSlavePlay.mSlaveTCPThread.setSlaveTcpFlag(true);
				//	mSlavePlay.mPlayThread.playFlag=true;
					Log.d(TAG, "receive S  start play");
				//	mSlavePlay.buf.start();
					
					Log.d(TAG, "start to play!");

				}
				else if(result.equals(mSlavePlay.standbyIsFalse)){
					mSlavePlay.standbyFlag=false;
					Log.d(TAG, "Set standby to false");
				}
				else if(result.equals(mSlavePlay.standbyIsTrue)){
					mSlavePlay.standbyFlag=true;
					Log.d(TAG, "Set standby to true");
				}
				else if(result.equals("s")) {
					standby=!standby;
					mSlavePlay.standbyFlag=standby;
					Log.d(TAG, "set stanbdby:"+standby);
				}
				else if(result.equals(mSlavePlay.hostCallComing)){
					Message message=new Message();
					message.what=1;
					mSlavePlay.mHandler.sendMessage(message);
					mSlavePlay.mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
				}
				else if(result.equals(mSlavePlay.hostCallGoing)) {
					Message message=new Message();
					message.what=2;
					mSlavePlay.mHandler.sendMessage(message);
					mSlavePlay.mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
				}
				else if(result.equals(mSlavePlay.screenOn)) {
					mSlavePlay.mainActivity.laus.unLockScreen();
					Log.d(TAG, "receive unlockScreen");
//					laus = new LockAndUnlockScreen(this.mSlavePlay.mContext);
//					laus.unLockScreen();
				}
				else if(result.equals(mSlavePlay.screenOff)) {
					mSlavePlay.mainActivity.laus.lockScreen();
					Log.d(TAG, "receive lockScreen");
//					laus = new LockAndUnlockScreen(this.mSlavePlay.mContext);
//					laus.lockScreen();
				}
				else if(result.equals(mSlavePlay.exiting)){
					if(!mSlavePlay.exitingState){
						mSlavePlay.exitType=1;
						Message msg=new Message();
						msg.what=88;						
						mSlavePlay.mainActivity.mHandler.sendMessage(msg);
					}
					
				}
				
				

				else {
				//	mSlavePlay.hostPlayTime=Long.parseLong(result);
				Log.d(TAG, "receive host time success!");
				}

			}
		}

	}

}
