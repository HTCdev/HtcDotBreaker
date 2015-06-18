package com.htc.dotbreaker;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;

public class ActivityList {
    private List<Activity> list = new LinkedList<Activity>(); // Create the linked-list of Activity
    static private ActivityList instance; // Create a static instance to handle addActivity() and exit()

    static public synchronized ActivityList getInstance() { // Use getInstance to new the instance just once.
        if (instance == null) {
            instance = new ActivityList();
        }
        return instance;
    }

    public void addActivity(Activity activity) { // Add Activity to the list
        list.add(activity);
    }

    public void exit() { // To do finish() for all Activity in the list
        try {
            for (Activity activity : list) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //System.exit(0);
        }
    }
}
