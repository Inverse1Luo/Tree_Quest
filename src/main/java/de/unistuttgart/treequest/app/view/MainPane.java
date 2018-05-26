package de.unistuttgart.treequest.app.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import de.unistuttgart.treequest.app.model.ContentView;
import de.unistuttgart.treequest.app.model.InfmaTreeMinimap;
import de.unistuttgart.treequest.app.model.InfmaTreeNodeDetail;
import de.unistuttgart.treequest.app.model.InfmaTreeNodeReporter;
import de.unistuttgart.treequest.app.model.InfmaTreemap;

public class MainPane extends JPanel {

	public static final int BAR_OBJ_WIDTH = 280;
	int sidebarY = 0;
	public JPanel tablePane;
	public InfmaTreemap scatterPane;
	public JPanel queryPane;
	public JPanel sidePane;
	public JPanel detailPane;
	public JTextField queryField;
	public JButton queryBtn;
	public JProgressBar progressBar;
	public InfmaTreeNodeReporter report;
	public InfmaTreeMinimap minimap;
	public JButton searchBtn;
	public ContentView tweetTable;
	public JButton classifyBtn;
	public JButton lensBtn;
	public JButton relBtn;
	public JButton irrelBtn;
	public JButton irNBtn;
	public JButton relNBtn;
	public JButton zoomInBtn;
	public JButton zoomOutBtn;
	public JButton exploreBtn;
	public JButton tsneBtn;
	public JButton timeRangeBtn;
	public JButton tagCloudBtn;
	public JButton saveModelBtn;
	public JScrollPane jScrollPane;
	public InfmaTreeNodeDetail detail;

	public MainPane() {
		this.setLayout(new BorderLayout(0, 0));

		// 散点图和表格
		detailPane = new JPanel();

		detailPane.setLayout(new BorderLayout());

		scatterPane = new InfmaTreemap(null);
		scatterPane.setPreferredSize(new Dimension(700, 500));

		detailPane.add(scatterPane, BorderLayout.CENTER);

		tablePane = tweetTable = new ContentView(null);
		
		tablePane.setPreferredSize(new Dimension(700, 300));

		// 表格
		detailPane.add(tablePane, BorderLayout.SOUTH);
		this.add(detailPane, BorderLayout.CENTER);

		// 旁边栏
		sidePane = new JPanel();
		this.sidePane.setPreferredSize(new Dimension(300, getHeight()));
		sidePane.setLayout(null);
		progressBar = new JProgressBar(0, 100);
		progressBar.setBounds(10, sidebarY + 10, BAR_OBJ_WIDTH, 30);
		progressBar.setStringPainted(true);
		sidePane.add(progressBar);
		
		report = new InfmaTreeNodeReporter();
		report.setBounds(10, sidebarY + 50, BAR_OBJ_WIDTH, 100);
		sidePane.add(report);
		

		minimap = new InfmaTreeMinimap(null);
		minimap.setBounds(10, sidebarY + 160, BAR_OBJ_WIDTH, 200);
		sidePane.add(minimap);

		//buttons
		sidebarY = 370;
		searchBtn = new JButton("Search");
		searchBtn.setBounds(10, sidebarY, BAR_OBJ_WIDTH, 30);
		sidePane.add(searchBtn);
		
		sidebarY = sidebarY + 40;
		zoomInBtn = new JButton("Zoom In");
//		zoomInBtn.setBounds(10, sidebarY + 690, BAR_OBJ_WIDTH, 30);
		zoomInBtn.setBounds(10, sidebarY, 135, 30);
		sidePane.add(zoomInBtn);

		zoomOutBtn = new JButton("Zoom Out");
//		zoomOutBtn.setBounds(10, sidebarY + 730, BAR_OBJ_WIDTH, 30);
		zoomOutBtn.setBounds(155, sidebarY, 135, 30);
		sidePane.add(zoomOutBtn);
		
		sidebarY = sidebarY + 40;
		lensBtn = new JButton("Content Lens");
		lensBtn.setBounds(10, sidebarY , BAR_OBJ_WIDTH, 30);
		sidePane.add(lensBtn);
		
		sidebarY = sidebarY + 40;
		tagCloudBtn = new JButton("Show Tag Cloud");
		tagCloudBtn.setBounds(10, sidebarY, BAR_OBJ_WIDTH, 30);
		sidePane.add(tagCloudBtn);
		
		sidebarY = sidebarY + 40;
		timeRangeBtn = new JButton("Use Post Date");
		timeRangeBtn.setBounds(10, sidebarY , BAR_OBJ_WIDTH, 30);
		sidePane.add(timeRangeBtn);
		
		sidebarY = sidebarY + 40;
		tsneBtn = new JButton("TSNE Map");
		tsneBtn.setBounds(10, sidebarY, BAR_OBJ_WIDTH, 30);
		sidePane.add(tsneBtn);
		
		sidebarY = sidebarY + 40;
		relBtn = new JButton("Relevant Point");
//		relBtn.setBounds(10, sidebarY + 460, BAR_OBJ_WIDTH, 30);
		relBtn.setBounds(10, sidebarY, 135, 30);
		sidePane.add(relBtn);

		irrelBtn = new JButton("Irrelevant Point");
//		irrelBtn.setBounds(10, sidebarY + 500, BAR_OBJ_WIDTH, 30);
		irrelBtn.setBounds(155, sidebarY, 135, 30);
		sidePane.add(irrelBtn);
		
		sidebarY = sidebarY + 40;
		relNBtn = new JButton("Relevant Node");
//		relNBtn.setBounds(10, sidebarY + 560, BAR_OBJ_WIDTH, 30);
		relNBtn.setBounds(10, sidebarY , 135, 30);
		sidePane.add(relNBtn);

		irNBtn = new JButton("Irrelevant Node");
//		irNBtn.setBounds(10, sidebarY + 600, BAR_OBJ_WIDTH, 30);
		irNBtn.setBounds(155, sidebarY, 135, 30);
		sidePane.add(irNBtn);
		
		sidebarY = sidebarY + 40;
		classifyBtn = new JButton("Train Classifier");
		classifyBtn.setBounds(10, sidebarY, BAR_OBJ_WIDTH, 30);
		sidePane.add(classifyBtn);
		
		sidebarY = sidebarY + 40;
		exploreBtn = new JButton("Explore");
		exploreBtn.setBounds(10, sidebarY, BAR_OBJ_WIDTH, 30);
		sidePane.add(exploreBtn);

		sidebarY = sidebarY + 40;
		saveModelBtn = new JButton("Save Current Classifier");
		saveModelBtn.setBounds(10, sidebarY, BAR_OBJ_WIDTH, 30);
		sidePane.add(saveModelBtn);
		
		sidebarY = sidebarY + 40;
		detail = new InfmaTreeNodeDetail();
//		report.setBounds(10, sidebarY + 50, BAR_OBJ_WIDTH, 100);
		jScrollPane = new JScrollPane(detail);
		jScrollPane.setBounds(10, sidebarY, BAR_OBJ_WIDTH, 320);
		///sidePane.add(report);
		sidePane.add(jScrollPane);
		
		this.add(sidePane, BorderLayout.EAST);
	}

}
