package net.sourceforge.ganttproject.ourAddIns;

import net.sourceforge.ganttproject.IGanttProject;
import net.sourceforge.ganttproject.resource.HumanResource;
import net.sourceforge.ganttproject.resource.HumanResourceManager;
import net.sourceforge.ganttproject.task.ResourceAssignment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Window extends JFrame implements ListSelectionListener,MouseListener{
	
	private JList list;
	private JPanel listPanel;
	private JScrollPane listScrollPane;
	private JScrollPane taskScrollPane;
	private JPanel profilePanel;
	private JPanel profileDetails;
	private JPanel taskPanel;
	private JPanel verticalStackPanel;
	private JPanel detailsPanel;
	private JPanel proPicPanel;
	private JPanel progressAndComplete;
	private JLabel proPic;
	private ProgressBar progress;

	private  IGanttProject project = null;

	private String icons = System.getProperty("user.home") + "/Desktop/ganttproject-master/icons/";
	
	private JLabel name;
	private JLabel number;
	private JLabel email;
	private JLabel role;
	private JLabel upload;

	private final int iSize = 16;

	
	private Color bGround = new Color(70,70,70);
	private Color textColor = Color.white;
	private Color flamingo = new Color(239,72,54);
	private Color pictonBlue = new Color(89,171,227);

	private ArrayList<Profile> profiles = new ArrayList<>();
	
	public Window(IGanttProject project){

		this.project = project;

		this.setSize(500,500);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		extractHRData();
		buildUi();
		this.setVisible(true);
	}

	private void extractHRData(){
		HumanResourceManager manager = project.getHumanResourceManager();
		HumanResource[] resources = manager.getResourcesArray();

		for(HumanResource r : resources){
			Profile p = new Profile(r.getName(),r.getPhone(),r.getMail(),r.getRole().getName(),r.getProfilePicture());
			ResourceAssignment[] assignments = r.getAssignments();
			for(ResourceAssignment ra : assignments){
				p.addTask(ra.getTask());
			}
			profiles.add(p);
		}
	}

	private String[] getProfileNames(){

		ArrayList<String> names = new ArrayList<>();

		for(Profile p : profiles){
			names.add(p.getName());
		}
		String[] aNames = new String[names.size()];
		aNames = names.toArray(aNames);
		return aNames;
	}
	
	//Loads Image
	public static ImageIcon imageLoader(String path, int width, int height){
		
		try {
			BufferedImage image = ImageIO.read(new File(path));
			Image img = image.getScaledInstance(width, height, Image.SCALE_DEFAULT);
			ImageIcon icon = new ImageIcon(img);
			return icon;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	//BUILD THE ENTIRE UI
	public void buildUi(){

		UIManager.put("Label.font", new Font("Verdana",Font.PLAIN,16));
		//JLabels That hold basic profile Details
		name = new JLabel("Curt-Nicholas Williams");;
		name.setForeground(textColor);

		number = new JLabel("(876) 377-71774");
		number.setForeground(textColor);
		number.setIcon(Window.imageLoader(icons+"phone.png",iSize+3,iSize+3));

		email = new JLabel("curtnicholas@live.com");
		email.setForeground(textColor);
		email.setIcon(Window.imageLoader(icons+"email.png",iSize+3,iSize+3));

		role = new JLabel("Software Engineer");
		role.setForeground(textColor);
		role.setIcon(Window.imageLoader(icons+"name.png",iSize+3,iSize+3));
		
		//Progress bar
		progress = new ProgressBar(65, Color.GREEN);
		progress.setBackground(bGround);
		
		//List
		list = new JList(getProfileNames());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setForeground(Color.WHITE);
		list.setBackground(bGround);
		list.addListSelectionListener(this);
		
		
		
		//List ScrollPane at the side
		listScrollPane = new JScrollPane(list);
		listScrollPane.setBorder(new EmptyBorder(0,0,0,0));
		

		
		//The List at the side
		listPanel = new JPanel(new BorderLayout());
		listPanel.setPreferredSize(new Dimension(100,400));
		listPanel.add(listScrollPane);
		
		//The panel that holds the profile picture
		proPicPanel = new JPanel(new BorderLayout());
		
		//The JLabel that hold the image for the profile picture
		proPic = new JLabel();
		proPic.setPreferredSize(new Dimension(150,0));
		proPic.setBorder(BorderFactory.createLoweredBevelBorder());
		proPicPanel.add(proPic);
		
		//The panel that hold the textual details about the profile
		detailsPanel = new JPanel();
		detailsPanel.setLayout(new BoxLayout(detailsPanel,BoxLayout.Y_AXIS));
		detailsPanel.setBackground(bGround);
		detailsPanel.setBorder(new EmptyBorder(10,10,10,10));
		detailsPanel.add(name);
		detailsPanel.add(Box.createRigidArea(new Dimension(0,5)));
		detailsPanel.add(number);
		detailsPanel.add(Box.createRigidArea(new Dimension(0,5)));
		detailsPanel.add(email);
		detailsPanel.add(Box.createRigidArea(new Dimension(0,5)));
		detailsPanel.add(role);
		detailsPanel.add(Box.createRigidArea(new Dimension(0,10)));
		upload = new JLabel("Upload");
		upload.setIcon(Window.imageLoader(icons+"camera.png",iSize+3,iSize+3));
		upload.setForeground(Color.WHITE);
		upload.addMouseListener(this);
		detailsPanel.add(upload);

		
		//The panel that holds both the profile pic and the details
		profileDetails = new JPanel(new BorderLayout());
		profileDetails.setPreferredSize(new Dimension(0,150));
		profileDetails.add(proPicPanel,BorderLayout.LINE_START);
		profileDetails.add(detailsPanel);
		
		//The panel that holds the task
		taskPanel = new JPanel();
		taskPanel.setBackground(bGround);
		taskPanel.setLayout(new BoxLayout(taskPanel,BoxLayout.Y_AXIS));


		//ScrollPane that holds the tasks panel
		taskScrollPane = new JScrollPane(taskPanel);
		taskScrollPane.setPreferredSize(new Dimension(0,80));
		taskScrollPane.setBorder(new EmptyBorder(0,0,0,0));

		//Holds big progress bar and label
		progressAndComplete = new JPanel();
		progressAndComplete.setLayout(new BoxLayout(progressAndComplete,BoxLayout.Y_AXIS));
		progressAndComplete.setPreferredSize(new Dimension(50,0));

		JLabel completion = new JLabel("Completion Rate");
		completion.setForeground(Color.WHITE);
		completion.setFont(new Font("Verdana",Font.ITALIC,16));
		completion.setIcon(Window.imageLoader(icons+"check.png",iSize,iSize));

		progressAndComplete.add(Box.createRigidArea(new Dimension(0,150/2)));
		progressAndComplete.add(progress);
		progressAndComplete.add(completion);
		progressAndComplete.add(Box.createRigidArea(new Dimension(0,150/2)));
		progressAndComplete.setBackground(bGround);
		
		//A panel with a boxlayout with vertical orientation (Horizontal after losing all files)
		verticalStackPanel = new JPanel();
		verticalStackPanel.setLayout(new BoxLayout(verticalStackPanel,BoxLayout.X_AXIS));
		verticalStackPanel.add(progressAndComplete);
		verticalStackPanel.setBackground(bGround);
		verticalStackPanel.add(taskScrollPane);
		
		
		
		
		//The Full profile panel to the right of the list
		profilePanel = new JPanel(new BorderLayout());
		profilePanel.add(profileDetails,BorderLayout.NORTH);
		profilePanel.add(verticalStackPanel);
	
		
		//Add To Window
		this.add(listPanel,BorderLayout.LINE_START);
		this.add(profilePanel);

		this.setVisible(true);
		list.setSelectedIndex(0);
	}
	
	public void addTask(){


	}

	
	//THIS METHOD DOES THE LOADING IN OF PROFILE INFORMATION
	private void loadProfileContent(Profile p){
		loadProfileDetails(p);
		loadProfilePicture(p);
		loadProfileTasks(p);
	}

	private void loadProfileDetails(Profile p){
		this.name.setText(p.getName());
		this.number.setText(p.getPhone());
		this.email.setText(p.getMail());
		this.role.setText(p.getRole());
	}
	private void loadProfilePicture(Profile p){

		ImageIcon pic = Window.imageLoader(p.getPic(), proPicPanel.getWidth(),proPicPanel.getHeight());
		proPic.setPreferredSize(new Dimension(proPicPanel.getWidth(), proPicPanel.getHeight()));
		proPic.setIcon(pic);
	}
	private void loadProfileTasks(Profile p){

		taskPanel.removeAll();
		taskPanel.revalidate();
		taskPanel.repaint();

		for(Task t : p.getTasks()){

			JPanel taskAndProgress = new JPanel();
			taskAndProgress.setLayout(new BoxLayout(taskAndProgress,BoxLayout.Y_AXIS));
			taskAndProgress.setBackground(bGround);
			taskAndProgress.setMaximumSize(new Dimension(100,115));

			ProgressBar bar = t.getProgressBar();
			bar.setPreferredSize(new Dimension(0,70));
			taskAndProgress.add(bar);

			JLabel name = new JLabel(t.getName());
			name.setForeground(Color.WHITE);
			name.setFont(new Font("Verdana",Font.PLAIN,11));
			name.setIcon(Window.imageLoader(icons+"task.png",iSize-1,iSize-1));

			JLabel date = new JLabel(t.getStart() +" - "+t.getEnd());
			date.setForeground(Color.WHITE);
			date.setFont(new Font("Verdana",Font.PLAIN,11));
			date.setIcon(Window.imageLoader(icons+"date.png",iSize-1,iSize-1));

			JLabel priority = new JLabel(t.getPriority());
			priority.setForeground(Color.WHITE);
			priority.setFont(new Font("Verdana",Font.PLAIN,11));

			taskAndProgress.add(name);
			taskAndProgress.add(date);
			taskAndProgress.add(priority);

			taskPanel.add(taskAndProgress);
			taskPanel.add(Box.createRigidArea(new Dimension(0,20)));

			bar.UpdateProgress(t.getProgress());

			progress.UpdateProgress(p.calculateCompletionRate());
		}

	}
	

	
	//THIS IS WHERE WE LOAD THE PROFILE CONTENT BASED ON VALUE SELECTED
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(e.getValueIsAdjusting() == false){
			Profile p = profiles.get(list.getSelectedIndex());
			loadProfileContent(p);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		try{
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "gif", "jpeg");
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Choose a Profile Photo");
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setFileFilter(filter);
			chooser.showOpenDialog(this);
			String filepath = chooser.getSelectedFile().getAbsolutePath();


			project.getHumanResourceManager().getResourcesArray()[list.getSelectedIndex()].setProfilePicture(filepath);
			System.out.println("HR SAVED");
			loadProfilePicture(profiles.get(list.getSelectedIndex()).setPic(filepath));
			JOptionPane.showMessageDialog(this,profiles.get(list.getSelectedIndex()).getPic());
			proPic.revalidate();
			proPic.repaint();
		}catch (Exception ex){
			JOptionPane.showMessageDialog(this,ex.getMessage());
		}



	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
}
