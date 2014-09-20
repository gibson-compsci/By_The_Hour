package com.example.cameron.bythehour;

import android.database.sqlite.*;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MyActivity extends ActionBarActivity {

    TextView todays_date, hours_today, out_today, in_today, total_hours;
    TimePicker time_out_pick, time_in_pick;
    String punchin_time, punchout_time, hours, date;
    Double total;
    SimpleDateFormat formatter;
    Calendar current_date;
    Integer timestampCount;

    List<Timestamp> Timestamps = new ArrayList<Timestamp>();
    List<Timestamp> addableTimestamps = new ArrayList<Timestamp>();

    ListView timestamplist_view;
    DatabaseHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        todays_date = (TextView) findViewById(R.id.txt_DATE);
        hours_today = (TextView) findViewById(R.id.txt_H_today);
        out_today = (TextView) findViewById(R.id.txt_OUT);
        time_out_pick = (TimePicker) findViewById(R.id.time_out);
        in_today = (TextView) findViewById(R.id.txt_IN);
        time_in_pick = (TimePicker) findViewById(R.id.time_in);
        total_hours = (TextView) findViewById(R.id.txt_H_total);
        dbHandler = new DatabaseHandler(getApplicationContext());
        dbHandler.getWritableDatabase();
        timestamplist_view = (ListView) findViewById(R.id.listView);

        //punchout_time = time_out_pick.getCurrentHour().toString()+":"+time_out_pick.getCurrentMinute().toString();
        //punchin_time = time_in_pick.getCurrentHour().toString()+":"+time_in_pick.getCurrentMinute().toString();
        //hours = calculateHours(punchin_time,punchout_time);
        current_date = Calendar.getInstance();
        formatter = new SimpleDateFormat("yyyy/MMM/dd");
        date = formatter.format(current_date.getTime());
        todays_date.setText(date);

        //Setting up the tabs at the top of the screen
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);

        //set up the creator tab with Indicator "Creator"
        tabHost.setup();
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("stats");
        tabSpec.setContent(R.id.tab_statistics);
        tabSpec.setIndicator("Statistics");
        tabHost.addTab(tabSpec);

        //set up the list tab with Indicator "List"
        tabSpec = tabHost.newTabSpec("calendar");
        tabSpec.setContent(R.id.tab_calendar);
        tabSpec.setIndicator("Calendar");
        tabHost.addTab(tabSpec);

        //set up the list tab with Indicator "List"
        tabSpec = tabHost.newTabSpec("punch");
        tabSpec.setContent(R.id.tab_punch_clock);
        tabSpec.setIndicator("Punch In/Out");
        tabHost.addTab(tabSpec);

        //set up the button
        final Button inbtn = (Button) findViewById(R.id.btn_in);

        inbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                punchin_time = time_in_pick.getCurrentHour().toString()+":"+time_in_pick.getCurrentMinute().toString();
                in_today.setText(punchin_time);
                Toast.makeText(getApplicationContext(), "Punch Recorded!", Toast.LENGTH_SHORT).show();
            }
        });

        //set up the button
        final Button outbtn = (Button) findViewById(R.id.btn_out);

        outbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                punchout_time = time_out_pick.getCurrentHour().toString()+":"+time_out_pick.getCurrentMinute().toString();
                hours = calculateHours(punchin_time,punchout_time);
                hours_today.setText("~"+hours+" hours");
                out_today.setText(punchout_time);
                Timestamp timestamp = new Timestamp(dbHandler.getTimestampsCount(),String.valueOf(todays_date.getText().toString()),String.valueOf(in_today.getText().toString()),String.valueOf(out_today.getText().toString()),String.valueOf(hours_today.getText().toString()));
                dbHandler.createTimestamp(timestamp);
                Timestamps.add(timestamp);
                Toast.makeText(getApplicationContext(), "Punch Recorded!", Toast.LENGTH_SHORT).show();
            }
        });

        final Button calcbtn = (Button) findViewById(R.id.btn_calc);

        calcbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                total = calculateTotal();
                total_hours.setText(total.toString());
            }
        });

        //set up the button
        final Button clearbtn = (Button) findViewById(R.id.btn_cleardata);

        clearbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert_dialog();
            }
        });

        addableTimestamps = dbHandler.getAllTimestamps();
        timestampCount = dbHandler.getTimestampsCount();

        for (int i = 0; i<timestampCount; i++) {
            Timestamps.add(addableTimestamps.get(i));
        }
        if (!addableTimestamps.isEmpty())
            populate_list();

    }

    public void alert_dialog() {
        new AlertDialog.Builder(this)
                .setTitle("CLEAR DATA")
                .setMessage("Are you sure you want to clear data?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "DATA CLEARED!", Toast.LENGTH_SHORT).show();
                        for (int i=0; i<timestampCount; i++) {
                            dbHandler.deleteTimestamp(addableTimestamps.get(i));
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public String calculateHours(String start, String end) {
        String[] timea=start.split(":");
        String[] timeb=end.split(":");
        double total;
        double hour = Integer.parseInt(timeb[0])-Integer.parseInt(timea[0]);
        double min = Integer.parseInt(timeb[1])-Integer.parseInt(timea[1]);
        if (min<0) {
            min += 60.0;
            total = (hour-1) + (min / 60.0);
        }
        else
            total = hour+(min/60.0);
        if (total<0)
            total+=24.0;
        return Double.toString(total);
    }

    public Double calculateTotal() {
        double total_time=0.0;
        String time_interval;
        for (int i=0; i<Timestamps.size(); i++) {
            time_interval = Timestamps.get(i).get_hours();
            time_interval = time_interval.substring(1);
            String[] time_list = time_interval.split(" ");
            total_time += Double.parseDouble(time_list[0]);
        }
        return total_time;
    }

    public void populate_list() {
        ArrayAdapter<Timestamp> adapter = new TimestampListAdapter();
        timestamplist_view.setAdapter(adapter);
    }

    private class TimestampListAdapter extends ArrayAdapter<Timestamp> {
        public TimestampListAdapter() {
            super(MyActivity.this, R.layout.listview_item, Timestamps);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null)
                view = getLayoutInflater().inflate(R.layout.listview_item, parent, false);

            Timestamp current_contact = Timestamps.get(position);

            TextView name = (TextView) view.findViewById(R.id.day_of_week_Label);
            name.setText(current_contact.get_day());
            TextView phone = (TextView) view.findViewById(R.id.start_time_Label);
            phone.setText(current_contact.get_start());
            TextView email = (TextView) view.findViewById(R.id.end_time_Label);
            email.setText(current_contact.get_end());
            TextView address = (TextView) view.findViewById(R.id.total_hours_Label);
            address.setText(current_contact.get_hours());

            return view;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
