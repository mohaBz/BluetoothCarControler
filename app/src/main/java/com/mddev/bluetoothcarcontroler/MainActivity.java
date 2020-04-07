package com.mddev.bluetoothcarcontroler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    //Bluetooth
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";
    //RecyclerView
    private RecyclerView mDeviceViewList;
    private DeviceListAdapter deviceListAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Button selectBt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //if the device has bluetooth
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if(myBluetooth == null)
        {
            //Show a message. that the device has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();

            //finish apk
            finish();
        }
        else if(!myBluetooth.isEnabled())
        {
            //Ask to the user turn the bluetooth on
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon,1);
        }
        //Initiate the recyclerView And the adapter
        mDeviceViewList=findViewById(R.id.rc_devicesList);
        selectBt=findViewById(R.id.bt_devices);
        layoutManager=new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setOrientation(LinearLayoutManager.VERTICAL);
        mDeviceViewList.setLayoutManager(layoutManager);
        mDeviceViewList.setHasFixedSize(true);
        deviceListAdapter=new DeviceListAdapter(this);
        deviceListAdapter.setDeviceList(pairedDevicesList());
        mDeviceViewList.setAdapter(deviceListAdapter);
        selectBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open new Activity which is the controllers activity
                Intent controlerIntent =new Intent(MainActivity.this,ControlActivity.class);
                controlerIntent.putExtra(EXTRA_ADDRESS,deviceListAdapter.getSelectedDevice().getAddress());
                startActivity(controlerIntent);
            }
        });

    }
    //gets all the paired Bluetooth devices
    private List<BluetoothDeviceItem> pairedDevicesList()
    {
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList<BluetoothDeviceItem> list = new ArrayList();

        if (pairedDevices.size()>0)
        {
            for(BluetoothDevice bt : pairedDevices)
            {
               BluetoothDeviceItem bluetoothDevice=new BluetoothDeviceItem();
               bluetoothDevice.setName(bt.getName());
               bluetoothDevice.setAddress(bt.getAddress());
               list.add(bluetoothDevice);
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }
        return list;
    }

}
