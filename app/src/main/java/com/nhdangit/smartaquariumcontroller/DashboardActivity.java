package com.nhdangit.smartaquariumcontroller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.Calendar;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    private String ip, port;

    private Button btnGiveFood, btnGetAllParams;

    private SeekbarVertical seekbarLight;

    private TextView tvTemperature, tvLight, tvWaterLevel;

    private TextView tvToggleLed, tvGiveFood, tvNotification;

    private String warning, light, timeGaveFood = "0", stillFood, waterLevel, temperature;

    private String timestamp;

    private String checkToggleLedStr = "0";

    private String showTime;

    private String timeGiveFood = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        btnGiveFood = findViewById(R.id.btn_give_food);
        btnGetAllParams = findViewById(R.id.btn_get_all_params);

        seekbarLight = findViewById(R.id.seekbar_light);

        tvToggleLed = findViewById(R.id.tv_toggle_led);
        tvGiveFood = findViewById(R.id.tv_give_food);
        tvNotification = findViewById(R.id.tv_notification);

        tvTemperature = findViewById(R.id.tv_temperature);
        tvLight = findViewById(R.id.tv_light);
        tvWaterLevel = findViewById(R.id.tv_water_level);

        btnGetAllParams.setOnClickListener(this);
        btnGiveFood.setOnClickListener(this);

        Intent intent = getIntent();
        ip = intent.getStringExtra(LoginActivity.IPADDRESS);
        port = intent.getStringExtra(LoginActivity.PORT);


        seekbarLight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            String pinNumber = "";

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                pinNumber = "2" + i;
                new HttpRequestAsyncTask(getApplicationContext(), pinNumber, ip, port, "pin").execute();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        timestamp = DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());

        Calendar calendar = Calendar.getInstance();
        showTime = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY) + 11) + ":" + String.valueOf(calendar.get(Calendar.MINUTE) + 13);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        String pinNumber = "";

        switch (view.getId()) {
            case R.id.btn_get_all_params:
                pinNumber = "0" + showTime + "$" + timeGaveFood + "&";
                break;
            case R.id.btn_give_food:
//                startGiveFood();
//                if (timeGiveFood.equals(0) == false) {
//                    PinNumber = "1" + timeGiveFood + "&";
//                }
                pinNumber = "1500&";
        }

        if (view.getId() != -1)
            new HttpRequestAsyncTask(view.getContext(), pinNumber, ip, port, "pin", (Button) view).execute();
    }

    // open dialog to give fish some food
    private void startGiveFood() {
        LayoutInflater inflater = LayoutInflater.from(DashboardActivity.this);
        View subView = inflater.inflate(R.layout.item_give_food, null);
        final EditText edtTime = subView.findViewById(R.id.edt_time_give_food);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set time");
        builder.setMessage("Please enter gram to give food");
        builder.setView(subView);
//        AlertDialog alertDialog = builder.create();
        builder.setCancelable(false);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int time = Integer.parseInt(edtTime.getText().toString()) * 400 / 15;
                timeGiveFood = String.valueOf(time);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(DashboardActivity.this, "Please try to enter gram food again!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    public String sendRequest(String pinNumber, String ipAddress, String portNumber, String param) {
        String ServerResponse = "ERROR";

        try {
            HttpClient Client = new DefaultHttpClient();
            URI url;
            if (pinNumber != null)
                url = new URI("http://" + ipAddress + ":" + portNumber + "/?" + param + "=" + pinNumber);
            else
                url = new URI("http://" + ipAddress + ":" + portNumber + "/?" + param);

            HttpGet getRequest = new HttpGet();
            getRequest.setURI(url);

            HttpResponse response = Client.execute(getRequest);
            InputStream content = null;
            content = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));

            ServerResponse = reader.readLine();
            content.close();
        } catch (URISyntaxException e) {
            ServerResponse = e.getMessage();
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            ServerResponse = e.getMessage();
            e.printStackTrace();
        } catch (IOException e) {
            ServerResponse = e.getMessage();
            e.printStackTrace();
        }
        return ServerResponse;
    }

    private class HttpRequestAsyncTask extends AsyncTask<Void, Void, Void> {

        String requestReply, ipAddress, portNumber;
        Context context;
        String param;
        String pinNumber;
        Button S;

        public HttpRequestAsyncTask(Context context, String pinNumber, String ipAddress, String portNumber, String param) {
            this.context = context;
            this.pinNumber = pinNumber;
            this.ipAddress = ipAddress;
            this.portNumber = portNumber;
            this.param = param;
        }

        public HttpRequestAsyncTask(Context context, String pinNumber, String ipAddress, String portNumber, String param, Button S) {
            this.context = context;
            this.pinNumber = pinNumber;
            this.ipAddress = ipAddress;
            this.portNumber = portNumber;
            this.param = param;
            this.S = S;
        }

        @Override
        protected Void doInBackground(Void... params) {
            showMessage("Data Sent, please wait!");

            if (param.equals("pin"))
                requestReply = sendRequest(pinNumber, ipAddress, portNumber, param);
            else {
                requestReply = sendRequest(null, ipAddress, portNumber, param);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (requestReply.contains("-49")) {
                showMessage("Error! Please try again!");
            } else {
                if (requestReply.contains("all")) {
                    String msg = requestReply;
                    msg = msg.replace("all", "");
                    String[] replies = msg.split(new String("&"));

                    warning = (replies[0]);
                    light = (replies[1]);
                    timeGaveFood = (replies[2]);
                    stillFood = (replies[3]);
                    waterLevel = (replies[4]);
                    temperature = (replies[5]);

                    tvTemperature.setText(temperature + "°C");
                    tvLight.setText(light);
                    tvWaterLevel.setText(waterLevel + "cm");

                    requestReply = "Received Data";

                } else if (requestReply.contains("led")) {
                    String temp = requestReply;
                    temp = temp.replace("led", "");
                    if (temp.equals("OFF")) {
                        tvToggleLed.setText("Light is OFF");
                        checkToggleLedStr = "0";

                        requestReply = "Light is OFF";
                    } else {
                        tvToggleLed.setText("Light is ON");
                        checkToggleLedStr = "1";

                        requestReply = "Light is ON";
                    }
                }
            }

            showMessage(requestReply);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            showMessage("Sending Data! Please wait...");
        }

        private void showMessage(String msg) {
            tvNotification.setText("");
            tvNotification.setText(msg);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}