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
package jp.sacredsanctuary.bledemo.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;

import jp.sacredsanctuary.bledemo.util.LogUtil;
import jp.sacredsanctuary.bledemo.util.Preconditions;

/**
 * Callback for GATT Writing
 */
public class BleGattCallback extends BluetoothGattCallback {
    private static final String ClassName = BleGattCallback.class.getSimpleName();
    private IBluetoothLowEnergyControllerCallback mCallback;

    /**
     * Sets an instance of {@link IBluetoothLowEnergyControllerCallback} to receive events.
     *
     * @param callback The new callback or null, if the callback should be unregistered.
     */
    void setCallback(IBluetoothLowEnergyControllerCallback callback) {
        mCallback = callback;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        LogUtil.V(ClassName,
                "onConnectionStateChange() [INF] status:" + status + " newState:" + newState);
        if (Preconditions.checkNotNull(mCallback)) {
            mCallback.onConnectionStateChange(gatt, status, newState);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        LogUtil.V(ClassName, "onServicesDiscovered() [INF] status:" + status);
        if (Preconditions.checkNotNull(mCallback)) {
            mCallback.onServicesDiscovered(gatt, status);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        LogUtil.V(ClassName, "onMtuChanged() [INF] mtu=" + mtu + " status=" + status);
        if (Preconditions.checkNotNull(mCallback)) {
            mCallback.onMtuChanged(gatt, mtu, status);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCharacteristicRead(BluetoothGatt gatt,
            BluetoothGattCharacteristic characteristic,
            int status) {
        LogUtil.I(ClassName, "onServicesDiscovered() [INF] status:" + status);
        if (Preconditions.checkNotNull(mCallback)) {
            mCallback.onCharacteristicRead(gatt, characteristic, status);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt,
            BluetoothGattCharacteristic characteristic,
            int status) {
        LogUtil.I(ClassName, "onCharacteristicWrite() [INF] status:" + status);
        if (Preconditions.checkNotNull(mCallback)) {
            mCallback.onCharacteristicWrite(gatt, characteristic, status);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt,
            BluetoothGattCharacteristic characteristic) {
        if (Preconditions.checkNotNull(mCallback)) {
            mCallback.onCharacteristicChanged(gatt, characteristic);
        }
    }
}
