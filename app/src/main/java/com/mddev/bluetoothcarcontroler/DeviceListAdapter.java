package com.mddev.bluetoothcarcontroler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DeviceListAdapter  extends RecyclerView.Adapter<DeviceListAdapter.deviceItemViewHolder> {
private List<BluetoothDeviceItem> deviceList;
private int SelectedItem=-1;
    private Context mcontext;
    public DeviceListAdapter(Context context){
        this.mcontext=context;
    }
    public void setDeviceList(List<BluetoothDeviceItem> deviceList) {
        this.deviceList = deviceList;
    }

    @NonNull
    @Override
    public deviceItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(mcontext).inflate(R.layout.device_item,parent,false);

        return new deviceItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull deviceItemViewHolder holder, int position) {
               BluetoothDeviceItem item=deviceList.get(position);
               holder.onBind(item);
    }

    @Override
    public int getItemCount() {
        if (deviceList == null) {
            return 0;
        }
        return deviceList.size();
    }

    public class deviceItemViewHolder extends  RecyclerView.ViewHolder{
       TextView deviceNameTx;
       ImageView selectImg;
       public deviceItemViewHolder(@NonNull View itemView) {
           super(itemView);
           deviceNameTx=itemView.findViewById(R.id.tx_deviceName);
           selectImg=itemView.findViewById(R.id.im_select);
       }
        public void onBind(BluetoothDeviceItem item){
                if (SelectedItem == -1) {
                    selectImg.setVisibility(View.GONE);
                } else {
                    if (SelectedItem == getAdapterPosition()) {
                        selectImg.setVisibility(View.VISIBLE);
                    } else {
                        selectImg.setVisibility(View.GONE);
                    }
                }
                deviceNameTx.setText(item.getName());
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectImg.setVisibility(View.VISIBLE);
                        if (SelectedItem != getAdapterPosition()) {
                            notifyItemChanged(SelectedItem);
                            SelectedItem = getAdapterPosition();
                        }
                    }
                });
            }
   }

   public BluetoothDeviceItem getSelectedDevice(){
       if (SelectedItem!=-1)
        return deviceList.get(SelectedItem);
        return null;
    }
}

