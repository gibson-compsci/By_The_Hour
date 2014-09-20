package com.example.cameron.bythehour;

/**
 * Created by Cameron on 9/17/2014.
 */
public class Timestamp {
    private String _day, _start, _end, _hours;
    private int _id;
    public Timestamp(int id, String day, String start, String end, String hours) {
        _id=id;
        _day=day;
        _start=start;
        _end=end;
        _hours=hours;
    }
    public int get_id() {return _id; }
    public String get_day() {return _day; }
    public String get_start() {return _start; }
    public String get_end() {return _end; }
    public String get_hours() {return _hours; }
}
