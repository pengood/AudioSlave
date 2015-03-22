package com.zhao.slave;

import android.R.integer;
import android.util.Log;

public class CircleBuffer {
	private static final String TAG = "Slave";
	int bufsize;
	Frame[] store;
	int numberOfEntries = 0;
	int front = 0;
	int back = 0;
	int flag = 0;
	public long tim;

	CircleBuffer(int n) {
		bufsize = n;
		store = new Frame[bufsize];

	}

	synchronized void start() {
		notifyAll();
	}

	public void clear() {
		front = back = numberOfEntries = 0;

	}

	synchronized void put(Frame rec) throws InterruptedException {
		if (numberOfEntries == bufsize)
			wait();
		store[back] = rec;
		 System.out.println("put " + back);
		back = back + 1;
		if (back == bufsize)
			back = 0;
		numberOfEntries += 1;
		notify();
	}

	synchronized Frame get() throws InterruptedException {
		Frame result = new Frame(10);
		if (0 == numberOfEntries)
			wait();
		if (flag == 0) {
			flag++;
			tim = System.nanoTime();
			Log.d(TAG, "Frame first get time: " + tim);

		}
		result = store[front];
		System.out.println("get  " + front);
		front += 1;
		if (front == bufsize)
			front = 0;
		numberOfEntries -= 1;
		notify();
		return result;

	}

	public class Frame {
		public Frame(int num2) {
			// TODO Auto-generated constructor stub
			this.num = num2;
			byteb = new byte[num];

		}

		int num;
		public byte[] byteb;

		public String toString() {
			return new String("num: " + num);
		}
	}
}
