package com.wxl.audiodemo;


import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;


import static com.wxl.audiodemo.GlobalConfig.AUDIO_FORMAT;
import static com.wxl.audiodemo.GlobalConfig.CHANNEL_CONFIG;
import static com.wxl.audiodemo.GlobalConfig.SAMPLE_RATE_INHZ;

import org.java_websocket.handshake.ServerHandshake;

public class AudioRecorder {


    private static final String TAG = "jqd";
    private volatile boolean isRecord;// 设置正在录制的状态
    private int minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT);
    private AudioRecord audioRecord;
    private byte data[] = new byte[minBufferSize];
    private boolean isConnect=false;

    private URI uri = URI.create("ws://192.168.1.100:8888/client/ws/speech");

    WebsocketConnect client = new WebsocketConnect(uri) {
        @Override
        public void onMessage(String message) {
            //message就是接收到的消息
            Log.e("Service", message);
        }

        @Override
        public void onError(Exception ex) {
            Log.e("Client", "onError()"+ex);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            super.onOpen(handshakedata);
            Log.e("Client", "success");

        }
    };

    public void startRecord() {
        //1.connect
        try {
            //connectBlocking多出一个等待操作，会先连接再发送，否则未连接发送会报错
            client.connectBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

            //2.create
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT, minBufferSize);

            //3.record
            isRecord = true;
            audioRecord.startRecording();

            //4.send
            Thread streamThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(isRecord == true) {
                    int read = audioRecord.read(data, 0, minBufferSize);
                    if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                        client.send(data);
                        //判断无误后发送bytes
                    }
                }
                if(isRecord== false){
                    client.send("EOS");
                }
            }
            });
            streamThread.start();

            //5.messege

    }

    public void stopRecord () {
        isRecord = false;

        // 释放资源
        if (null != audioRecord) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
            //recordingThread = null;
        }
        try {
            if (null != client) {
                client.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client = null;
        }


    }

}
