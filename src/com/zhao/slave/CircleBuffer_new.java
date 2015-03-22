package com.zhao.slave;

import java.nio.ByteBuffer;

import android.R.integer;
import android.R.raw;
import android.util.Log;

public class CircleBuffer_new {
	private static String TAG = "Buffer";
	private ByteBuffer mBuffer;
	private byte[] mByteBuffer;
	int bufsize;
	int numberOfEntries = 0;
	int front = 0;
	int back = 0;

	public CircleBuffer_new(int n) {
		bufsize = n;
		mBuffer = ByteBuffer.allocate(bufsize);
		mByteBuffer = new byte[bufsize];
	}
	synchronized void start() {
		notifyAll();
	}

	public void clear() {
		front = back = numberOfEntries = 0;
		Log.d(TAG, "Clear success");

	}

	public int availableToRead(){
		return numberOfEntries;
	}

	public int availableToWrite(){
		return bufsize-numberOfEntries;
	}
	public synchronized int Clear() {
		this.front = 0;
		this.back = 0;
		this.numberOfEntries = 0;
		notifyAll();
		Log.d(TAG, "Clear success");
		System.out.println("Clear success,front: " + front + " back: " + back);
		return 0;
	}

	synchronized void put(byte[] src) throws InterruptedException {
		int length = src.length;
		if (numberOfEntries == bufsize){
			Log.d(TAG, "Can't put data into buffer" );
			wait();
		}
			
		for (int i = 0; i < length; i++) {
			mByteBuffer[back++] = src[i];
			if (back == bufsize)
				back = 0;
			numberOfEntries++;
			if (numberOfEntries == bufsize)
			{
				notify();
		//		wait();
			}
				
		}
		notify();
	//	Log.d("buffer", "put->" + back);
		
	}

	synchronized byte[] get(int n) throws InterruptedException {
		if (0 == numberOfEntries){
			Log.d(TAG, "Can't get data from buffer" );
			wait();
		}
			
		byte[] newbyte = new byte[n];
		for (int i = 0; i < n; i++) {
			newbyte[i] = mByteBuffer[front++];
			if (front == bufsize)
				front = 0;
			numberOfEntries--;
			if (0 == numberOfEntries){
				notify();
			//	wait();
			}
				
		}
		notify();
	//	Log.d("buffer", "get->" + front);

	
		return newbyte;

	}

}
