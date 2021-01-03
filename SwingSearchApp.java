import javax.swing.*;  
import java.awt.event.*;  
import java.awt.*;  

public class SwingSearchApp extends JFrame implements ActionListener {  
//Initializing Components  
    JLabel lb, lb1, lb2, lb3, lb4, lb5;  
    JTextField tf1, tf2, tf3, tf4, tf5;  
    JButton btn;  
    //Creating Constructor for initializing JFrame components  
    SwingSearchApp() {  
        //Providing Title  
        super("Search Query Highlighter");
        lb5 = new JLabel("Enter Query:");
        lb5.setBounds(20, 20, 100, 20);  
        tf5 = new JTextField(20);  
        tf5.setBounds(130, 20, 200, 20);  
        btn = new JButton("Search");
        btn.setBounds(150, 50, 100, 20);
        btn.addActionListener(this);  
        lb = new JLabel("Search Results");
        lb.setBounds(150, 80, 450, 30);
        lb.setForeground(Color.red);  
        lb.setFont(new Font("Serif", Font.BOLD, 20));  
        setVisible(true);  
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        setSize(500, 500);  
        lb1 = new JLabel("Rank 1:");
        lb1.setBounds(20, 120, 100, 20);  
        tf1 = new JTextField(50);  
        tf1.setBounds(130, 120, 200, 20);  
        lb2 = new JLabel("Rank 2:");
        lb2.setBounds(20, 150, 100, 20);  
        tf2 = new JTextField(100);  
        tf2.setBounds(130, 150, 200, 20);  
        lb3 = new JLabel("Rank 3:");
        lb3.setBounds(20, 180, 100, 20);  
        tf3 = new JTextField(50);  
        tf3.setBounds(130, 180, 200, 20);  
        lb4 = new JLabel("Rank 4:");
        lb4.setBounds(20, 210, 100, 20);  
        tf4 = new JTextField(50);  
        tf4.setBounds(130, 210, 200, 20);
        setLayout(null);  
        //Add components to the JFrame  
        add(lb5);  
        add(tf5);  
        add(btn);  
        add(lb);  
        add(lb1);  
        add(tf1);  
        add(lb2);  
        add(tf2);  
        add(lb3);  
        add(tf3);  
        add(lb4);  
        add(tf4);  
        //Set TextField Editable False  
        tf1.setEditable(false);  
        tf2.setEditable(false);  
        tf3.setEditable(false);  
        tf4.setEditable(false);  
    }  
    public void actionPerformed(ActionEvent e) {
        try {  
            String str = tf5.getText();
            String inputPath = "C:\\Users\\budha\\Desktop\\docs";
            String indexPath = "index";
            Indexing.indexing(inputPath, indexPath);
            String[] strArray = new String[10];
            strArray = SearchIndex.searching(indexPath,str);
            //Main ob = new Main();
            tf1.setText(strArray[0]);
            tf2.setText(strArray[1]);
            tf3.setText(strArray[2]);
            tf4.setText(strArray[3]);

            }  
            //Create Exception Handler  
        catch (Exception ex) {
            System.out.println(ex);  
        }  
    }  
    //Running Constructor  
    public static void main(String args[]) {  
        new SwingSearchApp();  
    }  
}  