package com.alnsdev.e_taxi.background;

import java.util.ArrayList;

public class Processes {
    public ArrayList<Thread> thrs;

    public Processes()
    {
        if (thrs == null);
        {
            thrs = new ArrayList<Thread>();
        }
    }

    public void addToRow(int priority, Thread thread)
    {
        thrs.add(priority, thread);
    }

    public ArrayList handleThreads()
    {
        return thrs;
    }

    public Thread getTread(int priority)
    {
        Thread thread = thrs.get(priority);
        return thread;
    }
}
