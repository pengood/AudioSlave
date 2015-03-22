package com.zhao.slave;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class SlavePlayThread_new extends Thread {
	private static final String TAG = "SlavePlayThread";
	public SlavePlay mSlavePlay;
	protected byte[] data;
	public CircleBuffer_new mBuffer;
	public boolean firstFlag = true;
	public AudioTrack mAudioTrack;

	public SlavePlayThread_new(Object object) {
		this.mSlavePlay = (SlavePlay) object;
		this.mBuffer = mSlavePlay.buf;

	}

	public void run() {
		int count = 0;
		int Buffersize=AudioTrack.getMinBufferSize(48000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		byte[] newbyte = new byte[480];
		while (mSlavePlay.slavePlayFlag) {
			if (mSlavePlay.socketFlag) {
				try {

					if (firstFlag) {
						if (mBuffer.availableToRead() < 40960) {
					//		Log.d(TAG,"mBuffer.availableToRead()"+ mBuffer.availableToRead());
							continue;
						}
						
						
					//	Log.d(TAG, "bufferSize: "+Buffersize);
						mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 48000,
								AudioFormat.CHANNEL_CONFIGURATION_MONO,
								AudioFormat.ENCODING_PCM_16BIT, Buffersize, AudioTrack.MODE_STREAM);
						mAudioTrack.play();
				//		mSlavePlay.mAudioTrack.play();
						// newbyte = mBuffer.get(1024);
						new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									sleep(100);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								new SlaveUdpThread(mSlavePlay.hostIp,
										mSlavePlay.startPlaying).start();
							}
						}).start();

						firstFlag = false;
					}
					newbyte = mBuffer.get(480);
				//	mSlavePlay.mAudioTrack.write(newbyte, 0, newbyte.length);
					mAudioTrack.write(newbyte, 0, newbyte.length);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (mSlavePlay.standbyFlag) {
					Log.d(TAG, "Standby is true clear buffer");
					mBuffer.clear();
					mBuffer.start();
				//	mSlavePlay.mAudioTrack.pause();
				//	mSlavePlay.mAudioTrack.flush();
					
					mAudioTrack.pause();
					mAudioTrack.flush();
					mAudioTrack.stop();
					mAudioTrack.release();

					firstFlag = true;
				}

			}
		}
	}

}
