package app.madeinchile.battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private TextView batteryLevel;
    private TextView batteryStats;

    private int currentBatteryLevel = -1;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        batteryLevel = (TextView) findViewById(R.id.battery_level);
        batteryStats = (TextView) findViewById(R.id.battery_stats);
    }

    private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
            String health_str;
            switch (health){
                case BatteryManager.BATTERY_HEALTH_COLD:
                    health_str = "BATTERY_HEALTH_COLD";
                    break;
                case BatteryManager.BATTERY_HEALTH_DEAD:
                    health_str = "BATTERY_HEALTH_DEAD";
                    break;
                case BatteryManager.BATTERY_HEALTH_GOOD:
                    health_str = "BATTERY_HEALTH_GOOD";
                    break;
                case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                    health_str ="BATTERY_HEALTH_OVER_VOLTAGE";
                    break;
                case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                    health_str ="BATTERY_HEALTH_OVERHEAT";
                    break;
                case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                    health_str ="BATTERY_HEALTH_UNKNOWN";
                    break;
                case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                    health_str ="BATTERY_HEALTH_UNSPECIFIED_FAILURE";
                    break;
                default:
                    health_str ="";
                    break;
            }


            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
            String plugged_str;
            if (plugged == BatteryManager.BATTERY_PLUGGED_AC) {
                plugged_str = "BATTERY_PLUGGED_AC";
                // on AC power
            } else if (plugged == BatteryManager.BATTERY_PLUGGED_USB) {
                // on USB power
                plugged_str = "BATTERY_PLUGGED_USB";
            } else if (plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS) {
                // on battery power
                plugged_str = "BATTERY_PLUGGED_WIRELESS";
            } else {
                // intent didnt include extra info
                plugged_str = "";
            }


            boolean present = intent.getExtras().getBoolean(BatteryManager.EXTRA_PRESENT);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);

            String status_str;
            if (status == BatteryManager.BATTERY_STATUS_UNKNOWN) {
                status_str = "BATTERY_STATUS_UNKNOWN";
            } else if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                status_str = "BATTERY_STATUS_CHARGING";
            } else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
                status_str = "BATTERY_STATUS_DISCHARGING";
            } else if (status == BatteryManager.BATTERY_STATUS_FULL) {
                status_str = "BATTERY_STATUS_FULL";
            } else if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
                status_str = "BATTERY_STATUS_NOT_CHARGING";
            } else {
                // intent didnt include extra info
                status_str = "";
            }

            String technology = intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);
            int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
            int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);

            if ((currentBatteryLevel == -1) || (Math.abs(level - currentBatteryLevel) >= 1)) {
                currentBatteryLevel = level;

                /** Iobeam: Capture data point, send to API */
                Map<String, Object> values = new HashMap<String, Object>();
                values.put("battery_level", currentBatteryLevel);
                values.put("temperature", ((double) temperature / 10));
                values.put("voltage", voltage);


            }

            batteryLevel.setText(level + "%");
            batteryStats.setText(
                    "Time: " + Calendar.getInstance().getTime() + "\n" +
                            "Level: " + level + "/" + scale + "\n" +
                            "Health: " + health_str + "\n" +
                            "Plugged: " + plugged_str + "\n" +
                            "Battery Present: " + present + "\n" +
                            "Status: " + status_str + "\n" +
                            "Technology: " + technology + "\n" +
                            "Temperature: " + ((double) temperature / 10) + " °C\n" +
                            "Voltage: " + voltage + "\n");
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(this.batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(this.batteryInfoReceiver);
    }
}
