package com.example.v7;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static com.example.v7.MainActivity.entry_count_by_race;
import static com.example.v7.MainActivity.permitted_choices_per_race;
import static com.example.v7.MainActivity.race_index;

/*  RecyclerView requires that the items to be displayed must be stored in a list--
specifically an "ArrayList".  The connection of the data in the list to the display (or "View")
is performed by the "Adapter".

The ArrayList used for this purpose is used as a formal parameter to the Adapter method and
is included in the Java constructor of the Adapter's methods definition (see below).  I have prefixed
its name with 'fp_' to emphasize that is is a formal parameter (only).

When the Adapter is called, it needs to set its argument to
the name of the desired item list.

Finally, it is critical that when new list objects, such as when a single long list is comprised of
"pages" (i.e., multiple lists) of items (each page being a list object), the Adapter is invoked
with the new object's name in the call
AND the method XXX_adapter.notifyDataSetChanged(); method is invoked.

In this program a parent list holds a list of races.  Each race, in turn, holds a list of entries
into that race.  The adapter is called with the list of entries as its parameter.  When a new race is
selected a new list of entries is required.  This is accomplished by changing the race_index variable
and resetting the entry_index to 0.  The notifyDataSetChanged method makes RecyclerView display the
entries for the new race.
*/
public class v7_Adapter extends RecyclerView.Adapter<v7_Adapter.v7ViewHolder>
{
    private ArrayList<MainActivity.Entry_Card> fp_Entry_CardArrayList;
    public static class v7ViewHolder extends RecyclerView.ViewHolder {
        public static String etext= "holding variable for Write-ins.";
        public CheckBox mcBox;
        public TextView entry_view;  //referencing item_layout.xml
        public TextView party_view;
        v7ViewHolder(View itemView) {
            super(itemView);
            entry_view = itemView.findViewById(R.id.entry_name);
            mcBox= (CheckBox) itemView.findViewById(R.id.cB);
            party_view= itemView.findViewById(R.id.partyView);

            entry_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etext=entry_view.getText().toString();
                }
            });
        }
    }
    public v7_Adapter(ArrayList<MainActivity.Entry_Card> fp1) {
        fp_Entry_CardArrayList= fp1;
    }

    @Override
    public v7ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_layout, viewGroup, false);
        v7ViewHolder v7Holder = new v7ViewHolder(v);
        return v7Holder;
    }

    @Override
    public void onBindViewHolder(v7ViewHolder v7Holder, int i) {

        // "i" is the working variable supplied by the RecyclerView widget to inform the iteration number
        MainActivity.Entry_Card current_item = fp_Entry_CardArrayList.get((i));
        v7Holder.entry_view.setText(MainActivity.working_ballot.race_cardList.get(MainActivity.race_index).entry_cardList.get(i).entry_name);
        v7Holder.mcBox.setChecked(MainActivity.working_ballot.race_cardList.get(MainActivity.race_index).entry_cardList.get(i).chosen);
        v7Holder.party_view.setText(MainActivity.working_ballot.race_cardList.get(race_index).entry_cardList.get(i).party);
    }

    @Override
    public int getItemCount() {
        int total_entries= MainActivity.entry_count_by_race[MainActivity.race_index]+
                MainActivity.permitted_choices_per_race[MainActivity.race_index];
        return total_entries; }
}
