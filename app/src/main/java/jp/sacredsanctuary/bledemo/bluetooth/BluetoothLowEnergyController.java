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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jp.sacredsanctuary.bledemo.util.LogUtil;
import jp.sacredsanctuary.bledemo.util.Preconditions;

/**
 * Controller used to operation Bluetooth Low Energy.
 */
public class BluetoothLowEnergyController {
    private static final String ClassName = BluetoothLowEnergyController.class.getSimpleName();
    // Descriptor UUID for enabling characteristic changed notifications
    private static final UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString(
            "00002902-0000-1000-8000-00805f9b34fb");
    private final Context mContext;
    private final BluetoothAdapter mBluetoothAdapter;
    private final BluetoothLeScanner mBluetoothScanner;
    private final Handler mBackgroundHandler;
    private final BleScanCallback mBleScanCallback;
    private boolean mScanning = false;
    private BluetoothGatt mBluetoothGatt;
    private String mBluetoothDeviceAddress = null;
    private IBluetoothLowEnergyControllerCallback mCallback;

    private BleGattCallback mGattCallback = new BleGattCallback();

    /**
     * Create a new BluetoothLowEnergyController.
     *
     * @param context A context of the current app
     */
    public BluetoothLowEnergyController(Context context) {
        this.mContext = context;
        this.mBackgroundHandler = new Handler();
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mBluetoothScanner = Preconditions.checkNotNull(mBluetoothAdapter)
                ? mBluetoothAdapter.getBluetoothLeScanner() : null;
        this.mBleScanCallback = new BleScanCallback();
    }

    /**
     * Sets an instance of {@link IBluetoothLowEnergyControllerCallback} to receive events.
     *
     * @param callback The new callback or null, if the callback should be unregistered.
     */
    public void setCallback(IBluetoothLowEnergyControllerCallback callback) {
        mCallback = callback;
        mGattCallback.setCallback(callback);
    }

