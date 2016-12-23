package com.slightech.choujiang;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Rokey on 2016/12/3.
 */

public class EditActivity extends Activity {

    @InjectView(R.id.edt_add_people_id)
    EditText edtAddPeopleId;
    @InjectView(R.id.edt_add_people_name)
    EditText edtAddPeopleName;
    @InjectView(R.id.btn_add)
    Button btnAdd;
    @InjectView(R.id.edt_remove_people)
    EditText edtRemovePeople;
    @InjectView(R.id.btn_remove)
    Button btnRemove;
    @InjectView(R.id.lv_all)
    ListView lvAll;
    @InjectView(R.id.count)
    TextView count;
    @InjectView(R.id.btn_removeall)
    Button btnRemoveall;
    private Button add;
    private Button remove;
    private SharedPreferences namelist;
    private List<People> peoples = new ArrayList<>();
    private List<HashMap<String, Objects>> peopleList = new ArrayList<>();
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_llayout);
        ButterKnife.inject(this);
        init();
        adapter = new SimpleAdapter(this, peopleList, R.layout.item_layout, new String[]{"id", "name"}, new int[]{R.id.name_id, R.id.name});
        lvAll.setAdapter(adapter);
        count.setText("总人数：" + peopleList.size());
    }

    private void init() {
        namelist = getSharedPreferences("namelist", MODE_PRIVATE);
        List<People> objects = Util.loadArray(namelist);
        peoples.clear();
        peoples.addAll(objects);
        peopleList.clear();
        for (People people : peoples) {
            HashMap map = new HashMap();
            map.put("id", people.getId());
            map.put("name", people.getName());
            peopleList.add(map);
        }
    }

    private void saveList() {
        Util.saveArray(namelist, peoples);
    }


    @OnClick({R.id.btn_add, R.id.btn_remove,R.id.btn_removeall})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add: {
                String id = edtAddPeopleId.getText().toString();
                edtAddPeopleId.setText("");
                String name = edtAddPeopleName.getText().toString();
                edtAddPeopleName.setText("");
                if (TextUtils.isEmpty(id) || TextUtils.isEmpty(name)) return;
                People people = new People(name, id);
                peoples.add(people);
                saveList();
                init();
                lvAll.setSelection(peopleList.size() - 1);
                adapter.notifyDataSetChanged();
            }
            break;
            case R.id.btn_remove: {
                String id = edtRemovePeople.getText().toString();
                edtRemovePeople.setText("");
                if (TextUtils.isEmpty(id)) return;
                for (People people : peoples) {
                    if (people.getId().equals(id)) {
                        peoples.remove(people);
                        break;
                    }
                }
                saveList();
                init();
                adapter.notifyDataSetChanged();
            }
            break;
            case R.id.btn_removeall:
                Util.saveArray(namelist, new ArrayList<Object>());
                init();
                adapter.notifyDataSetChanged();
        }
        count.setText("总人数：" + peopleList.size());
    }


}
