package com.wxl.audiodemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public  class MainActivity extends AppCompatActivity  implements View.OnClickListener{

    private static final int MY_PERMISSIONS_REQUEST = 1001;
    private static final String TAG = "jqd";
    private Button mBtnControl;
    private List<String> mPermissionList = new ArrayList<>();
    private AudioRecorder audioRecorder = new AudioRecorder();
    private Context mContext;
    private ExtAudioRecorder extAudioRecorder;
    String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "media.wav";




    /**
     * 需要申请的运行时权限
     */
    private String[] permissions = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE
    };
    /**
     * 被用户拒绝的权限列表
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, permissions[i] + " 权限被用户禁止！");
                }
            }
            // 运行时权限的申请不是本demo的重点，所以不再做更多的处理，请同意权限申请。
        }
    }

    private void checkPermissions() {
        // Marshmallow开始才用申请运行时权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(this, permissions[i]) !=
                        PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);
                }
            }
            if (!mPermissionList.isEmpty()) {
                String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);
                ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST);
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extAudioRecorder = ExtAudioRecorder.getInstanse(false);

        mContext=MainActivity.this;
        setContentView(R.layout.activity_main);
        mBtnControl = (Button) findViewById(R.id.btn_control);
        mBtnControl.setOnClickListener(this);
        checkPermissions();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_control:
                Button button = (Button) view;
                if (button.getText().toString().equals(getString(R.string.start_record))) {
                    button.setText(getString(R.string.stop_record));
                    //audioRecorder.startRecord();
                    extAudioRecorder.setOutputFile(filePath);
                    extAudioRecorder.prepare();
                    extAudioRecorder.start();
                } else {
                    button.setText(getString(R.string.start_record));
                    //audioRecorder.stopRecord();
                    extAudioRecorder.stop();
                    extAudioRecorder.release();

                    }
            default:
                break;
        }
    }



 /*   class AudioRecordThread implements Runnable {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void run() {
            ByteBuffer audioBuffer = ByteBuffer.allocateDirect(bufferSizeInBytes * 100).order(ByteOrder.LITTLE_ENDIAN);
            int readSize = 0;
            Log.d(TAG, "isRecord=" + isRecord);
            while (isRecord) {
                readSize = audioRecord.read(audioBuffer, audioBuffer.capacity());
                if (readSize == AudioRecord.ERROR_INVALID_OPERATION || readSize == AudioRecord.ERROR_BAD_VALUE) {
                    Log.d("NLPService", "Could not read audio data.");
                    break;
                }
                boolean send = webSocket.send(ByteString.of(audioBuffer));
                Log.d("NLPService", "send=" + send);
                audioBuffer.clear();
            }

            webSocket.send("EOS");
        }
    } */

    /*Thread streamThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(isRecording == true) {
                    int read = audioRecord.read(data, 0, minBufferSize);
                    if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                        manager.send(data);
                        //判断无误后发送bytes
                    }
                }
                if(isRecording == false){
                    manager.send("EOS");
                }
            }
        });
        streamThread.start();*/
}