    /**
     * Determines if bluetooth low energy is supported or not.
     *
     * @return Returns {@code true} if bluetooth low energy is supported, {@code false} otherwise.
     */
    public boolean isBluetoothLowEnergySupported() {
        return isBluetoothSupported()
                && mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /**
     * Determines if bluetooth is supported or not.
     *
     * @return Returns {@code true} if bluetooth is supported, {@code false} otherwise.
     */
    public boolean isBluetoothSupported() {
        return Preconditions.checkNotNull(mBluetoothAdapter);
    }

    /**
     * Determines if bluetooth had enabled or not.
     *
     * @return Returns {@code true} if bluetooth had enabled, {@code false} otherwise.
     */
    public boolean isEnabled() {
        return isBluetoothSupported() && mBluetoothAdapter.isEnabled();
    }

    /**
     * Initiate a connection to a Bluetooth GATT capable device.
     *
     * @param address Bluetooth address as string
     * @return true, if the connection attempt was initiated successfully
     */
    public boolean connect(String address) {
        LogUtil.V(ClassName, "connect() [INF] address:" + address);
        if (!Preconditions.checkNotNull(mBluetoothAdapter) || Preconditions.isEmpty(address)) {
            LogUtil.W(ClassName, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        // Previously connected device.  Try to reconnect.
        if (!Preconditions.isEmpty(mBluetoothDeviceAddress)
                && address.equals(mBluetoothDeviceAddress)
                && Preconditions.checkNotNull(mBluetoothGatt)) {
            LogUtil.D(ClassName, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (!Preconditions.checkNotNull(device)) {
            LogUtil.W(ClassName, "Device not found.  Unable to connect.");
            return false;
        }
        LogUtil.V(ClassName, "connect() [INF] device:" + device);
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback,
                BluetoothDevice.TRANSPORT_LE);
        LogUtil.W(ClassName, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        return true;
    }

    /**
     * Disconnects an established connection, or cancels a connection attempt
     * currently in progress.
     */
    public void disconnect() {
        if (!Preconditions.checkNotNull(mBluetoothAdapter)
                || !Preconditions.checkNotNull(mBluetoothGatt)) {
            LogUtil.W(ClassName, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (!Preconditions.checkNotNull(mBluetoothGatt)) {
            LogUtil.W(ClassName, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Return the remote bluetooth device this GATT client targets to
     *
     * @return remote bluetooth device
     */
    @Nullable
    public BluetoothDevice getDevice() {
        if (!Preconditions.checkNotNull(mBluetoothGatt)) {
            LogUtil.W(ClassName, "BluetoothAdapter not initialized");
            return null;
        }
        return mBluetoothGatt.getDevice();
    }

    /**
     * Start Bluetooth LE scan. The scan results will be delivered through {@code mBleScanCallback}.
     * For unfiltered scans, scanning is stopped on screen off to save power. Scanning is
     * resumed when screen is turned on again. To avoid this, do filetered scanning by
     * using proper {@link android.bluetooth.le.ScanFilter}.
     */
    public void scanBluetoothLowEnergyDevice(final long time) {
        LogUtil.V(ClassName, "scanBluetoothLowEnergyDevice() [INF] time:" + time);
        if (mScanning) return;
        if (!Preconditions.checkNotNull(mBluetoothScanner)) return;

        // Stops scanning after a pre-defined scan period.
        mBackgroundHandler.postDelayed(() -> {
            mScanning = false;
            LogUtil.V(ClassName, "scanLeDevice() [INF] call stopScan() ");
            mBluetoothScanner.stopScan(mBleScanCallback);
            if (Preconditions.checkNotNull(mCallback)) {
                mCallback.onScanCompleted(mBleScanCallback.getScanResults());
            }
        }, time);

        mScanning = true;
        mBleScanCallback.clear();
        LogUtil.V(ClassName, "scanLeDevice() [INF] call startScan() ");
        mBluetoothScanner.startScan(
                buildScanFilters(),
                buildScanSettings(),
                mBleScanCallback);
    }

    /**
     * Writes a given characteristic and its values to the associated remote device.
     *
     * @param serviceUuid The UUID of the requested service
     * @param uuid The UUID of the requested descriptor
     * @param data Data to write to Bluetooth
     */
    public void writeCharacteristic(UUID serviceUuid, UUID uuid, byte[] data) {
        if (!Preconditions.checkNotNull(mBluetoothGatt)) return;
        if (Preconditions.isEmpty(data)) return;
        LogUtil.V(ClassName, "writeCharacteristic() [INF] serviceUuid:" + serviceUuid
                + " uuid:" + uuid + " data length:" + data.length);
        BluetoothGattService service = mBluetoothGatt.getService(serviceUuid);
        if (Preconditions.checkNotNull(service)) {
            BluetoothGattCharacteristic blechar = service.getCharacteristic(uuid);
            if (Preconditions.checkNotNull(blechar)) {
                blechar.setValue(data);
                mBluetoothGatt.writeCharacteristic(blechar);
            }
        }
    }

    /**
     * Writes a given characteristic and its values to the associated remote device.
     *
     * @param serviceUuid The UUID of the requested service
     * @param uuid The UUID of the requested descriptor
     * @param data Data to write to Bluetooth
     */
    public void writeCharacteristic(UUID serviceUuid, UUID uuid, String data) {
        if (!Preconditions.checkNotNull(mBluetoothGatt)) return;
        if (Preconditions.isEmpty(data)) return;
        LogUtil.V(ClassName, "writeCharacteristic() [INF] serviceUuid:" + serviceUuid
                + " uuid:" + uuid + " data:" + data);
        BluetoothGattService service = mBluetoothGatt.getService(serviceUuid);
        if (Preconditions.checkNotNull(service)) {
            BluetoothGattCharacteristic blechar = service.getCharacteristic(uuid);
            if (Preconditions.checkNotNull(blechar)) {
                blechar.setValue(data);
                mBluetoothGatt.writeCharacteristic(blechar);
            }
        }
    }

    /**
     * Discovers services offered by a remote device as well as their
     * characteristics and descriptors.
     */
    public void discoverServices() {
        if (Preconditions.checkNotNull(mBluetoothGatt)) {
            mBluetoothGatt.discoverServices();
        }
    }

    /**
     * Enable notifications/indications for a given characteristic.
     */
    public void setCharacteristicNotification() {
        if (Preconditions.isEmpty(getSupportedGattServices())) return;
        for (BluetoothGattService gattService : getSupportedGattServices()) {
            for (BluetoothGattCharacteristic gattCharacteristic :
                    gattService.getCharacteristics()) {
                LogUtil.V(ClassName,
                        "setCharacteristicNotification() [INF] Service:"
                                + gattService.getUuid().toString()
                                + ", Characteristic:"
                                + gattCharacteristic.getUuid().toString());
                setCharacteristicNotification(gattCharacteristic, true);
            }
        }
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification. False otherwise.
     */
    private void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
            boolean enabled) {
        LogUtil.V(ClassName,
                "setCharacteristicNotification() [INF] characteristic:" + characteristic);
        LogUtil.V(ClassName, "setCharacteristicNotification() [INF] enabled:" + enabled);
        if (!Preconditions.checkNotNull(mBluetoothAdapter)
                || !Preconditions.checkNotNull(mBluetoothGatt)) {
            LogUtil.W(ClassName, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        BluetoothGattDescriptor descriptor =
                characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
        if (Preconditions.checkNotNull(descriptor)) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    /**
     * Request an MTU size used for a given connection.
     *
     * @param mtu The new MTU size to request
     */
    public void requestMtu(int mtu) {
        LogUtil.V(ClassName, "requestMtu() [INF] ");
        if (!Preconditions.checkNotNull(mBluetoothGatt)) return;

        mBluetoothGatt.requestMtu(mtu);
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    private List<BluetoothGattService> getSupportedGattServices() {
        if (!Preconditions.checkNotNull(mBluetoothGatt)) return null;

        return mBluetoothGatt.getServices();
    }

    /**
     * Return a List of {@link android.bluetooth.le.ScanFilter} objects to filter by Service UUID.
     */
    private List<ScanFilter> buildScanFilters() {
        List<ScanFilter> scanFilters = new ArrayList<>();

        ScanFilter.Builder builder = new ScanFilter.Builder();
        // Comment out the below line to see all BLE devices around you
        builder.setServiceUuid(null);
        scanFilters.add(builder.build());

        return scanFilters;
    }

    /**
     * Return a {@link android.bluetooth.le.ScanSettings} object set to use low power (to preserve
     * battery life).
     */
    private ScanSettings buildScanSettings() {
        ScanSettings.Builder builder = new ScanSettings.Builder();
        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
        return builder.build();
    }
}
