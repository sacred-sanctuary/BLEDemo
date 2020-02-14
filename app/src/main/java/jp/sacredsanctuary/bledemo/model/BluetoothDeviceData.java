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
package jp.sacredsanctuary.bledemo.model;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

/**
 * This class is used to hold the BluetoothDevice.
 */
public class BluetoothDeviceData {
    private BluetoothDevice mBluetoothDevice;

    public BluetoothDeviceData(@NonNull BluetoothDevice bluetoothDevice) {
        this.mBluetoothDevice = bluetoothDevice;
    }

    /**
     * Gets the BluetoothDevice with the given {@code android.bluetooth.BluetoothDevice}.
     *
     * @return Returns the BluetoothDevice.
     */
    @NonNull
    public BluetoothDevice getBluetoothDevice() {
        return this.mBluetoothDevice;
    }
}