/*
 * Copyright (C) 2015 HTC Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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