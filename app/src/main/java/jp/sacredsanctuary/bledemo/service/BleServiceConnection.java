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

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;

import java.util.UUID;

import jp.sacredsanctuary.bledemo.bluetooth.IBluetoothLowEnergyControllerCallback;
import jp.sacredsanctuary.bledemo.util.LogUtil;
import jp.sacredsanctuary.bledemo.util.Preconditions;

/**
 * Connection for controlling the BluetoothLeService.
 */
public class BleServiceConnection implements ServiceConnection {
    private static final String ClassName = BleServiceConnection.class.getSimpleName();
    private BluetoothLeService mBluetoothLeService;
    private final Handler mHandler;
    private final ServiceConnection mCallbackServiceConnection;

    // BLE API call after 10 millisecond.
    private static final long WAIT_TIME = 10;

    public BleServiceConnection(ServiceConnection callback) {
        mHandler = new Handler(Looper.getMainLooper());
        mCallbackServiceConnection = callback;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        LogUtil.V(ClassName, "onServiceConnected() [INF] name:" + name);
        mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
        if (!mBluetoothLeService.initialize()) {
            LogUtil.E(ClassName, "Unable to initialize Bluetooth");
        }
        mCallbackServiceConnection.onServiceConnected(name, service);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServiceDisconnected(ComponentName name) {
        LogUtil.V(ClassName, "onServiceDisconnected() [INF] name:" + name);
        mBluetoothLeService = null;
        mCallbackServiceConnection.onServiceDisconnected(name);
    }

    /**
     * Sets an instance of {@link IBluetoothLowEnergyControllerCallback} to receive events.
     *
     * @param callback The new callback or null, if the callback should be unregistered.
     */
    public void setCallback(IBluetoothLowEnergyControllerCallback callback) {
        if (Preconditions.checkNotNull(mBluetoothLeService)) {
            mBluetoothLeService.setCallback(callback);
        }
    }

    /**
     * Start Bluetooth LE scan.
     */
    public void scanBluetoothLowEnergyDevice(final long time) {
        mHandler.postDelayed(() -> {
            if (Preconditions.checkNotNull(mBluetoothLeService)) {
                mBluetoothLeService.scanBluetoothLowEnergyDevice(time);
            }
        }, WAIT_TIME);
    }

    /**
     * Initiate a connection to a Bluetooth GATT capable device.
     *
     * @param address The device address of the destination device.
     */
    public void connect(final String address) {
        mHandler.postDelayed(() -> {
            if (Preconditions.checkNotNull(mBluetoothLeService)) {
                mBluetoothLeService.connect(address);
            }
        }, WAIT_TIME);
    }

    /**
     * Disconnects an established connection, or cancels a connection attempt
     * currently in progress.
     */
    public void disconnect() {
        mHandler.postDelayed(() -> {
            if (Preconditions.checkNotNull(mBluetoothLeService)) {
                mBluetoothLeService.disconnect();
            }
        }, WAIT_TIME);
    }

    /**
     * Close this Bluetooth GATT client.
     *
     * Application should call this method as early as possible after it is done with
     * this GATT client.
     */
    public void close() {
        mHandler.postDelayed(() -> {
            if (Preconditions.checkNotNull(mBluetoothLeService)) {
                mBluetoothLeService.close();
            }
        }, WAIT_TIME);
    }

    /**
     * Discovers services offered by a remote device as well as their
     * characteristics and descriptors.
     */
    public void discoverServices() {
        mHandler.postDelayed(() -> {
            if (Preconditions.checkNotNull(mBluetoothLeService)) {
                mBluetoothLeService.discoverServices();
            }
        }, WAIT_TIME);
    }

    /**
     * Enable or disable notifications/indications for a given characteristic.
     */
    public void setCharacteristicNotification() {
        mHandler.postDelayed(() -> {
            if (Preconditions.checkNotNull(mBluetoothLeService)) {
                mBluetoothLeService.setCharacteristicNotification();
            }
        }, WAIT_TIME);
    }

    /**
     * Request an MTU size used for a given connection.
     */
    public void requestMtu(final int mtu) {
        mHandler.postDelayed(() -> {
            if (Preconditions.checkNotNull(mBluetoothLeService)) {
                mBluetoothLeService.requestMtu(mtu);
            }
        }, WAIT_TIME);
    }

    /**
     * Determines if bluetooth low energy is supported or not.
     *
     * @return Returns {@code true} if bluetooth low energy is supported, {@code false} otherwise.
     */
    public boolean isBluetoothLowEnergySupported() {
        if (Preconditions.checkNotNull(mBluetoothLeService)) {
            return mBluetoothLeService.isBluetoothLowEnergySupported();
        } else {
            LogUtil.I(ClassName, "isBluetoothLowEnergySupported() return:false");
            return false;
        }
    }

    /**
     * Return true if Bluetooth is currently enabled and ready for use.
     *
     * @return true if the local adapter is turned on
     */
    public boolean isEnabled() {
        if (Preconditions.checkNotNull(mBluetoothLeService)) {
            return mBluetoothLeService.isEnabled();
        } else {
            return false;
        }
    }

    /**
     * Return the remote bluetooth device this GATT client targets to
     *
     * @return remote bluetooth device
     */
    @Nullable
    public BluetoothDevice getDevice() {
        if (Preconditions.checkNotNull(mBluetoothLeService)) {
            return mBluetoothLeService.getDevice();
        } else {
            return null;
        }
    }

    /**
     * Writes a given characteristic and its values to the associated remote device.
     *
     * @param serviceUuid The UUID of the requested service
     * @param uuid The UUID of the requested descriptor
     * @param data Data to write to Bluetooth
     */
    public void writeCharacteristic(final UUID serviceUuid, final UUID uuid, final byte[] data) {
        mHandler.postDelayed(() -> {
            if (Preconditions.checkNotNull(mBluetoothLeService)) {
                mBluetoothLeService.writeCharacteristic(serviceUuid, uuid, data);
            }
        }, WAIT_TIME);
    }

    /**
     * Writes a given characteristic and its values to the associated remote device.
     *
     * @param serviceUuid The UUID of the requested service
     * @param uuid The UUID of the requested descriptor
     * @param data Data to write to Bluetooth
     */
    public void writeCharacteristic(final UUID serviceUuid, final UUID uuid, final String data) {
        mHandler.postDelayed(() -> {
            if (Preconditions.checkNotNull(mBluetoothLeService)) {
                mBluetoothLeService.writeCharacteristic(serviceUuid, uuid, data);
            }
        }, WAIT_TIME);
    }
}
