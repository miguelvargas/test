package com.mvwsolutions.util;

public final class Semaphore
{
    public Semaphore()
    {
        protectedCount=0;
        inCritical=false;
        criticalThread=null;
    }

    public synchronized void enterProtected()
    {
        while ( inCritical)
        {
            try
            {
                wait();
            }
            catch ( InterruptedException ie)
            {
            }
        }
        protectedCount++;
    }

    public synchronized void leaveProtected()
    {
        if ( protectedCount<=0 || inCritical)
            throw new IllegalStateException( "Not in protected state");
        --protectedCount;
        notify();
    }

    public synchronized void enterCritical()
    {
        Thread currentThread=Thread.currentThread();
        if ( inCritical && criticalThread==currentThread)
            return;
        while ( protectedCount!=0 || inCritical)
        {
            try
            {
                wait();
            }
            catch ( InterruptedException ie)
            {
            }
        }
        criticalThread=currentThread;
        inCritical=true;
    }

    public synchronized void leaveCritical()
    {
        if ( protectedCount!=0 || ! inCritical || criticalThread!=
            Thread.currentThread())
        {
            throw new IllegalStateException( "Not in critical state");
        }
        inCritical=false;
        criticalThread=null;
        notifyAll();
    }

    public synchronized void runCritical( Action toRun)
        throws Exception
    {
        enterCritical();
        try
        {
            toRun.perform();
        }
        finally
        {
            leaveCritical();
        }
    }

    public synchronized void criticalToProtected()
    {
        if ( protectedCount!=0 || ! inCritical || criticalThread!=
            Thread.currentThread())
        {
            throw new IllegalStateException( "Not in critical state");
        }
        inCritical=false;
        protectedCount=1;
        criticalThread=null;
        notifyAll();
    }

    private int protectedCount;
    private boolean inCritical;
    private Thread criticalThread;
}
