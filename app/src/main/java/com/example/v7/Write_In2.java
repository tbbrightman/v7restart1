package com.example.v7;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class Write_In2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_in2);

        EditText write_in2= findViewById(R.id.write_in_name);

        write_in2.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    MainActivity.etext = write_in2.getText().toString();
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    MainActivity.working_ballot.race_cardList.get(MainActivity.race_index).entry_cardList.get
                            (MainActivity.entry_index).entry_name=MainActivity.etext;
                    MainActivity.working_ballot.race_cardList.get(MainActivity.race_index).entry_cardList.get
                            (MainActivity.entry_index).chosen= true ;                   finish();
                    return true;
                }
                return false;
            }
        });
//        super.onBackPressed();

    }
    class DoneOnEditorActionListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            return false;
        }
    }
}