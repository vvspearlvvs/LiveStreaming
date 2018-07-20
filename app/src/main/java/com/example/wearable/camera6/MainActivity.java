package com.example.wearable.camera6;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.wearable.camera6.Audio.AudioQuality;
//import com.example.wearable.camera6.Rtsp.RtspClient;
import com.example.wearable.camera6.Session.Session;
import com.example.wearable.camera6.Session.SessionBuilder;
import com.example.wearable.camera6.gl.SurfaceView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends Activity implements
        Session.Callback, SurfaceHolder.Callback {


    public final static String TAG = "Main";

    //화면에서 카메라로 비춘 그 surface view
    private static SurfaceView mSurfaceView;

    //rtsp session
    private Session mSession;
   // private static RtspClient mClient;

    //server ip address
    private String STREAM_URL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent firstintent =getIntent();
        STREAM_URL=firstintent.getStringExtra("serverip");

        setContentView(R.layout.activity_main);

        mSurfaceView = (SurfaceView) findViewById(R.id.surface);
        mSurfaceView.getHolder().addCallback(this);
        Log.i(TAG,"카메라로 비춘 화면 초기화");


        //
        initRtsp(); //카메라 화면을 가진 안드로이드가 클라이언트로, rtsp통신 초기화

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) { }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) { }

    @Override
    public void onBitrateUpdate(long bitrate) { }

   /* @Override rtspclient.callback없애고
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }*/


    @Override
    public void onSessionError(int reason, int streamType, Exception e) {
        switch (reason) {
            case Session.ERROR_CAMERA_ALREADY_IN_USE:
                break;
            case Session.ERROR_CAMERA_HAS_NO_FLASH:
                break;
            case Session.ERROR_INVALID_SURFACE:
                break;
            case Session.ERROR_STORAGE_NOT_READY:
                break;
            case Session.ERROR_CONFIGURATION_NOT_SUPPORTED:
                break;
            case Session.ERROR_OTHER:
                break;
        }

        if (e != null) {
            alertError(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onPreviewStarted() { }

    @Override
    public void onSessionConfigured() { }

    @Override
    public void onSessionStarted() { }

    @Override
    public void onSessionStopped() { }

    private void alertError(final String msg) {
        final String error = (msg == null) ? "Unknown error: " : msg;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(error).setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

/*rtspclient.callback없애고
    @Override
    public void onRtspUpdate(int message, Exception exception) {
        switch (message) {
            case RtspClient.ERROR_CONNECTION_FAILED:
         */
/*   case RtspClient.ERROR_WRONG_CREDENTIALS:
                alertError(exception.getMessage());
                exception.printStackTrace();*//*

                break;
        }
    }
*/

    @Override
    protected void onResume() {
        super.onResume();
        toggleStreaming();
    }

    @Override
    protected void onPause() {
        super.onPause();
        toggleStreaming();
    }

    //클라이언트 오면 화면실시간으로 초기화하는 부분, 스트리밍되는 비디오를 와우자서버로
    private void initRtsp(){
        mSession = SessionBuilder.getInstance()
                .setContext(getApplicationContext())
                .setAudioEncoder(SessionBuilder.AUDIO_AAC)
                .setAudioQuality(new AudioQuality(8000,16000))
                .setVideoEncoder(SessionBuilder.VIDEO_H264)
                .setSurfaceView(mSurfaceView)
                .setPreviewOrientation(90)
                .setCallback(this)
                .build();
        Log.i(TAG,"세션까지 연결"+mSession);


        // Configures the RTSP client
       // mClient = new RtspClient();
      //  mClient.setSession(mSession);
      //  mClient.setCallback(this);

        mSurfaceView.setAspectRatioMode(SurfaceView.ASPECT_RATIO_PREVIEW);
        String ip,port,path;

        // We parse the URI written in the Editext
        Pattern uri = Pattern.compile("rtsp://(.+):(\\d+)/(.+)");
        Matcher m = uri.matcher(STREAM_URL);
        m.find();
        ip = m.group(1);
        port = m.group(2);
        path = m.group(3);

      //  mClient.setServerAddress(ip, Integer.parseInt(port));
   //     mClient.setStreamPath("/" + path);

    }

    //스트리밍 껏다꼇다하는거
    private void toggleStreaming(){
        mSession.startPreview();

          /*
        if(!mClient.isStreaming()){
            mSession.startPreview(); //카메라 preview
           mClient.startStream(); //비디오 송수신 stream
            Log.i(TAG,"스트리밍 시작");
        }
        else{
            mSession.stopPreview();
            mClient.stopStream();
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
      //  mClient.release();
        mSession.release();
        mSurfaceView.getHolder().removeCallback(this);
    }



}
