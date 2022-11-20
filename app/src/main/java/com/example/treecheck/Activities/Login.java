package com.example.treecheck.Activities;

import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.example.treecheck.Bazres_menu;
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

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class Login extends AppCompatActivity {
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

    EditText username, password;
    Button login_btn ,show_pass;
    ProgressDialog progressDialog;
    Db_helper db_helper;
    Spinner login_spinner ;
    String sp_item ;
    SharedPreferences preferences;
    SharedPreferences.Editor editor ;
    LinearLayout linearLayout3 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username =findViewById(R.id.login_username);
        password =findViewById(R.id.login_password);
        login_btn=findViewById(R.id.login_btn);
        show_pass=findViewById(R.id.show_pass_login);
        login_spinner=findViewById(R.id.login_spinner) ;
        linearLayout3=findViewById(R.id.login_lin3) ;
        preferences = getSharedPreferences("sharedpref", MODE_PRIVATE);
        editor = preferences.edit();

        String[] spinner_item={"من ادمین هستم." , "من بازرس هستم."} ;
        sp_item=null ;
        login_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sp_item=parent.getSelectedItem().toString();
                if (sp_item.equals("من بازرس هستم.")){
                    linearLayout3.setVisibility(View.VISIBLE);
                }else {
                    linearLayout3.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        login_spinner.setAdapter(setLogin_spinner(spinner_item));
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        progressDialog =new ProgressDialog(Login.this);
        progressDialog.setCancelable(false);
        queue.add(get_country_request());
        queue.add(get_province_request());
        queue.add(get_city_request());

        show_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getInputType()==InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD){
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    show_pass.setBackgroundDrawable(getResources().getDrawable(R.drawable.password_show));
                }else {
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    show_pass.setBackgroundDrawable(getResources().getDrawable(R.drawable.password_lock));
                }
                password.setSelection(password.getText().length());
            }
        });
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (username.getText().toString().trim().isEmpty()) {
                    username.setError("این فیلد نباید خالی باشد!");
                }
                if (password.getText().toString().trim().isEmpty()) {
                    password.setError("این فیلد نباید خالی باشد!");
                } else {
                    progressDialog.setMessage("بذار ببینم درسته اطلاعاتت...");
                    progressDialog.show();
                    if (sp_item.equals("من ادمین هستم.")){
                        queue.add(admin_login_request());
                    }else if (sp_item.equals("من بازرس هستم.")){
                        queue.add(bazres_login_request());
                    }

                }
            }
        });
    }
    public StringRequest get_country_request(){
        progressDialog.setMessage("لطفا صبر کنید."+"\n"+"در حال گرفتن اطلاعات از سرور...");
        progressDialog.show();
        String url="https://mhnaddaf.ir/Tree_manager/get_all_country.php";
        StringRequest stringRequest_country=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Gson gson =  new Gson() ;
                Country_response[] country_response =  gson.fromJson(response , Country_response[].class) ;
                db_helper=new Db_helper(getApplicationContext());
                List<Country_response> country_responseList=new ArrayList<>();
                country_responseList=Arrays.asList(country_response);

                    db_helper.initialize_db();
                    Cursor cursor=db_helper.get_countries();
                    if (cursor.getCount()>0){
                        db_helper.delete_all_value(db_helper.country_table);
                        db_helper.initialize_db();
                        for (int i=0 ;  i<country_responseList.size(); ++ i ) {
                            db_helper.insert_to_country(country_responseList.get(i).getId()
                                    , country_responseList.get(i).getName(), country_responseList.get(i).getLatitude()
                                    , country_responseList.get(i).getLongitude());
                        }
                    }else {
                        for (int i=0 ;  i<country_responseList.size(); ++ i ) {
                        db_helper.insert_to_country(country_responseList.get(i).getId()
                                , country_responseList.get(i).getName(), country_responseList.get(i).getLatitude()
                                , country_responseList.get(i).getLongitude());
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                check_Enternet_with_volley_error(error);
            }

        });
        return stringRequest_country ;
    }
    public StringRequest get_province_request(){
        String url="https://mhnaddaf.ir/Tree_manager/get_all_province.php";
        StringRequest stringRequest_province=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Gson gson =  new Gson() ;
                Province_response[] province_response =  gson.fromJson(response , Province_response[].class) ;
                db_helper=new Db_helper(getApplicationContext());
                List<Province_response> province_responseList=new ArrayList<>();
                province_responseList=Arrays.asList(province_response);
                db_helper.initialize_db();
                Cursor cursor=db_helper.get_provinces();
                if (cursor.getCount()>0){
                    db_helper.delete_all_value(db_helper.province_table);
                    db_helper.initialize_db();
                    for (int i=0 ;  i<province_responseList.size(); ++ i ) {
                        db_helper.initialize_db();
                        db_helper.insert_to_province(province_responseList.get(i).getId(),province_responseList.get(i).getCountry_id()
                                ,province_responseList.get(i).getName(),province_responseList.get(i).getLatitude()
                                ,province_responseList.get(i).getLongitude());
                    }
                }else {
                    for (int i=0 ;  i<province_responseList.size(); ++ i ) {
                        db_helper.initialize_db();
                        db_helper.insert_to_province(province_responseList.get(i).getId(),province_responseList.get(i).getCountry_id()
                                ,province_responseList.get(i).getName(),province_responseList.get(i).getLatitude()
                                ,province_responseList.get(i).getLongitude());
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                check_Enternet_with_volley_error(error);
            }

        });
        return stringRequest_province ;
    }
    public StringRequest get_city_request(){
        String url="https://mhnaddaf.ir/Tree_manager/get_all_city.php";
        StringRequest stringRequest_city=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Gson gson =  new Gson() ;
                City_response[] city_response =  gson.fromJson(response , City_response[].class) ;
                db_helper=new Db_helper(getApplicationContext());
                List<City_response> city_responseList=new ArrayList<>();
                city_responseList=Arrays.asList(city_response);
                db_helper.initialize_db();
                Cursor cursor=db_helper.get_cities();
                if (cursor.getCount()>0){
                    db_helper.delete_all_value(db_helper.city_table);
                    db_helper.initialize_db();
                    for (int i=0 ;  i<city_responseList.size(); ++ i ) {
                        db_helper.insert_to_city(city_responseList.get(i).getId(),city_responseList.get(i).getProvince_id()
                                ,city_responseList.get(i).getName(),city_responseList.get(i).getLatitude()
                                ,city_responseList.get(i).getLongitude());
                    }
                }else {
                    for (int i=0 ;  i<city_responseList.size(); ++ i ) {
                        db_helper.insert_to_city(city_responseList.get(i).getId(),city_responseList.get(i).getProvince_id()
                                ,city_responseList.get(i).getName(),city_responseList.get(i).getLatitude()
                                ,city_responseList.get(i).getLongitude());
                    }
                }
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                check_Enternet_with_volley_error(error);
            }

        });
        return stringRequest_city ;
    }
    public StringRequest admin_login_request(){
        String url_login="https://mhnaddaf.ir/Tree_manager/check_admin_login.php";
        StringRequest stringRequest_login=new StringRequest(Request.Method.POST, url_login, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Gson gson =  new Gson() ;
                ResponseLogin responseLogin =  gson.fromJson(response , ResponseLogin.class) ;
                if (responseLogin.isStatus()){
                        editor.putBoolean("admin_login", true);
                        editor.putString("admin_username", username.getText().toString().trim());
                        editor.apply();
                        Intent intent = new Intent(Login.this, Admin_menu.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        Toast.makeText(Login.this, responseLogin.getMessage(), Toast.LENGTH_SHORT).show();
                        Login.this.finish();
                        progressDialog.dismiss();
                }else if (responseLogin.isStatus()==false){
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, responseLogin.getMessage(), Toast.LENGTH_SHORT).show();
                }

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
                map.put("username", username.getText().toString().trim());
                map.put("password", password.getText().toString().trim());
                return map;
            }
        };
        return stringRequest_login ;
    }
    public StringRequest bazres_login_request(){
        String url_login="https://mhnaddaf.ir/Tree_manager/check_bazres_login.php";
        StringRequest stringRequest_login=new StringRequest(Request.Method.POST, url_login, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Gson gson =  new Gson() ;
                ResponseLogin responseLogin =  gson.fromJson(response , ResponseLogin.class) ;
                if (responseLogin.isStatus()){
                    editor.putBoolean("bazres_login", true);
                    editor.putString("bazres_username", username.getText().toString().trim());
                    editor.apply();
                    Intent intent = new Intent(Login.this, Bazres_menu.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    Toast.makeText(Login.this, responseLogin.getMessage(), Toast.LENGTH_SHORT).show();
                    Login.this.finish();
                    progressDialog.dismiss();
                }else if (responseLogin.isStatus()==false){
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, responseLogin.getMessage(), Toast.LENGTH_SHORT).show();
                }

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
                map.put("username", username.getText().toString().trim());
                map.put("password", password.getText().toString().trim());
                return map;
            }
        };
        return stringRequest_login ;
    }

    private ArrayAdapter setLogin_spinner(String[] spinner_item){


        final Typeface spinner_typeface= Typeface.createFromAsset(getAssets(), "fonts/IRANSans_Light.ttf");
        ArrayAdapter spinner_adapter=new ArrayAdapter<String>(getApplicationContext()
                ,R.layout.support_simple_spinner_dropdown_item,spinner_item){public View getView(int position, View convertView, ViewGroup parent) {
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
        return spinner_adapter;
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
}