package com.onursedef.paymentpos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;

import io.socket.client.IO;
import io.socket.client.Socket;

public class PaymentSelectionActivity extends AppCompatActivity {

    URI wsUri = URI.create("http://192.168.1.8:3000");
    Socket mSocket = IO.socket(wsUri);

    TextView productNamePlaceholder;
    TextView productPricePlaceholder;
    Button QrCodeButton;
    Button BankButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_selection);

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

            Intent intent = new Intent(PaymentSelectionActivity.this, Failed.class);
            startActivity(intent);
            mSocket.close();
        });

        String productName = getIntent().getStringExtra("name");
        String productPrice = getIntent().getStringExtra("price");
        String productCode = getIntent().getStringExtra("code");

        productNamePlaceholder = findViewById(R.id.productName);
        productNamePlaceholder.setText(productName);

        productPricePlaceholder = findViewById(R.id.productPrice);
        productPricePlaceholder.setText(productPrice);

        QrCodeButton = findViewById(R.id.qrCodeButton);
        BankButton = findViewById(R.id.bankButton);

        QrCodeButton.setOnClickListener(view -> {
            QrCodeButton.setEnabled(false);
            BankButton.setEnabled(false);

            Intent intent = new Intent(PaymentSelectionActivity.this, QRCodeApprove.class);
            intent.putExtra("name", productName);
            intent.putExtra("price", productPrice);
            intent.putExtra("code", productCode);
            startActivity(intent);
        });

        BankButton.setOnClickListener(view -> {
            QrCodeButton.setEnabled(false);
            BankButton.setEnabled(false);

            String transcationStart = "{\"type\": \"BANK_APPROVE_PENDING\", \"name\": \"" + productName + "\",\"price\": " + productPrice + ", \"code\": \"" + productCode + "\"}";

            mSocket.send(transcationStart);

            Intent intent = new Intent(PaymentSelectionActivity.this, WaitingForApprove.class);
            startActivity(intent);
        });
    }
}