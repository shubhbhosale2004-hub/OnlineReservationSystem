package com.reservation.view;

import com.reservation.controller.ReservationController;
import com.reservation.model.Route;
import com.reservation.model.User;
import com.reservation.util.FareCalculator;
import com.reservation.util.ValidationUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/*
 * ReservationFormView.java - Journey booking form
 * Dynamically loads route options and computes fare in real time
 */
public class ReservationFormView extends JFrame {

    private static final Color C_BG     = new Color(26, 26, 46);
    private static final Color C_PANEL  = new Color(22, 33, 62);
    private static final Color C_EDGE   = new Color(15, 52, 96);
    private static final Color C_ACTION = new Color(233, 69, 96);
    private static final Color C_TXT    = new Color(224, 224, 224);
    private static final Color C_DIM    = new Color(160, 160, 160);
    private static final Color C_INPUT  = new Color(30, 30, 55);

    private User activeUser;
    private ReservationController ctrl;

    private JComboBox<String> originBox, terminusBox, genderBox, berthBox;
    private JTextField dateInput, paxNameInput, ageInput;
    private JLabel priceLbl, errLbl;

    public ReservationFormView(User user) {
        this.activeUser = user;
        this.ctrl = new ReservationController();
        setupForm();
    }

    private void setupForm() {
        setTitle("Book Journey – Rail Booking Platform");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));

        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(C_BG);

        // Header bar
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(18, 18, 38));
        bar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        bar.setPreferredSize(new Dimension(0, 55));

        JButton goBack = actionBtn("\u2190 Dashboard", C_EDGE, Color.WHITE);
        goBack.setPreferredSize(new Dimension(160, 32));
        goBack.addActionListener(e -> { new DashboardView(activeUser).setVisible(true); dispose(); });

        JLabel title = new JLabel("\u2708  Book Your Journey");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        bar.add(goBack, BorderLayout.WEST);
        bar.add(title,  BorderLayout.CENTER);

        // Form body
        JPanel formArea = new JPanel(new BorderLayout());
        formArea.setBackground(C_BG);
        formArea.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setBackground(C_PANEL);
        grid.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_EDGE, 1),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(8, 8, 8, 8);

        // Combos
        originBox   = new JComboBox<>(); applyCbStyle(originBox);
        terminusBox = new JComboBox<>(); applyCbStyle(terminusBox);
        genderBox   = new JComboBox<>(new String[]{"Male", "Female", "Other"}); applyCbStyle(genderBox);
        berthBox    = new JComboBox<>(new String[]{"SLEEPER","AC_3TIER","AC_2TIER","AC_FIRST"}); applyCbStyle(berthBox);

        try {
            List<String> origins = ctrl.getAllSourceStations();
            originBox.addItem("-- Pick Departure --");
            if (origins != null) for (String s : origins) originBox.addItem(s);
        } catch (Exception ex) { originBox.addItem("No routes loaded"); }

        terminusBox.addItem("-- Pick Departure First --");

        originBox.addActionListener(e -> refreshTerminus());
        terminusBox.addActionListener(e -> refreshPrice());
        berthBox.addActionListener(e -> refreshPrice());

        dateInput    = styledField(15);
        dateInput.setToolTipText("Enter as YYYY-MM-DD");
        paxNameInput = styledField(20);
        paxNameInput.setText(activeUser.getFullName());
        ageInput     = styledField(5);

        priceLbl = new JLabel("Estimated Fare: Rs. 0.00");
        priceLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        priceLbl.setForeground(new Color(76, 175, 80));

        errLbl = new JLabel(" ");
        errLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errLbl.setForeground(new Color(255, 82, 82));

        int row = 0;
        addRow(grid, gc, row++, "Departure Station:",       originBox);
        addRow(grid, gc, row++, "Arrival Station:",         terminusBox);
        addRow(grid, gc, row++, "Date of Travel (YYYY-MM-DD):", dateInput);
        addRow(grid, gc, row++, "Passenger Name:",          paxNameInput);
        addRow(grid, gc, row++, "Age:",                     ageInput);
        addRow(grid, gc, row++, "Gender:",                  genderBox);
        addRow(grid, gc, row++, "Berth Class:",             berthBox);

        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 2;
        gc.insets = new Insets(15, 8, 5, 8);
        grid.add(priceLbl, gc); row++;

        gc.gridy = row; gc.insets = new Insets(2, 8, 8, 8);
        grid.add(errLbl, gc); row++;

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btns.setBackground(C_PANEL);
        JButton confirmBtn = actionBtn("\u2714 Confirm Booking", C_ACTION, Color.WHITE);
        confirmBtn.setPreferredSize(new Dimension(170, 40));
        confirmBtn.addActionListener(e -> processBooking());
        JButton clearBtn = actionBtn("\u21BA Clear", C_EDGE, Color.WHITE);
        clearBtn.setPreferredSize(new Dimension(120, 40));
        clearBtn.addActionListener(e -> clearForm());
        btns.add(confirmBtn); btns.add(clearBtn);

        gc.gridy = row; gc.insets = new Insets(10, 8, 8, 8);
        grid.add(btns, gc);

        formArea.add(grid, BorderLayout.CENTER);
        shell.add(bar,      BorderLayout.NORTH);
        shell.add(formArea, BorderLayout.CENTER);
        setContentPane(shell);
    }

    private void addRow(JPanel p, GridBagConstraints gc, int r, String lbl, JComponent comp) {
        gc.gridx = 0; gc.gridy = r; gc.gridwidth = 1; gc.weightx = 0.3;
        JLabel l = new JLabel(lbl);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(C_TXT);
        p.add(l, gc);
        gc.gridx = 1; gc.weightx = 0.7;
        p.add(comp, gc);
    }

    private void refreshTerminus() {
        String src = (String) originBox.getSelectedItem();
        terminusBox.removeAllItems();
        if (src == null || src.startsWith("--")) {
            terminusBox.addItem("-- Pick Departure First --"); return;
        }
        try {
            List<String> dests = ctrl.getDestinationStations(src);
            if (dests != null && !dests.isEmpty()) {
                terminusBox.addItem("-- Pick Arrival --");
                for (String d : dests) terminusBox.addItem(d);
            } else {
                terminusBox.addItem("None available");
            }
        } catch (Exception ex) { terminusBox.addItem("Load error"); }
    }

    private void refreshPrice() {
        try {
            String src  = (String) originBox.getSelectedItem();
            String dst  = (String) terminusBox.getSelectedItem();
            String cls  = (String) berthBox.getSelectedItem();
            if (src == null || dst == null || src.startsWith("--") || dst.startsWith("--")) {
                priceLbl.setText("Estimated Fare: Rs. 0.00"); return;
            }
            Route r = ctrl.getRouteByStations(src, dst);
            if (r != null) {
                double amt = ctrl.calculateFare(r.getBaseFare(), cls);
                priceLbl.setText("Estimated Fare: " + FareCalculator.formatFare(amt));
            }
        } catch (Exception ex) { priceLbl.setText("Estimated Fare: Rs. 0.00"); }
    }

    private void processBooking() {
        try {
            String src  = (String) originBox.getSelectedItem();
            String dst  = (String) terminusBox.getSelectedItem();
            String dt   = dateInput.getText().trim();
            String name = paxNameInput.getText().trim();
            String ageTxt = ageInput.getText().trim();
            String gen  = (String) genderBox.getSelectedItem();
            String cls  = (String) berthBox.getSelectedItem();

            if (src == null || src.startsWith("--"))   { errLbl.setText("Choose a departure station."); return; }
            if (dst == null || dst.startsWith("--") || dst.startsWith("No")) { errLbl.setText("Choose an arrival station."); return; }
            if (dt.isEmpty())                          { errLbl.setText("Travel date is required."); return; }
            if (!ValidationUtil.isValidName(name))     { errLbl.setText("Enter a valid passenger name."); return; }
            if (ageTxt.isEmpty())                      { errLbl.setText("Age is required."); return; }

            int age;
            try { age = Integer.parseInt(ageTxt);
                if (!ValidationUtil.isValidAge(age))   { errLbl.setText("Age must be 1–120."); return; }
            } catch (NumberFormatException nf)         { errLbl.setText("Age must be numeric."); return; }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            java.util.Date parsed;
            try { parsed = sdf.parse(dt); }
            catch (ParseException pe)                  { errLbl.setText("Date format: YYYY-MM-DD."); return; }
            if (!ValidationUtil.isValidDate(parsed))   { errLbl.setText("Date cannot be in the past."); return; }

            Route route = ctrl.getRouteByStations(src, dst);
            if (route == null)                         { errLbl.setText("Route not found."); return; }

            java.sql.Date sqlDt = new java.sql.Date(parsed.getTime());
            if (!ctrl.checkSeatAvailability(route.getId(), sqlDt, cls)) {
                errLbl.setText("No seats left for this class and date."); return;
            }

            String code = ctrl.createReservation(
                    activeUser.getId(), route.getId(), parsed, name, age, gen, cls);

            if (code != null) {
                double fare = ctrl.calculateFare(route.getBaseFare(), cls);
                JOptionPane.showMessageDialog(this,
                        "Booking confirmed!\n\nCode: " + code
                                + "\nRoute: " + src + " → " + dst
                                + "\nFare: " + FareCalculator.formatFare(fare),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                new DashboardView(activeUser).setVisible(true);
                dispose();
            } else {
                errLbl.setText("Booking failed — try again.");
            }
        } catch (Exception ex) {
            errLbl.setText("Unexpected error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void clearForm() {
        if (originBox.getItemCount() > 0) originBox.setSelectedIndex(0);
        dateInput.setText(""); paxNameInput.setText(activeUser.getFullName());
        ageInput.setText(""); genderBox.setSelectedIndex(0); berthBox.setSelectedIndex(0);
        priceLbl.setText("Estimated Fare: Rs. 0.00"); errLbl.setText(" ");
    }

    /* --- component factories --- */

    private void applyCbStyle(JComboBox<String> cb) {
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cb.setBackground(C_INPUT); cb.setForeground(Color.WHITE);
    }

    private JTextField styledField(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setForeground(Color.WHITE); tf.setBackground(C_INPUT);
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_EDGE, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        return tf;
    }

    private JButton actionBtn(String txt, Color bg, Color fg) {
        JButton b = new JButton(txt);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(fg); b.setBackground(bg);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        final Color base = bg;
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(base.brighter()); }
            public void mouseExited(MouseEvent e)  { b.setBackground(base); }
        });
        return b;
    }
}
