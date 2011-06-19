package com.synchack.android.simpleexit;

import java.lang.reflect.Method;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SimpleExitActivity extends Activity {
	private boolean enabled_bg = false;
	
	// テスト用関数
	private void testFunc(int i){
		ELog.v("=====> Enter testFunc() is " + i);
		
		enabled_bg = false;
		
		switch(i){
		case 0:
			System.exit(0);
			ELog.v(",,, after System.exit()...><");
			break;
		case 1:
			Process.killProcess( Process.myPid() );
			break;
		case 2:
			finish();
			break;
		case 3:
			moveTaskToBack(true);
			break;
		case 4:
    		if( Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO ){
				// compatible
				try{
					final ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
					am.restartPackage( getPackageName() );
				}catch(Exception e){
					e.printStackTrace();
				}
    		}else{
    			// for killBackgroundProcesses()
    			moveTaskToBack(true);
    			enabled_bg = true;
    		}
			break;
		}
	}
	
	TextView tv = null;
	private void addInfoString(String str){
		if( tv == null ){
			tv = (TextView)findViewById(R.id.view_test);
		}
		String str_info = tv.getText().toString();
		str_info += str;
		tv.setText(str_info);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Intent intent = getIntent();
        String action = intent.getAction();
        String disp = "onCreate() pid=["+ Process.myPid() +"], tid=["+Process.myTid()+"]" + "\n";
        
        if( Intent.ACTION_SEND.equals(action) ){
        	// Share
        	Uri send_uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        	disp += "ACTION_SEND : " + send_uri.toString();
        }else{
        	// Home, and more...
        	disp += "action : " + action;
        }
        
        ELog.v( disp );
        addInfoString( disp );
        
        // init button
		Button b0 = (Button) findViewById(R.id.btn_exit);
		b0.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				testFunc(0);
			}
		});
		
		Button b1 = (Button) findViewById(R.id.btn_kill);
		b1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				testFunc(1);
			}
		});
		
		Button b2 = (Button) findViewById(R.id.btn_finish);
		b2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				testFunc(2);
			}
		});
		
		Button b3 = (Button) findViewById(R.id.btn_move);
		b3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				testFunc(3);
			}
		});

		Button b4 = (Button) findViewById(R.id.btn_killbg);
		b4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				testFunc(4);
			}
		});
		
		enabled_bg = false;
    }
	
	
	@Override
	protected void onRestart(){
		super.onRestart();	
        ELog.v("  onRestart()");		
	}
	
	@Override
	protected void onStart(){
		super.onStart();
        ELog.v("  onStart()");	
	}
	
	@Override
	protected void onResume(){
		super.onResume();
        ELog.v("    onResume()");	
	}

	@Override
	protected void onPause(){
		super.onPause();
        ELog.v("    onPause()");
	}	
	
	@Override
	protected void onStop(){
		super.onStop();	
        ELog.v("  onStop()");
        
		// above API Levels 8        
		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ){
	        if( enabled_bg ){
	    		final ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
	    		// am.killBackgroundProcesses( getPackageName() );
	    		Class<? extends ActivityManager> dummy = am.getClass();
	    		try {
					Method method = dummy.getMethod( "killBackgroundProcesses", new Class[] { String.class } );
					method.invoke( am, getPackageName() );
				} catch (Exception e) {
					e.printStackTrace();
				};
		        ELog.v(",,, after killBackgroundProcesses()...><");
	        }
	        enabled_bg = false;
		}
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();		
        ELog.v("onDestroy(), isFinishing() is " + isFinishing() );
	}

}