package com.example.v7;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
//import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    /*  Declare all the global variables
     */
    public static class Ballot {
        int nbr_of_races;       // total number of races on ballot.
        List<Race_Card> race_cardList;   // one card for each race.
        String ballot_name;     // textual name of this ballot
    }

    public static class Race_Card {
        int race_nbr;           // number of race entered.
        String title;           //text of this office name.
        ArrayList<Entry_Card> entry_cardList;  // one card per entrant in each race
        int nreg_entries;       // number of registered entries in this race
        int permitted_choices;  //(=...ballot.Race_Card[race_nbr].size
    }

    public static class Entry_Card {
        int candidate_nbr;      //index nbr of candidate in candidate_list.
        String entry_name;      // also name of TextView in activity_main.xml
        String party;           //3 or 4 character party affiliation,
        Boolean chosen;
    }

    // Declarations
    Race_Card temp_Race_Card;
    Entry_Card temp_Entry_Card;
    public static final String FILE_NAME="Ballot.txt";
    public static Ballot blank_ballot;     //"ballot" is an unfilled Ballot
    public static Ballot working_ballot;    //"working_ballot" is the in-process Ballot
    public static Ballot final_ballot;      //"final_ballot" is the Ballot to be submitted
    public final static int nraces = 8, nentriesmax = 8; //number of races, upper limit on nuber of entried per race
    public final static int stride = 2; //number of Strings in the XML String-Array of each Entry
    public static int progress;
    public static double scale_factor;         //the factor by which to multiply (race_index+1) to get 1..100 progress
    public static int[] permitted_choices_per_race;
    public static int[] entry_count_by_race;
    public static int[] start_index_by_race;
    public static int race_index = 0, entry_index = 0; //scratch variable
    public static String[] raceStringarrayy, races;
    public ProgressBar basic_progress_bar;
    public static String etext;
    private RecyclerView v7_recyclerView;
    private RecyclerView.LayoutManager v7_layoutManager;
    private RecyclerView.Adapter v7_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
// Instantiate the variables
        blank_ballot = new Ballot();
        working_ballot = new Ballot();
        final_ballot = new Ballot();
        String[] races= getResources().getStringArray(R.array.races);
//      The races array is formatted <String>=race title, <String>=nbr of registered entries & <String>=nbr of choices
        ArrayList<String> list_of_races= new ArrayList<String>(nraces);
        entry_count_by_race= new int[nraces];
        permitted_choices_per_race= new int[nraces];
        start_index_by_race= new int[nraces+1];
        start_index_by_race[0]= 0;
        for (int i=0; i<nraces; i++) {
            list_of_races.add(races[3 * i]);
            entry_count_by_race[i] = Integer.parseInt(races[3 * i + 1]);
            start_index_by_race[i+1] = start_index_by_race[i]+entry_count_by_race[i]+permitted_choices_per_race[i];
            permitted_choices_per_race[i] = Integer.parseInt(races[3 * i + 2]);
        }
        scale_factor = 100.0 / (double) nraces;
        String[] entryStringarray = new String[50];
        entryStringarray = getResources().getStringArray(R.array.candidates);
//  entryStringarray is formatted as <String>entry_name, <String> party w/entry_name=="^" to delimit races
//  Try parsing with outer loop on nraces & 2 inner loops (1) 1st, on nreg entries & 2nd on
//  permitted choices (1 write-in per choice).
        int i = 0, j = 0, k = 0;
        String[] name_String_array= new String[40];
        String[] party_String_array= new String[40];
        int cur_input_index=0, base_index_adder=0;
        int cur_output_index=0;
        for (i = 0; i < nraces; i++) {     // i is the race nbr
            temp_Race_Card = new Race_Card();
            temp_Race_Card.entry_cardList= new ArrayList<Entry_Card>();   // instantiate each list
            for (j=0; j<entry_count_by_race[i];j++) {  // j is the entry nbr
                name_String_array[j+cur_output_index]= entryStringarray[cur_input_index];
                party_String_array[j+cur_output_index]= entryStringarray[cur_input_index+1];
                cur_input_index=cur_input_index+2;  //step past name & party
            }
            cur_output_index= cur_output_index+j;                       // record outbound index
            for (j=0; j<permitted_choices_per_race[i]; j++) {           // synthesize write-in slots
                name_String_array[j+ cur_output_index]="Write-in Name";
                party_String_array[j+ cur_output_index]="UNK";
//                cur_input_index=cur_input_index+2;
            }
            cur_output_index++;
            cur_input_index=cur_input_index +1; // Skip delmiter
//            base_index_adder= entry_count_by_race[i]+permitted_choices_per_race[i];
            // the subList method is EXCLUSIVE of the item at the end index so add one to include it in the result
        }
