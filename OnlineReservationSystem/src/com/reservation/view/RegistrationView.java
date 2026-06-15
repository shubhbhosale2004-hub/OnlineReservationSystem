package com.reservation.view;

import com.reservation.controller.AuthController;
import com.reservation.util.ValidationUtil;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

/*
 * RegistrationView.java - New account creation form
 * Includes real-time password strength feedback
 */
public class RegistrationView extends JFrame {

    private static final Color CLR_DARK   = new Color(26, 26, 46);
    private static final Color CLR_PANEL  = new Color(22, 33, 62);
    private static final Color CLR_EDGE   = new Color(15, 52, 96);
    private static final Color CLR_ACCENT = new Color(233, 69, 96);
    private static final Color CLR_TXT    = new Color(224, 224, 224);
    private static final Color CLR_DIM    = new Color(160, 160, 160);
    private static final Color CLR_INPUT  = new Color(30, 30, 55);

    private JTextField     nameInput, loginInput, mailInput, phoneInput;
    private JPasswordField passInput, confirmInput;
    private JLabel         errLbl, strengthLbl;
    private AuthController auth;

    public RegistrationView() {
        auth = new AuthController();
        assembleUI();
    }

    private void assembleUI() {
        setTitle("Rail Booking Platform – Create Account");
        setSize(550, 780);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(CLR_DARK);

        // Header
        JPanel hdr = new JPanel();
        hdr.setLayout(new BoxLayout(hdr, BoxLayout.Y_AXIS));
        hdr.setBackground(CLR_DARK);
        hdr.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));

        JLabel title = makeCentredLbl("Create Your Account",
                new Font("Segoe UI", Font.BOLD, 26), Color.WHITE);
        JLabel sub = makeCentredLbl("Please fill in the details below",
                new Font("Segoe UI", Font.PLAIN, 13), CLR_DIM);
        hdr.add(title); hdr.add(Box.createVerticalStrut(5)); hdr.add(sub);

        // Form card
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CLR_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_EDGE, 1),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)));

        nameInput    = inputField(20);
        loginInput   = inputField(20);
        mailInput    = inputField(20);
        phoneInput   = inputField(20);
        passInput    = secretField(20);
        confirmInput = secretField(20);

        strengthLbl = new JLabel("Strength: ");
        strengthLbl.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        strengthLbl.setForeground(CLR_DIM);
        strengthLbl.setAlignmentX(LEFT_ALIGNMENT);

        passInput.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { refreshStrength(); }
            public void removeUpdate(DocumentEvent e)  { refreshStrength(); }
            public void changedUpdate(DocumentEvent e)  { refreshStrength(); }
        });

        errLbl = new JLabel(" ");
        errLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errLbl.setForeground(new Color(255, 82, 82));
        errLbl.setAlignmentX(LEFT_ALIGNMENT);

        JButton createBtn = styledBtn("Create Account", CLR_ACCENT, Color.WHITE);
        createBtn.addActionListener(e -> performRegistration());

        JButton switchBtn = new JButton("Already registered? Sign in");
        switchBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        switchBtn.setForeground(CLR_ACCENT);
        switchBtn.setContentAreaFilled(false);
        switchBtn.setBorderPainted(false);
        switchBtn.setFocusPainted(false);
        switchBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        switchBtn.setAlignmentX(CENTER_ALIGNMENT);
        switchBtn.addActionListener(e -> { new LoginView().setVisible(true); dispose(); });

        fieldRow(card, "Full Name",         nameInput);
        fieldRow(card, "Username",          loginInput);
        fieldRow(card, "E-mail",            mailInput);
        fieldRow(card, "Phone (10 digits)", phoneInput);
        fieldRow(card, "Password",          passInput);
        card.add(strengthLbl); card.add(Box.createVerticalStrut(8));
        fieldRow(card, "Confirm Password",  confirmInput);
        card.add(errLbl); card.add(Box.createVerticalStrut(12));
        card.add(createBtn); card.add(Box.createVerticalStrut(10));
        card.add(switchBtn);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(CLR_DARK);
        wrap.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        wrap.add(card, BorderLayout.CENTER);

        root.add(hdr,  BorderLayout.NORTH);
        root.add(wrap, BorderLayout.CENTER);
        setContentPane(root);
    }

    private void refreshStrength() {
        String pwd = new String(passInput.getPassword());
        if (pwd.isEmpty()) { strengthLbl.setText("Strength: "); strengthLbl.setForeground(CLR_DIM); return; }
        String level = ValidationUtil.getPasswordStrength(pwd);
        strengthLbl.setText("Strength: " + level);
        switch (level) {
            case "Strong": strengthLbl.setForeground(new Color(76, 175, 80)); break;
            case "Medium": strengthLbl.setForeground(new Color(255, 193, 7)); break;
            default:       strengthLbl.setForeground(new Color(255, 82, 82)); break;
        }
    }

    private void performRegistration() {
        String name    = nameInput.getText().trim();
        String login   = loginInput.getText().trim();
        String mail    = mailInput.getText().trim();
        String phone   = phoneInput.getText().trim();
        String pwd     = new String(passInput.getPassword());
        String confirm = new String(confirmInput.getPassword());

        if (!ValidationUtil.isValidName(name))     { errLbl.setText("Name: 2–100 characters required."); return; }
        if (!ValidationUtil.isValidUsername(login)) { errLbl.setText("Username: 3–20 chars, letters/digits/underscore."); return; }
        if (!ValidationUtil.isValidEmail(mail))    { errLbl.setText("Enter a valid e-mail address."); return; }
        if (!phone.isEmpty() && !ValidationUtil.isValidPhone(phone)) { errLbl.setText("Phone must be exactly 10 digits."); return; }
        if (!ValidationUtil.isValidPassword(pwd))  { errLbl.setText("Password must be 6+ characters."); return; }
        if (!pwd.equals(confirm))                  { errLbl.setText("Passwords do not match."); return; }

        boolean ok = auth.registerUser(login, pwd, mail, name, phone);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Account created! You may now sign in.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            new LoginView().setVisible(true);
            dispose();
        } else {
            String msg = auth.getValidationError();
            errLbl.setText(msg != null ? msg : "Registration could not be completed.");
        }
    }

    /* --- factory helpers --- */

    private void fieldRow(JPanel p, String label, JComponent field) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(CLR_TXT);
        l.setAlignmentX(LEFT_ALIGNMENT);
        p.add(l); p.add(Box.createVerticalStrut(4)); p.add(field); p.add(Box.createVerticalStrut(10));
    }

    private JLabel makeCentredLbl(String txt, Font f, Color c) {
        JLabel l = new JLabel(txt); l.setFont(f); l.setForeground(c);
        l.setAlignmentX(CENTER_ALIGNMENT); return l;
    }

    private JTextField inputField(int cols) {
        JTextField tf = new JTextField(cols);
        applyInputStyle(tf); return tf;
    }
    private JPasswordField secretField(int cols) {
        JPasswordField pf = new JPasswordField(cols);
        applyInputStyle(pf); return pf;
    }
    private void applyInputStyle(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setForeground(Color.WHITE); f.setBackground(CLR_INPUT);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_EDGE, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        f.setAlignmentX(LEFT_ALIGNMENT);
    }

    private JButton styledBtn(String txt, Color bg, Color fg) {
        JButton b = new JButton(txt);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(fg); b.setBackground(bg);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        b.setAlignmentX(LEFT_ALIGNMENT);
        final Color base = bg;
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(base.brighter()); }
            public void mouseExited(MouseEvent e)  { b.setBackground(base); }
        });
        return b;
    }
}
