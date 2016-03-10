package thermometer.quovantis.com.lib.helper;

import android.content.Context;
import android.view.ViewGroup;
import com.quovantis.bluetoothlibs.DeviceItem;
import com.quovantis.common.helpers.BaseRecyclerAdapter;
import com.quovantis.common.helpers.ItemHolder;
import com.quovantis.common.helpers.ItemManager;

public class BluetoothDeviceItemManager implements ItemManager<DeviceItem, DeviceItemView> {
    @Override
    public DeviceItemView createView(Context context, ViewGroup parent) {
        return new DeviceItemView(context);
    }

    @Override
    public void bindData(DeviceItemView view, DeviceItem data, int position, ItemHolder itemHolder
            , BaseRecyclerAdapter adapter) {
        view.setData(data, position);
    }
}
