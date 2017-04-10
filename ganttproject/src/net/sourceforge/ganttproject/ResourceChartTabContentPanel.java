/*
 * Created on 22.10.2005
 */
package net.sourceforge.ganttproject;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import net.sourceforge.ganttproject.chart.Chart;
import net.sourceforge.ganttproject.gui.ResourceTreeUIFacade;
import net.sourceforge.ganttproject.gui.TestGanttRolloverButton;
import net.sourceforge.ganttproject.gui.UIFacade;
import net.sourceforge.ganttproject.gui.view.GPView;
import net.sourceforge.ganttproject.ourAddIns.HRStore;
import net.sourceforge.ganttproject.ourAddIns.Window;

class ResourceChartTabContentPanel extends ChartTabContentPanel implements GPView {
  private ResourceTreeUIFacade myTreeFacade;
  private Component myResourceChart;
  private JComponent myTabContentPanel;
  private JButton profileButton;
  private IGanttProject project;

  ResourceChartTabContentPanel(IGanttProject project, UIFacade workbenchFacade, ResourceTreeUIFacade resourceTree,
      Component resourceChart) {
    super(project, workbenchFacade, workbenchFacade.getResourceChart());
    myTreeFacade = resourceTree;
    myResourceChart = resourceChart;
    this.project = project;
  }

  JComponent getComponent() {
    if (myTabContentPanel == null) {
      myTabContentPanel = createContentComponent();
    }
    return myTabContentPanel;
  }

  @Override
  protected Component createButtonPanel() {

    System.out.println("Ran!");
    JToolBar buttonBar = new JToolBar();
    buttonBar.setFloatable(false);
    buttonBar.setBorderPainted(false);

    TestGanttRolloverButton upButton = new TestGanttRolloverButton(myTreeFacade.getMoveUpAction());
    upButton.setTextHidden(true);
    buttonBar.add(upButton);

    TestGanttRolloverButton downButton = new TestGanttRolloverButton(myTreeFacade.getMoveDownAction());
    downButton.setTextHidden(true);
    buttonBar.add(downButton);

    //ADDED BY ME
    profileButton = new JButton("Profile");
    profileButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if(project.getHumanResourceManager()!= null){
          Window ourWindow = new Window(project);
        }
      }
    });
    buttonBar.add(profileButton);

    JPanel buttonPanel = new JPanel(new BorderLayout());
    buttonPanel.add(buttonBar, BorderLayout.WEST);
    return buttonPanel;
  }

  @Override
  protected Component getChartComponent() {
    return myResourceChart;
  }

  @Override
  protected Component getTreeComponent() {
    return myTreeFacade.getTreeComponent();
  }

  @Override
  public void setActive(boolean active) {
    if (active) {
      getTreeComponent().requestFocus();
    }
  }

  @Override
  public Chart getChart() {
    return getUiFacade().getResourceChart();
  }

  @Override
  public Component getViewComponent() {
    return getComponent();
  }
}