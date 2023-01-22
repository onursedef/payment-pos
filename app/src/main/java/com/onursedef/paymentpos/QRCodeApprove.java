package com.onursedef.paymentpos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
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

import java.net.URI;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import io.socket.client.IO;
import io.socket.client.Socket;

public class QRCodeApprove extends AppCompatActivity {

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


            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setComponent(new ComponentName("com.onursedef.salepos", "com.onursedef.salepos.Success"));
            startActivity(intent);
        });

        declineButton.setOnClickListener(view -> {

            Intent intent = new Intent(Intent.CATEGORY_ALTERNATIVE);
            intent.setComponent(new ComponentName("com.onursedef.salepos", "com.onursedef.salepos.Failure"));
            startActivity(intent);
        });
    }
}