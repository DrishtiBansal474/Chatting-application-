import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;
import javax.swing.border.EmptyBorder;

public class Client implements ActionListener {

    JTextField text;
    static JPanel a1;
    static Box vertical = Box.createVerticalBox();
    static JFrame f = new JFrame();
    static DataOutputStream dout;

    static String userName = "User";   // dynamic name

    Client() {

        f.setLayout(null);

        JPanel p1 = new JPanel();
        p1.setBackground(new Color(7, 94, 84));
        p1.setBounds(0, 0, 450, 70);
        p1.setLayout(null);
        f.add(p1);

        JLabel name = new JLabel(userName);   // <-- Replaced Bunty
        name.setBounds(110, 15, 100, 18);
        name.setForeground(Color.WHITE);
        name.setFont(new Font("SAN_SERIF", Font.BOLD, 18));
        p1.add(name);

        JLabel status = new JLabel("Active Now");
        status.setBounds(110, 35, 100, 18);
        status.setForeground(Color.WHITE);
        status.setFont(new Font("SAN_SERIF", Font.BOLD, 14));
        p1.add(status);

        a1 = new JPanel();
        a1.setBounds(5, 75, 440, 570);
        a1.setLayout(new BoxLayout(a1, BoxLayout.Y_AXIS));
        f.add(a1);

        text = new JTextField();
        text.setBounds(5, 655, 310, 40);
        text.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
        f.add(text);

        JButton send = new JButton("Send");
        send.setBounds(320, 655, 123, 40);
        send.setBackground(new Color(7, 94, 84));
        send.setForeground(Color.WHITE);
        send.addActionListener(this);
        send.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
        f.add(send);

        f.setSize(450, 700);
        f.setLocation(800, 50);
        f.setUndecorated(true);
        f.getContentPane().setBackground(Color.WHITE);

        f.setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        try {
            String out = userName + ": " + text.getText();  // attach name to message

            JPanel p2 = formatLabel(out);

            JPanel right = new JPanel(new BorderLayout());
            right.add(p2, BorderLayout.LINE_END);

            vertical.add(right);
            vertical.add(Box.createVerticalStrut(15));

            a1.add(vertical);

            dout.writeUTF(out);
            text.setText("");

            f.repaint();
            f.validate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JPanel formatLabel(String out) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel output = new JLabel("<html><p style=\"width: 150px\">" + out + "</p></html>");
        output.setFont(new Font("Tahoma", Font.PLAIN, 16));
        output.setBackground(new Color(37, 211, 102));
        output.setOpaque(true);
        output.setBorder(new EmptyBorder(15, 15, 15, 50));

        panel.add(output);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        JLabel time = new JLabel();
        time.setText(sdf.format(cal.getTime()));

        panel.add(time);
        return panel;
    }

    public static void main(String[] args) {

        // ---- ASK USER NAME BEFORE GUI ----
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your name: ");
        userName = scanner.nextLine();

        new Client();

        try {
            Socket s = new Socket("127.0.0.1", 6001);
            DataInputStream din = new DataInputStream(s.getInputStream());
            dout = new DataOutputStream(s.getOutputStream());

            while (true) {
                String msg = din.readUTF();
                JPanel panel = formatLabel(msg);

                JPanel left = new JPanel(new BorderLayout());
                left.add(panel, BorderLayout.LINE_START);

                vertical.add(left);
                vertical.add(Box.createVerticalStrut(15));

                a1.add(vertical);
                f.validate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}