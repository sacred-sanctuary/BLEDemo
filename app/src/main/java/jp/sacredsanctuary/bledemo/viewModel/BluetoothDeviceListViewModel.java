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
import android.bluetooth.le.ScanResult;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jp.sacredsanctuary.bledemo.model.BluetoothDeviceData;
import jp.sacredsanctuary.bledemo.util.LogUtil;
import jp.sacredsanctuary.bledemo.util.Preconditions;

/**
 * Store the information for BluetoothDeviceData.
 */
public class BluetoothDeviceListViewModel extends ViewModel {
    private static final String ClassName = BluetoothDeviceListViewModel.class.getSimpleName();
    private MutableLiveData<List<BluetoothDeviceData>> mMutableDeviceDataList;
    private List<BluetoothDeviceData> mDeviceDataList;

    public LiveData<List<BluetoothDeviceData>> getBluetoothDeviceDataList() {
        if (!Preconditions.checkNotNull(mDeviceDataList)) {
            mDeviceDataList = new ArrayList<>();
        }
        if (!Preconditions.checkNotNull(mMutableDeviceDataList)) {
            mMutableDeviceDataList = new MutableLiveData<>();
            mMutableDeviceDataList.postValue(mDeviceDataList);
        }
        return mMutableDeviceDataList;
    }

    public void setBluetoothDeviceDataList(Set<ScanResult> list) {
        mDeviceDataList.clear();
        for (ScanResult result : list) {
            if (Preconditions.checkNotNull(result.getDevice())
                    && !containsDeviceDataList(result.getDevice())) {
                LogUtil.V(ClassName, "setBluetoothDeviceDataList() [INF] result:" + result);
                mDeviceDataList.add(new BluetoothDeviceData(result.getDevice()));
            }
        }
        mMutableDeviceDataList.postValue(mDeviceDataList);
    }

    public void setBluetoothDeviceDataList(List<ScanResult> list) {
        for (ScanResult result : list) {
            if (Preconditions.checkNotNull(result.getDevice())
                    && !containsDeviceDataList(result.getDevice())) {
                mDeviceDataList.add(new BluetoothDeviceData(result.getDevice()));
            }
        }
        mMutableDeviceDataList.postValue(mDeviceDataList);
    }

    private boolean containsDeviceDataList(@NonNull BluetoothDevice bluetoothDevice) {
        for (BluetoothDeviceData data : mDeviceDataList) {
            if (data.getBluetoothDevice().getAddress().equals(bluetoothDevice.getAddress())) {
                return true;
            }
        }
        return false;
    }
}
