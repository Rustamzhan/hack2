package com.example.myapplication;


import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Objects;



public class MainActivity extends Activity implements SensorEventListener {

    static {
        //noinspection InjectedReferences
        System.loadLibrary("tensorflow_demo");
    }
        private SensorManager msensorManager;

        private float[] accelerometerData;
//        private float[] accelerationData;
//        private float[] gyroscopData;
        private int count = 0;
//        Interpreter tflite;
        float[] mass_out = new float[6];
        float[] mass_in = new float[600];
        int[] tracker = new int[6];
        float prev = 0;
        long    start = 0;
        private TensorFlowClassifier classifier;
        long first = 0;


        public float x = 0;
        public float y = 0;
        public float z = 0;

        public Button button;
        public TextView View1a;
        public TextView View1b;
        public TextView View1c;
        public TextView View2a;
//        public TextView View2b;
//        public TextView View2c;
//        public TextView View3a;
//        public TextView View3b;
//        public TextView View3c;
        public TextView Viewres1;
        public TextView Viewres2;
        public TextView Viewres3;
        public TextView Viewres4;
        public TextView Viewres5;
        public TextView Viewres6;
//        public TextView Viewres7;
//    public TextView Viewres8;

        /** Called when the activity is first created. */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            msensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
            classifier = new TensorFlowClassifier(getApplicationContext());

            accelerometerData = new float[3];
//            accelerationData = new float[3];
//            gyroscopData = new float[3];

            View1a = findViewById(R.id.Value1a);  //
            View1b = findViewById(R.id.Value1b);  //
            View1c = findViewById(R.id.Value1c);  //
            View2a = findViewById(R.id.Value2a);  //
//            View2b = findViewById(R.id.Value2b);  //
//            View2c = findViewById(R.id.Value2c);
//            View3a = findViewById(R.id.Value3a);  //
//            View3b = findViewById(R.id.Value3b);  //
//            View3c = findViewById(R.id.Value3c);

            setContentView(R.layout.activity_main);


        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onResume() {
            super.onResume();

            button = findViewById(R.id.button);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tracker[0] = 0;
                    tracker[1] = 0;
                    tracker[2] = 0;
                    tracker[3] = 0;
                    tracker[4] = 0;
                    tracker[5] = 0;
                }
            };
            button.setOnClickListener(onClickListener);



//            try {
//                tflite = new Interpreter(loadModelFile());
//            } catch (IOException e) {
//                Log.d("tag", Objects.requireNonNull(e.getMessage()));
//            }

            msensorManager.registerListener(this, msensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
//            msensorManager.registerListener(this, msensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_FASTEST);
//            msensorManager.registerListener(this, msensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
        }


