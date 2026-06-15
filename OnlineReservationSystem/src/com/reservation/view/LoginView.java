package com.reservation.view;

import com.reservation.controller.AuthController;
import com.reservation.model.Admin;
import com.reservation.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/*
 * LoginView.java - Entry screen for the Rail Booking Platform
 * Offers dual-mode authentication: traveler sign-in and admin sign-in
 */
public class LoginView extends JFrame {

    // Theme palette
    private static final Color CLR_DARK    = new Color(26, 26, 46);
    private static final Color CLR_PANEL   = new Color(22, 33, 62);
    private static final Color CLR_BORDER  = new Color(15, 52, 96);
    private static final Color CLR_ACTION  = new Color(233, 69, 96);
    private static final Color CLR_TXT     = new Color(224, 224, 224);
    private static final Color CLR_MUTED   = new Color(160, 160, 160);
    private static final Color CLR_INPUT   = new Color(30, 30, 55);

    private JTextField     loginField;
    private JPasswordField secretField;
    private JLabel         feedbackLbl;
    private AuthController auth;

    public LoginView() {
        auth = new AuthController();
        buildUI();
    }

    private void buildUI() {
        setTitle("Rail Booking Platform – Sign In");
        setSize(520, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(CLR_DARK);

        /* --- top branding --- */
        JPanel brand = new JPanel();
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setBackground(CLR_DARK);
        brand.setBorder(BorderFactory.createEmptyBorder(40, 0, 10, 0));

        JLabel logo = centredLabel("\u2708", new Font("Segoe UI", Font.PLAIN, 48), CLR_ACTION);
        JLabel heading = centredLabel("Rail Booking Platform",
                new Font("Segoe UI", Font.BOLD, 28), Color.WHITE);
        JLabel tagline = centredLabel("Sign in to manage your journeys",
                new Font("Segoe UI", Font.PLAIN, 14), CLR_MUTED);

        brand.add(logo);  brand.add(Box.createVerticalStrut(10));
        brand.add(heading); brand.add(Box.createVerticalStrut(5));
        brand.add(tagline);

        /* --- credential card --- */
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CLR_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER, 1),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)));

        loginField  = makeInput(20);
        secretField = makeSecret(20);

        feedbackLbl = new JLabel(" ");
        feedbackLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        feedbackLbl.setForeground(new Color(255, 82, 82));
        feedbackLbl.setAlignmentX(LEFT_ALIGNMENT);

        JButton signInBtn  = makeBtn("Sign In",     CLR_ACTION,  Color.WHITE);
        JButton adminBtn   = makeBtn("Admin Login",  CLR_BORDER,  Color.WHITE);
        signInBtn.addActionListener(e -> attemptUserLogin());
        adminBtn.addActionListener(e -> attemptAdminLogin());

        JButton regLink = new JButton("Don't have an account? Register");
        regLink.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        regLink.setForeground(CLR_ACTION);
        regLink.setContentAreaFilled(false);
        regLink.setBorderPainted(false);
        regLink.setFocusPainted(false);
        regLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        regLink.setAlignmentX(CENTER_ALIGNMENT);
        regLink.addActionListener(e -> { new RegistrationView().setVisible(true); dispose(); });
        regLink.addMouseListener(hoverFg(regLink, CLR_ACTION));

        addFieldGroup(card, "\u2709  Username", loginField);
        card.add(Box.createVerticalStrut(10));
        addFieldGroup(card, "\u26BF  Password", secretField);
        card.add(Box.createVerticalStrut(6));
        card.add(feedbackLbl);
        card.add(Box.createVerticalStrut(14));
        card.add(signInBtn);
        card.add(Box.createVerticalStrut(8));
        card.add(adminBtn);
        card.add(Box.createVerticalStrut(14));
        card.add(regLink);

        JPanel cardWrap = new JPanel(new BorderLayout());
        cardWrap.setBackground(CLR_DARK);
        cardWrap.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        cardWrap.add(card, BorderLayout.CENTER);

        /* --- footer --- */
        JLabel footer = new JLabel("\u00A9 2026 Rail Booking Platform", SwingConstants.CENTER);
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setForeground(CLR_MUTED);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));
        footer.setOpaque(true);
        footer.setBackground(CLR_DARK);

        root.add(brand,    BorderLayout.NORTH);
        root.add(cardWrap, BorderLayout.CENTER);
        root.add(footer,   BorderLayout.SOUTH);
        setContentPane(root);
    }

    /* --- action handlers --- */

    private void attemptUserLogin() {
        String user = loginField.getText().trim();
        String pass = new String(secretField.getPassword());
        if (user.isEmpty() || pass.isEmpty()) {
            feedbackLbl.setText("Both fields are required."); return;
        }
        User u = auth.loginUser(user, pass);
        if (u != null) { new DashboardView(u).setVisible(true); dispose(); }
        else {
            String msg = auth.getValidationError();
            feedbackLbl.setText(msg != null ? msg : "Incorrect credentials.");
            secretField.setText("");
        }
    }

    private void attemptAdminLogin() {
        String user = loginField.getText().trim();
        String pass = new String(secretField.getPassword());
        if (user.isEmpty() || pass.isEmpty()) {
            feedbackLbl.setText("Enter admin credentials."); return;
        }
        Admin a = auth.loginAdmin(user, pass);
        if (a != null) { new AdminPanelView(a).setVisible(true); dispose(); }
        else { feedbackLbl.setText("Admin authentication failed."); secretField.setText(""); }
    }

    /* --- component factories --- */

    private void addFieldGroup(JPanel target, String caption, JComponent input) {
        JLabel lbl = new JLabel(caption);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(CLR_TXT);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        target.add(lbl);
        target.add(Box.createVerticalStrut(5));
        target.add(input);
    }

    private JLabel centredLabel(String txt, Font f, Color c) {
        JLabel l = new JLabel(txt); l.setFont(f); l.setForeground(c);
        l.setAlignmentX(CENTER_ALIGNMENT); return l;
    }

    private JTextField makeInput(int cols) {
        JTextField tf = new JTextField(cols);
        styleInput(tf);
        return tf;
    }

    private JPasswordField makeSecret(int cols) {
        JPasswordField pf = new JPasswordField(cols);
        styleInput(pf);
        return pf;
    }

    private void styleInput(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setForeground(Color.WHITE);
        f.setBackground(CLR_INPUT);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        f.setAlignmentX(LEFT_ALIGNMENT);
    }

    private JButton makeBtn(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(fg); b.setBackground(bg);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        b.setAlignmentX(LEFT_ALIGNMENT);
        final Color orig = bg;
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(orig.brighter()); }
            public void mouseExited(MouseEvent e)  { b.setBackground(orig); }
        });
        return b;
    }

    private MouseAdapter hoverFg(JButton btn, Color base) {
        return new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(Color.WHITE); }
            public void mouseExited(MouseEvent e)  { btn.setForeground(base); }
        };
    }
}
