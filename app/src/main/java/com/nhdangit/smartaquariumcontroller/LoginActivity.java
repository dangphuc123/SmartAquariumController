package com.nhdangit.smartaquariumcontroller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by nhdangit on 5/6/18.
 */

public class LoginActivity extends AppCompatActivity {

    private EditText edtIpAddress, edtPort;
    private Button btnConnect;

    public final static String IPADDRESS = "IP_ADDRESS";
    public final static String PORT = "PORT_NUMBER";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        edtIpAddress = findViewById(R.id.edt_ip_address);
        edtPort = findViewById(R.id.edt_port);

        btnConnect = findViewById(R.id.btn_connect);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtIpAddress.getText().toString().equals("192.168.43.213")
                        && edtPort.getText().toString().equals("100")) {
                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    intent.putExtra(IPADDRESS, edtIpAddress.getText().toString());
                    intent.putExtra(PORT, edtPort.getText().toString());
                    startActivity(intent);
                    btnConnect.setBackgroundResource(R.drawable.btn_connected_wifi);
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid IP Address and Port", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
