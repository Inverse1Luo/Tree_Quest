package de.unistuttgart.treequest.app.view;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JTextField;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import java.awt.Font;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.InputVerifier;
import javax.swing.border.EmptyBorder;
import javax.swing.JSlider;

public class SearchPane extends JPanel {
	public JTextField keywordField;
	public JFormattedTextField latField;
	public JFormattedTextField lonField;
	public JTextField lonRangeField;
	public JTextField latRangeField;
	public JFormattedTextField distanceField;
	public JFormattedTextField startField;
	public JFormattedTextField endField;
	public JTextField urlField;
	public JTextField hashTagField;
	public JTextField atUserField;
	public JButton sendButton;
	public JSlider textReserveSlider;
	
	private SimpleDateFormat dateFormat;
    private NumberFormat distanceFormat;
    private NumberFormat latLonFormat;
    
    // This input verifier will allow JFormattedTextField to be null
    private InputVerifier inputVerifier = new InputVerifier() {
		@Override
		public boolean verify(JComponent input) {
			if (input instanceof JFormattedTextField) {
	             JFormattedTextField ftf = (JFormattedTextField)input;
	             try {
					ftf.commitEdit();
				} catch (ParseException e) {
				}
	             AbstractFormatter formatter = ftf.getFormatter();
	             if (formatter != null) {
	                 String text = ftf.getText();
	                 // if the text in ftf is empty
	                 // then allow this null input
	                 if (text.isEmpty()) {
	                	 ftf.setValue(null);
	                	 return true;
	                 }
	                 try {
	                      formatter.stringToValue(text);
	                      return true;
	                  } catch (ParseException pe) {
	                      return false;
	                  }
	              }
	          }
	          return true;
		}

		@Override
		public boolean shouldYieldFocus(JComponent input) {
			return verify(input);
		}
	};
    
