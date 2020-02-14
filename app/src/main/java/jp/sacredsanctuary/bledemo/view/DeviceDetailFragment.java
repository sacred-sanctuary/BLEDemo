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

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Arrays;

import jp.sacredsanctuary.bledemo.R;
import jp.sacredsanctuary.bledemo.databinding.FragmentDeviceDetailBinding;
import jp.sacredsanctuary.bledemo.model.BluetoothDeviceDetail;
import jp.sacredsanctuary.bledemo.util.LogUtil;
import jp.sacredsanctuary.bledemo.util.Preconditions;
import jp.sacredsanctuary.bledemo.viewModel.BluetoothDeviceDataViewModel;

public class DeviceDetailFragment extends Fragment {
    private static final String ClassName = DeviceDetailFragment.class.getSimpleName();
    FragmentDeviceDetailBinding mBinding;
    private BluetoothDeviceDetail mBluetoothDeviceDetail;

    /**
     * {@inheritDoc}
     */
    @Override
    @MainThread
    @CallSuper
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtil.V(ClassName, "onCreate() [INF] ");
        super.onCreate(savedInstanceState);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @MainThread
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        LogUtil.V(ClassName, "onCreateView() [INF] ");
        super.onCreateView(inflater, container, savedInstanceState);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_device_detail, container,
                false);

        String address = Preconditions.checkNotNull(getArguments()) ?
                DeviceDetailFragmentArgs.fromBundle(getArguments()).getDeviceAddress() : null;
        LogUtil.V(ClassName, "onCreateView() [INF] address:" + address);

        if (Preconditions.checkNotNull(getActivity())) {
            ((MainActivity) getActivity()).showProgressDialog(null,
                    getResources().getString(
                            R.string.progress_msg_connecting_bluetooth_low_energy));
            ((MainActivity) getActivity()).getBleServiceConnection().connect(address);

            BluetoothDeviceDataViewModel model =
                    new ViewModelProvider(getActivity()).get(BluetoothDeviceDataViewModel.class);
            model.getBluetoothDevice().observe(getActivity(), this::updateDetailView);
        }

        return mBinding.getRoot();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @MainThread
    @CallSuper
    public void onDestroy() {
        super.onDestroy();
        mBinding.setBluetoothDeviceDetail(null);
        mBinding = null;
        mBluetoothDeviceDetail = null;
    }

    private void updateDetailView(BluetoothDevice device) {
        if (!Preconditions.checkNotNull(device)) return;
        mBluetoothDeviceDetail = new BluetoothDeviceDetail(device);
        if (Preconditions.checkNotNull(mBinding)) {
            mBinding.setBluetoothDeviceDetail(mBluetoothDeviceDetail);
        }
        LogUtil.V(ClassName, "updateDetailView() [INF] device:" + device);
        LogUtil.V(ClassName,
                "updateDetailView() [INF] BluetoothClass:" + device.getBluetoothClass());
        LogUtil.V(ClassName, "updateDetailView() [INF] BondState:" + device.getBondState());
        LogUtil.V(ClassName, "updateDetailView() [INF] Type:" + device.getType());
        LogUtil.V(ClassName, "updateDetailView() [INF] Address:" + device.getAddress());
        LogUtil.V(ClassName, "updateDetailView() [INF] Name:" + device.getName());
        LogUtil.V(ClassName,
                "updateDetailView() [INF] Uuids:" + Arrays.toString(device.getUuids()));
    }
}
