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
package jp.sacredsanctuary.bledemo.service;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.UUID;

import jp.sacredsanctuary.bledemo.R;
import jp.sacredsanctuary.bledemo.bluetooth.BluetoothLowEnergyController;
import jp.sacredsanctuary.bledemo.bluetooth.IBluetoothLowEnergyControllerCallback;
import jp.sacredsanctuary.bledemo.util.LogUtil;
import jp.sacredsanctuary.bledemo.util.Preconditions;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {
    private final static String ClassName = BluetoothLeService.class.getSimpleName();

    private BluetoothLowEnergyController mBleController;

    class LocalBinder extends Binder {
        BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Sets an instance of {@link IBluetoothLowEnergyControllerCallback} to receive events.
     *
     * @param callback The new callback or null, if the callback should be unregistered.
     */
    public void setCallback(IBluetoothLowEnergyControllerCallback callback) {
        if (Preconditions.checkNotNull(mBleController)) {
            mBleController.setCallback(callback);
        }
    }

    /**
     * Initializes a reference to the local Bluetooth controller .
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBleController == null) {
            mBleController = new BluetoothLowEnergyController(getApplicationContext());
        }

        if (!mBleController.isBluetoothSupported()
                || !mBleController.isBluetoothLowEnergySupported()) {
            LogUtil.E(ClassName, "Unable to initialize BluetoothManager.");
            Toast.makeText(getApplicationContext(),
                    R.string.bluetooth_not_support, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int,
     * int)}
     * callback.
     */
    public boolean connect(String address) {
        if (!Preconditions.checkNotNull(mBleController) || Preconditions.isEmpty(mBleController)) {
            LogUtil.W(ClassName, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        return mBleController.connect(address);
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int,
     * int)}
     * callback.
     */
    public void disconnect() {
        if (!Preconditions.checkNotNull(mBleController)) {
            LogUtil.W(ClassName, "disconnect() [WAN] BluetoothAdapter not initialized");
            return;
        }
        mBleController.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (!Preconditions.checkNotNull(mBleController)) {
            LogUtil.W(ClassName, "close() [WAN] BluetoothAdapter not initialized");
            return;
        }
        mBleController.close();
        mBleController = null;
    }

    /**
     * Discovers services offered by a remote device as well as their
     * characteristics and descriptors.
     */
    public void discoverServices() {
        if (!Preconditions.checkNotNull(mBleController)) {
            LogUtil.W(ClassName, "discoverServices() [WAN] BluetoothAdapter not initialized");
            return;
        }
        mBleController.discoverServices();
    }

    /**
     * Enable or disable notifications/indications for a given characteristic.
     */
    public void setCharacteristicNotification() {
        if (!Preconditions.checkNotNull(mBleController)) {
            LogUtil.W(ClassName, "discoverServices() [WAN] BluetoothAdapter not initialized");
            return;
        }
        mBleController.setCharacteristicNotification();
    }

    /**
     * Request an MTU size used for a given connection.
     *
     * @param mtu The new MTU size to request
     */
    public void requestMtu(int mtu) {
        if (!Preconditions.checkNotNull(mBleController)) {
            LogUtil.W(ClassName, "discoverServices() [WAN] BluetoothAdapter not initialized");
            return;
        }
        mBleController.requestMtu(mtu);
    }

    /**
     * Determines if bluetooth low energy is supported or not.
     *
     * @return Returns {@code true} if bluetooth low energy is supported, {@code false} otherwise.
     */
    public boolean isBluetoothLowEnergySupported() {
        if (!Preconditions.checkNotNull(mBleController)) {
            LogUtil.W(ClassName, "isBluetoothLowEnergySupported() [WAN] BluetoothAdapter not initialized");
            return false;
        }
        LogUtil.I(ClassName, "isBluetoothLowEnergySupported() isBluetoothLowEnergySupported:" + mBleController.isBluetoothLowEnergySupported());
        return mBleController.isBluetoothLowEnergySupported();
    }

    /**
     * Return true if Bluetooth is currently enabled and ready for use.
     *
     * @return true if the local adapter is turned on
     */
    public boolean isEnabled() {
        if (!Preconditions.checkNotNull(mBleController)) {
            LogUtil.W(ClassName, "isEnabled() [WAN] BluetoothAdapter not initialized");
            return false;
        }
        return mBleController.isEnabled();
    }

    /**
     * Return the remote bluetooth device this GATT client targets to
     *
     * @return remote bluetooth device
     */
    @Nullable
    public BluetoothDevice getDevice() {
        if (!Preconditions.checkNotNull(mBleController)) {
            LogUtil.W(ClassName, "getDevice() [WAN] BluetoothAdapter not initialized");
            return null;
        }
        return mBleController.getDevice();
    }

    /**
     * Start Bluetooth LE scan.
     */
    public void scanBluetoothLowEnergyDevice(long time) {
        if (!Preconditions.checkNotNull(mBleController)) {
            LogUtil.W(ClassName,
                    "scanBluetoothLowEnergyDevice() [WAN] BluetoothAdapter not initialized");
            return;
        }
        mBleController.scanBluetoothLowEnergyDevice(time);
    }

    /**
     * Writes a given characteristic and its values to the associated remote device.
     *
     * @param serviceUuid The UUID of the requested service
     * @param uuid The UUID of the requested descriptor
     * @param data Data to write to Bluetooth.
     */
    public void writeCharacteristic(UUID serviceUuid, UUID uuid, byte[] data) {
        if (!Preconditions.checkNotNull(mBleController)) {
            LogUtil.W(ClassName, "writeCharacteristic() [WAN] BluetoothAdapter not initialized");
            return;
        }
        mBleController.writeCharacteristic(serviceUuid, uuid, data);
    }

    /**
     * Writes a given characteristic and its values to the associated remote device.
     *
     * @param serviceUuid The UUID of the requested service
     * @param uuid The UUID of the requested descriptor
     * @param data Data to write to Bluetooth.
     */
    public void writeCharacteristic(UUID serviceUuid, UUID uuid, String data) {
        if (!Preconditions.checkNotNull(mBleController)) {
            LogUtil.W(ClassName, "writeCharacteristic() [WAN] BluetoothAdapter not initialized");
            return;
        }
        mBleController.writeCharacteristic(serviceUuid, uuid, data);
    }
}
