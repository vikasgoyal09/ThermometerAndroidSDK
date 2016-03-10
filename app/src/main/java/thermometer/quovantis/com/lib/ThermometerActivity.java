package thermometer.quovantis.com.lib;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.quovantis.common.event.EventManager;
import com.quovantis.common.event.EventTypes;
import thermometer.quovantis.com.lib.thermometer.ThermometerManager;
import thermometer.quovantis.com.lib.thermometer.models.ThermometerReading;

public class ThermometerActivity extends Activity implements View.OnClickListener {
    private TextView mTemperature;
    private Button mChangeMode;
    private Button mChangeUnit;
    private ThermometerReadingsListener mReadingsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thermometer_activity);

        mTemperature = (TextView) findViewById(R.id.txv_activity_thermometer_current_reading);
        mChangeMode = (Button) findViewById(R.id.btn_activity_thermometer_mode);
        mChangeUnit = (Button) findViewById(R.id.btn_activity_thermometer_unit);
        Button scanReading = (Button) findViewById(R.id.btn_activity_thermometer_reading);

        mChangeMode.setOnClickListener(this);
        mChangeUnit.setOnClickListener(this);
        scanReading.setOnClickListener(this);

        mReadingsListener = new ThermometerReadingsListener();
        EventManager.getInstance().registerForEvent(EventTypes.EVENT_ACTION_TEMPERATURE_READING_RECEIVED
                , mReadingsListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventManager.getInstance().unregisterReceiver(EventTypes.EVENT_ACTION_TEMPERATURE_READING_RECEIVED
                , mReadingsListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_activity_thermometer_mode:
                if (ThermometerManager.getInstance(this).changeMode()) {
                    setData(ThermometerManager.getInstance(this).getThermometerReading());
                }
                break;
            case R.id.btn_activity_thermometer_unit:
                if (ThermometerManager.getInstance(this).changeUnit()) {
                    setData(ThermometerManager.getInstance(this).getThermometerReading());
                }
                break;
            case R.id.btn_activity_thermometer_reading:
                ThermometerManager.getInstance(this).readTemperature();
                break;
        }
    }

    private class ThermometerReadingsListener implements EventManager.EventReceivedListener<ThermometerReading> {
        @Override
        public void onEventReceived(ThermometerReading data) throws ClassCastException {
            setData(data);
        }
    }

    private void setData(ThermometerReading data) {
        if (null == data) {
            return;
        }
        switch (data.getThermometerUnit()) {
            case FAHRENHEIT:
                mChangeUnit.setText(R.string.unit_fahrenheit);
                mTemperature.setText(String.valueOf(data.getTemperature()) + " F");
                break;
            case CELSIUS:
                mChangeUnit.setText(R.string.unit_celsius);
                mTemperature.setText(String.valueOf(data.getTemperature()) + " C");
                break;
        }
        switch (data.getThermometerMode()) {
            case BODY:
                mChangeMode.setText(R.string.mode_body);
                break;
            case SURFACE:
                mChangeMode.setText(R.string.mode_surface);
                break;
        }
    }
}
