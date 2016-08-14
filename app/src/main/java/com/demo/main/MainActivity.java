package com.demo.main;

import com.jzj.socket.SocketTransceiver;
import com.jzj.socket.TcpClient;
import com.jzj.socket.TcpServer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private Button bnConnect;
	private TextView txReceive;
	private EditText edIP, edPort, edData;

	private Handler handler = new Handler(Looper.getMainLooper());
//change here

	//int port = 1234;
	TcpServer server = new TcpServer(1234) {
		@Override
		public void onConnect(SocketTransceiver client) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(MainActivity.this, "onConnectServer",
							Toast.LENGTH_SHORT).show();
				}
			});
		}

		@Override
		public void onConnectFailed() {
			handler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(MainActivity.this, "连接失败onConnectFailed",
							Toast.LENGTH_SHORT).show();
				}
			});
		}

		@Override
		public void onReceive(SocketTransceiver client,final String s) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					txReceive.append(s);
				}
			});
		}

		@Override
		public void onDisconnect(SocketTransceiver client) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(MainActivity.this, "连接onDisconnect",
							Toast.LENGTH_SHORT).show();
				}
			});
		}

		@Override
		public void onServerStop() {
			handler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(MainActivity.this, "连接onServerStop",
							Toast.LENGTH_SHORT).show();
				}
			});
		}

		//HWWW
		public void onReceive_byte(SocketTransceiver client, final byte[] bytes){
			handler.post(new Runnable() {
				@Override
				public void run() {
					txReceive.append(bytes.toString());
				}
			});
		}
	};

/*	private TcpClient client = new TcpClient() {

		@Override
		public void onConnect(SocketTransceiver transceiver) {
			refreshUI(true);
		}

		@Override
		public void onDisconnect(SocketTransceiver transceiver) {
			refreshUI(false);
		}

		@Override
		public void onConnectFailed() {
			handler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(MainActivity.this, "连接失败",
							Toast.LENGTH_SHORT).show();
				}
			});
		}

		@Override
		public void onReceive(SocketTransceiver transceiver, final String s) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					txReceive.append(s);
				}
			});
		}
	};*/

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.findViewById(R.id.bn_send).setOnClickListener(this);
		bnConnect = (Button) this.findViewById(R.id.bn_connect);
		bnConnect.setOnClickListener(this);

		edIP = (EditText) this.findViewById(R.id.ed_ip);
		edPort = (EditText) this.findViewById(R.id.ed_port);
		edData = (EditText) this.findViewById(R.id.ed_dat);
		txReceive = (TextView) this.findViewById(R.id.tx_receive);
		txReceive.setOnClickListener(this);

		refreshUI(false);

		//change here


	}

	@Override
	public void onStop() {

		//change here
		server.stop();

		//client.disconnect();
		super.onStop();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bn_connect:
			connect();

			break;
		case R.id.bn_send:
			sendStr();
			break;
		case R.id.tx_receive:
			clear();
			break;
		}
	}

	/**
	 * 刷新界面显示
	 * 
	 * @param isConnected
	 */
	private void refreshUI(final boolean isConnected) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				edPort.setEnabled(!isConnected);
				edIP.setEnabled(!isConnected);
				bnConnect.setText(isConnected ? "断开" : "连接");
			}
		});
	}

	/**
	 * 设置IP和端口地址,连接或断开
	 */
	private void connect() {
		Log.d("MainActivity","connect");
		server.start();
		Toast.makeText(MainActivity.this, "server.start",
				Toast.LENGTH_SHORT).show();
		/*
		if (client.isConnected()) {
			// 断开连接
			client.disconnect();
		} else {
			try {
				String hostIP = edIP.getText().toString();
				int port = Integer.parseInt(edPort.getText().toString());
				client.connect(hostIP, port);
			} catch (NumberFormatException e) {
				Toast.makeText(this, "端口错误", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		}*/
	}

	/**
	 * 发送数据
	 */
	private void sendStr() {
		try {
			String data = edData.getText().toString();
			//client.getTransceiver().send(data);

			//change here
			try {
				for(SocketTransceiver client : server.getClients()){
					client.send(data);
				}
			} catch (Exception e){
				Toast.makeText(this, "Server端口send错误", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 清空接收框
	 */
	private void clear() {
		new AlertDialog.Builder(this).setTitle("确认清除?")
				.setNegativeButton("取消", null)
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						txReceive.setText("");
					}
				}).show();
	}
}
