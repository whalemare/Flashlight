package ru.whalemare.flashlight;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends Activity {

    ImageView imageView;
    boolean hasFlash; // флаг поддержки устройством камеры

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);

        // Проверяем поддержку вспышки в камере
        hasFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash)
            Toast.makeText(getApplicationContext(), "Камеры нет", Toast.LENGTH_LONG).show();

        getCamera();
    }

    private Camera camera;
    private Camera.Parameters params;
    private boolean isFlashOn;

    private void getCamera()
    {
        if (camera == null)
        {
            try {
                camera = Camera.open();
                params = camera.getParameters();
            } catch (RuntimeException e) {
                Log.e("CAMERA NE RABOTAIT!11", e.getMessage());
            }
        }
    }

    private void turnOnFlash()
    {
        if (!isFlashOn){ // если вспышка выключена
            if (camera == null || params == null){
                return;
            }
            params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            imageView.setImageResource(R.drawable.flashon); // меняем картинку
            isFlashOn = true;

            Toast.makeText(getApplicationContext(), "Камера включена", Toast.LENGTH_LONG).show();
        }
        else
            Log.d("WHALETAG", "turnOnFlash() - камера включена");
    }

    private void turnOffFlash()
    {
        if (isFlashOn){
            if (camera == null || params == null)
                return;
            params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            camera.release();
            imageView.setImageResource(R.drawable.flashoff); // меняем рисунок
            isFlashOn = false;

            Toast.makeText(getApplicationContext(), "Камера выключена", Toast.LENGTH_SHORT).show();
        }
    }

    public void clickToImage (View view){ // обработчик нажатия
        if (isFlashOn)
            turnOffFlash();
        else
            turnOnFlash();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        turnOffFlash();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasFlash)
            turnOnFlash();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getCamera(); // получаем параметры камеры
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (camera!=null)
        {
            camera.release();
            camera = null;
        }
    }
}