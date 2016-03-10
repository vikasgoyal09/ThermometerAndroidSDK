package thermometer.quovantis.com.lib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import com.quovantis.bluetoothlibs.DeviceItem;
import com.quovantis.common.helpers.BaseRecyclerAdapter;
import com.quovantis.common.event.EventManager;
import com.quovantis.common.event.EventTypes;
import thermometer.quovantis.com.lib.helper.BluetoothDeviceItemManager;
import thermometer.quovantis.com.lib.thermometer.ThermometerManager;

public class MainActivity extends Activity implements View.OnClickListener {

    private BaseRecyclerAdapter mAdapter;
    /**
     * Callback listener will call when their is any device is
     * discovered by bluetooth service
     */
    private DeviceDiscoverListener mDeviceDiscoverListener;
    /**
     * Callback listener will call when any device is selected
     * from the discovered bluetooth device list
     */
    private DeviceSelectListener mDeviceSelectListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startScanBtn = (Button) findViewById(R.id.btn_activity_main_scan);
        Button stopScanBtn = (Button) findViewById(R.id.btn_activity_main_stop);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rcv_activity_main_devices);

        startScanBtn.setOnClickListener(this);
        stopScanBtn.setOnClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BaseRecyclerAdapter(this);
        mAdapter.register(DeviceItem.class, new BluetoothDeviceItemManager());
        recyclerView.setAdapter(mAdapter);

        init();
    }

    private void init() {
        ThermometerManager.getInstance(this);
        mDeviceDiscoverListener = new DeviceDiscoverListener();
        EventManager.getInstance().registerForEvent(EventTypes.EVENT_ACTION_DEVICE_STATE_CHANGED
                , mDeviceDiscoverListener);
        mDeviceSelectListener = new DeviceSelectListener();
        EventManager.getInstance().registerForEvent(EventTypes.EVENT_ACTION_DEVICE_SELECTED
                , mDeviceSelectListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ThermometerManager.getInstance(this).getDeviceItems();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventManager.getInstance().unregisterReceiver(EventTypes.EVENT_ACTION_DEVICE_STATE_CHANGED
                , mDeviceDiscoverListener);
        EventManager.getInstance().unregisterReceiver(EventTypes.EVENT_ACTION_DEVICE_SELECTED
                , mDeviceSelectListener);

        ThermometerManager.getInstance(this).disconnectBT();
        ThermometerManager.getInstance(this).close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_activity_main_scan:
                ThermometerManager.getInstance(this).startScanForBTDevices();
                break;
            case R.id.btn_activity_main_stop:
                ThermometerManager.getInstance(this).stopScanForBTDevices();
                break;
        }
    }

    private class DeviceDiscoverListener implements EventManager.EventReceivedListener<DeviceItem> {
        @Override
        public void onEventReceived(DeviceItem data) throws ClassCastException {
            if (data.isDiscovered()) {
                mAdapter.add(data);
            }
            mAdapter.notifyDataSetChanged();
        }
    }


    private class DeviceSelectListener implements EventManager.EventReceivedListener<DeviceItem> {
        @Override
        public void onEventReceived(DeviceItem device) throws ClassCastException {
            if (ThermometerManager.getInstance(MainActivity.this).connectThermometer(device)) {
                Intent intent = new Intent(MainActivity.this, ThermometerActivity.class);
                startActivity(intent);
            }
        }
    }

}
