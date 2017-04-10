package net.sourceforge.ganttproject.ourAddIns;

/**
 * Created by curt on 3/24/17.
 */
public class High extends Task {

    public High(net.sourceforge.ganttproject.task.Task t) {
        super(t);
    }

    @Override
    public ProgressBar getProgressBar() {
        return new ProgressBar(SMALL_TASK_SIZE,HIGH_COLOR);
    }
}
