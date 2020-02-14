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

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import jp.sacredsanctuary.bledemo.R;
import jp.sacredsanctuary.bledemo.adapter.BluetoothDeviceListAdapter;
import jp.sacredsanctuary.bledemo.model.BluetoothDeviceData;
import jp.sacredsanctuary.bledemo.util.LogUtil;
import jp.sacredsanctuary.bledemo.util.Preconditions;
import jp.sacredsanctuary.bledemo.viewModel.BluetoothDeviceListViewModel;

public class BluetoothDeviceDiscoveryListFragment extends Fragment {
    private static final String ClassName =
            BluetoothDeviceDiscoveryListFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private BluetoothDeviceListAdapter mBluetoothDeviceListAdapter;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        if (!isAdded() || !Preconditions.checkNotNull(getActivity())) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
        LogUtil.V(ClassName, "onCreateView() [INF] ");
        View view = inflater.inflate(R.layout.fragment_device_discovery_list, container, false);

        mBluetoothDeviceListAdapter = new BluetoothDeviceListAdapter() {
            @Override
            protected void onItemClicked(@NonNull BluetoothDeviceData data) {
                LogUtil.V(ClassName,
                        "onItemClicked() [INF] getName:" + data.getBluetoothDevice().getName());
                LogUtil.V(ClassName, "onItemClicked() [INF] getAddress:"
                        + data.getBluetoothDevice().getAddress()
                        + ", getType:" + data.getBluetoothDevice().getType()
                        + ", getBondState:" + data.getBluetoothDevice().getBondState()
                        + ", getBluetoothClass:" + data.getBluetoothDevice().getBluetoothClass());
                if (isAdded()) {
                    BluetoothDeviceDiscoveryListFragmentDirections.ActionListToDetail action =
                            BluetoothDeviceDiscoveryListFragmentDirections.actionListToDetail();
                    action.setDeviceAddress(data.getBluetoothDevice().getAddress());
                    NavHostFragment.findNavController(BluetoothDeviceDiscoveryListFragment.this)
                            .navigate(action);
                }
            }
        };

        mRecyclerView = view.findViewById(R.id.device_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setAdapter(mBluetoothDeviceListAdapter);

        BluetoothDeviceListViewModel model =
                new ViewModelProvider(getActivity()).get(BluetoothDeviceListViewModel.class);
        mBluetoothDeviceListAdapter.setItems(model.getBluetoothDeviceDataList().getValue());
        mBluetoothDeviceListAdapter.notifyDataSetChanged();

        return view;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        LogUtil.V(ClassName, "onDestroy() [INF] ");
        super.onDestroy();

        if (Preconditions.checkNotNull(mBluetoothDeviceListAdapter)) {
            mBluetoothDeviceListAdapter.clearItems();
            mBluetoothDeviceListAdapter = null;
        }

        if (Preconditions.checkNotNull(mRecyclerView)) {
            mRecyclerView = null;
        }
    }
}
