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
package jp.sacredsanctuary.bledemo.view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import jp.sacredsanctuary.bledemo.R;

/**
 * Provide a reference to the type of views that you are using (custom ViewHolder)
 */
public class ViewHolder extends RecyclerView.ViewHolder {
    private TextView mDeviceName;
    private TextView mDeviceHardwareAddress;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        this.mDeviceName = itemView.findViewById(R.id.device_name);
        this.mDeviceHardwareAddress = itemView.findViewById(R.id.device_hardware_address);
    }

    public TextView getDeviceName() {
        return this.mDeviceName;
    }

    public TextView geDeviceHardwareAddress() {
        return this.mDeviceHardwareAddress;
    }
}
