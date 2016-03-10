package thermometer.quovantis.com.lib.helper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.quovantis.bluetoothlibs.DeviceItem;
import thermometer.quovantis.com.lib.R;
import com.quovantis.common.event.EventManager;
import com.quovantis.common.event.EventTypes;

public class DeviceItemView extends FrameLayout implements View.OnClickListener {

    private TextView mNameView;
    private TextView mAddressView;
    private DeviceItem mDeviceItem;
    private int mPosition;

    public DeviceItemView(Context context) {
        super(context);
        if (!isInEditMode()) {
            init();
        }
    }

    public DeviceItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init();
        }
    }

    public DeviceItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode()) {
            init();
        }
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.bluetooth_device_item, this, true);
        mNameView = (TextView) findViewById(R.id.txv_bluetooth_device_item_name);
        mAddressView = (TextView) findViewById(R.id.txv_bluetooth_device_item_address);
        setOnClickListener(this);
    }

    public void setData(DeviceItem device, int position) {
        mPosition = position;
        mDeviceItem = device;
        mNameView.setText(device.getDeviceName());
        mAddressView.setText(device.getDeviceAddress());
    }

    @Override
    public void onClick(View v) {
        EventManager.getInstance().broadcastEvent(EventTypes.EVENT_ACTION_DEVICE_SELECTED, mDeviceItem);
    }
}
