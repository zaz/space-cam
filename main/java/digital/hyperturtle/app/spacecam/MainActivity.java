package digital.hyperturtle.app.spacecam;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    public SensorManager sMgr;
    public TextView t_time;
    public TextView t_pos;
    public TextView t_vel;
    public TextView t_lux;
    public TextView t_press;
    public TextView t_temp;
    public TextView t_humid;


    // Define a listener that responds to location updates
    public LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // FIXME: latitude and longitude are rounded to 4DP rather than the original 6DP
            String latitude = Location.convert(location.getLatitude(), Location.FORMAT_DEGREES);
            String longitude = Location.convert(location.getLongitude(), Location.FORMAT_DEGREES);
            String altitude = String.valueOf(location.getAltitude());
            String bearing = String.valueOf(location.getBearing());
            String speed = String.valueOf(location.getSpeed());
            String accuracy = String.valueOf(location.getAccuracy());
            String time = String.valueOf(location.getTime());
            t_time.setText("time: " + time + "ms");
            t_pos.setText("pos: " + latitude + "," + longitude + "  " + altitude + "m  SD:" + accuracy + "m");
            t_vel.setText("vel: " + bearing + "°  " + speed + "m/s");
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
        t_temp = (TextView) findViewById(R.id.temp);
        t_humid = (TextView) findViewById(R.id.humid);

        SensorManager sMgr = (SensorManager) this.getSystemService(SENSOR_SERVICE);

        Sensor s_lux = sMgr.getDefaultSensor(Sensor.TYPE_LIGHT);
        Sensor s_press = sMgr.getDefaultSensor(Sensor.TYPE_PRESSURE);
        Sensor s_temp = sMgr.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        Sensor s_humid = sMgr.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        sMgr.registerListener(this, s_lux, SensorManager.SENSOR_DELAY_NORMAL);
        sMgr.registerListener(this, s_press, SensorManager.SENSOR_DELAY_NORMAL);
        sMgr.registerListener(this, s_temp, SensorManager.SENSOR_DELAY_NORMAL);
        sMgr.registerListener(this, s_humid, SensorManager.SENSOR_DELAY_NORMAL);


        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            // If user denies request, let the app crash
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
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
                t_press.setText("hPa: " + String.valueOf(val));
            } else if (sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                t_temp.setText("temp: " + String.valueOf(val));
            } else if (sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
                t_humid.setText("humid: " + String.valueOf(val));
            }
        }

    }
}