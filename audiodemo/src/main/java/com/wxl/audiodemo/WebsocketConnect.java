package com.wxl.audiodemo;

import android.app.Service;
import android.media.AudioRecord;
import android.os.Handler;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class WebsocketConnect extends WebSocketClient  {


    public WebsocketConnect(URI serverUri) {
        super(serverUri, new Draft_6455());
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.e("Client", "onOpen()");
    }


    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.e("Client", "onClose()");
    }

    @Override
    public void onError(Exception ex) {
        Log.e("Client", "onError()"+ex);
    }

    @Override
    public void onMessage(String message) {
        //message就是接收到的消息
        Log.e("Service", message);
    }

}

