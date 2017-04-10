package net.sourceforge.ganttproject;

import java.awt.Color;
import java.net.URL;

import biz.ganttproject.core.calendar.AlwaysWorkingTimeCalendarImpl;
import biz.ganttproject.core.calendar.GPCalendarCalc;
import biz.ganttproject.core.time.CalendarFactory;
import biz.ganttproject.core.time.GanttCalendar;
import biz.ganttproject.core.time.TimeUnitStack;
import biz.ganttproject.core.time.impl.GPTimeUnitStack;
import net.sourceforge.ganttproject.gui.NotificationManager;
import net.sourceforge.ganttproject.resource.HumanResourceManager;
import net.sourceforge.ganttproject.roles.RoleManager;
import net.sourceforge.ganttproject.roles.RoleManagerImpl;
import net.sourceforge.ganttproject.task.CustomColumnsManager;
import net.sourceforge.ganttproject.task.TaskManager;
import net.sourceforge.ganttproject.task.TaskManagerConfig;

public class TestSetupHelper {
    public static class TaskManagerBuilder implements TaskManagerConfig {
        private GPCalendarCalc myGPCalendar = new AlwaysWorkingTimeCalendarImpl();

        private TimeUnitStack myTimeUnitStack;

        private HumanResourceManager myResourceManager;

        private RoleManager myRoleManager;

        public TaskManagerBuilder() {
            myTimeUnitStack = new GPTimeUnitStack();
            myRoleManager = new RoleManagerImpl();
            myResourceManager = new HumanResourceManager(myRoleManager.getDefaultRole(), new CustomColumnsManager(), myRoleManager);
        }

        @Override
        public Color getDefaultColor() {
            return null;
        }

        @Override
        public GPCalendarCalc getCalendar() {
            return myGPCalendar;
        }

        @Override
        public TimeUnitStack getTimeUnitStack() {
            return myTimeUnitStack;
        }

        @Override
        public HumanResourceManager getResourceManager() {
            return myResourceManager;
        }

        @Override
        public URL getProjectDocumentURL() {
            return null;
        }

        public TaskManagerBuilder withCalendar(GPCalendarCalc calendar) {
            myGPCalendar = calendar;
            return this;
        }

        public TaskManager build() {
            return TaskManager.Access.newInstance(null, this);
        }

        @Override
        public NotificationManager getNotificationManager() {
          return null;
        }
    }

    public static TaskManagerBuilder newTaskManagerBuilder() {
        return new TaskManagerBuilder();
    }

    public static GanttCalendar newFriday() {
        return CalendarFactory.createGanttCalendar(2004, 9, 15);
    }

    public static GanttCalendar newSaturday() {
        return CalendarFactory.createGanttCalendar(2004, 9, 16);
    }

    public static GanttCalendar newSunday() {
        return CalendarFactory.createGanttCalendar(2004, 9, 17);
    }

    public static GanttCalendar newTuesday() {
        return CalendarFactory.createGanttCalendar(2004, 9, 19);
    }

    public static GanttCalendar newMonday() {
        return CalendarFactory.createGanttCalendar(2004, 9, 18);
    }

    public static GanttCalendar newWendesday() {
        return CalendarFactory.createGanttCalendar(2004, 9, 20);
    }

    public static GanttCalendar newThursday() {
        return CalendarFactory.createGanttCalendar(2004, 9, 21);
    }


}
