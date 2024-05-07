package com.ma.bouchama_hajar_atelier_8;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TableLayout tableLayout;
    private float[] gravity = new float[3];
    private float[] linear_acceleration = new float[3];
    private int[] confidences = new int[5];
    private TextView confidenceDebout, confidenceAssis, confidenceMarcher, confidenceSauter, confidenceCourir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        // Initialisation des vues

        confidenceAssis = findViewById(R.id.confidence_1);
        confidenceDebout = findViewById(R.id.confidence_2);
        confidenceSauter = findViewById(R.id.confidence_3);
        confidenceMarcher = findViewById(R.id.confidence_4);
        confidenceCourir = findViewById(R.id.confidence_5);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            final float alpha = 0.8f;

            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];

            int activite = TypeActivite(linear_acceleration);
            confidences[activite]++;

            updateConfidenceDisplay();
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void updateConfidenceDisplay() {
        int totalConfidence = 0;
        for (int value : confidences) {
            totalConfidence += value;
        }
        if (totalConfidence > 0) {
            confidenceAssis.setText(String.format("%d%%", (confidences[0] * 100 / totalConfidence)));
            confidenceDebout.setText(String.format("%d%%", (confidences[1] * 100 / totalConfidence)));
            confidenceMarcher.setText(String.format("%d%%", (confidences[2] * 100 / totalConfidence)));
            confidenceSauter.setText(String.format("%d%%", (confidences[3] * 100 / totalConfidence)));
            confidenceCourir.setText(String.format("%d%%", (confidences[4] * 100 / totalConfidence)));
        }




    }
    private int TypeActivite(float[] acceleration) {
        float x = acceleration[0];
        float y = acceleration[1];
        float z = acceleration[2];
        float magnitude = (float) Math.sqrt(x * x + y * y + z * z);

        if (magnitude < 2.0f) return 0;
        else if (magnitude < 4.0f) return 1;
        else if (magnitude < 7.0f) return 2;
        else if (magnitude < 10.0f) return 3;
        else return 4;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

}