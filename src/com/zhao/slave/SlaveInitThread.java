package com.zhao.slave;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

import android.R.integer;
import android.util.Log;
import android.widget.Toast;

/**
 * /**
 * 
 * @author 赵鹏
 * @version 创建时间：2014年9月14日 上午10:38:33 说明 从机初始化线程
 * 
 */
public class SlaveInitThread extends Thread {
	private static final String TAG = "SlaveInit";
	static DatagramSocket udpSocket = null;
	static DatagramPacket udpPacket = null;
	// public String hostIpString;
	public boolean isRunning;
	public int initCount = 0;
	public long t1;
	public long t2;
	public long t;
	public SlavePlay mSlavePlay;

	public SlaveInitThread(Object object) {
		this.mSlavePlay = (SlavePlay) object;
		// this.hostIpString=mSlavePlay.hostIp;

	}

	// public void setFlag(boolean flag) {
	// isRunning = flag;
	// }

	public void run() {
		t1 = System.currentTimeMillis();

		new SlaveUdpThread(mSlavePlay.hostIp, "I").start();
		Log.d(TAG, "send fisrt udp to init" + " t1: " + t1);
		byte[] data = new byte[10];
		try {
			// udpSocket = new DatagramSocket(43708);

			if (udpSocket == null) {
				udpSocket = new DatagramSocket(null);
				udpSocket.setReuseAddress(true);
				udpSocket.bind(new InetSocketAddress(43708));
			}

			udpPacket = new DatagramPacket(data, data.length);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		// while (isRunning) {
		try {
			udpSocket.receive(udpPacket);
			Log.d(TAG, "pass");
		}
		
		catch (Exception e) {
		}
		if (null != udpPacket.getAddress()) {
			// final String quest_ip = udpPacket.getAddress().toString();
			final String result = new String(udpPacket.getData(),
					udpPacket.getOffset(), udpPacket.getLength());

			if (result.equals("I")) {
				t2 = System.currentTimeMillis();
				Log.d(TAG, "receive success");
				isRunning = false;
				// break;
			}
		}

		udpSocket.close();

		t = (t2 - t1)/2;
		
		mSlavePlay.time = t;
		Log.d(TAG, "upp time t2-t1: " + t+"ms");
	}

}
