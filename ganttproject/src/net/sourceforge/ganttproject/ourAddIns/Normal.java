package net.sourceforge.ganttproject.ourAddIns;

/**
 * Created by curt on 3/24/17.
 */
public class Normal extends Task {

    public Normal(net.sourceforge.ganttproject.task.Task t) {
        super(t);
    }

    @Override
    public ProgressBar getProgressBar() {
        return new ProgressBar(SMALL_TASK_SIZE,NORMAL_COLOR);
    }
}
