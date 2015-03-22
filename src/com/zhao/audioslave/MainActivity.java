package com.zhao.audioslave;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.zhao.slave.SlavePlay;

/**
 * /**
 * 
 * @author 赵鹏
 * @version 创建时间：2014年9月14日 上午10:38:17 说明 从机界面
 * 
 */
public class MainActivity extends Activity {
	public ImageButton btnConnect;
	public ImageButton btnInit;
	public ImageButton btnQuit;
	public SlavePlay msp;
	public boolean firstInit;
	public ExitHandler mHandler;
	public LockAndUnlockScreen laus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.slave_main);
		firstInit = true;
		mHandler = new ExitHandler();
		laus = new LockAndUnlockScreen(this);
		laus.getAdmin();
		msp = new SlavePlay(MainActivity.this);
//		View.OnClickListener ImageButtonListener = new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				int r;
//				switch (v.getId()) {
//				case R.id.wifi:
//					msp.openWifi();
//					btnInit.setEnabled(true);
//					break;
//				case R.id.init:
//					msp.init();
//					break;
////				case R.id.ImageButton1:
////					msp.exit();
////					break;
//				}
//			}
//		};
		
		View.OnTouchListener ImageButtonTouchListener=new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				int rc;
				switch (v.getId()){
				case R.id.wifi:
					if (event.getAction()==event.ACTION_DOWN){
						btnConnect.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.wifi_press));
					}
					else if(event.getAction()==event.ACTION_UP){
						btnConnect.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.wifi));
						msp.openWifi();
					}
					break;
				case R.id.init:
					if (event.getAction()==event.ACTION_DOWN){
						btnInit.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.init_press));
					}
					else if(event.getAction()==event.ACTION_UP){
						btnInit.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.init));
						msp.init();
					}
					break;
				
				}
				return false;
			}
		};

		
		
		
		btnConnect = (ImageButton) findViewById(R.id.wifi);
		btnConnect.setOnTouchListener(ImageButtonTouchListener);
		btnConnect.setEnabled(true);
		btnInit = (ImageButton) findViewById(R.id.init);
		btnInit.setOnTouchListener(ImageButtonTouchListener);
//		btnQuit = (ImageButton) findViewById(R.id.ImageButton1);
//		btnQuit.setOnClickListener(ImageButtonListener);
		// btnInit.setEnabled(false);
		//setOnTouchListener
				
		
		
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		msp.mAudioTrack.stop();
		msp.mAudioTrack.release();
	}

	public class ExitHandler extends Handler {
		// public ExitHandler() {
		// }

		public void handleMessage(Message msg) {
			if(msg.what==1){
				Toast.makeText(MainActivity.this, 
						"主机来电，设置静音", Toast.LENGTH_SHORT).show();
			}
			else if(msg.what==2){
				Toast.makeText(MainActivity.this, 
						"主机通话结束，恢复音量", Toast.LENGTH_SHORT).show();
			}
			if (msg.what == 88) {
				msp.exitingState = true;
				msp.exit();
				System.exit(0);

			}
		}
	}

}