//        public MappedByteBuffer loadModelFile() throws IOException
//        {
//            AssetFileDescriptor fd = this.getAssets().openFd("tf_lite_model.tflite");
//            FileInputStream inputStream = new FileInputStream(fd.getFileDescriptor());
//
//
//            FileChannel fileChannel = inputStream.getChannel();
//            long startOffset = fd.getStartOffset();
//            long declaredLength = fd.getDeclaredLength();
//            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
//        }

        @Override
        protected void onPause() {
            super.onPause();
            msensorManager.unregisterListener(this);
        }


        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }

        public void onSensorChanged(SensorEvent event) {
//            loadNewSensorData(event);
            accelerometerData = event.values.clone();

            if (View1a == null) //|| (View2a == null) || (View3a == null))
            {
                View1a = findViewById(R.id.Value1a);  //
                View1b = findViewById(R.id.Value1b);  // ���� ��������� ���� ��� ������ ���������
                View1c = findViewById(R.id.Value1c);  //
//                View2a = findViewById(R.id.Value2a);  //
//                View2b = findViewById(R.id.Value2b);  // ���� ��������� ���� ��� ������ ���������
//                View2c = findViewById(R.id.Value2c);
//                View3a = findViewById(R.id.Value3a);  //
//                View3b = findViewById(R.id.Value3b);  // ���� ��������� ���� ��� ������ ���������
//                View3c = findViewById(R.id.Value3c);
            }

            View1a.setText(String.valueOf(accelerometerData[0]));
            View1b.setText(String.valueOf(accelerometerData[1]));
            View1c.setText(String.valueOf(accelerometerData[2]));
//            View2a.setText(String.valueOf(accelerationData[0]));
//            View2b.setText(String.valueOf(accelerationData[1]));
//            View2c.setText(String.valueOf(accelerationData[2]));
//            View3a.setText(String.valueOf(gyroscopData[0]));
//            View3b.setText(String.valueOf(gyroscopData[1]));
//            View3c.setText(String.valueOf(gyroscopData[2]));

            if (count == 199) {
                count = 0;
                View2a = findViewById(R.id.Value2a);

                View2a.setText(String.valueOf(System.currentTimeMillis() - first));
                first = System.currentTimeMillis();
//                tflite.run(mass_in, mass_out);
                mass_out = classifier.predictProbabilities(mass_in);
                int max = find_max(mass_out);
                if (prev != mass_out[max] && (System.currentTimeMillis()-start) > 500) {
                    tracker[max]++;
                    start = System.currentTimeMillis();
                    prev = mass_out[max];
              }
//                first = System.currentTimeMillis() - first;
//                Viewres7 = findViewById(R.id.Valueres7);
//                Viewres8 = findViewById(R.id.Valueres8);
//                Viewres7.setText(String.valueOf(first));
//                Viewres8.setText(String.valueOf(max + 1));
                Viewres1 = findViewById(R.id.Valueres1);  //
                Viewres2 = findViewById(R.id.Valueres2);  // ���� ��������� ���� ��� ������ ���������
                Viewres3 = findViewById(R.id.Valueres3);  //
                Viewres4 = findViewById(R.id.Valueres4);  //
                Viewres5 = findViewById(R.id.Valueres5);  // ���� ��������� ���� ��� ������ ���������
                Viewres6 = findViewById(R.id.Valueres6);
                Viewres1.setText(String.valueOf(tracker[0]));
                Viewres2.setText(String.valueOf(tracker[1]));
                Viewres3.setText(String.valueOf(tracker[2]));
                Viewres4.setText(String.valueOf(tracker[3]));
                Viewres5.setText(String.valueOf(tracker[4]));
                Viewres6.setText(String.valueOf(tracker[5]));

            } else {
                int ind = count;
                if (Math.abs(x - accelerometerData[0]) < 0.06 && Math.abs(y - accelerometerData[1]) < 0.06
                        && Math.abs(z-accelerometerData[2]) < 0.6)
                    return ;
                mass_in[ind] = accelerometerData[0];
                mass_in[ind + 200] = accelerometerData[1];
                mass_in[ind + 400] = accelerometerData[2];
//                mass_in[ind + 3] = accelerationData[0];
//                mass_in[ind + 4] = accelerationData[1];
//                mass_in[ind + 5] = accelerationData[2];
//                mass_in[ind + 6] = gyroscopData[0];
//                mass_in[ind + 7] = gyroscopData[1];
//                mass_in[ind + 8] = gyroscopData[2];
                count++;
//                String line = String.valueOf(accelerometerData[0]) + " " + String.valueOf(accelerometerData[1]) +
//                        " " + String.valueOf(accelerometerData[2]) + " " + String.valueOf(accelerationData[0]) +
//                        " " + String.valueOf(accelerationData[1]) + " " + String.valueOf(accelerationData[2]) +
//                        " " + String.valueOf(gyroscopData[0]) + " " + String.valueOf(gyroscopData[1]) + " " +
//                        String.valueOf(gyroscopData[2]) + "\n";

            }
            x = accelerometerData[0];
            y = accelerometerData[1];
            z = accelerometerData[2];
        }

    private int find_max(float[] floats) {
            float max = 0;
            int     j = 0;
            for(int i =0; i < floats.length; i++) {
                if (max < floats[i]) {
                    max = floats[i];
                    j = i;
                }
            }
            return j;
    }

//    private void loadNewSensorData(SensorEvent event) {
//
//            final int type = event.sensor.getType();
//
////            if (type == Sensor.TYPE_LINEAR_ACCELERATION) {
////                accelerationData = event.values.clone();
////            }
//
//            if (type == Sensor.TYPE_ACCELEROMETER) {
//                accelerometerData = event.values.clone();
//            }
//
////            if (type == Sensor.TYPE_GYROSCOPE) {
////                gyroscopData = event.values.clone();
////            }
//        }
}
