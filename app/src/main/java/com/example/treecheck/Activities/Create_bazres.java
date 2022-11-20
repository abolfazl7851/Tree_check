package com.example.treecheck.Activities;

import androidx.appcompat.app.AppCompatActivity;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import ir.hamsaa.persiandatepicker.Listener;
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.util.PersianCalendar;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.service.autofill.RegexValidator;
import android.text.InputType;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.treecheck.Date_convertor.DateConvertor;
import com.example.treecheck.Db.Db_helper;
import com.example.treecheck.Models.City_response;
import com.example.treecheck.Models.Country_response;
import com.example.treecheck.Models.Province_response;
import com.example.treecheck.Models.ResponseLogin;
import com.example.treecheck.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Create_bazres extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {

        //Implement this for api 28 and below
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
        }
        //Or implement this for api 29 and above
        else {
            super.attachBaseContext(newBase);
        }
    }

    Typeface spinner_typeface;
    EditText b_name,b_family,b_username,b_password,b_email,b_mobile;
    TextView code_bazresi,date_tx ;
    Spinner spinner_country,spinner_province,spinner_city;
    Button btn_date,btn_save,btn_getcode,btn_show_password;
    String date="null";
    int country_id=0,province_id=0,city_id=0;
    ProgressDialog progressDialog;
    RequestQueue queue ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_bazres);
        spinner_typeface= Typeface.createFromAsset(getAssets(), "fonts/IRANSans_Light.ttf");
        queue= Volley.newRequestQueue(getApplicationContext());
        progressDialog=new ProgressDialog(Create_bazres.this);
        spinner_country=findViewById(R.id.spinner_country);
        set_spinner_country_adapter();
        spinner_province=findViewById(R.id.spinner_province);
        spinner_city=findViewById(R.id.spinner_city);
        b_name=findViewById(R.id.bazres_name);
        b_family=findViewById(R.id.bazres_family);
        b_username=findViewById(R.id.bazres_username);
        b_password=findViewById(R.id.bazres_password);
        b_email=findViewById(R.id.bazres_email);
        b_mobile=findViewById(R.id.bazres_mobile);
        btn_getcode=findViewById(R.id.bazres_getcode_btn);
        btn_show_password=findViewById(R.id.show_pass_bazres);
        code_bazresi=findViewById(R.id.bazres_code_tx);
        date_tx=findViewById(R.id.bazres_date_tx);
        code_bazresi.setText(null);
        btn_date=findViewById(R.id.bazres_date_btn);
        btn_save=findViewById(R.id.btn_save_bazres);
        btn_getcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("یزار یه کد بگیرم...");
                progressDialog.show();
                queue.add(get_code_bazresi()) ;

            }
        });
        btn_show_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (b_password.getInputType()== InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD){
                    b_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    btn_show_password.setBackgroundDrawable(getResources().getDrawable(R.drawable.password_show));
                }else {
                    b_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    btn_show_password.setBackgroundDrawable(getResources().getDrawable(R.drawable.password_lock));
                }
                b_password.setSelection(b_password.getText().length());
            }
        });
        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pikerdialog();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (b_name.getText().toString().trim().isEmpty()){
                    b_name.setError("این فیلد نباید خالی باشد!");
                }if (b_family.getText().toString().trim().isEmpty()){
                    b_family.setError("این فیلد نباید خالی باشد!");
                }if (b_username.getText().toString().trim().isEmpty()){
                    b_username.setError("این فیلد نباید خالی باشد!");
                }if (b_password.getText().toString().trim().isEmpty()){
                    b_password.setError("این فیلد نباید خالی باشد!");
                }if (code_bazresi.getText().toString().trim().isEmpty()){
                    code_bazresi.setError("روی دکمه دریافت کد بازرسی کلیک کن و کد بگیر.");
                }if (date_tx.getText().toString().trim().isEmpty()){
                    date_tx.setError("تاریخ ایجاد رو بزن.");
                }if (b_email.getText().toString().trim().isEmpty()){
                    b_email.setError("ایمیل را وارد کنید.");
                }if (b_mobile.getText().toString().trim().isEmpty()){
                    b_mobile.setError("این فیلد نباید خالی باشد!");
                }else if (emailValidator(b_email.getText().toString().trim())){

                    if (isValidMobileNumber(b_mobile.getText().toString().trim())) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Create_bazres.this);
                        alertDialog.setTitle("ارسال اطلاعات")
                                .setMessage("مطمئنی همه چیو درست انتخاب کردی؟ بفرستمش؟")
                                .setPositiveButton("آره بابا", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        progressDialog.setMessage("یکم صبر کن...");
                                        progressDialog.show();
                                        queue.add(send_bazres_information());
                                    }
                                })
                                .setNegativeButton("بزار یه بار دیگه چک کنم", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                }else {
                    b_email.setError("ایمیل نامعتبر است!");
                }
            }
        });
    }

    private StringRequest get_code_bazresi() {
        String url="https://mhnaddaf.ir/Tree_manager/get_random_key.php";
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                code_bazresi.setText(response);
                code_bazresi.setError(null);
                btn_getcode.setClickable(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                check_Enternet_with_volley_error(error);
            }

        });
        return stringRequest ;
    }

    private void Pikerdialog() {

        PersianCalendar initDate = new PersianCalendar();
        initDate.setPersianDate(1399, 7, 15);
        final String TAG="date picker" ;
        PersianDatePickerDialog picker = new PersianDatePickerDialog(this)
                .setPositiveButtonString("باشه")
                .setNegativeButton("بیخیال")
                .setTodayButton("امروز")
                .setTodayButtonVisible(true)
                .setMinYear(1300)
                .setMaxYear(PersianDatePickerDialog.THIS_YEAR)
                .setInitDate(initDate)
                .setActionTextColor(getResources().getColor(R.color.app_theme_color))
                .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
                .setShowInBottomSheet(true)
                .setListener(new Listener() {
                    @Override
                    public void onDateSelected(PersianCalendar persianCalendar) {
//                        Log.d(TAG, "onDateSelected: "+persianCalendar.getGregorianChange());//Fri Oct 15 03:25:44 GMT+04:30 1582
//                        Log.d(TAG, "onDateSelected: "+persianCalendar.getTimeInMillis());//1583253636577
//                        Log.d(TAG, "onDateSelected: "+persianCalendar.getTime());//Tue Mar 03 20:10:36 GMT+03:30 2020
//                        Log.d(TAG, "onDateSelected: "+persianCalendar.getDelimiter());//  /
//                        Log.d(TAG, "onDateSelected: "+persianCalendar.getPersianLongDate());// سه‌شنبه  13  اسفند  1398
//                        Log.d(TAG, "onDateSelected: "+persianCalendar.getPersianLongDateAndTime()); //سه‌شنبه  13  اسفند  1398 ساعت 20:10:36
//                        Log.d(TAG, "onDateSelected: "+persianCalendar.getPersianMonthName()); //اسفند
//                        Log.d(TAG, "onDateSelected: "+persianCalendar.isPersianLeapYear());//false
//                        Toast.makeText(Create_bazres.this, persianCalendar.getPersianYear() + "/" + persianCalendar.getPersianMonth() + "/" + persianCalendar.getPersianDay(), Toast.LENGTH_SHORT).show();
                        DateConvertor dateConvertor=new DateConvertor();
                        dateConvertor.PersianToGregorian(persianCalendar.getPersianYear(),persianCalendar.getPersianMonth(),persianCalendar.getPersianDay());
                        date_tx.setText(persianCalendar.getPersianYear()+"-"+persianCalendar.getPersianMonth()+"-"+persianCalendar.getPersianDay());
                        date_tx.setError(null);
                        date=dateConvertor.toString();
                    }

                    @Override
                    public void onDismissed() {
                        if (date_tx.getText().toString().isEmpty())
                        Toast.makeText(Create_bazres.this, "شما باید یک تاریخ ایجاد تعیین کنید!", Toast.LENGTH_SHORT).show();
                    }
                });

        picker.setCancelable(false);
        picker.show();
    }

    public void set_spinner_country_adapter(){
        List<Country_response> country_responses=new ArrayList<>();
        ArrayAdapter<Country_response> country_responseArrayAdapter;
        Db_helper db_helper=new Db_helper(getApplicationContext());
        db_helper.initialize_db();
        Cursor cursor=db_helper.get_countries();
        if (cursor.getCount()>0){
            while (cursor.moveToNext()){
                Country_response country_response=new Country_response(cursor.getInt(0),cursor.getString(1)
                ,cursor.getString(2),cursor.getString(3));
                country_responses.add(country_response);
            }
        }
        country_responseArrayAdapter=new ArrayAdapter<Country_response>(getApplicationContext()
                ,R.layout.support_simple_spinner_dropdown_item,country_responses){public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            ((TextView) v).setTextSize(15);
            ((TextView) v).setTypeface(spinner_typeface);
            ((TextView) v).setGravity(Gravity.CENTER);
            ((TextView) v).setTextColor(getResources().getColor(R.color.colorPrimary));
            return v;
        }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);

                ((TextView) v).setTextSize(15);
                ((TextView) v).setTypeface(spinner_typeface);
                ((TextView) v).setGravity(Gravity.CENTER);
                ((TextView) v).setTextColor(getResources().getColor(R.color.app_theme_color));
                return v;
            }
        };
        spinner_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            final int[] check = {0};
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Country_response country_response= (Country_response) parent.getSelectedItem();
                country_id=country_response.getId();
                set_spinner_province_adapter(country_response);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_country.setAdapter(country_responseArrayAdapter);
    }

    public void set_spinner_province_adapter(Country_response country_response){
        List<Province_response> province_responseList=new ArrayList<>();
        ArrayAdapter<Province_response> province_responseArrayAdapter;
        Db_helper db_helper=new Db_helper(getApplicationContext());
        db_helper.initialize_db();
        Cursor cursor=db_helper.get_province_with_country_id(country_response.getId());
        if (cursor.getCount()>0){
            while (cursor.moveToNext()){
                Province_response province_response=new Province_response(cursor.getInt(0),cursor.getInt(1)
                        ,cursor.getString(2),cursor.getString(3),cursor.getString(4));
                province_responseList.add(province_response);
            }
        }
        province_responseArrayAdapter=new ArrayAdapter<Province_response>(getApplicationContext()
                ,R.layout.support_simple_spinner_dropdown_item,province_responseList){public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            ((TextView) v).setTextSize(15);
            ((TextView) v).setTypeface(spinner_typeface);
            ((TextView) v).setGravity(Gravity.CENTER);
            ((TextView) v).setTextColor(getResources().getColor(R.color.colorPrimary));
            return v;
        }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);

                ((TextView) v).setTextSize(15);
                ((TextView) v).setTypeface(spinner_typeface);
                ((TextView) v).setGravity(Gravity.CENTER);
                ((TextView) v).setTextColor(getResources().getColor(R.color.app_theme_color));
                return v;
            }
        };
        spinner_province.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            final int[] check = {0};
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Province_response province_response= (Province_response) parent.getSelectedItem();
                province_id=province_response.getId();
                set_spinner_city_adapter(province_response);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_province.setAdapter(province_responseArrayAdapter);
    }

    public void set_spinner_city_adapter(Province_response province_response){
        List<City_response> city_responseList=new ArrayList<>();
        ArrayAdapter<City_response> city_responseArrayAdapter;
        Db_helper db_helper=new Db_helper(getApplicationContext());
        db_helper.initialize_db();
        Cursor cursor=db_helper.get_city_with_province_id(province_response.getId());
        if (cursor.getCount()>0){
            while (cursor.moveToNext()){
                City_response city_response=new City_response(cursor.getInt(0),cursor.getInt(1)
                        ,cursor.getString(2),cursor.getString(3),cursor.getString(4));
                city_responseList.add(city_response);
            }
        }
        city_responseArrayAdapter=new ArrayAdapter<City_response>(getApplicationContext()
                ,R.layout.support_simple_spinner_dropdown_item,city_responseList){public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            ((TextView) v).setTextSize(15);
            ((TextView) v).setTypeface(spinner_typeface);
            ((TextView) v).setGravity(Gravity.CENTER);
            ((TextView) v).setTextColor(getResources().getColor(R.color.colorPrimary));
            return v;
        }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);

                ((TextView) v).setTextSize(15);
                ((TextView) v).setTypeface(spinner_typeface);
                ((TextView) v).setGravity(Gravity.CENTER);
                ((TextView) v).setTextColor(getResources().getColor(R.color.app_theme_color));
                return v;
            }
        };
        spinner_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            final int[] check = {0};
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                City_response city_response= (City_response) parent.getSelectedItem();
                city_id=city_response.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_city.setAdapter(city_responseArrayAdapter);
    }

    public StringRequest send_bazres_information(){
        String url="https://mhnaddaf.ir/Tree_manager/insert_to_bazres.php";
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Gson gson =  new Gson() ;
                ResponseLogin responseLogin =  gson.fromJson(response , ResponseLogin.class) ;
                if (responseLogin.isStatus()==false){
                    Toast.makeText(Create_bazres.this, ""+responseLogin.getMessage(), Toast.LENGTH_SHORT).show();
                }else if (responseLogin.isStatus()){
                    Toast.makeText(Create_bazres.this, ""+responseLogin.getMessage(), Toast.LENGTH_SHORT).show();btn_getcode.setClickable(true);
                }else
                Toast.makeText(Create_bazres.this, ""+responseLogin.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                check_Enternet_with_volley_error(error);
            }

        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("name", b_name.getText().toString().trim());
                map.put("family", b_family.getText().toString().trim());
                map.put("username", b_username.getText().toString().trim());
                map.put("password", b_password.getText().toString().trim());
                map.put("bazresi_code", code_bazresi.getText().toString().trim());
                map.put("date", date);
                map.put("country", String.valueOf(country_id));
                map.put("province", String.valueOf(province_id));
                map.put("city", String.valueOf(city_id));
                map.put("email", b_email.getText().toString().trim());
                map.put("mobile", b_mobile.getText().toString().trim());
                return map;
            }
        };
        return  stringRequest;
    }

    public void check_Enternet_with_volley_error(VolleyError volleyError)
    {

        String message = null;
        if (volleyError instanceof NetworkError) {
            //  message = "اتصال به اینترنت برقرار نیست ، لطفا  اتصال به اینترنت بررسی نمایید";
            message = "اتصال به اینترنت برقرار نیست ، لطفا  اتصال به اینترنت بررسی نمایید";
        } else if (volleyError instanceof ServerError) {
            message = "مشکل در ارتباط با سرور ، لطفادر زمان دیگری تلاش کنید.";
            // message = "مشکل در ارتباط با سرور ، لطفادر زمان دیگری تلاش کنید.";
        } else if (volleyError instanceof AuthFailureError) {
            //  message = "اتصال به اینترنت برقرار نیست ، لطفا  اتصال به اینترنت بررسی نمایید";
            message = "اتصال به اینترنت برقرار نیست ، لطفا  اتصال به اینترنت بررسی نمایید";
        } else if (volleyError instanceof ParseError) {
            message = "Parsing error! Please try again after some time!!";
            //  message = "اتصال به اینترنت برقرار نیست ، لطفا  اتصال به اینترنت بررسی نمایید";
        } else if (volleyError instanceof NoConnectionError) {
            //   message = "اتصال به اینترنت برقرار نیست ، لطفا  اتصال به اینترنت بررسی نمایید";
            message = "اتصال به اینترنت برقرار نیست ، لطفا  اتصال به اینترنت بررسی نمایید";
        } else if (volleyError instanceof TimeoutError) {
            message = "Connection TimeOut! Please check your internet connection.";
            // message = "زمان مورد انتظار به پایان رسید ، لطفا اتصال به اینترنت را بررسی کنید";

        }

        Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();
    }

    public boolean emailValidator(String email)
    {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
    private boolean isValidMobileNumber(String phone)
    {

        if(Pattern.matches("^09[0|1|2|3][0-9]{8}", phone)) {
           return true ;
        }else {
            b_mobile.setError("شماره موبایل نامعتبر است!");
            return false;
        }
    }
}