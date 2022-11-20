package com.example.treecheck.Db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Db_helper {
    SQLiteDatabase db;
    private Context context;
    String db_name="database";
    public String country_table="country";
    public String province_table="province";
    public String city_table="city";

    public Db_helper(Context context) {
        this.context = context;
    }

    String Create_table_country=" CREATE TABLE IF NOT EXISTS `country` (\n" +
            "\t`id`\tINTEGER NOT NULL PRIMARY KEY ,\n" +
            "\t`name`\tINTEGER NOT NULL,\n" +
            "\t`latitude`\tTEXT NOT NULL,\n" +
            "\t`longitude`\tTEXT NOT NULL\n" +
            "); ";
    String Create_table_province=" CREATE TABLE IF NOT EXISTS `province` (\n" +
            "\t`id`\tINTEGER NOT NULL PRIMARY KEY ,\n" +
            "\t`country_id`\tINTEGER NOT NULL,\n" +
            "\t`name`\tTEXT NOT NULL,\n" +
            "\t`latitude`\tTEXT NOT NULL,\n" +
            "\t`longitude`\tTEXT NOT NULL,\n" +
            " FOREIGN KEY(country_id) REFERENCES country(country_id) \n"+
            "); ";
    String Create_table_city=" CREATE TABLE IF NOT EXISTS `city` (\n" +
            "\t`id`\tINTEGER NOT NULL PRIMARY KEY ,\n" +
            "\t`province_id`\tINTEGER NOT NULL,\n" +
            "\t`name`\tTEXT NOT NULL,\n" +
            "\t`latitude`\tTEXT NOT NULL,\n" +
            "\t`longitude`\tTEXT NOT NULL,\n" +
            " FOREIGN KEY(province_id) REFERENCES province(province_id) \n"+
            "); ";


    public void initialize_db(){
        db = context.openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
        db.execSQL(Create_table_country);
        db.execSQL(Create_table_province);
        db.execSQL(Create_table_city);
    }
    public void insert_to_country(int id,String name,String latitude,String longitude){
        db.execSQL(" insert into country (id ,name ,latitude, longitude )" +
                " values ('"+id+"','"+name+"','"+latitude+"','"+longitude+"')");
    }
    public void insert_to_province(int id,int country_id,String name,String latitude,String longitude){
        db.execSQL(" insert into province (id,country_id ,name ,latitude, longitude )" +
                " values ('"+id+"','"+country_id+"','"+name+"','"+latitude+"','"+longitude+"')");
    }
    public void insert_to_city(int id,int province_id,String name,String latitude,String longitude){
        db.execSQL(" insert into city (id ,province_id,name ,latitude, longitude )" +
                " values ('"+id+"','"+province_id+"','"+name+"','"+latitude+"','"+longitude+"')");
    }
    public Cursor get_countries(){
        Cursor cursor=db.rawQuery(" select * from country",null);
        return cursor ;
    }
    public Cursor get_provinces(){
        Cursor cursor=db.rawQuery(" select * from province",null);
        return cursor ;
    }
    public Cursor get_cities(){
        Cursor cursor=db.rawQuery(" select * from city",null);
        return cursor ;
    }
    public void update_country(int id,String name,String latitude,String longitude){
        db.execSQL(" update country set name='"+name+"' , latitude='"+latitude+"' , longitude='"+longitude+"' " +
                " where id=" + id);
    }
    public void delete_all_value(String table_name){
        db.delete(table_name, null, null);
    }

    public Cursor get_province_with_country_id(int country_id){
        Cursor cursor=db.rawQuery("select * from province where country_id="+country_id,null);
        return cursor;
    }
    public Cursor get_city_with_province_id(int province_id){
        Cursor cursor=db.rawQuery("select * from city where province_id="+province_id,null);
        return cursor;
    }

}
