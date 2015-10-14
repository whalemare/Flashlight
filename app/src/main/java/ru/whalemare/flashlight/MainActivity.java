package ru.whalemare.flashlight;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;


import com.melnykov.fab.FloatingActionButton;

public class MainActivity extends Activity {

    FloatingActionButton fab;
    boolean hasFlash = false; // флаг поддержки устройством камеры
    private Camera camera;

    Animation animation = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
        fab = (FloatingActionButton) findViewById(R.id.fab); // Наша центральная кнопка

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
                fab.setColorNormal(getResources().getColor(R.color.fab_primary));
                fab.setColorPressed(getResources().getColor(R.color.fab_primary_pressed));

                isFlashOn = true;
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
                fab.setColorNormal(getResources().getColor(R.color.fab_primary_off));
                fab.setColorPressed(getResources().getColor(R.color.fab_primary_pressed_off));

                isFlashOn = false;
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
     //   setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        getCamera(); // получаем параметры камеры
    }

    @Override
    protected void onPause() {
        super.onPause();
        turnOffFlash();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (camera!=null)
        {
            camera.release();
            camera = null;
        }
        turnOffFlash();
    }
}