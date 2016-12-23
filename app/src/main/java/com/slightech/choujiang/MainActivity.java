package com.slightech.choujiang;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @InjectView(R.id.btn_reset)
    Button btnReset;
    @InjectView(R.id.btn_edit)
    Button btnEdit;
    @InjectView(R.id.lv_name)
    ListView lvName;
    @InjectView(R.id.luck_number)
    EditText luckNumber;
    @InjectView(R.id.btn_choujiang)
    Button btnChoujiang;
    private SharedPreferences namelist;
    private List<HashMap<String, Objects>> peopleList = new ArrayList<>();
    private List<People> peoples = new ArrayList<>();
    private List<People> luckPeople = new ArrayList<>();
    private List<Integer> luckNumbers = new ArrayList<>();
    private static boolean STOP = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    int arg1 = msg.arg1;
                    luckNumbers.clear();
                    luckPeople.clear();
                    peopleList.clear();
                    if (arg1 > peoples.size()) {
                        arg1 = peoples.size();
                    }
                    for (int i = 0; i < arg1; i++) {
                        int luckNumber = getLuckNumber();
                        while (luckNumbers.contains(luckNumber)) {
                            luckNumber = getLuckNumber();
                        }
                        luckNumbers.add(luckNumber);
                    }
                    for (Integer number : luckNumbers) {
                        luckPeople.add(peoples.get(number));
                    }
                    for (People people : luckPeople) {
                        HashMap map = new HashMap();
                        map.put("id", people.getId());
                        map.put("name", people.getName());
                        peopleList.add(map);
                    }
                    adapter.notifyDataSetChanged();
                    break;
                case 1:
                    peoples.removeAll(luckPeople);
                    break;
                default:
                    break;
            }
        }
    };
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        init();
        adapter = new SimpleAdapter(this, peopleList, R.layout.item_layout, new String[]{"id", "name"}, new int[]{R.id.name_id, R.id.name});
        lvName.setAdapter(adapter);
    }

    private void init() {
        namelist = getSharedPreferences("namelist", MODE_PRIVATE);
        List<People> objects = Util.loadArray(namelist);
        peoples.clear();
        peoples.addAll(objects);

    }

    @OnClick({R.id.btn_choujiang, R.id.btn_reset, R.id.btn_edit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_choujiang:
                if (btnChoujiang.getText().toString().equals("开始抽奖")) {
                    STOP = false;
                    if (TextUtils.isEmpty(luckNumber.getText().toString()))return;
                    final int number = Integer.valueOf(luckNumber.getText().toString());

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (!STOP) {
                                 Message message = Message.obtain();
                                message.what = 0;
                                message.arg1 = number;
                                handler.sendMessage(message);
                                try {
                                    Thread.sleep(50);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    thread.start();
                    btnChoujiang.setText("停止");
                } else if (btnChoujiang.getText().toString().equals("停止")) {
                    STOP = true;
                    btnChoujiang.setText("开始抽奖");
                    handler.sendEmptyMessage(1);
                }
                break;
            case R.id.btn_reset:
                init();
                peopleList.clear();
                adapter.notifyDataSetChanged();
                break;
            case R.id.btn_edit:
                Intent intent = new Intent(this, EditActivity.class);
                startActivity(intent);
                break;
        }
        InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }

    public int getLuckNumber() {
        int luckID = (int) (Math.random() * peoples.size());
        return luckID;
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }
}
