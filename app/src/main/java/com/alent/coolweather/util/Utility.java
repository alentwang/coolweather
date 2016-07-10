package com.alent.coolweather.util;

import android.text.TextUtils;

import com.alent.coolweather.model.City;
import com.alent.coolweather.model.County;
import com.alent.coolweather.model.Province;

/**
 * Created by wyl on 2016/7/10.
 */
public class Utility {

    public static boolean handleProvincesResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                for (String s : allProvinces) {
                    String[] array = s.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0].trim());
                    province.setProvinceName(array[1].trim());
                    province.save();
                }
                return true;
            }
        }
        return false;
    }

    public static boolean handleCitiesResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String s : allCities) {
                    String[] array = s.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0].trim());
                    city.setCityName(array[1].trim());
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            }
        }
        return false;
    }

    public static boolean handleCountiesResponse(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0) {
                for (String s : allCounties) {
                    String[] array = s.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0].trim());
                    county.setCountyName(array[1].trim());
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            }
        }
        return false;
    }
}