//  Parse entryStringarray into two arrays: entry & party
//  the array index delivers pairs of entry, party at [index]
//  Instantiate ballots
//  The blank_ballot
        blank_ballot.race_cardList = new ArrayList<Race_Card>();
        blank_ballot.nbr_of_races = nraces;
        blank_ballot.ballot_name = "Original (blank)";
/*  Instantiate scratch variables      */
        temp_Race_Card = new Race_Card();
        temp_Entry_Card = new Entry_Card();
        blank_ballot.race_cardList.clear();
        int end_index = 0;
        basic_progress_bar = findViewById(R.id.progressBar);
/*
    Each Ballot needs its own list of Race_Cards
    Each race needs its own list of Entry_Cards

    Stated more clearly, each race needs its own Race_Card; the Race_Card needs its own list of Entry_Cards
    the Ballot sits above this and holds a list of Race_Cards or races.
*/
//  Use a loop to instantiate an empty Race_Card for each race
        for (i = 0; i < nraces; i++) {
            temp_Race_Card = new Race_Card();
            temp_Race_Card.entry_cardList = new ArrayList<Entry_Card>(); //each Race_Card gets a list of entries
            temp_Race_Card.entry_cardList.clear(); //clear the card's entry_cardList
            temp_Race_Card.nreg_entries = entry_count_by_race[i];
            temp_Race_Card.permitted_choices = permitted_choices_per_race[i];
            temp_Race_Card.title = list_of_races.get(i); //list_of_races is a list of String titles
// add the Race_Card at position i to the list of races
            blank_ballot.race_cardList.add(i, temp_Race_Card);
        }
