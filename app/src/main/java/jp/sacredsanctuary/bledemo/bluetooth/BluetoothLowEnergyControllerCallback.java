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
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanResult;
import android.content.Context;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import jp.sacredsanctuary.bledemo.util.LogUtil;
import jp.sacredsanctuary.bledemo.util.Preconditions;
import jp.sacredsanctuary.bledemo.view.MainActivity;
import jp.sacredsanctuary.bledemo.viewModel.BluetoothDeviceDataViewModel;
import jp.sacredsanctuary.bledemo.viewModel.BluetoothDeviceListViewModel;

public class BluetoothLowEnergyControllerCallback implements IBluetoothLowEnergyControllerCallback {
    private static final String ClassName =
            BluetoothLowEnergyControllerCallback.class.getSimpleName();
    private static final int MAX_MTU_SIZE = 512;

    private final Context mContext;

    /**
     * Create a new BluetoothLowEnergyControllerCallback.
     *
     * @param context A context of the current app
     */
    public BluetoothLowEnergyControllerCallback(Context context) {
        mContext = context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        LogUtil.I(ClassName,
                "onConnectionStateChange() [INF] status:" + status + " newState:" + newState);
        if (BluetoothProfile.STATE_CONNECTED == newState) {
            LogUtil.I(ClassName, "Connected to GATT server.");
            ((MainActivity) mContext).getBleServiceConnection().discoverServices();
        } else if (BluetoothProfile.STATE_DISCONNECTED == newState) {
            LogUtil.I(ClassName, "Disconnected from GATT server.");
            ((MainActivity) mContext).getBleServiceConnection().disconnect();
            ((MainActivity) mContext).onConnectionFailed();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        LogUtil.I(ClassName, "onServicesDiscovered() [INF] status:" + status);
        if (BluetoothGatt.GATT_SUCCESS == status) {
            ((MainActivity) mContext).getBleServiceConnection().setCharacteristicNotification();
            ((MainActivity) mContext).getBleServiceConnection().requestMtu(MAX_MTU_SIZE);
        } else {
            ((MainActivity) mContext).getBleServiceConnection().close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        LogUtil.I(ClassName, "onMtuChanged() [INF] mtu=" + mtu + " status=" + status);
        if ((BluetoothGatt.GATT_SUCCESS == status) && (MAX_MTU_SIZE == mtu)) {
            ((MainActivity) mContext).onConnectionCompleted();
            setBluetoothDevice();
        } else {
            ((MainActivity) mContext).onConnectionFailed();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCharacteristicRead(BluetoothGatt gatt,
            BluetoothGattCharacteristic characteristic,
            int status) {
        if (characteristic.getValue().length > 32) {
            LogUtil.I(ClassName,
                    "onCharacteristicRead() [INF] status=" + status
                            + " uid=" + characteristic.getUuid()
                            + " val.length=" + characteristic.getValue().length);
        } else {
            LogUtil.I(ClassName,
                    "onCharacteristicRead() [INF] status=" + status
                            + " uid=" + characteristic.getUuid()
                            + " val=" + Arrays.toString(characteristic.getValue()));
        }
        if (status != BluetoothGatt.GATT_SUCCESS) {
            LogUtil.E(ClassName, "Read characteristic failure on " + gatt + " " + characteristic);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt,
            BluetoothGattCharacteristic characteristic,
            int status) {
        if (characteristic.getValue().length > 32) {
            LogUtil.I(ClassName,
                    "onCharacteristicWrite() [INF] status=" + status
                            + " uid=" + characteristic.getUuid()
                            + " val.length=" + characteristic.getValue().length);
        } else {
            LogUtil.I(ClassName,
                    "onCharacteristicWrite() [INF] status=" + status
                            + " uid=" + characteristic.getUuid()
                            + " val=" + Arrays.toString(characteristic.getValue()));
        }
        if (status != BluetoothGatt.GATT_SUCCESS) {
            LogUtil.E(ClassName, "Write characteristic failure on " + gatt + " " + characteristic);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt,
            BluetoothGattCharacteristic characteristic) {
        UUID uuid = characteristic.getUuid();
        LogUtil.I(ClassName, "onCharacteristicChanged() [INF] uuid:" + uuid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScanCompleted(Set<ScanResult> results) {
        LogUtil.V(ClassName, "onScanCompleted() [INF] results:" + results);
        BluetoothDeviceListViewModel model =
                new ViewModelProvider((FragmentActivity) mContext).get(BluetoothDeviceListViewModel.class);
        model.setBluetoothDeviceDataList(results);
        if (Preconditions.isEmpty(results)) ((MainActivity) mContext).onScanCompleted();
    }

    private void setBluetoothDevice() {
        LogUtil.V(ClassName, "setBluetoothDevice() [INF] ");
        BluetoothDeviceDataViewModel model =
                new ViewModelProvider((FragmentActivity) mContext).get(BluetoothDeviceDataViewModel.class);
        model.setBluetoothDevice(((MainActivity) mContext).getBleServiceConnection().getDevice());
    }
}
