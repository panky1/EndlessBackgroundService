package com.bcil.endlessservicejava;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bcil.endlessservicejava.network.WebCall;
import com.bcil.endlessservicejava.network.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements WebCall {
    private static final String TAG = "MainActivity";
    private Button btnStartService;
    private Button btnStopService;
    private Button btnShowList;
    private Menu menu;
    private TextView tvCount;
    private ImageView ivNotiBell;
    private String getPageName;
    private WebService service;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Endless Service");
        service = new WebService().getInstance();
        service.setActivity(MainActivity.this);
        btnStartService = findViewById(R.id.btnStartService);
        btnStopService = findViewById(R.id.btnStopService);
        btnShowList = findViewById(R.id.btnShowList);
        if (getIntent() != null)
            getPageName = getIntent().getStringExtra(Utils.NAVPAGE);
        getPageName = TAG;
        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Utils().log("START THE FOREGROUND SERVICE ON DEMAND");
                actionOnService(Actions.START);
            }
        });

        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Utils().log("STOP THE FOREGROUND SERVICE ON DEMAND");
                actionOnService(Actions.STOP);
            }
        });
        btnShowList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ViewListActivity.class));
            }
        });
    }

    private void actionOnService(Actions actions) {
        if (new ServiceTracker().getServiceState(this).equals("STOPPED") && actions.equals(Actions.STOP))
            return;
        Intent intent = new Intent(MainActivity.this, EndlessService.class);
        intent.setAction(actions.name());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            new Utils().log("actionOnService:Starting the service in >=26 Mode");
            startForegroundService(intent);
            return;
        }
        new Utils().log("Starting the service in < 26 Mode");
        startService(intent);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        this.menu = menu;
        View notificaitons = menu.findItem(R.id.action_notification).getActionView();
        tvCount = (TextView) notificaitons.findViewById(R.id.tvCount);
        ivNotiBell = (ImageView) notificaitons.findViewById(R.id.ivNotiBell);
        if (getPageName != null) {
            if (getPageName.equals("NotificationUtils") || getPageName.equals("EndlessService")|| getPageName.equals("MainActivity")) {
                if (new Utils().isNetworkAvailable(MainActivity.this)) {
                    service.setDATA("NOTIFICATIONCOUNT" + "~" + "amar");
                    service.setResponseListener(MainActivity.this);
                    service.startToHitService();
                } else {
                    Toast.makeText(this, "Please check internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        }
//        setNotificationCount(tvCount);
        /*t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
                                  @Override
                                  public void run() {
//                                      Log.d(TAG, "MainMenuActivity:callNotificationcount");
                                      setNotificationCount(tvCount);
                                  }

                              },
                0,
                5000);*/

        ivNotiBell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ViewListActivity.class));
            }
        });
        return true;
    }

    @Override
    public void getResponse(String response) throws JSONException {
        if (response != null) {
            List<String> stringList = getMessage(response);
            if (stringList.size() > 0) {
                tvCount.setText(stringList.get(0));
            }
        }
    }

    private List<String> getMessage(String response) throws JSONException {
        JSONArray jsonarray = new JSONArray(response);
        List<String> stringList = new ArrayList<>();
        JSONObject jsonobject = jsonarray.getJSONObject(0);
        stringList.add(String.valueOf(jsonobject.getInt("NOTICOUNT")));
        return stringList;
    }
}
