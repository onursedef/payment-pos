package com.onursedef.paymentpos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;

public class WaitingForApprove extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting_for_approve);

        new Thread(() -> {
            try {

                Socket socket = new Socket("192.168.1.8", 8000);

                // Continuously read from the input stream
                while (true) {
                    byte[] buffer = new byte[1024];
                    int bytesRead = socket.getInputStream().read(buffer);
                    if (bytesRead > 0) {
                        String message = new String(buffer, 0, bytesRead);
                        JSONObject json = new JSONObject(message);
                        String type = json.getString("type");

                        Log.i("ws", message);

                        if (type.equals("BANK_APPROVE_APPROVED")) {
                            // navigate to success page
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setComponent(new ComponentName("com.onursedef.salepos", "com.onursedef.salepos.Success"));
                            startActivity(intent);
                        } else if (type.equals("BANK_APPROVE_DECLINED")) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
                            intent.setComponent(new ComponentName("com.onursedef.salepos", "com.onursedef.salepos.Failure"));
                            startActivity(intent);
                        }
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