//  Then, fill in each Race_card's entry_cardList by adding Entry_Cards
        int offset_to_next=0;
        int lstart_index= 0;
        for (i=0; i < nraces; i++) { // i counts race #
            for (j =0; j < entry_count_by_race[i]; j++) {  // j counts entry #
                temp_Entry_Card = new Entry_Card();
// Now, instantiate an entry card for each entry in each and add it to the list for each race
                temp_Entry_Card.candidate_nbr = j;
                temp_Entry_Card.entry_name = name_String_array[lstart_index+j];
                temp_Entry_Card.party = party_String_array[lstart_index+j];
                temp_Entry_Card.chosen = false;
                blank_ballot.race_cardList.get(i).entry_cardList.add(temp_Entry_Card);
            }
            offset_to_next=entry_count_by_race[i]+permitted_choices_per_race[i];
            for (j=0; j < permitted_choices_per_race[i]; j++){  // Synthesize write-ins
                temp_Entry_Card = new Entry_Card();
                temp_Entry_Card.candidate_nbr = j;
                temp_Entry_Card.entry_name = getString(R.string.write_in);
                temp_Entry_Card.party = "UNK";
                temp_Entry_Card.chosen = false;
                blank_ballot.race_cardList.get(i).entry_cardList.add(temp_Entry_Card);
            }
            lstart_index= lstart_index+ offset_to_next;
//            cum_index = cum_index + 2 * entry_count_by_race[i] + 1; //skip the delimiter
        }
        copyBlankToWorking();
        race_index = 0;
        TextView raceView = findViewById(R.id.Race_name);
        TextView entryView = findViewById(R.id.entry_name);
        raceView.setText(working_ballot.race_cardList.get(race_index).title);
        progress = (int) (scale_factor * (double) (race_index + 1));
        basic_progress_bar.setProgress(progress);
        entry_index = 0;

        v7_recyclerView = findViewById(R.id.v7_RecyclerAdapter);
        v7_layoutManager = new LinearLayoutManager(this);
        v7_recyclerView.setLayoutManager(v7_layoutManager);
        v7_adapter = new v7_Adapter(working_ballot.race_cardList.get(race_index).entry_cardList);
        v7_recyclerView.setAdapter(v7_adapter);

    }

    public void clicknext_btn(View view) {
        // Do something in response to button click
        if (race_index < nraces - 1) {
            race_index++;
            entry_index = 0;
            progress = (int) (scale_factor * (double) (race_index + 1));
            basic_progress_bar.setProgress(progress);
            v7_adapter.notifyDataSetChanged();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Reached end of balllot.", Toast.LENGTH_LONG);
            toast.setMargin(50, 20);
            toast.show();
        }

        TextView raceView = findViewById(R.id.Race_name);
        raceView.setText(working_ballot.race_cardList.get(race_index).title);
        TextView entryView = findViewById(R.id.entry_name);
        entryView.setText(working_ballot.race_cardList.get(race_index).entry_cardList.get(entry_index).entry_name);

    }
    @Override
    public void onResume(){
        super.onResume();
        v7_adapter.notifyDataSetChanged();
    }

    public void clickprev_btn(View view) {
        // Do something in response to button click
        if (race_index > 0) {
            --race_index;
            entry_index = 0;
            progress = (int) (scale_factor * (double) (race_index + 1));
            basic_progress_bar.setProgress(progress);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Reached start of balllot.", Toast.LENGTH_LONG);
            toast.setMargin(50, 20);
            toast.show();
        }
        v7_adapter.notifyDataSetChanged();
        TextView raceView = findViewById(R.id.Race_name);
        raceView.setText(working_ballot.race_cardList.get(race_index).title);
        TextView entryView = findViewById(R.id.entry_name);
        entryView.setText(working_ballot.race_cardList.get(race_index).entry_cardList.get(entry_index).entry_name);

    }

    public void onClick(View v) {
    // Is the view now checked?
    int tview=v.getId();
    int position = 0;
    // Check which checkbox was clicked
    switch (v.getId()) {
            case R.id.cB: {
//The critical code to return the index of the Entry_Card in the entry_card_list
                boolean checked = ((CheckBox) v).isChecked();
                View view = v;
                View parent = (View) v.getParent();
                while (!(parent instanceof RecyclerView)) {
                    view = parent;
                    parent = (View) parent.getParent();
                }
                position = v7_recyclerView.getChildAdapterPosition(view);
                entry_index = position;
                // if chosen then un-choose it,
                if (working_ballot.race_cardList.get(race_index).entry_cardList.get(position).chosen)
                    working_ballot.race_cardList.get(race_index).entry_cardList.get(position).chosen =
                            !working_ballot.race_cardList.get(race_index).entry_cardList.get(position).chosen;
                else {//try to choose it, if allowed
                    int nselected = 0;
                    if (checked) {
                        for (int j = 0; j < working_ballot.race_cardList.get(race_index).nreg_entries; j++)
                            if (working_ballot.race_cardList.get(race_index).entry_cardList.get(j).chosen)
                                nselected++;
                        working_ballot.race_cardList.get(race_index).entry_cardList.get(entry_index).chosen =
                                (nselected < permitted_choices_per_race[race_index]);
                    }
                }
                //now, deal with write-in candidates
                if (working_ballot.race_cardList.get(race_index).entry_cardList.get(position).entry_name.contains(getString(R.string.write_in))) {
                    Intent intent1 = new Intent(this, Write_In2.class);
                    startActivity(intent1);
                }
                break;
            }
            case R.id.review_btn: {//Code to review working ballot
                Intent intent2= new Intent(this, Review.class);
                startActivity(intent2);
                break;
            } //end 2nd case
            case R.id.cast_vote_btn: { //code to cast the vote summary
                Intent intent3 = new Intent(this, Cast_Vote.class);
                startActivity(intent3);
            }

            break;
        default:
            throw new IllegalStateException("Unexpected value: " + v.getId());
    } //end switch

//        working_ballot.race_cardList.get(race_index).entry_cardList.get(entry_index).entry_name=etext;
        v7_adapter.notifyDataSetChanged();
        v7_adapter.notifyDataSetChanged();
    } //end onClick

    public void copyBlankToWorking() {
        // On entry, a variable called WorkingBallot has been instantiated.
//  BlankBallot has been instantiated & populated.
//  This method copies BlankBallot to WorkingBallot with no entanglements.
        temp_Race_Card = new Race_Card();
        temp_Entry_Card = new Entry_Card();
        working_ballot.nbr_of_races = nraces;
        working_ballot.race_cardList = new ArrayList<Race_Card>();
        working_ballot.ballot_name = "Working copy";
        for (int i = 0; i < working_ballot.nbr_of_races; i++) {  //fill it with empty RaceCards
            temp_Race_Card = new Race_Card();
            working_ballot.race_cardList.add(temp_Race_Card);
        }
        for (int i = 0; i < working_ballot.nbr_of_races; i++) {
            working_ballot.race_cardList.get(i).nreg_entries = entry_count_by_race[i];
            working_ballot.race_cardList.get(i).permitted_choices= permitted_choices_per_race[i];
            working_ballot.race_cardList.get(i).race_nbr = i;
            working_ballot.race_cardList.get(i).title =
                    blank_ballot.race_cardList.get(i).title;
            working_ballot.race_cardList.get(i).entry_cardList = new ArrayList<Entry_Card>();
            for (int j = 0; j < entry_count_by_race[i]+permitted_choices_per_race[i]; j++) {    // now do Entry_Cards
                temp_Entry_Card = new Entry_Card();
                temp_Entry_Card.chosen =
                        blank_ballot.race_cardList.get(i).entry_cardList.get(j).chosen;
                temp_Entry_Card.party =
                        blank_ballot.race_cardList.get(i).entry_cardList.get(j).party;
                temp_Entry_Card.candidate_nbr =
                        blank_ballot.race_cardList.get(i).entry_cardList.get(j).candidate_nbr;
                temp_Entry_Card.entry_name =
                        blank_ballot.race_cardList.get(i).entry_cardList.get(j).entry_name;
                working_ballot.race_cardList.get(i).entry_cardList.add(temp_Entry_Card);
            }
        }
    }
}