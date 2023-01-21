package com.onursedef.paymentpos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

import io.socket.client.IO;
import io.socket.client.Socket;

public class WaitingForApprove extends AppCompatActivity {

    URI wsUri = URI.create("http://192.168.1.8:3000");
    Socket mSocket = IO.socket(wsUri);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting_for_approve);

        mSocket.connect();

        mSocket.on(Socket.EVENT_CONNECT, args -> {
            Log.i("websocket", "client connected");
        });

        mSocket.on(Socket.EVENT_CONNECT_ERROR, args -> {
            Log.e("websocket", "couldn't connect");
        });

        mSocket.on(Socket.EVENT_DISCONNECT, args -> {
            String json = "{\"type\": \"BANK_DECLINED\"}";

            mSocket.send(json);

            Intent intent = new Intent(WaitingForApprove.this, Failed.class);
            startActivity(intent);
            mSocket.close();
        });

        mSocket.on("message", args -> {
           String message = args[0].toString();

           try {
               JSONObject json = new JSONObject(message);
               String type = json.getString("type");

               if (type.equals("BANK_APPROVED")) {
                   Intent intent = new Intent(WaitingForApprove.this, Success.class);
                   startActivity(intent);
               } else if (type.equals("BANK_DECLINED")) {
                   Intent intent = new Intent(WaitingForApprove.this, Failed.class);
                   startActivity(intent);
               }
           } catch (JSONException e) {
               e.printStackTrace();
           }
        });
    }
}