	/**
	 * Create the panel.
	 */
	public SearchPane() {
		setUpFormats();

		setBorder(new EmptyBorder(10, 10, 10, 10));
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel_1 = new JPanel();
		add(panel_1, BorderLayout.NORTH);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JLabel lblFilter = new JLabel("Filter:");
		lblFilter.setFont(new Font("Arial Black", Font.PLAIN, 30));
		panel_1.add(lblFilter, BorderLayout.CENTER);
		
		Component verticalStrut = Box.createVerticalStrut(20);
		panel_1.add(verticalStrut, BorderLayout.SOUTH);
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		panel_1.add(horizontalStrut, BorderLayout.WEST);
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.PREF_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("pref:grow"),},
			new RowSpec[] {
				RowSpec.decode("fill:pref:grow"),
				RowSpec.decode("pref:grow"),
				RowSpec.decode("fill:pref:grow"),
				RowSpec.decode("6dlu:grow"),
				RowSpec.decode("fill:pref:grow"),
				RowSpec.decode("6dlu:grow"),
				RowSpec.decode("fill:pref:grow"),
				RowSpec.decode("6dlu:grow"),
				RowSpec.decode("fill:pref:grow"),
				RowSpec.decode("6dlu:grow"),
				RowSpec.decode("fill:pref:grow"),
				RowSpec.decode("6dlu:grow"),
				RowSpec.decode("fill:pref:grow"),
				RowSpec.decode("6dlu:grow"),
				RowSpec.decode("fill:pref:grow"),
				RowSpec.decode("6dlu:grow"),
				RowSpec.decode("fill:pref:grow"),
				RowSpec.decode("6dlu"),
				RowSpec.decode("fill:pref:grow"),
				RowSpec.decode("6dlu"),
				RowSpec.decode("fill:pref:grow"),
				RowSpec.decode("6dlu"),
				RowSpec.decode("pref:grow"),}));
		
		JLabel lblKeyword = new JLabel("Keyword:");
		lblKeyword.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel.add(lblKeyword, "1, 1, right, fill");
		
		keywordField = new JTextField();
		keywordField.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel.add(keywordField, "3, 1, fill, default");
		keywordField.setColumns(10);
		
		textReserveSlider = new JSlider();
		textReserveSlider.setPaintLabels(true);
		textReserveSlider.setMinorTickSpacing(1);
		textReserveSlider.setMajorTickSpacing(10);
		textReserveSlider.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textReserveSlider.setToolTipText("Set keyword reserve ratio with the range of [0, 1]");
		textReserveSlider.setPaintTicks(true);
		textReserveSlider.setValue(0);
		panel.add(textReserveSlider, "3, 2, fill, center");
		
		JLabel lblLat = new JLabel("Lat:");
		lblLat.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel.add(lblLat, "1, 3, right, fill");
		
		latField = new JFormattedTextField(latLonFormat);
		latField.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel.add(latField, "3, 3, fill, default");
		latField.setColumns(20);
		latField.setInputVerifier(inputVerifier);
		
		JLabel lblLon = new JLabel("Lon:");
		lblLon.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel.add(lblLon, "1, 5, right, fill");
		
		lonField = new JFormattedTextField(latLonFormat);
		lonField.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel.add(lonField, "3, 5, fill, default");
		lonField.setColumns(20);
		lonField.setInputVerifier(inputVerifier);
		
		JLabel lblDistance = new JLabel("Distance:");
		lblDistance.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel.add(lblDistance, "1, 7, right, fill");
		
		distanceField = new JFormattedTextField(distanceFormat);
		distanceField.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel.add(distanceField, "3, 7, fill, default");
		distanceField.setColumns(20);
		distanceField.setInputVerifier(inputVerifier);
		
		JLabel lblLatRange = new JLabel(" Lat Range:");
		lblLatRange.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel.add(lblLatRange, "1, 9, right, fill");
		
		latRangeField = new JTextField();
		latRangeField.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel.add(latRangeField, "3, 9, fill, default");
		latRangeField.setColumns(20);
		latRangeField.setInputVerifier(inputVerifier);
		
		JLabel lblLonRange = new JLabel("Lon Range:");
		lblLonRange.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel.add(lblLonRange, "1, 11, right, fill");
		
		lonRangeField = new JTextField();
		lonRangeField.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel.add(lonRangeField, "3, 11, fill, default");
		lonRangeField.setColumns(20);
		lonRangeField.setInputVerifier(inputVerifier);
		
		
		JLabel lblStartTime = new JLabel("Start Time:");
		lblStartTime.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel.add(lblStartTime, "1, 13, right, fill");
		
		startField = new JFormattedTextField(dateFormat);
		startField.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel.add(startField, "3, 13, fill, default");
		startField.setColumns(20);
		startField.setInputVerifier(inputVerifier);
		
		JLabel lblEndTime = new JLabel("End Time:");
		lblEndTime.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel.add(lblEndTime, "1, 15, right, fill");
		
		endField = new JFormattedTextField(dateFormat);
		endField.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel.add(endField, "3, 15, fill, default");
		endField.setColumns(20);
		endField.setInputVerifier(inputVerifier);
		
		JLabel lblUrl = new JLabel("URL:");
		lblUrl.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel.add(lblUrl, "1, 17, right, fill");
		
		urlField = new JTextField();
		urlField.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel.add(urlField, "3, 17, fill, default");
		urlField.setColumns(10);
		
		JLabel labelHashTag = new JLabel("#:");
		labelHashTag.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel.add(labelHashTag, "1, 19, right, fill");
		
		hashTagField = new JTextField();
		hashTagField.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel.add(hashTagField, "3, 19, fill, default");
		hashTagField.setColumns(10);
		
		JLabel labelAtUser = new JLabel("@:");
		labelAtUser.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel.add(labelAtUser, "1, 21, right, fill");
		
		atUserField = new JTextField();
		atUserField.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel.add(atUserField, "3, 21, fill, default");
		atUserField.setColumns(10);
		
		sendButton = new JButton("Search");
		sendButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel.add(sendButton, "3, 23, right, fill");
	}
	
	//Create and set up number formats. These objects also
    //parse numbers input by user.
    private void setUpFormats() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    	
        
        // Distance should be a float number with only 2 fraction digits
//        distanceFormat = NumberFormat.getNumberInstance();
//        distanceFormat.setMinimumFractionDigits(2);
//        distanceFormat.setMaximumFractionDigits(2);
        
        // Lat and Lon should only contain 10 fraction digits.
        latLonFormat = NumberFormat.getNumberInstance();
        latLonFormat.setMaximumFractionDigits(10);
        latLonFormat.setMinimumFractionDigits(10);
    }
}
