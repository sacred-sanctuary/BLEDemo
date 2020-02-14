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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.Toast;

import jp.sacredsanctuary.bledemo.R;
import jp.sacredsanctuary.bledemo.bluetooth.BluetoothLowEnergyControllerCallback;
import jp.sacredsanctuary.bledemo.bluetooth.IBluetoothLowEnergyControllerCallback;
import jp.sacredsanctuary.bledemo.service.BleServiceConnection;
import jp.sacredsanctuary.bledemo.service.BluetoothLeService;
import jp.sacredsanctuary.bledemo.util.LogUtil;
import jp.sacredsanctuary.bledemo.util.Preconditions;

public class MainActivity extends AppCompatActivity implements ServiceConnection {
    private static final String ClassName = MainActivity.class.getSimpleName();

    private static final int REQUEST_ENABLE_BT = 1;

    // Code to manage Service lifecycle.
    public BleServiceConnection mBleServiceConnection;
    private IBluetoothLowEnergyControllerCallback mBluetoothLowEnergyControllerCallback;
    private ProgressDialog mProgressDialog;
    private Handler mMainHandler;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.V(ClassName, "onCreate() [INF] ");
        super.onCreate(savedInstanceState);
        if (RequestPermissionsActivity.startPermissionActivity(this)) {
            finish();
            return;
        }

        //FragmentManager.enableDebugLogging(true);
        mMainHandler = new Handler(Looper.getMainLooper());
        mBluetoothLowEnergyControllerCallback = new BluetoothLowEnergyControllerCallback(this);
        mBleServiceConnection = new BleServiceConnection(this);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mBleServiceConnection, BIND_AUTO_CREATE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
        LogUtil.V(ClassName, "onResume() [INF] ");
        if (RequestPermissionsActivity.startPermissionActivity(this)) {
            finish();
        }
        super.onResume();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        LogUtil.V(ClassName, "onServiceConnected() [INF] name:" + name);
        if (!isFinishing()) {
            if (mBleServiceConnection.isBluetoothLowEnergySupported()) {
                mBleServiceConnection.setCallback(mBluetoothLowEnergyControllerCallback);
                setContentView(R.layout.activity_main);

                Toolbar toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                ImageView reload = findViewById(R.id.reload);
                reload.setOnClickListener(v -> onReload());

                requestEnableBluetooth();
            } else {
                showBleNotSupported();
            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        LogUtil.V(ClassName, "onServiceDisconnected() [INF] name:" + name);
        mBleServiceConnection.setCallback(null);
    }

    private void requestEnableBluetooth() {
        LogUtil.V(ClassName, "requestEnableBluetooth() mBluetoothAdapter.isEnabled():"
                + mBleServiceConnection.isEnabled());
        if (!mBleServiceConnection.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public BleServiceConnection getBleServiceConnection() {
        return mBleServiceConnection;
    }

    public void onConnectionCompleted() {
        dismissProgressDialog();
    }

    public void onConnectionFailed() {
        dismissProgressDialog();
        showToastMsg(getResources().getString(R.string.bluetooth_low_energy_connection_failed));
    }

    private void showBleNotSupported() {
        showDialog(getResources().getString(R.string.bluetooth_not_support),
                getResources().getString(R.string.application_cannot_be_used_on_this_device),
                "OK",
                (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                },
                null,
                null
        );
    }

    public void showDialog(final String title, final String msg,
            final String positiveMsg, final DialogInterface.OnClickListener positiveListener,
            final String negativeMsg, final DialogInterface.OnClickListener negativeListener) {
        if (!isFinishing()) {
            mMainHandler.post(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(title);
                builder.setMessage(msg);
                builder.setPositiveButton(positiveMsg, positiveListener);
                builder.setNegativeButton(negativeMsg, negativeListener);
                builder.show();
            });
        }
    }

    public void showProgressDialog(String title, String msg) {
        if (!isFinishing()) {
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setTitle(title);
            mProgressDialog.setMessage(msg);
            mProgressDialog.show();
        }
    }

    public void dismissProgressDialog() {
        if (Preconditions.checkNotNull(mProgressDialog) && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void showToastMsg(String msg) {
        if (!isFinishing()) {
            new Handler(Looper.getMainLooper()).post(
                    () -> Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show());
        }
    }

    public void onScanCompleted() {
        Fragment navHostFragment = getSupportFragmentManager().getPrimaryNavigationFragment();
        Fragment fragment = Preconditions.checkNotNull(navHostFragment) ?
                navHostFragment.getChildFragmentManager().getFragments().get(0) : null;
        if (Preconditions.checkNotNull(fragment) && fragment instanceof ScanFragment) {
            ((ScanFragment) fragment).onScanCompleted();
        }
    }

    private void onReload() {
        mBleServiceConnection.disconnect();
        if (Preconditions.checkNotNull(
                getSupportFragmentManager().getPrimaryNavigationFragment())) {
            Fragment navHostFragment = getSupportFragmentManager().getPrimaryNavigationFragment();
            Fragment fragment = Preconditions.checkNotNull(navHostFragment) ?
                    navHostFragment.getChildFragmentManager().getFragments().get(0) : null;
            if (Preconditions.checkNotNull(fragment) && fragment instanceof ScanFragment) {
                ((ScanFragment) fragment).reload();
            } else {
                NavHostFragment.findNavController(
                        getSupportFragmentManager().getPrimaryNavigationFragment()).popBackStack(
                        R.id.scan_fragment, false);
            }
        }
    }
}
