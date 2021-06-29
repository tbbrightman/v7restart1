package com.example.v7;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.AppBarConfiguration;
import com.example.v7.databinding.ActivityReviewBinding;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import android.os.Bundle;

import static com.example.v7.MainActivity.working_ballot;

// import org.w3c.dom.Text;

public class Review extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
//set actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

// Show the workingballot in summary form:
        TextView ballot_summary= findViewById(R.id.textViewSummary);
        String test="";
        for(int i = 0; i< working_ballot.nbr_of_races; i++) {
            test= test + working_ballot.race_cardList.get(i).title+"\n";
            for(int k=0; k<(working_ballot.race_cardList.get(i).nreg_entries+
                    working_ballot.race_cardList.get(i).permitted_choices); k++)
                if(working_ballot.race_cardList.get(i).entry_cardList.get(k).chosen)
                    test= test+"\t" +
                            working_ballot.race_cardList.get(i).entry_cardList.get(k).entry_name+"\n";
        }
        ballot_summary.setText(test);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.finish();
        return super.onOptionsItemSelected(item);
    }


}