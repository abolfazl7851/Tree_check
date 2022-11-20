package com.example.treecheck.Activities;

import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.example.treecheck.Models.Province_response;
import com.example.treecheck.Models.ResponseLogin;
import com.example.treecheck.Models.Tree_type;
import com.example.treecheck.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class Create_tree extends AppCompatActivity {

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

    EditText tree_rfid,tree_address,tree_region,tree_plaque,tree_latitude,tree_longitude,tree_sal_kasht;
    Spinner spinner_tree_type ;
    ProgressDialog progressDialog;
    RequestQueue queue;
    List<Tree_type> tree_typeList=new ArrayList<>();
    ArrayAdapter<Tree_type> tree_typeArrayAdapter ;
    int tree_type_id=0 ;
    Button btn_create_tree ;
    Typeface spinner_typeface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tree);
        progressDialog=new ProgressDialog(this);
        queue= Volley.newRequestQueue(getApplicationContext());
        spinner_typeface= Typeface.createFromAsset(getAssets(), "fonts/IRANSans_Light.ttf");
        tree_rfid=findViewById(R.id.tree_rfid);
        tree_address=findViewById(R.id.tree_address);
        tree_region=findViewById(R.id.tree_region);
        tree_plaque=findViewById(R.id.tree_plaque);
        tree_latitude=findViewById(R.id.tree_latitude);
        tree_longitude=findViewById(R.id.tree_longitude);
        tree_sal_kasht=findViewById(R.id.tree_sal_kasht);
        spinner_tree_type=findViewById(R.id.spinner_tree_type);
        btn_create_tree=findViewById(R.id.btn_create_tree);
        queue.add(get_tree_type()) ;

        btn_create_tree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tree_rfid.getText().toString().trim().isEmpty()){
                    tree_rfid.setError("این فیلد نباید خالی باشد!");
                }else if (tree_rfid.length()!=10){
                    tree_rfid.setError("این تگ ده رقمی است!");
                }else {
                    if (tree_address.getText().toString().trim().isEmpty()){
                        tree_address.setError("این فیلد نباید خالی باشد!");
                    }
                    if (tree_region.getText().toString().trim().isEmpty()){
                        tree_region.setError("این فیلد نباید خالی باشد!");
                    }
                    if (tree_plaque.getText().toString().trim().isEmpty()){
                        tree_plaque.setError("این فیلد نباید خالی باشد!");
                    }
                    if (tree_latitude.getText().toString().trim().isEmpty()){
                        tree_latitude.setError("این فیلد نباید خالی باشد!");
                    }
                    if (tree_longitude.getText().toString().trim().isEmpty()){
                        tree_longitude.setError("این فیلد نباید خالی باشد!");
                    }
                    if (tree_sal_kasht.getText().toString().trim().isEmpty()){
                        tree_sal_kasht.setError("این فیلد نباید خالی باشد!");
                    }else if(tree_sal_kasht.length()!=4){
                        tree_sal_kasht.setError("سال چهار رقمی است!");
                    }else if (1280>Integer.parseInt(tree_sal_kasht.getText().toString().trim())
                            || Integer.parseInt(tree_sal_kasht.getText().toString().trim())>1500){
                        tree_sal_kasht.setError("سال کاشت معتبر نيست!!!");
                    }
                    else  {
                        AlertDialog.Builder alertDialog=new AlertDialog.Builder(Create_tree.this);
                        alertDialog.setTitle("ارسال اطلاعات")
                                .setMessage("مطمئنی همه چیو درست انتخاب کردی؟ بفرستمش؟")
                                .setPositiveButton("آره بابا", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        progressDialog.setMessage("یکم صبر کن...");
                                        progressDialog.show();
                                        queue.add(send_tree_information());
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
                }

            }
        });

    }

    private StringRequest send_tree_information() {
        String url="https://mhnaddaf.ir/Tree_manager/admin_insert_tree.php";
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();

                Gson gson =  new Gson() ;
                ResponseLogin responseLogin =  gson.fromJson(response , ResponseLogin.class) ;
                if (responseLogin.isStatus()==false){
                    Toast.makeText(Create_tree.this, ""+responseLogin.getMessage(), Toast.LENGTH_SHORT).show();
                }else if (responseLogin.isStatus()){
                    Toast.makeText(Create_tree.this, ""+responseLogin.getMessage(), Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(Create_tree.this, ""+responseLogin.getMessage(), Toast.LENGTH_SHORT).show();
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
                map.put("rfid", tree_rfid.getText().toString().trim());
                System.out.println("rfif: "+tree_rfid.getText().toString().trim());
                map.put("tree_type", String.valueOf(tree_type_id));
                map.put("tree_address", tree_address.getText().toString().trim());
                map.put("tree_region", tree_region.getText().toString().trim());
                map.put("tree_plaque", tree_plaque.getText().toString().trim());
                map.put("tree_latitude", tree_latitude.getText().toString().trim());
                map.put("tree_longitude", tree_longitude.getText().toString().trim());
                map.put("tree_sal_kasht", tree_sal_kasht.getText().toString().trim());
                return map;
            }
        };
        return  stringRequest;
    }

    private void set_spinner_tree_type() {
        tree_typeArrayAdapter=new ArrayAdapter<Tree_type>(getApplicationContext()
                ,R.layout.support_simple_spinner_dropdown_item,tree_typeList){public View getView(int position, View convertView, ViewGroup parent) {
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
        spinner_tree_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Tree_type tree_type= (Tree_type) parent.getSelectedItem();
                tree_type_id=tree_type.getId() ;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_tree_type.setAdapter(tree_typeArrayAdapter);

    }
    private StringRequest get_tree_type(){
        tree_typeList=new ArrayList<>() ;
        progressDialog.setMessage("در حال گرفتن گونه های درخت...");
        progressDialog.show();
        String url="https://mhnaddaf.ir/Tree_manager/get_item_by_tname.php";
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Toast.makeText(Create_tree.this, "response : "+response, Toast.LENGTH_SHORT).show();
                Gson gson =  new Gson() ;
                Tree_type [] tree_types =  gson.fromJson(response , Tree_type[].class) ;
                tree_typeList = Arrays.asList(tree_types) ;
                set_spinner_tree_type();
                progressDialog.dismiss();
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
                map.put("tname", "Tree_type");
                return map;
            }
        };
        return  stringRequest ;
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