package org.uncommons.reportng;


import org.uncommons.reportng.Chronology.TimedMethod;

public class ChronologyTool 
{
    private double scale = 1.0;
    private final Chronology chronology;

    public ChronologyTool(Chronology chronology)
    {
        this.chronology = chronology;
    }
    
    public void setDesiredWidth(int width)
    {
        scale = width / (float) chronology.getTotalDuration();
    }
    
    public int scale(long value)
    {
        return (int) Math.ceil(scale * value);
    }
    
    public int marks(int mark)
    {
        return (int)chronology.getTotalDuration() / mark;
    }

    public int increment(long duration)
    {
        if ( chronology.getTotalDuration() < 1000 )  return 10;
        if ( chronology.getTotalDuration() < 60000 ) return 1000;
        return 60000;
    }

    public int getWidth(TimedMethod method)
    {
        return scale(method.getDurationMillis());
    }
    
    public int getStart(TimedMethod method)
    {
        TimedMethod preceeding = chronology.getPreceedingMethod(method);
        long relativeStart     = getRelativeStart(preceeding, method);
        return scale(relativeStart);
    }

    private long getRelativeStart(TimedMethod preceeding, TimedMethod method) {
        long relativeStart = 0;
        if ( preceeding == null ) 
        {
            relativeStart = method.getStartTimeMillis() - chronology.getSuiteStartTime();
        }
        else
        {
            relativeStart = method.getStartTimeMillis() - preceeding.getEndTimeMillis();
        }
        return relativeStart;
    }
}
