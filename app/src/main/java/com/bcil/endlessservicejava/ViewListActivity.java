package com.bcil.endlessservicejava;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bcil.endlessservicejava.network.WebCall;
import com.bcil.endlessservicejava.network.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewListActivity extends AppCompatActivity implements WebCall {

    private RecyclerView rvList;
    private WebService service;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);
        initView();
        initData();
    }

    private void initData() {
        service = new WebService().getInstance();
        service.setActivity(ViewListActivity.this);
        if (new Utils().isNetworkAvailable(ViewListActivity.this)) {
            service.setDATA("NOTIFICATIONLISTDATA" + "~" + "amar");
            service.setResponseListener(ViewListActivity.this);
            service.startToHitService();
        } else {
            Toast.makeText(this, "Please check internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        rvList = findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(ViewListActivity.this));
    }

    @Override
    public void getResponse(String response) throws JSONException {
        if (response != null) {
            List<MsgInfo> stringList = getMessage(response);
            if (stringList.size() > 0) {
                MsgListAdapter msgListAdapter = new MsgListAdapter(ViewListActivity.this, stringList);
                rvList.setAdapter(msgListAdapter);
                msgListAdapter.notifyDataSetChanged();
            }
        } else {
            Toast.makeText(this, "Something went wrong,please try again", Toast.LENGTH_SHORT).show();
        }
    }

    private List<MsgInfo> getMessage(String response) throws JSONException {
        JSONArray jsonarray = new JSONArray(response);
        List<MsgInfo> stringList = new ArrayList<>();
        for (int i = 0; i < jsonarray.length(); i++) {
            JSONObject jsonobject = jsonarray.getJSONObject(i);
            stringList.add(new MsgInfo(jsonobject.getString("Msg"),jsonobject.getString("scanon")));
        }
        return stringList;

    }
}
