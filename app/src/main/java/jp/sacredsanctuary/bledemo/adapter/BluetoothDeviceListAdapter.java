/*
 * Copyright (C) 2020 Sacred Sanctuary Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.sacredsanctuary.bledemo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import jp.sacredsanctuary.bledemo.R;
import jp.sacredsanctuary.bledemo.model.BluetoothDeviceData;
import jp.sacredsanctuary.bledemo.view.ViewHolder;

/**
 * Adapter for a GridView containing image items from the Image data of the device.
 */
public class BluetoothDeviceListAdapter extends RecyclerView.Adapter<ViewHolder> {
    private static final String ClassName = BluetoothDeviceListAdapter.class.getSimpleName();
    private List<BluetoothDeviceData> mDeviceDataList;

    /**
     * Create a new BluetoothDeviceListAdapter.
     */
    public BluetoothDeviceListAdapter() {
        this.mDeviceDataList = new ArrayList<>();
    }

    public void setItems(List<BluetoothDeviceData> deviceDataList) {
        mDeviceDataList.clear();
        mDeviceDataList.addAll(deviceDataList);
        notifyItemRangeInserted(0, getItemCount());
    }

    public void clearItems() {
        mDeviceDataList.clear();
    }

    public List<BluetoothDeviceData> getAllItem() {
        return mDeviceDataList;
    }

    protected void onItemClicked(@NonNull BluetoothDeviceData data) {
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View inflate = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(inflate);
        inflate.setOnClickListener(v -> {
            final int position = holder.getAdapterPosition();
            onItemClicked(mDeviceDataList.get(position));
        });
        return holder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getDeviceName().setText(mDeviceDataList.get(
                position).getBluetoothDevice().getName());
        holder.geDeviceHardwareAddress().setText(mDeviceDataList.get(
                position).getBluetoothDevice().getAddress());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemCount() {
        return (mDeviceDataList != null) ? mDeviceDataList.size() : 0;
    }
}
