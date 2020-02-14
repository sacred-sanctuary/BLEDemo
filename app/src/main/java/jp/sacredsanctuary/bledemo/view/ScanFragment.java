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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import jp.sacredsanctuary.bledemo.R;
import jp.sacredsanctuary.bledemo.util.LogUtil;
import jp.sacredsanctuary.bledemo.util.Preconditions;
import jp.sacredsanctuary.bledemo.viewModel.BluetoothDeviceListViewModel;

public class ScanFragment extends Fragment {
    private static final String ClassName = ScanFragment.class.getSimpleName();
    // Stops scanning after 5 seconds.
    private static final long SCAN_PERIOD = 5000;

    private ImageView mScanView;
    private Animation mAnimation;
    private TextView mScanningTextView;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtil.V(ClassName, "onCreate() [INF] ");
        super.onCreate(savedInstanceState);
        if (isAdded() && Preconditions.checkNotNull(getActivity())) {
            BluetoothDeviceListViewModel model =
                    new ViewModelProvider(getActivity()).get(BluetoothDeviceListViewModel.class);
            model.getBluetoothDeviceDataList().observe(getActivity(), bluetoothDeviceList -> {
                LogUtil.V(ClassName,
                        "observe() [INF] bluetoothDeviceList:" + bluetoothDeviceList);
                if (!Preconditions.isEmpty(bluetoothDeviceList) && isAdded()) {
                    NavHostFragment.findNavController(this).navigate(
                            R.id.action_scan_to_list);
                }
            });
        }
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
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        mScanView = view.findViewById(R.id.scan_view);
        mScanningTextView = view.findViewById(R.id.scanning_text_view);
        mAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.scanning);
        mScanView.startAnimation(mAnimation);

        if (isAdded() && Preconditions.checkNotNull(getActivity())) {
            ((MainActivity) getActivity()).getBleServiceConnection().scanBluetoothLowEnergyDevice(
                    SCAN_PERIOD);
        }
        return view;
    }

    public void onScanCompleted() {
        if (isAdded()) {
            mScanView.clearAnimation();
            mScanningTextView.setText(R.string.could_not_search_any_device);
        }
    }

    public void reload() {
        if (isAdded()) {
            mScanView.startAnimation(mAnimation);
            mScanningTextView.setText(R.string.scanning_bluetooth_low_energy);
            if (Preconditions.checkNotNull(getActivity())) {
                ((MainActivity) getActivity()).getBleServiceConnection()
                        .scanBluetoothLowEnergyDevice(SCAN_PERIOD);
            }
        }
    }
}
