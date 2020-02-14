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

import java.util.Set;

/**
 * Callbacks invoked when actions have been taken on a bluetooth.
 * <p>
 * NOTE: The current arguments are merely to support the existing use cases. This
 * needs to be properly thought out with appropriate arguments for each of the
 * callback methods.
 */
public interface IBluetoothLowEnergyControllerCallback {
    /**
     * Callback indicating when GATT client has connected/disconnected to/from a remote
     * GATT server.
     *
     * @param gatt     GATT client
     * @param status   Status of the connect or disconnect operation. {@link
     *                 BluetoothGatt#GATT_SUCCESS} if the operation succeeds.
     * @param newState Returns the new connection state. Can be one of {@link
     *                 BluetoothProfile#STATE_DISCONNECTED} or
     *                 {@link BluetoothProfile#STATE_CONNECTED}
     */
    void onConnectionStateChange(BluetoothGatt gatt, int status, int newState);

    /**
     * Callback invoked when the list of remote services, characteristics and descriptors
     * for the remote device have been updated, ie new services have been discovered.
     *
     * @param gatt   GATT client invoked {@link BluetoothGatt#discoverServices}
     * @param status {@link BluetoothGatt#GATT_SUCCESS} if the remote device has been explored
     *               successfully.
     */
    void onServicesDiscovered(BluetoothGatt gatt, int status);

    /**
     * Callback indicating the MTU for a given device connection has changed.
     *
     * This callback is triggered in response to the
     * {@link BluetoothGatt#requestMtu} function, or in response to a connection
     * event.
     *
     * @param gatt   GATT client invoked {@link BluetoothGatt#requestMtu}
     * @param mtu    The new MTU size
     * @param status {@link BluetoothGatt#GATT_SUCCESS} if the MTU has been changed successfully
     */
    void onMtuChanged(BluetoothGatt gatt, int mtu, int status);

    /**
     * Callback reporting the result of a characteristic read operation.
     *
     * @param gatt           GATT client invoked {@link BluetoothGatt#readCharacteristic}
     * @param characteristic Characteristic that was read from the associated remote device.
     * @param status         {@link BluetoothGatt#GATT_SUCCESS} if the read operation was completed
     *                       successfully.
     */
    void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,
            int status);

    /**
     * Callback indicating the result of a characteristic write operation.
     *
     * <p>If this callback is invoked while a reliable write transaction is
     * in progress, the value of the characteristic represents the value
     * reported by the remote device. An application should compare this
     * value to the desired value to be written. If the values don't match,
     * the application must abort the reliable write transaction.
     *
     * @param gatt           GATT client invoked {@link BluetoothGatt#writeCharacteristic}
     * @param characteristic Characteristic that was written to the associated remote device.
     * @param status         The result of the write operation {@link BluetoothGatt#GATT_SUCCESS} if
     *                       the
     *                       operation succeeds.
     */
    void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,
            int status);

    /**
     * Callback triggered as a result of a remote characteristic notification.
     *
     * @param gatt           GATT client the characteristic is associated with
     * @param characteristic Characteristic that has been updated as a result of a remote
     *                       notification event.
     */
    void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);

    /**
     * Called to notify when the Bluetooth scanner has finished scanning.
     *
     * @param results List of scan results that are previously scanned.
     */
    void onScanCompleted(Set<ScanResult> results);
}
