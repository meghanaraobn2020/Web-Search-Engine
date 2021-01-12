import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.Image;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class MainWindow extends JDialog {
    public static HashMap<Integer, ArrayList<String>> dictionary ;

    public static JFrame f;
    public static JPanel p;

    public static JTextField searchField;

    public static JButton searchButton;
    public static JButton clearButton;

    public static JLabel[] rankLbl;
    public static JLabel[] pathLbl;
    public static JLabel[] lastModifiedLbl;
    public static JLabel[] scoreLbl;
    public static JLabel[] highltedTxtLbl;

    public static JTextField[] rankTF;
    public static JTextField[] pathTF;
    public static JTextField[] lastModifiedTF;
    public static JTextField[] scoreTF;
    public static JEditorPane[] editorPane;

    public static JPanel[] jpanel;

    public static void main(String[] args) {
        String inputPath = "Dataset";
        //check whether the Dataset exist and give error message
        String indexPath = "Index";
        Indexing.indexing(inputPath, indexPath);

        f=new JFrame();

        f.setSize(1000,800);
        f.getContentPane().setLayout(null);

        p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
        p.setBounds(10, 109, 964, 613);
        f.getContentPane().add(p);

        searchField = new JTextField();
        searchField.setBounds(166, 22, 608, 20);
        f.getContentPane().add(searchField);
        searchField.setColumns(10);

        searchButton = new JButton("Search");

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
                p.removeAll();
                p.revalidate();
                p.repaint();
                int count = 0;

//				System.out.println("After search");
                for (ArrayList<String> val : dictionary.values()) {

                    jpanel[count] = new JPanel();
                    //jpanel[count].setLayout(null);
                    rankLbl[count] = new JLabel("Rank:");
                    jpanel[count].add(rankLbl[count]);

                    rankTF[count] = new JTextField();
                    jpanel[count].add(rankTF[count]);
                    rankTF[count].setText(val.get(0));

                    pathLbl[count] = new JLabel("Path:");
                    jpanel[count].add(pathLbl[count]);

                    pathTF[count] = new JTextField();
                    jpanel[count].add(pathTF[count]);
                    pathTF[count].setText(val.get(1));


                    lastModifiedLbl[count] = new JLabel("Last Modified:");
                    jpanel[count].add(lastModifiedLbl[count]);

                    lastModifiedTF[count] = new JTextField();
                    jpanel[count].add(lastModifiedTF[count]);
                    lastModifiedTF[count].setText(val.get(2));

                    scoreLbl[count] = new JLabel("Score:");
                    jpanel[count].add(scoreLbl[count]);

                    scoreTF[count] = new JTextField();
                    jpanel[count].add(scoreTF[count]);
                    scoreTF[count].setText(val.get(3));

                    highltedTxtLbl[count] = new JLabel("Text:");
                    jpanel[count].add(highltedTxtLbl[count]);


                    editorPane[count] = new JEditorPane();
                    editorPane[count].setContentType("text/html");
                    jpanel[count].add(editorPane[count]);

                    String htmlTxt = "<html>"+val.get(4)+"</html>";
                    editorPane[count].setText(htmlTxt);

                    try {
                        BufferedImage image = ImageIO.read(new File(val.get(5)));
                        Image newimg = image.getScaledInstance(40, 40,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
						JLabel label = new JLabel(new ImageIcon(newimg));
						jpanel[count].add(label);
                    }catch(Exception e) {

                    }
                    p.add(jpanel[count]);
                    p.revalidate();
                    p.repaint();
                    f.revalidate();
                    f.repaint();
                    count ++;
                }
            }
        });
        searchButton.setBounds(376, 53, 89, 23);
        f.getContentPane().add(searchButton);

        clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                searchField.setText("");
                p.removeAll();
                p.revalidate();
                p.repaint();
            }
        });
        clearButton.setBounds(475, 53, 89, 23);
        f.getContentPane().add(clearButton);

        rankLbl= new JLabel[100];
        pathLbl = new JLabel[100];
        lastModifiedLbl= new JLabel[100];
        scoreLbl = new JLabel[100];
        highltedTxtLbl = new JLabel[100];

        rankTF = new JTextField[100];
        pathTF = new JTextField[100];
        lastModifiedTF= new JTextField[100];
        scoreTF = new JTextField[100];
        editorPane = new JEditorPane[100];

        jpanel = new JPanel[100];

        f.setVisible(true);//making the frame visible
    }
}
