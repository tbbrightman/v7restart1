package com.example.v7;

import android.widget.Button;
import android.widget.TextView;

public class item_layout {
    private int mbuttonResource ;
    private String mentry;

    public int ItemLayout( int buttonResource, String entry) {
        mbuttonResource = buttonResource;
        mentry = entry;
    return 1;}
    public String getEntry() {
        return mentry;
    }
    public int getMbuttonResource() {
        return mbuttonResource;
    }
}
