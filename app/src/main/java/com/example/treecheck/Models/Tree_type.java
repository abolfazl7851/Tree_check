package com.example.treecheck.Models;

import com.google.gson.annotations.SerializedName;

public class Tree_type {
    public Tree_type(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name ;
    }
}
