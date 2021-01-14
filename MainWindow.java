import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.*;

public class MainWindow {

	public static HashMap<Integer, ArrayList<String>> dictionary ;

	
    public static void main(String[] args)
    {
    	//Folders where the Dataset is located
    	String inputPath = "Dataset";
        String indexPath = "Index";
        Boolean flag = Indexing.indexing(inputPath, indexPath);
        
        if (flag == false) {
        	String message = "There was not possible to index any file.\n"
        	        + "Please create a folder Dataset in the same path as the jar file \n";
        	JOptionPane.showMessageDialog(new JFrame(), message, "Indexing Error",
        	        JOptionPane.ERROR_MESSAGE);
        }else {
        	JFrame frame = new JFrame();

            frame.setSize(1000,900);

            frame.setTitle("Search Engine - The Strings");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            frame.getContentPane().setBackground(Color.decode("#FFFFFF"));

            JTextField searchField = new JTextField();
            searchField.setBounds(58, 22, 608, 20);
            frame.getContentPane().add(searchField);
            searchField.setColumns(10);

            
            
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setAutoscrolls(true);
            panel.setOpaque(false);
            frame.add(panel,BorderLayout.NORTH);

            JScrollPane scrollPane = new JScrollPane(panel);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setBounds(20, 50, 900, 600);
            //scrollPane.setOpaque(false);
            scrollPane.setBackground(Color.WHITE);

            JPanel contentPane = new JPanel(null);
            contentPane.setPreferredSize(new Dimension(1000, 900));
            contentPane.add(scrollPane);
            contentPane.setOpaque(false);
            
            JButton searchButton = new JButton("Search");
            searchButton.setBounds(695, 21, 89, 23);
            frame.getContentPane().add(searchButton);
            searchButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                	String queryInput = searchField.getText();
	                dictionary = null;
	                try {
	                    dictionary = SearchIndex.searching(indexPath, queryInput);
	                } catch (Exception e) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
	                }
	                panel.removeAll();
	                panel.revalidate();
	                panel.repaint();
	                if (dictionary.isEmpty()) {
	                	JLabel labelError = new JLabel("There are no results that match your search");
	                	panel.add(labelError);
	                }
	                
	                for (ArrayList<String> val : dictionary.values()) {
	                	JPanel generalPanel = new JPanel();
	                	generalPanel.setOpaque(false);
	                	generalPanel.setLayout(new FlowLayout());
	                    JLabel label = new JLabel();
	                    if(val.get(5) == "") {
							
							label = new JLabel();
							label.setText("--No Image--");
	
						}
						else {

							 try {
			                        BufferedImage image = ImageIO.read(new File(val.get(5)));
			                        Image newimg = image.getScaledInstance(60, 60,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
									label = new JLabel(new ImageIcon(newimg));
			                    }catch(Exception e) {

			                    }
						}
	                    
	                    
	                    JLabel rankLbl = new JLabel("Rank:");
	                    //rankLbl.setFont(rankLbl.getFont().deriveFont(rankLbl.getFont().getStyle() & ~Font.BOLD));
	                    JLabel rankValue = new JLabel(val.get(0));
	                    rankValue.setFont(rankValue.getFont().deriveFont(rankValue.getFont().getStyle() & ~Font.BOLD));
	                    JLabel nameLbl = new JLabel("Name:");
	                    JLabel nameValue = new JLabel(val.get(1));
	                    nameValue.setFont(nameValue.getFont().deriveFont(nameValue.getFont().getStyle() & ~Font.BOLD));
	                    JLabel scoreLbl = new JLabel("Score:");
	                    JLabel scoreValue = new JLabel(val.get(3));
	                    scoreValue.setFont(scoreValue.getFont().deriveFont(scoreValue.getFont().getStyle() & ~Font.BOLD));
	                    JLabel lastModifiedLbl = new JLabel("Last Modified:");
	                    JLabel lastModifiedValue = new JLabel(val.get(2));
	                    lastModifiedValue.setFont(lastModifiedValue.getFont().deriveFont(lastModifiedValue.getFont().getStyle() & ~Font.BOLD));
	                    JLabel highltedTxtLbl = new JLabel("Text:");
	                    JEditorPane editorPane = new JEditorPane();
	                    editorPane.setContentType("text/html");
	                    String htmlTxt = "<html>"+val.get(4)+"</html>";
	                    editorPane.setText(htmlTxt);
	                    
	                    
	                    
	                    JPanel ssp1 = new JPanel();
                        //ssp1.setLayout(new FlowLayout());
                        ssp1.setBackground(Color.WHITE);
                        ssp1.setPreferredSize(new Dimension(panel.getWidth(), 170));
                        GroupLayout layout = new GroupLayout(ssp1);
                        ssp1.setLayout(layout);
	                    layout.setAutoCreateGaps(false);
	                    layout.setAutoCreateContainerGaps(true);

                                                
                        layout.setHorizontalGroup(
	                    		layout.createSequentialGroup()
	                    			  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
	    	                    		      .addComponent(rankLbl)
	    	                    		      .addComponent(label)
	                    					  )	 
	                    			  .addComponent(rankValue)
	                    		      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
	                    		           .addComponent(nameLbl)
	                    		           .addComponent(scoreLbl)
	                    		           .addComponent(lastModifiedLbl)
	                    		           .addComponent(highltedTxtLbl))
	                    		      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING) 
                   		        		   .addComponent(nameValue)
                   		        		   .addComponent(scoreValue)
                   		        		   .addComponent(lastModifiedValue)
                   		        		   .addComponent(editorPane))
	                    		);
	                    layout.setVerticalGroup(
	                    		   layout.createSequentialGroup()
	                    		      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	                    		           .addComponent(rankLbl)
	                    		           .addComponent(rankValue))
	                    		      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	                    		           .addComponent(label)
	                    		           .addGroup(layout.createSequentialGroup()
	                    		        		   .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	    	                    		        		   .addComponent(nameLbl)
	    	    	                    		           .addComponent(nameValue))
	                    		        		   .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	         	                    		    		  .addComponent(scoreLbl)
	         	                    		    		  .addComponent(scoreValue))
	         	                    		       .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	         	                    		    		  .addComponent(lastModifiedLbl)
	         	                    		    		  .addComponent(lastModifiedValue))
	         	                    		       .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	         	                    		    		  .addComponent(highltedTxtLbl)
	         	                    		    		  .addComponent(editorPane))
	                    		        		   )
	                    		           )
	                    );

                       

                        generalPanel.add(ssp1);
                        panel.add(generalPanel); 
	                    
	                }
	                
                	
                	
                	frame.add(contentPane, BorderLayout.CENTER);
                    frame.pack();
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    frame.setVisible(true);
                }
                
                    
            });

            

            JButton clearButton = new JButton("Clear");
	        clearButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent arg0) {
	                searchField.setText("");
	                panel.removeAll();
	                panel.revalidate();
	                panel.repaint();
	            }
	        });
	        clearButton.setBounds(817, 21, 89, 23);
	        frame.getContentPane().add(clearButton); 
	        
	        
            frame.add(contentPane, BorderLayout.CENTER);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
        }
        
    }

}