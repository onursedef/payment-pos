package com.onursedef.paymentpos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class PaymentSelectionActivity extends AppCompatActivity {

    TextView productNamePlaceholder;
    TextView productPricePlaceholder;
    Button QrCodeButton;
    Button BankButton;

    Socket socket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_selection);

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

        new ConnectTask().execute();

        BankButton.setOnClickListener(view -> {

            QrCodeButton.setEnabled(false);
            BankButton.setEnabled(false);

            String transactionStart = "{\"type\": \"BANK_APPROVE_PENDING\", \"name\": \"" + productName + "\",\"price\": " + productPrice + ", \"code\": \"" + productCode + "\"}";

            new SendTask().execute(transactionStart);

            Intent intent = new Intent(PaymentSelectionActivity.this, WaitingForApprove.class);
            startActivity(intent);
        });
    }

    class ConnectTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // Create a new socket and connect to the server
                socket = new Socket("192.168.1.8", 8000);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class SendTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... messages) {
            try {
                // Send data to the server
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(messages[0].getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}