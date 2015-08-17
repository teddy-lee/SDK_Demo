package cn.koolcloud.demo.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.koolcloud.sdk.fmsc.service.IApmpInterface;
import com.koolcloud.sdk.fmsc.service.IDevicesInterface;
import com.koolcloud.sdk.fmsc.service.ITransactionInterface;

public class BaseActivity extends Activity {
	
	protected IApmpInterface mIApmpInterface;
    private ServiceConnection mIApmpInterfaceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mIApmpInterface = IApmpInterface.Stub.asInterface(service);
			Log.i("Client", "Bind IApmpInterface Success:" + mIApmpInterface.getClass().toString());
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mIApmpInterface = null;
		}
	};
	
	protected IDevicesInterface mIDevicesInterface;
    private ServiceConnection mIDevicesInterfaceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mIDevicesInterface = IDevicesInterface.Stub.asInterface(service);
			Log.i("Client", "Bind IDevicesInterface Success:" + mIDevicesInterface.getClass().toString());
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mIDevicesInterface = null;
		}
	};
	
	protected ITransactionInterface mITransactionInterface;
    private ServiceConnection mITransactionInterfaceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mITransactionInterface = ITransactionInterface.Stub.asInterface(service);
			Log.i("Client", "Bind ITransactionInterface Success:" + mITransactionInterface.getClass().toString());
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mITransactionInterface = null;
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent mIApmpInterface = new Intent(IApmpInterface.class.getName());
        bindService(mIApmpInterface, mIApmpInterfaceConnection, BIND_AUTO_CREATE);
        
        Intent mIDevicesInterface = new Intent(IDevicesInterface.class.getName());
        bindService(mIDevicesInterface, mIDevicesInterfaceConnection, BIND_AUTO_CREATE);
        
        Intent mITransactionInterface = new Intent(ITransactionInterface.class.getName());
        bindService(mITransactionInterface, mITransactionInterfaceConnection, BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onDestroy() {
		unbindService(mIApmpInterfaceConnection);
		unbindService(mITransactionInterfaceConnection);
		unbindService(mIDevicesInterfaceConnection);
		super.onDestroy();
	}

}
