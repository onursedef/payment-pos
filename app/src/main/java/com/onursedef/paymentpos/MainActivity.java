package com.onursedef.paymentpos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.UUID;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    URI wsUri = URI.create("http://192.168.1.8:3000");
    Socket mSocket = IO.socket(wsUri);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSocket.connect();

        mSocket.on(Socket.EVENT_CONNECT, args -> {
            Log.i("websocket", "client connected");
        });

        mSocket.on(Socket.EVENT_CONNECT_ERROR, args -> {
           Log.e("websocket", "couldn't connect");
        });

        mSocket.on(Socket.EVENT_DISCONNECT, args -> {
           mSocket.close();
        });

        mSocket.on("message", args -> {
            String message = args[0].toString();
            try {
                JSONObject json = new JSONObject(message);
                String type =json.getString("type");

                if (type.equals("PRODUCT_PAYMENT_SEND")) {
                    Intent intent = new Intent(MainActivity.this, PaymentSelectionActivity.class);
                    intent.putExtra("name", json.getString("name"));
                    intent.putExtra("price", json.getString("price"));
                    intent.putExtra("code", json.getString("code"));
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }
}