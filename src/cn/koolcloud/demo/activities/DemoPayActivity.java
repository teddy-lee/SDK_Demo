package cn.koolcloud.demo.activities;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cn.koolcloud.sdk.daemon.R;

import com.koolcloud.sdk.fmsc.service.IApmpCallBack;
import com.koolcloud.sdk.fmsc.service.IDevicesCallBack;
import com.koolcloud.sdk.fmsc.service.ITransactionCallBack;

public class DemoPayActivity extends BaseActivity implements
		View.OnClickListener {
	private final static String TAG = "DemoPayActivity";

	private final static int SHOW_COMMON_RESULT = 0;

	private Button btnLogin;
	private Button btnSignIn;
	private Button btnDownloadPayments;
	private Button btnBalanceQuery;
	private Button btnSuperTransfer;
	private Button btnGetPayments;
	private Button btnConsume;
	private Button btnLogout;
	private Button btnClearReversal;
	private Button btnStopTransaction;
	private TextView resultTextView;

	private final IApmpCallBack.Stub mIApmpCallBack = new IApmpCallBack.Stub() {

		@Override
		public void loginApmpCallBack(String loginResult)
				throws RemoteException {
			Log.e(TAG, "login result:" + loginResult);
			Message msg = mHandler.obtainMessage();
			msg.what = SHOW_COMMON_RESULT;
			msg.obj = loginResult;
			mHandler.sendMessage(msg);
		}

		@Override
		public void onDownloadPaymentParamsCallBack(String paymentObj)
				throws RemoteException {
			Message msg = mHandler.obtainMessage();
			msg.what = SHOW_COMMON_RESULT;
			msg.obj = paymentObj;
			mHandler.sendMessage(msg);
		}

		@Override
		public void logoutCallBack(String logoutResult) throws RemoteException {
			Message msg = mHandler.obtainMessage();
			msg.what = SHOW_COMMON_RESULT;
			msg.obj = logoutResult;
			mHandler.sendMessage(msg);
			
		}

	};

	private final ITransactionCallBack.Stub mTransactionCallBack = new ITransactionCallBack.Stub() {

		@Override
		public void signInCallBack(String signInResult) throws RemoteException {
			Log.e(TAG, "signIn result:" + signInResult);
			// TODO Auto-generated method stub
			Message msg = mHandler.obtainMessage();
			msg.what = SHOW_COMMON_RESULT;
			msg.obj = signInResult;
			mHandler.sendMessage(msg);
		}

		@Override
		public void onTransactionCallBack(String transactionResult)
				throws RemoteException {
			// TODO Auto-generated method stub

			mIDevicesInterface.onStopSwipeCard();
			Message msg = mHandler.obtainMessage();
			msg.what = SHOW_COMMON_RESULT;
			msg.obj = transactionResult;
			mHandler.sendMessage(msg);
		}

		@Override
		public void onClearReversalDataCallBack(boolean result)
				throws RemoteException {
			// TODO Auto-generated method stub
			
		}

	};

	private final IDevicesCallBack.Stub mDevicesCallBack = new IDevicesCallBack.Stub() {

		@Override
		public void swipeCardCallBack(boolean swipeCardResult)
				throws RemoteException {
			// TODO Auto-generated method stub
			/*
			 * Message msg = mHandler.obtainMessage(); msg.what =
			 * SHOW_COMMON_RESULT; msg.obj = swipeCardResult;
			 * mHandler.sendMessage(msg);
			 */
		}

		@Override
		public void onSwipeCardErrorCallBack(String errorResult)
				throws RemoteException {
			// TODO Auto-generated method stub
			Message msg = mHandler.obtainMessage();
			msg.what = SHOW_COMMON_RESULT;
			msg.obj = errorResult;
			mHandler.sendMessage(msg);
		}

		@Override
		public void onGetCardDataCallBack(String cardData)
				throws RemoteException {
			// TODO Auto-generated method stub
			Message msg = mHandler.obtainMessage();
			msg.what = SHOW_COMMON_RESULT;
			msg.obj = cardData;
			mHandler.sendMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		initViews();
		hiddenViews();
	}

	private void initViews() {

		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(this);
		btnSignIn = (Button) findViewById(R.id.btnSignIn);
		btnSignIn.setOnClickListener(this);

		btnDownloadPayments = (Button) findViewById(R.id.btnDownloadPayments);
		btnDownloadPayments.setOnClickListener(this);
		btnBalanceQuery = (Button) findViewById(R.id.btnBalanceQuery);
		btnBalanceQuery.setOnClickListener(this);
		btnSuperTransfer = (Button) findViewById(R.id.btnSuperTransfer);
		btnSuperTransfer.setOnClickListener(this);
		btnConsume = (Button) findViewById(R.id.btnConsume);
		btnConsume.setOnClickListener(this);
		btnLogout = (Button) findViewById(R.id.btnLogout);
		btnLogout.setOnClickListener(this);
		btnClearReversal = (Button) findViewById(R.id.btnClearReversal);
		btnClearReversal.setOnClickListener(this);
		btnStopTransaction = (Button) findViewById(R.id.btnStopTransaction);
		btnStopTransaction.setOnClickListener(this);

		resultTextView = (TextView) findViewById(R.id.resultText);

	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_COMMON_RESULT:
				try {
					String reslut = (String) msg.obj;
					if (!TextUtils.isEmpty(reslut)) {
						JSONObject resultObj = new JSONObject(reslut);
						
						if (!resultObj.isNull("show_dialog")) {
							boolean showDialog = resultObj.optBoolean("show_dialog");
							if (showDialog) {
								showDialog();
							} else {
								new StartPinPadThread(false).start();
							}
						}
						
						showCommonResult(reslut);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;

			default:
				break;
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View view) {
		hiddenViews();
		switch (view.getId()) {
		case R.id.btnLogin:
			new LoginApmpThread().start();
			break;
		case R.id.btnLogout:
			new LogoutApmpThread().start();
			break;
		case R.id.btnSignIn:
			new SignInPospThread().start();
			break;
		case R.id.btnConsume:
			showCommonResult(getResources().getString(
					R.string.msg_swipe_the_card_please));
			new ExeConsumeThread().start();
			break;
		case R.id.btnDownloadPayments:
			new DownloadPaymentsThread().start();
			break;
		case R.id.btnClearReversal:
			new ClearReversalDataThread().start();
			break;
		case R.id.btnStopTransaction:
			new StopTransactionThread().start();
			break;
		case R.id.btnBalanceQuery:
			showCommonResult(getResources().getString(
					R.string.msg_swipe_the_card_please));
			new BalanceQueryThread().start();
			break;
		case R.id.btnSuperTransfer:
			showCommonResult(getResources().getString(
					R.string.msg_swipe_the_card_please));
			new SuperTransferThread().start();
			break;
		/*
		 * case R.id.btnGetPayments: PaymentProvider
		 * getContentResolver().query(uri, projection, selection, selectionArgs,
		 * sortOrder) break;
		 */
		default:
			break;

		}
	}

	private void hiddenViews() {
		resultTextView.setText("");
	}

	private void showCommonResult(String msg) {
		resultTextView.setText(msg);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_clear:
			hiddenViews();
			break;

		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void showDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage(getResources().getString(R.string.msg_please_confirm_emv_card_or_not));

		builder.setTitle(getResources().getString(R.string.msg_confirm_emv_card));

		builder.setPositiveButton(getResources().getString(R.string.msg_button_yes), new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				new StartPinPadThread(true).start();
			}
		});

		builder.setNegativeButton(getResources().getString(R.string.msg_button_no), new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				new StartPinPadThread(false).start();
			}
		});

		builder.create().show();
	}

	// 金额单位转换 分转为元
	private String formatAmountStr(String amount) {
		String floatAmount = null;
		int length = amount.length();
		if (length == 0) {
			floatAmount = "";
		} else if (length == 1) {
			floatAmount = "0.0" + amount;
		} else if (length == 2) {
			floatAmount = "0." + amount;
		} else {
			floatAmount = amount.substring(0, length - 2) + "."
					+ amount.substring(length - 2, length);
		}
		return floatAmount;
	}

	class LoginApmpThread extends Thread {

		@Override
		public void run() {
			try {
				mIApmpInterface.loginApmp("999290053110041", "teddy", "123456",
						mIApmpCallBack);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	class LogoutApmpThread extends Thread {
		
		@Override
		public void run() {
			try {
				mIApmpInterface.logoutApmp(mIApmpCallBack);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	class DownloadPaymentsThread extends Thread {

		@Override
		public void run() {
			try {
				mIApmpInterface.downloadPaymentParams("999290053110041",
						mIApmpCallBack);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

	}
	
	class ClearReversalDataThread extends Thread {
		
		@Override
		public void run() {
			try {
				mITransactionInterface.clearReversalData(mTransactionCallBack);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	class StopTransactionThread extends Thread {
		
		@Override
		public void run() {
			try {
				mIDevicesInterface.stopTransaction();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
	}

	class SignInPospThread extends Thread {

		@Override
		public void run() {
			try {
				mITransactionInterface.signIn("0000000053",
						mTransactionCallBack);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

	}

	class BalanceQueryThread extends Thread {

		@Override
		public void run() {
			try {
				UUID uuid = UUID.randomUUID();
				mIDevicesInterface.onStartSwipeCard("0000000009", "", "1041",
						"", "", uuid.toString(), mDevicesCallBack, mTransactionCallBack);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	class SuperTransferThread extends Thread {

		@Override
		public void run() {
			try {
				UUID uuid = UUID.randomUUID();
				mIDevicesInterface.onStartSwipeCard("0000000025", "0.01",
						"1721", "123456", "6214180100000324856", uuid.toString(),
						mDevicesCallBack, mTransactionCallBack);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

	}

	class StartPinPadThread extends Thread {
		boolean isEMVCard;

		public StartPinPadThread(boolean isEMVCard) {
			this.isEMVCard = isEMVCard;
		}

		@Override
		public void run() {
			try {
				mIDevicesInterface.startPinPad(isEMVCard);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	class ExeConsumeThread extends Thread {

		@Override
		public void run() {
			try {
				UUID uuid = UUID.randomUUID();
				mIDevicesInterface.onStartSwipeCard("0000000053", "0.01", "1021",
						"", "", uuid.toString(), mDevicesCallBack, mTransactionCallBack);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
}
