package com.zhao.slave;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.util.Log;

import com.zhao.slave.CircleBuffer.Frame;

public class SlaveTCPThread_new extends Thread {
	private static final String TAG = "Slave";
	public static final int DEFAULT_PORT_TCP = 43709;
	public SlavePlay mSlavePlay;
	public boolean isRunning = true;
	public boolean slaveTcpFlag = true;
	public boolean firstToConnect = true;
	public boolean lockFlag=false;
	public ServerSocket serverSocket = null;
	public Socket socket = null;
	public Socket socket1 = null;
	private BufferedInputStream bis;
	protected byte[] data;
	public CircleBuffer_new mBuffer;
	private int flag = 0;
	private long tcpTime;
	public String hostIpsString;
	public InputStream inputStream;

	public SlaveTCPThread_new(Object object) {
		this.mSlavePlay = (SlavePlay) object;
		this.mBuffer = mSlavePlay.buf;
		this.hostIpsString = mSlavePlay.hostIp;
		data = new byte[480];

	}

	public void run() {
		int totalbytes, singlebytes,count;
		boolean breakFlag = false;
		while (mSlavePlay.tcpFlag) {
			try {
				if (!mSlavePlay.standbyFlag && !lockFlag) {
					socket = new Socket(hostIpsString, DEFAULT_PORT_TCP);
					inputStream = socket.getInputStream();
				//	bis = new BufferedInputStream(inputStream);
					Log.d(TAG, "TCP connect");
					mSlavePlay.socketFlag = true;
				//	mSlavePlay.slavePlayFlag=true;
					lockFlag=true;
				}
				if(mSlavePlay.socketFlag){
					if ( (inputStream.read(data) != -1)) {
//						byte[] newbyte=new byte[960];
//						for(int i=0; i<480;i=i+2){
//							newbyte[2*i]=newbyte[2*i+2]=data[i];
//							newbyte[2*i+1]=newbyte[2*i+3]=data[i+1];
//						}
						mBuffer.put(data);
					}
					
				}
				

				if (mSlavePlay.standbyFlag&&lockFlag) {
					socket.close();
					inputStream = null;
					socket = null;
					mSlavePlay.socketFlag = false;
				//	mSlavePlay.slavePlayFlag=false;;
					lockFlag=false;
					Log.d(TAG, "TCP close");
				}

			} catch (IOException e) {
				// TODO Auto-generated catch

				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}

}
