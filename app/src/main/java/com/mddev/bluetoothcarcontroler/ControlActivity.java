package com.mddev.bluetoothcarcontroler;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class ControlActivity extends AppCompatActivity {
private Button goLeftBt;
    private Button goRightBt;
    private Button goForwardBt;
    private Button goBackwardBt;
    TextView debuTx;
    // the device address which is bluetooth module connected to ardueno
    private String carAddress;
    private ProgressDialog progress;
    private BluetoothAdapter myBluetooth=null;
    private BluetoothSocket btSocket;
    private boolean isCarConnected = false;
    private String LEFT="L";
    private String RIGHT="R";
    private String FORWARD="F";
    private String BACKWARD="B";
    //the unique  id for the socket
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        carAddress=getIntent().getExtras().getString(MainActivity.EXTRA_ADDRESS);
        goBackwardBt=findViewById(R.id.bt_backward);
        goForwardBt=findViewById(R.id.bt_forward);
        goRightBt=findViewById(R.id.bt_right);
        goLeftBt=findViewById(R.id.bt_left);
        debuTx=findViewById(R.id.tx_debug);
        new ConnectBT().execute();
        final writeToBluetooth writeThread =new writeToBluetooth(btSocket,"".getBytes());
        goLeftBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new writeToBluetooth(btSocket,LEFT.getBytes()).execute();
            }
        });
        goRightBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new writeToBluetooth(btSocket,RIGHT.getBytes()).execute();
            }
        });
        goForwardBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new writeToBluetooth(btSocket,FORWARD.getBytes()).execute();
            }
        });
        goBackwardBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new writeToBluetooth(btSocket,BACKWARD.getBytes()).execute();
            }
        });
    }





// to get  the connection to the device it need to be in different thread then the main thread UI
    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(ControlActivity.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isCarConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(carAddress);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                //finish();
            }
            else
            {
                msg("Connected.");
                isCarConnected = true;
            }
            progress.dismiss();
        }
    }
    // to get  the OutputStream and write to the device it need to be in different thread then the main thread UI
    private class writeToBluetooth extends AsyncTask<Void,Void,Void>{
        private BluetoothSocket socket;
        private final OutputStream mmOutStream;

        public void setBytes(byte[] bytes) {
            this.bytes = bytes;
        }

        private byte[] bytes;
        public writeToBluetooth(BluetoothSocket socket,byte[] bytes){
            this.socket=socket;
            this.bytes=bytes;
            OutputStream tmpOut = null;
            if (socket!=null){
                // Get the output streams; using temp objects because
                // member streams are final.
                try {
                    tmpOut = socket.getOutputStream();
                } catch (IOException e) {
                    Log.e("getOutPutStream", "Error occurred when creating output stream", e);
                }

            }

    mmOutStream = tmpOut;
}
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(mmOutStream!=null){
                try {
                    mmOutStream.write(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("writeBytesError", "Error occurred when sending data", e);
                }

            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    debuTx.setText(String.valueOf(new String(bytes)));
                }
            });
            return null;
        }
    }
    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }
}
