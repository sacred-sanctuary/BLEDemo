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
package jp.sacredsanctuary.bledemo.viewModel;

import android.bluetooth.BluetoothDevice;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import jp.sacredsanctuary.bledemo.util.Preconditions;


public class BluetoothDeviceDataViewModel extends ViewModel {
    private MutableLiveData<BluetoothDevice> mBluetoothDevice;

    public void setBluetoothDevice(BluetoothDevice device) {
        mBluetoothDevice.postValue(device);
    }

    public LiveData<BluetoothDevice> getBluetoothDevice() {
        if (!Preconditions.checkNotNull(mBluetoothDevice)) {
            mBluetoothDevice = new MutableLiveData<>();
            mBluetoothDevice.postValue(null);
        }
        return mBluetoothDevice;
    }
}
