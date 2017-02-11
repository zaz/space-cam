package digital.hyperturtle.app.spacecam;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    public SensorManager sMgr;
    public TextView t_time;
    public TextView t_pos;
    public TextView t_vel;
    public TextView t_lux;
    public TextView t_press;
    public TextView t_temp;
    public TextView t_humid;
    public double v_press;
    public Location v_loc;
    public double last_lat;
    public double last_long;
    public Boolean sent_message = Boolean.FALSE;
    private int interval = 1000 * 6; // 10s seconds

    Handler mHandler = new Handler();

    Runnable main = new Runnable()
    {
        @Override
        public void run() {
            // FIXME: latitude and longitude are rounded to 4DP rather than the original 6DP
            // String latitude = Location.convert(v_loc.getLatitude(), Location.FORMAT_DEGREES);
            // String longitude = Location.convert(v_loc.getLongitude(), Location.FORMAT_DEGREES);
            String latitude = String.valueOf(v_loc.getLatitude());
            String longitude = String.valueOf(v_loc.getLongitude());
            String altitude = String.valueOf((int)v_loc.getAltitude());
            String bearing = String.valueOf(v_loc.getBearing());
            String speed = String.valueOf(v_loc.getSpeed());
            String accuracy = String.valueOf((int)v_loc.getAccuracy());
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(v_loc.getTime()));
            String pos = latitude + "," + longitude + "  " + altitude + "m  SD:" + accuracy + "m";
            String vel = bearing + "°  " + speed + "m/s";
            String press = String.valueOf(v_press) + "hPa";
            t_time.setText("time: " + time + " z");
            t_pos.setText("pos: " + pos);
            t_vel.setText("vel: " + vel);
            t_press.setText("hPa: " + String.valueOf(v_press));
            //if (! sent_message) {
            //    sent_message = Boolean.TRUE;
            //    SmsManager smsManager = SmsManager.getDefault();
            //    smsManager.sendTextMessage("+13302527470", null, time + " z\n" + pos + "\n" + vel + "\n" + press, null, null);
            //}

            mHandler.postDelayed(main, interval);
        }
    };

    // Define a listener that responds to location updates
    public LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            v_loc = location;
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t_time = (TextView) findViewById(R.id.time);
        t_pos = (TextView) findViewById(R.id.pos);
        t_vel = (TextView) findViewById(R.id.vel);
        t_lux = (TextView) findViewById(R.id.lux);
        t_press = (TextView) findViewById(R.id.press);

        SensorManager sMgr = (SensorManager) this.getSystemService(SENSOR_SERVICE);

        Sensor s_lux = sMgr.getDefaultSensor(Sensor.TYPE_LIGHT);
        Sensor s_press = sMgr.getDefaultSensor(Sensor.TYPE_PRESSURE);
        sMgr.registerListener(this, s_lux, SensorManager.SENSOR_DELAY_NORMAL);
        sMgr.registerListener(this, s_press, SensorManager.SENSOR_DELAY_NORMAL);


        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS, Manifest.permission.WRITE_SECURE_SETTINGS}, 1);
        // If user denies request, let the app crash

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        mHandler.postDelayed(main, interval);
        Settings.Global.putString(getContentResolver(), "airplane_mode_on", "1");
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        // The light sensor returns a single value.
        // Many sensors return 3 values, one for each axis.
        synchronized (this) {
            float val = event.values[0];
            Sensor sensor = event.sensor;
            if (sensor.getType() == Sensor.TYPE_LIGHT) {
                t_lux.setText("lux: " + String.valueOf(val));
            } else if (sensor.getType() == Sensor.TYPE_PRESSURE) {
                v_press = val;
            }
        }

    }
}