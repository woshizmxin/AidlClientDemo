package com.test.aidldemoclient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.test.aidldemo.IGreetBinder;
import com.test.aidldemo.IGreetCallback;

public class MainActivity extends Activity {

    private Button bindBtn;
    private Button greetBtn;
    private Button unbindBtn;

    private IGreetBinder iGreetBinder;
    private Handler handler = new Handler();


    private IGreetCallback mCallback = new IGreetCallback.Stub() {

        @Override
        public void greetBack(final String someone) throws RemoteException {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this,"greet back"+someone,Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("ServiceConnection", "onServiceConnected() called");
            iGreetBinder = IGreetBinder.Stub.asInterface(service);
            try {
                iGreetBinder.registerCallback(mCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //This is called when the connection with the service has been unexpectedly disconnected,
            //that is, its process crashed. Because it is running in our same process, we should never see this happen.
            Log.i("ServiceConnection", "onServiceDisconnected() called");
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindBtn = (Button) findViewById(R.id.bindBtn);
        bindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.AIDLService");
                bindService(intent, conn, Context.BIND_AUTO_CREATE);

                bindBtn.setEnabled(false);
                greetBtn.setEnabled(true);
                unbindBtn.setEnabled(true);
            }
        });

        greetBtn = (Button) findViewById(R.id.greetBtn);
        greetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String retVal = iGreetBinder.greet("scott");
                    Toast.makeText(MainActivity.this, retVal, Toast.LENGTH_SHORT).show();
                } catch (RemoteException e) {
                    Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        unbindBtn = (Button) findViewById(R.id.unbindBtn);
        unbindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unbindService(conn);

                bindBtn.setEnabled(true);
                greetBtn.setEnabled(false);
                unbindBtn.setEnabled(false);
            }
        });
    }
}