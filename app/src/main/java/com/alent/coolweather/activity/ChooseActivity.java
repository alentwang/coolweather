package com.alent.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alent.coolweather.R;
import com.alent.coolweather.model.City;
import com.alent.coolweather.model.County;
import com.alent.coolweather.model.Province;
import com.alent.coolweather.util.HttpCallbackListener;
import com.alent.coolweather.util.HttpUtil;
import com.alent.coolweather.util.Utility;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wyl on 2016/7/10.
 */
public class ChooseActivity extends Activity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ListView lvChoose;
    private TextView tvTitle;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private List<Province> provincesList;
    private List<City> citiesList;
    private List<County> countiesList;
    private ProgressDialog progressDialog;
    private Province selectedProvince;
    private City selectedCity;
    private int currentLevel = LEVEL_PROVINCE;

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choose_area);

        lvChoose = (ListView) findViewById(R.id.lv_choose);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        lvChoose.setAdapter(adapter);

        SQLiteDatabase db = Connector.getDatabase();
        Log.w("TAG", "ChooseActivity onCreate");
        lvChoose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provincesList.get(position);
                    queryCities();

                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = citiesList.get(position);
                    queryCounties();
                }
            }
        });
        queryProvinces();
    }

    private void queryProvinces() {
        provincesList = DataSupport.findAll(Province.class);
        if (provincesList.size() > 0) {
            dataList.clear();
            for (Province p : provincesList) {
                dataList.add(p.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            lvChoose.setSelection(0);
            tvTitle.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        }else {
            queryFromServer(null,"province");
        }
    }

    private void queryCities() {
        citiesList = DataSupport.where("provinceId = ?", "" + selectedProvince.getId()).find(City.class);
        if (citiesList.size() > 0) {
            dataList.clear();
            for (City c : citiesList) {
                dataList.add(c.getCityName());
            }
            adapter.notifyDataSetChanged();
            lvChoose.setSelection(0);
            tvTitle.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    private void queryCounties() {
        countiesList = DataSupport.where("cityId = ?", "" + selectedCity.getId()).find(County.class);
        if (countiesList.size() > 0) {
            dataList.clear();
            for (County c : countiesList) {
                dataList.add(c.getCountyName());
            }
            adapter.notifyDataSetChanged();
            lvChoose.setSelection(0);
            tvTitle.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        }else {
            queryFromServer(selectedCity.getCityCode(), "county");
        }
    }

    private void queryFromServer(final String code, final String type) {
        String address;
        if (TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }else {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if("province".equals(type)) {
                    result = Utility.handleProvincesResponse(response);
                }else if("city".equals(type)){
                    result = Utility.handleCitiesResponse(response, selectedProvince.getId());
                }else if("county".equals(type)) {
                    result = Utility.handleCountiesResponse(response, selectedCity.getId());
                }

                if(result) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)) {
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    /**
     * 显示进度
     */
    private void showProgressDialog() {
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("loading");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if(progressDialog != null) {
            progressDialog.dismiss();
        }

    }

    /**
     * back建的功能
     */
    @Override
    public void onBackPressed() {
        if(currentLevel == LEVEL_COUNTY) {
            queryCities();
        }else if(currentLevel == LEVEL_CITY) {
            queryProvinces();
        }else{

            finish();
        }
    }

}
