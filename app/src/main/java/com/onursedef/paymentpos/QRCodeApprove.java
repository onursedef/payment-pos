package com.onursedef.paymentpos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.encoder.QRCode;

import java.net.URI;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import io.socket.client.IO;
import io.socket.client.Socket;

public class QRCodeApprove extends AppCompatActivity {

    URI wsUri = URI.create("http://192.168.1.8:3000");
    Socket mSocket = IO.socket(wsUri);

    ImageView qrCode;
    TextView productNameText;
    TextView productPriceText;
    Button acceptButton;
    Button declineButton;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code_approve);

        mSocket.connect();

        mSocket.on(Socket.EVENT_CONNECT, args -> {
            Log.i("websocket", "client connected");
        });

        mSocket.on(Socket.EVENT_CONNECT_ERROR, args -> {
            Log.e("websocket", "couldn't connect");
        });

        mSocket.on(Socket.EVENT_DISCONNECT, args -> {
            String json = "{\"type\": \"QRCODE_DECLINED\"}";

            mSocket.send(json);

            Intent intent = new Intent(QRCodeApprove.this, Failed.class);
            startActivity(intent);
            mSocket.close();
        });

        String productName = getIntent().getStringExtra("name");
        String productPrice = getIntent().getStringExtra("price");
        String productCode = getIntent().getStringExtra("code");

        qrCode = findViewById(R.id.qrCodeImage);

        productNameText = findViewById(R.id.productNameQr);
        productNameText.setText(productName);

        productPriceText = findViewById(R.id.productPriceQr);
        productPriceText.setText(productPrice);

        acceptButton = findViewById(R.id.acceptButton);

        declineButton = findViewById(R.id.declineButton);


        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);

        Display display = manager.getDefaultDisplay();

        Point point = new Point();
        display.getSize(point);

        int width = point.x;
        int height = point.y;

        int dimension = Math.min(width, height);
        dimension = dimension * 3 / 4;

        String qrJson = "{\"name\": \"" + productName + "\", \"price\": " + productPrice + ", \"code\": \"" + productCode + "\" }";

        qrgEncoder = new QRGEncoder(qrJson, null, QRGContents.Type.TEXT, dimension);

        bitmap = qrgEncoder.getBitmap();

        qrCode.setImageBitmap(bitmap);

        acceptButton.setOnClickListener(view -> {
            String json = "{\"type\": \"QRCODE_APPROVED\", \"name\": \"" + productName + "\", \"price\": " + productPrice + ", \"code\": \"" + productCode + "\" }";

            mSocket.send(json);

            Intent intent = new Intent(QRCodeApprove.this, Success.class);
            startActivity(intent);
        });

        declineButton.setOnClickListener(view -> {
            String json = "{\"type\": \"QRCODE_DECLINED\", \"name\": \"" + productName + "\", \"price\": " + productPrice + ", \"code\": \"" + productCode + "\" }";

            mSocket.send(json);

            Intent intent = new Intent(QRCodeApprove.this, Failed.class);
            startActivity(intent);
        });
    }
}