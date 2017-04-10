package net.sourceforge.ganttproject.ourAddIns;

import java.awt.Color;

/**
 * Created by curt on 3/24/17.
 */
public abstract class Task implements TaskBehavior {

    protected String name;
    protected String start;
    protected String end;
    protected int progress;
    protected String priority;

    protected final Color LOW_COLOR = new Color(217,30,24);
    protected final Color NORMAL_COLOR = Color.BLUE;
    protected final Color HIGH_COLOR = Color.GREEN;

    protected final int SMALL_TASK_SIZE = 35;

    //EXTRACT ORGINAL TASK DATA IN THE CONSTRUCTOR
    public Task(net.sourceforge.ganttproject.task.Task t){

        this.name = t.getName();
        this.start = t.getStart().toString();
        this.end = t.getEnd().toString();
        this.progress = t.getCompletionPercentage();
        this.priority = t.getPriority().getLowerString();
    }



    //GETTERS
    public String getName() {
        return name;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public int getProgress() {
        return progress;
    }

    public String getPriority() {
        return priority;
    }
}
