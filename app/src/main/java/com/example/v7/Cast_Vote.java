package com.example.v7;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;


import android.app.Activity;
import android.content.Intent;

//Imports for file I/O
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.widget.Toast;

import org.w3c.dom.Text;

import static com.example.v7.MainActivity.working_ballot;


public class Cast_Vote extends AppCompatActivity {
    public String test = "";
    public Intent ztent ;
    public Uri currentURI;
    //  File I/O code
    private static final int CREATE_REQUEST_CODE = 40;
    private static final int OPEN_REQUEST_CODE = 41;
    private static final int SAVE_REQUEST_CODE = 42;
    String ballotFilename;
    ParcelFileDescriptor pfd;
    boolean callback1=false, callback2=false, callback3=false;
    String pattern = "yyyy-MM-dd HH:mm";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    String date = simpleDateFormat.format(new Date());


    public void write_Ballot(View view){
        setContentView(R.layout.activity_cast_vote);
        ballotFilename= "ballot "+date+".txt";
        createFile(view);   //CREATE returns no URI
    }
// A method originally invoked by a button interactively tapped by the user (CREATE).
    public void createFile(View view) {
        Intent ztent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        ztent.addCategory(Intent.CATEGORY_OPENABLE);
        ztent.setType("text/plain");
        ztent.putExtra(Intent.EXTRA_TITLE, ballotFilename);
        startActivityForResult(ztent, CREATE_REQUEST_CODE);
    }   // Invoking this method produces onActivityResult callback.
// A method originally invoked interactively by a user button tap; (OPEN after tapping CREATE)
    public void openFile(View view) {
        Intent ztent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        ztent.addCategory(Intent.CATEGORY_OPENABLE);
        ztent.setType("text/plain");

        startActivityForResult(ztent, OPEN_REQUEST_CODE);
    }
// A method originally invoked interactively by a user button tap; (SAVE after OPEN)
    public void saveFile(View view) {
        ztent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        ztent.addCategory(Intent.CATEGORY_OPENABLE);
        ztent.setType("text/plain");

        startActivityForResult(ztent, SAVE_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        currentURI = null;
        EditText messageBox = findViewById(R.id.messageBox);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CREATE_REQUEST_CODE: { //requestCode == CREATE_REQUEST_CODE means no work required.
                    if (resultData != null) {
//  Flash ballot created message.
                        messageBox.setText("Created " + ballotFilename);
//The openFile routines:
                        Intent ztent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        ztent.addCategory(Intent.CATEGORY_OPENABLE);
                        ztent.setType("text/plain");
                        startActivityForResult(ztent, OPEN_REQUEST_CODE);
                    } // Invoking this Activity produces onActivityResult callback when completed.
                }
                case SAVE_REQUEST_CODE: { // it's time to actually write data
                    if (resultData != null) {
//  Flash writing choices message.
                        messageBox.setText("Writing choices to: " + ballotFilename);
                        currentURI = resultData.getData();
                        setContentView(R.layout.completed);
                        writeFileContent(currentURI);   // Close the output file and say goodbye.
                        messageBox.setText("Finished. Ballot saved as " + ballotFilename);
//                        this.finishAffinity();
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        super.finishAffinity(); // Close all activites
                        System.exit(0);  // closing files, releasing resources
//                        super.finish();

//  End SAVE_REQUEST_CODE case
                    }
                }
                case OPEN_REQUEST_CODE: {  // returns URI in resultData; means proceed to SAVE actions.
                    if (resultData != null) {
                        messageBox.setText("Opened " + ballotFilename);
//  Flash ballot & choices written message.
                        currentURI = resultData.getData();
                        // a copy of saveFile() method
                        // saveFile(view);
                        ztent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        ztent.addCategory(Intent.CATEGORY_OPENABLE);
                        ztent.setType("text/plain");
                        startActivityForResult(ztent, SAVE_REQUEST_CODE);
                        // Invoking this Activity produces onActivityResult callback when completed.
                        try {
                            String content = readFileContent(currentURI); }
                        catch (IOException e) {  // Handle error here
                        }
                    }
                }
//  / end of OPEN_REQUEST_CODE case
            }  // end switch
        }   //end of (RESULT_OK) block

    }  //end of onActivityResult

    private void writeFileContent(Uri uri) {
        try {
            pfd =
                    this.getContentResolver().openFileDescriptor(uri, "w");
            FileOutputStream fileOutputStream =
                    new FileOutputStream(
                            pfd.getFileDescriptor());
            String textContent = test;
            fileOutputStream.write(textContent.getBytes());
            fileOutputStream.close();
            pfd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String readFileContent(Uri uri) throws IOException {
        InputStream inputStream =
                getContentResolver().openInputStream(uri);
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String currentline;
        while ((currentline = reader.readLine()) != null) {
            stringBuilder.append(currentline).append("\n");
        }
        inputStream.close();
        return stringBuilder.toString();
    }

    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView fileactivity;
        Button cast_vote2 = findViewById(R.id.cast_vote2);
//        cast_vote2.setOnClickListener((View.OnClickListener) this);

        setContentView(R.layout.activity_cast_vote);

        //set actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        TextView ballot_summary2 = findViewById(R.id.summary2);
        for (int i = 0; i < MainActivity.working_ballot.nbr_of_races; i++) {
            test = test + MainActivity.working_ballot.race_cardList.get(i).title + "\n";
            for (int k = 0; k < (MainActivity.working_ballot.race_cardList.get(i).nreg_entries+
                    working_ballot.race_cardList.get(i).permitted_choices) ; k++)
            if(working_ballot.race_cardList.get(i).entry_cardList.get(k).chosen)
                if (MainActivity.working_ballot.race_cardList.get(i).entry_cardList.get(k).chosen)
                    test = test + "\t" +
                            MainActivity.working_ballot.race_cardList.get(i).entry_cardList.get(k).entry_name + "\n";
        }
        ballot_summary2.setText(test);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.finish();
        return super.onOptionsItemSelected(item);
    };

}