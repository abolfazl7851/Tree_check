package com.example.treecheck.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.example.treecheck.Models.ResponseLogin;
import com.example.treecheck.R;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class Admin_menu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer ;

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

    CardView admin_create_tree,admin_create_bazres,admin_create_tree_type,admin_create_tree_disease,admin_create_tree_pest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);
        SharedPreferences preferences = getSharedPreferences("sharedpref",MODE_PRIVATE);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView drawer_username= (TextView) header.findViewById(R.id.drawer_username);
        ImageView imageView= (ImageView) header.findViewById(R.id.imageView);
        drawer_username.setText(preferences.getString("admin_username","null"));
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        Button btn_test = findViewById(R.id.show_drawer_btn) ;
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(Gravity.RIGHT)) {
                    drawer.closeDrawer(Gravity.RIGHT);
                } else {
                    drawer.openDrawer(Gravity.RIGHT);
                }
            }
        });

        admin_create_bazres=findViewById(R.id.admin_create_bazres);
        admin_create_tree=findViewById(R.id.admin_create_tree);
        admin_create_tree_type=findViewById(R.id.admin_create_tree_type);
        admin_create_tree_disease=findViewById(R.id.admin_create_tree_disease);
        admin_create_tree_pest=findViewById(R.id.admin_create_tree_pest);
        admin_create_bazres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Admin_menu.this,Create_bazres.class);
                startActivity(intent);

            }
        });

        admin_create_tree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Admin_menu.this,Create_tree.class) ;
                startActivity(intent);
            }
        });

        admin_create_tree_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_id(R.id.admin_create_tree_type);
            }
        });
        admin_create_tree_disease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_id(R.id.admin_create_tree_disease);
            }
        });
        admin_create_tree_pest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_id(R.id.admin_create_tree_pest);
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.logout:
               show_logout_dialog();
               drawer.closeDrawer(Gravity.RIGHT)
               ;break;
            case R.id.settings:
                Toast.makeText(this, "در دست ساخت...", Toast.LENGTH_SHORT).show();break;
        }

        return false;
    }


    private void check_id(int id){
        switch (id){
            case R.id.admin_create_tree_type:
                show_dialog(" گونه ","Tree_type");break;
            case R.id.admin_create_tree_disease:
                show_dialog(" بیماری ","Tree_disease");break;
            case R.id.admin_create_tree_pest:
                show_dialog(" آفت ","Tree_pest");break;
        }
    }
    private void show_dialog(String mtxt, final String tname) {

        final Dialog dialog = new Dialog(this,R.style.DialogTheme);
        dialog.setContentView(R.layout.new_item);
        dialog.setCancelable(true);
        TextView main_text,item_text ;
        final EditText item_edtxt;
        Button btn ;
        final ProgressBar pb=dialog.findViewById(R.id.dialog_pb);
        main_text=dialog.findViewById(R.id.item_main_txt) ;
        item_text=dialog.findViewById(R.id.item_textview) ;
        item_edtxt=dialog.findViewById(R.id.item_name) ;
        btn=dialog.findViewById(R.id.btn_create_item) ;
        main_text.setText("ثبت اطلاعات"+mtxt+"جدید");
        item_text.setText("نام"+mtxt);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item_edtxt.getText().toString().isEmpty()){
                    item_edtxt.setError("این فیلد نباید خالی باشد!");
                }else {
                    pb.setVisibility(View.VISIBLE);
                    RequestQueue queue= Volley.newRequestQueue(getApplicationContext());
                    String url="https://mhnaddaf.ir/Tree_manager/admin_insert_new_item_by_tname.php";
                    StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Gson gson =  new Gson() ;
                            ResponseLogin responseLogin =  gson.fromJson(response , ResponseLogin.class) ;
                            if (responseLogin.isStatus()==false){
                                pb.setVisibility(View.INVISIBLE);
                                Toast.makeText(Admin_menu.this, ""+responseLogin.getMessage(), Toast.LENGTH_SHORT).show();
                            } else if (responseLogin.isStatus()) {
                                Toast.makeText(Admin_menu.this, ""+responseLogin.getMessage(), Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                pb.setVisibility(View.INVISIBLE);
                            }else {
                                Toast.makeText(Admin_menu.this, "" + responseLogin.getMessage(), Toast.LENGTH_SHORT).show();
                                pb.setVisibility(View.INVISIBLE);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            dialog.dismiss();
                            pb.setVisibility(View.INVISIBLE);
                            check_Enternet_with_volley_error(error);
                        }

                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("tname",tname) ;
                            map.put("name" ,item_edtxt.getText().toString().trim()) ;
                            return map;
                        }
                    };
                    queue.add(stringRequest) ;

                }
            }
        });
        dialog.show();
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

    private void show_logout_dialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.logout);
        dialog.setCancelable(true);
        TextView main_text;
        Button btn_logout,btn_cancel ;
        main_text=dialog.findViewById(R.id.logout_main_txt) ;
        btn_logout=dialog.findViewById(R.id.btn_logout) ;
        btn_cancel=dialog.findViewById(R.id.btn_cancel) ;
        main_text.setText("هشدار!"+"\n"+"شما درخواست خروج از حساب کاربری خود کرده اید.");
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref=getSharedPreferences("sharedpref",MODE_PRIVATE);
                SharedPreferences.Editor editor=pref.edit();
                editor.putBoolean("admin_login",false);
                editor.putString("admin_username",null);
                editor.apply();
                Intent intent =new Intent(Admin_menu.this,Splash.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Admin_menu.this.finish();
                Toast.makeText(Admin_menu.this, "شما با موفقیت از حساب کاربری خود خارج شدید.", Toast.LENGTH_SHORT).show();
                }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}