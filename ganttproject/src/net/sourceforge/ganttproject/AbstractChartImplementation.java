/*
GanttProject is an opensource project management tool.
Copyright (C) 2011 GanttProject Team

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 3
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package net.sourceforge.ganttproject;

import java.awt.AWTEvent;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.plaf.LayerUI;

import net.sourceforge.ganttproject.chart.Chart;
import net.sourceforge.ganttproject.chart.ChartModel;
import net.sourceforge.ganttproject.chart.ChartModelBase;
import net.sourceforge.ganttproject.chart.ChartRendererBase;
import net.sourceforge.ganttproject.chart.ChartSelection;
import net.sourceforge.ganttproject.chart.ChartSelectionListener;
import net.sourceforge.ganttproject.chart.ChartUIConfiguration;
import net.sourceforge.ganttproject.chart.TimelineChart;
import net.sourceforge.ganttproject.chart.export.ChartDimensions;
import net.sourceforge.ganttproject.chart.export.ChartImageBuilder;
import net.sourceforge.ganttproject.chart.export.ChartImageVisitor;
import net.sourceforge.ganttproject.chart.export.RenderedChartImage;
import net.sourceforge.ganttproject.chart.mouse.MouseInteraction;
import net.sourceforge.ganttproject.chart.mouse.ScrollViewInteraction;
import net.sourceforge.ganttproject.chart.mouse.TimelineFacadeImpl;
import net.sourceforge.ganttproject.gui.UIFacade;
import net.sourceforge.ganttproject.gui.zoom.ZoomEvent;
import net.sourceforge.ganttproject.gui.zoom.ZoomListener;
import net.sourceforge.ganttproject.language.GanttLanguage;
import net.sourceforge.ganttproject.resource.HumanResource;
import net.sourceforge.ganttproject.task.Task;
import net.sourceforge.ganttproject.task.TaskSelectionManager;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import biz.ganttproject.core.chart.grid.Offset;
import biz.ganttproject.core.option.GPOptionGroup;
import biz.ganttproject.core.time.CalendarFactory;
import biz.ganttproject.core.time.TimeDuration;
import biz.ganttproject.core.time.TimeUnit;
import biz.ganttproject.core.time.impl.GPTimeUnitStack;

public class AbstractChartImplementation implements TimelineChart, ZoomListener {
  private final ChartModelBase myChartModel;
  private final IGanttProject myProject;
  private Set<ChartSelectionListener> mySelectionListeners = new LinkedHashSet<ChartSelectionListener>();
  private final ChartComponentBase myChartComponent;
  private MouseInteraction myActiveInteraction;
  private final UIFacade myUiFacade;
  private VScrollController myVScrollController;
  private final Timer myTimer = new Timer();
  private Runnable myTimerTask = null;

  public class MouseHoverLayerUi extends LayerUI<ChartComponentBase> {
    private Point myHoverPoint;

    @Override
    public void installUI(JComponent c) {
      super.installUI(c);
      JLayer jlayer = (JLayer)c;
      jlayer.setLayerEventMask(
        AWTEvent.MOUSE_MOTION_EVENT_MASK
      );
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent e, JLayer l) {
      myHoverPoint = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), l);
      l.repaint();
    }

    @Override
    public void paint(Graphics g, JComponent c) {
      Graphics2D g2 = (Graphics2D)g.create();
      super.paint(g2, c);
      if (myHoverPoint == null) {
        return;
      }
      ChartModelBase chartModel = getChartModel();
      if (chartModel.getBottomUnit() == GPTimeUnitStack.DAY) {
        return;
      }
      Offset offset = chartModel.getOffsetAt(myHoverPoint.x);
      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .4f));
      g2.setFont(chartModel.getChartUIConfiguration().getChartFont().deriveFont(9.0f));
      g2.setColor(Color.BLACK);
      int offsetMidPx = (offset.getStartPixels() + offset.getOffsetPixels()) / 2;
      int headerBottomPx = chartModel.getChartUIConfiguration().getHeaderHeight();
      int[] xPoints = new int[] {offsetMidPx - 3, offsetMidPx, offsetMidPx + 3};
      int[] yPoints = new int[] {headerBottomPx + 6, headerBottomPx, headerBottomPx + 6};

      g2.fillPolygon(xPoints, yPoints, 3);
      g2.drawString(GanttLanguage.getInstance().formatShortDate(CalendarFactory.createGanttCalendar(offset.getOffsetStart())),
          offsetMidPx, headerBottomPx + 15);
    }
  }

  public AbstractChartImplementation(IGanttProject project, UIFacade uiFacade, ChartModelBase chartModel,
      ChartComponentBase chartComponent) {
    assert chartModel != null;
    myUiFacade = uiFacade;
    myChartModel = chartModel;
    myProject = project;

    myChartComponent = chartComponent;
    uiFacade.getTaskSelectionManager().addSelectionListener(new TaskSelectionManager.Listener() {
      @Override
      public void userInputConsumerChanged(Object newConsumer) {
        fireSelectionChanged();
      }

      @Override
      public void selectionChanged(List<Task> currentSelection) {
        fireSelectionChanged();
      }
    });
    myTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        if (myTimerTask != null) {
          SwingUtilities.invokeLater(myTimerTask);
          myTimerTask = null;
        }
      }
    }, 1000, 1000);
  }


  @Override
  public void init(IGanttProject project) {
    // Skip as we already have a project instance.
  }

  protected void setCursor(Cursor cursor) {
    myChartComponent.setCursor(cursor);
  }

  protected UIFacade getUIFacade() {
    return myUiFacade;
  }

  @Override
  public IGanttProject getProject() {
    return myProject;
  }

  @Override
  public void setVScrollController(TimelineChart.VScrollController vscrollController) {
    myVScrollController = vscrollController;
  }

  public void beginScrollViewInteraction(MouseEvent e) {
    TimelineFacadeImpl timelineFacade = new TimelineFacadeImpl(getChartModel(), myProject.getTaskManager());
    timelineFacade.setVScrollController(myVScrollController);
    setActiveInteraction(new ScrollViewInteraction(e, timelineFacade));
  }

  public MouseInteraction finishInteraction() {
    try {
      if (getActiveInteraction() != null) {
        getActiveInteraction().finish();
      }
      return getActiveInteraction();
    } finally {
      setActiveInteraction(null);
    }
  }

  protected void setActiveInteraction(MouseInteraction myActiveInteraction) {
    this.myActiveInteraction = myActiveInteraction;
  }

  public MouseInteraction getActiveInteraction() {
    return myActiveInteraction;
  }

  @Override
  public void zoomChanged(ZoomEvent e) {
    myChartComponent.invalidate();
    myChartComponent.repaint();
  }

  public void paintChart(Graphics g) {
    getChartModel().paint(g);
  }

  protected ChartModelBase getChartModel() {
    return myChartModel;
  }

  protected void scheduleTask(Runnable task) {
    myTimerTask = task;
  }

  protected ChartComponentBase getChartComponent() {
    return myChartComponent;
  }

  private Image getLogo() {
    return myUiFacade.getLogo();
  }
  // ///////////////////////////////////////////////////////////
  // interface Chart
  @Override
  public void buildImage(GanttExportSettings settings, ChartImageVisitor imageVisitor) {
    ChartModelBase modelCopy = getChartModel().createCopy();
    modelCopy.setBounds(myChartComponent.getSize());
    if (settings.getStartDate() == null) {
      settings.setStartDate(modelCopy.getStartDate());
    }
    if (settings.getEndDate() == null) {
      settings.setEndDate(modelCopy.getEndDate());
    }
    if (settings.isCommandLineMode()) {
      myChartComponent.getTreeTable().getTable().getTableHeader().setVisible(true);
      myChartComponent.getTreeTable().doLayout();
      myChartComponent.getTreeTable().getTable().setRowHeight(modelCopy.calculateRowHeight());
      myChartComponent.getTreeTable().autoFitColumns();
    }
    settings.setLogo(getLogo());
    ChartImageBuilder builder = new ChartImageBuilder(settings, modelCopy, myChartComponent.getTreeTable());
    builder.buildImage(imageVisitor);
  }

  @Override
  public RenderedImage getRenderedImage(GanttExportSettings settings) {
    class ChartImageVisitorImpl implements ChartImageVisitor {
      private RenderedChartImage myRenderedImage;
      private Graphics2D myGraphics;
      private BufferedImage myTreeImage;

      @Override
      public void acceptLogo(ChartDimensions d, Image logo) {
        if (d.getTreeWidth() <= 0) {
          return;
        }
        Graphics2D g = getGraphics(d);
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, d.getTreeWidth(), d.getLogoHeight());
        // Hack: by adding 35, the left part of the logo becomes visible,
        // otherwise it gets chopped off
        g.drawImage(logo, 35, 0, null);
      }

      @Override
      public void acceptTable(ChartDimensions d, Component header, Component table) {
        if (d.getTreeWidth() <= 0) {
          return;
        }
        Graphics2D g = getGraphics(d);
        g.translate(0, d.getLogoHeight());
        header.print(g);

        g.translate(0, d.getTableHeaderHeight());
        table.print(g);
      }

      @Override
      public void acceptChart(ChartDimensions d, ChartModel model) {
        if (myTreeImage == null) {
          myTreeImage = new BufferedImage(1, d.getChartHeight() + d.getLogoHeight(), BufferedImage.TYPE_INT_RGB);
        }
        myRenderedImage = new RenderedChartImage(model, myTreeImage, d.getChartWidth(), d.getChartHeight()
            + d.getLogoHeight(), d.getLogoHeight());
      }

      private Graphics2D getGraphics(ChartDimensions d) {
        if (myGraphics == null) {
          myTreeImage = new BufferedImage(d.getTreeWidth(), d.getChartHeight() + d.getLogoHeight(),
              BufferedImage.TYPE_INT_RGB);
          myGraphics = myTreeImage.createGraphics();
        }
        return myGraphics;
      }
    }
    ;
    ChartImageVisitorImpl visitor = new ChartImageVisitorImpl();
    buildImage(settings, visitor);
    return visitor.myRenderedImage;
  }

  @Override
  public Date getStartDate() {
    return getChartModel().getStartDate();
  }

  @Override
  public void setStartDate(Date startDate) {
    startDate = getBottomTimeUnit().adjustLeft(startDate);
    getChartModel().setStartDate(startDate);
  }

  @Override
  public void scrollBy(TimeDuration duration) {
    setStartDate(getChartModel().getTaskManager().shift(getStartDate(), duration));
  }

  @Override
  public void setStartOffset(int pixels) {
    getChartModel().setHorizontalOffset(pixels);
  }

  private TimeUnit getBottomTimeUnit() {
    return getChartModel().getBottomUnit();
  }

  @Override
  public Date getEndDate() {
    return getChartModel().getEndDate();
  }

  @Override
  public void setDimensions(int height, int width) {
    Dimension bounds = new Dimension(width, height);
    getChartModel().setBounds(bounds);
  }

  @Override
  public void setBottomUnit(TimeUnit bottomUnit) {
    getChartModel().setBottomTimeUnit(bottomUnit);
  }

  @Override
  public void setTopUnit(TimeUnit topUnit) {
    getChartModel().setTopTimeUnit(topUnit);
  }

  @Override
  public void setBottomUnitWidth(int width) {
    getChartModel().setBottomUnitWidth(width);
  }

  @Override
  public String getName() {
    return myChartComponent.getName();
  }

  @Override
  public void reset() {
    myChartComponent.reset();
  }

  @Override
  public GPOptionGroup[] getOptionGroups() {
    return getChartModel().getChartOptionGroups();
  }

  @Override
  public Chart createCopy() {
    return new AbstractChartImplementation(myProject, myUiFacade, getChartModel().createCopy(), myChartComponent);
  }

  @Override
  public Object getAdapter(Class arg0) {
    return null;
  }

  @Override
  public ChartSelection getSelection() {
    throw new UnsupportedOperationException();
  }

  @Override
  public IStatus canPaste(ChartSelection selection) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void paste(ChartSelection selection) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addSelectionListener(ChartSelectionListener listener) {
    mySelectionListeners.add(listener);
  }

  @Override
  public void removeSelectionListener(ChartSelectionListener listener) {
    mySelectionListeners.remove(listener);
  }

  protected void fireSelectionChanged() {
    for (Iterator<ChartSelectionListener> listeners = mySelectionListeners.iterator(); listeners.hasNext();) {
      ChartSelectionListener nextListener = listeners.next();
      nextListener.selectionChanged();
    }
  }

  @Override
  public void addRenderer(ChartRendererBase renderer) {
    myChartModel.addRenderer(renderer);
  }

  @Override
  public void resetRenderers() {
    myChartModel.resetRenderers();
  }

  @Override
  public ChartModel getModel() {
    return myChartModel;
  }

  @Override
  public ChartUIConfiguration getStyle() {
    return myChartModel.getChartUIConfiguration();
  }

  private Integer myCachedHeaderHeight = null;
  public int getHeaderHeight(final JComponent tableContainer, final JComponent table) {
    if (myCachedHeaderHeight == null) {
      tableContainer.addComponentListener(new ComponentAdapter() {
        @Override
        public void componentMoved(ComponentEvent e) {
          myCachedHeaderHeight = null;
          tableContainer.removeComponentListener(this);
        }
        @Override
        public void componentResized(ComponentEvent e) {
          myCachedHeaderHeight = null;
          tableContainer.removeComponentListener(this);
        }
      });
      table.addComponentListener(new ComponentAdapter() {
        @Override
        public void componentMoved(ComponentEvent e) {
          myCachedHeaderHeight = null;
          table.removeComponentListener(this);
        }
        @Override
        public void componentResized(ComponentEvent e) {
          myCachedHeaderHeight = null;
          table.removeComponentListener(this);
        }
      });
      Point tableLocation = table.getLocationOnScreen();
      Point containerLocation = tableContainer.getLocationOnScreen();

      int height = tableLocation.y - containerLocation.y + myUiFacade.getLogo().getHeight(null);
      myCachedHeaderHeight = height;
    }
    return myCachedHeaderHeight;
  }

  public static class ChartSelectionImpl implements ChartSelection {
    private List<Task> myTasks = new ArrayList<Task>();
    private List<HumanResource> myHumanResources = new ArrayList<HumanResource>();
    private boolean isTransactionRunning;

    @Override
    public boolean isEmpty() {
      return myTasks.isEmpty() && myHumanResources.isEmpty();
    }

    @Override
    public IStatus isDeletable() {
      return Status.OK_STATUS;
    }

    @Override
    public void startCopyClipboardTransaction() {
      if (isTransactionRunning) {
        throw new IllegalStateException("Transaction is already running");
      }
      isTransactionRunning = true;
    }

    @Override
    public void startMoveClipboardTransaction() {
      if (isTransactionRunning) {
        throw new IllegalStateException("Transaction is already running");
      }
      isTransactionRunning = true;
    }

    @Override
    public void cancelClipboardTransaction() {
      isTransactionRunning = false;
    }

    @Override
    public void commitClipboardTransaction() {
      isTransactionRunning = false;
    }

  }

  public MouseHoverLayerUi createMouseHoverLayer() {
    return new MouseHoverLayerUi();
  }
}