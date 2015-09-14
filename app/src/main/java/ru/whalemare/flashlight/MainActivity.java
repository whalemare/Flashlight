package ru.whalemare.flashlight;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends Activity {

    ImageView imageView;
    boolean hasFlash = false; // флаг поддержки устройством камеры
    private Camera camera;
    Animation animation = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
        imageView.startAnimation(animation);

        // Проверяем поддержку вспышки в камере
        hasFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash) // Если камеры нет TODO открывать другое активити со светлым экраном.
                        // TODO + запомнить, что если нет фонарика, то всегда будет открывтаься оно
            Toast.makeText(getApplicationContext(), "Камеры нет", Toast.LENGTH_LONG).show();
        else {
            getCamera(); // получаем параметры камеры
            turnOnFlash();
        }
    }

    private Camera.Parameters params;
    private boolean isFlashOn;
    public void getCamera() {
        if (camera == null)
            try {
                camera = Camera.open();
                params = camera.getParameters();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
    }

    private void turnOnFlash() {
        if (!isFlashOn) { // если вспышка выключена
            if (camera == null || params == null)
                return;
            else {
                params = camera.getParameters();
                params.setFlashMode(Parameters.FLASH_MODE_TORCH);
                camera.setParameters(params);
                camera.startPreview(); // ?

                imageView.setImageResource(R.drawable.flashon); // меняем картинку
                isFlashOn = true;

                Log.d("WHALETAG", "turnOnFlash() - камера включена" + params.getFlashMode().toString());
            }
        }
    }

    private void turnOffFlash()
    {
        if (isFlashOn){
            if (camera != null) {
                params = camera.getParameters();
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(params);

                imageView.setImageResource(R.drawable.flashoff); // меняем рисунок
                isFlashOn = false;

                Log.d("WHALETAG", "turnOffFlash() - камера выключена -> " + params.getFlashMode().toString());
            }
        }
    }

    public void clickToImage (View view){ // обработчик нажатия
        if (isFlashOn)
            turnOffFlash();
        else
            turnOnFlash();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getCamera(); // получаем параметры камеры
    }

    @Override
    protected void onPause() {
        super.onPause();

        turnOffFlash(); //Временно выключаем фонарик:
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (camera!=null)
        {
            camera.release();
            camera = null;
        }
        Log.d("WHALETAG", "onStop() - зашли в метод");
    }
}