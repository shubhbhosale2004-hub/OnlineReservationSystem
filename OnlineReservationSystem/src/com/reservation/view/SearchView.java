package com.reservation.view;

import com.reservation.controller.ReservationController;
import com.reservation.model.Reservation;
import com.reservation.model.User;
import com.reservation.util.PDFExporter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;

/*
 * SearchView.java - Lookup a booking by its reservation code
 * Displays full details and offers ticket export
 */
public class SearchView extends JFrame {

    private static final Color C_BG    = new Color(26, 26, 46);
    private static final Color C_PANEL = new Color(22, 33, 62);
    private static final Color C_EDGE  = new Color(15, 52, 96);
    private static final Color C_RED   = new Color(233, 69, 96);
    private static final Color C_TXT   = new Color(224, 224, 224);
    private static final Color C_DIM   = new Color(160, 160, 160);
    private static final Color C_INPUT = new Color(30, 30, 55);

    private User activeUser;
    private ReservationController ctrl;
    private JTextField queryField;
    private JPanel resultArea;

    public SearchView(User user) {
        this.activeUser = user;
        this.ctrl = new ReservationController();
        composeUI();
    }

    private void composeUI() {
        setTitle("Search Booking – Rail Booking Platform");
        setSize(850, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(700, 500));

        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(C_BG);

        // Top bar
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(18, 18, 38));
        bar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        bar.setPreferredSize(new Dimension(0, 55));

        JButton backBtn = btn("\u2190 Dashboard", C_EDGE, Color.WHITE);
        backBtn.setPreferredSize(new Dimension(160, 32));
        backBtn.addActionListener(e -> { new DashboardView(activeUser).setVisible(true); dispose(); });

        JLabel heading = new JLabel("\uD83D\uDD0D  Search Booking");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        heading.setForeground(Color.WHITE);

        bar.add(backBtn, BorderLayout.WEST);
        bar.add(heading, BorderLayout.CENTER);

        // Search input
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        searchBar.setBackground(C_BG);

        queryField = new JTextField(25);
        queryField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        queryField.setForeground(Color.WHITE); queryField.setBackground(C_INPUT);
        queryField.setCaretColor(Color.WHITE);
        queryField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_EDGE, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        queryField.setPreferredSize(new Dimension(350, 42));
        queryField.addActionListener(e -> performSearch());

        JButton goBtn = btn("\uD83D\uDD0D Search", C_RED, Color.WHITE);
        goBtn.setPreferredSize(new Dimension(120, 40));
        goBtn.addActionListener(e -> performSearch());

        searchBar.add(queryField); searchBar.add(goBtn);

        // Results area
        resultArea = new JPanel(new BorderLayout());
        resultArea.setBackground(C_BG);
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));

        JLabel hint = new JLabel("Enter a Booking ID (e.g. RES-20260615-1234) and press Search",
                SwingConstants.CENTER);
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        hint.setForeground(C_DIM);
        resultArea.add(hint, BorderLayout.CENTER);

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(C_BG);
        topSection.add(bar,       BorderLayout.NORTH);
        topSection.add(searchBar, BorderLayout.SOUTH);

        shell.add(topSection, BorderLayout.NORTH);
        shell.add(resultArea, BorderLayout.CENTER);
        setContentPane(shell);
    }

    private void performSearch() {
        String term = queryField.getText().trim();
        if (term.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Type a booking ID to search.");
            return;
        }

        Reservation bk = ctrl.getReservation(term);
        resultArea.removeAll();

        if (bk == null) {
            JLabel nf = new JLabel("No booking found for \"" + term + "\"", SwingConstants.CENTER);
            nf.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            nf.setForeground(new Color(255, 82, 82));
            resultArea.add(nf, BorderLayout.CENTER);
        } else {
            resultArea.add(buildCard(bk), BorderLayout.CENTER);
        }

        resultArea.revalidate();
        resultArea.repaint();
    }

    private JPanel buildCard(Reservation bk) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(C_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_EDGE, 1),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)));

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

        addDetail(card, "Booking Code",   bk.getReservationId());
        addDetail(card, "State",          bk.getStatus());
        addDetail(card, "Departure",      bk.getSourceStation()      != null ? bk.getSourceStation()      : "—");
        addDetail(card, "Arrival",        bk.getDestinationStation() != null ? bk.getDestinationStation() : "—");
        addDetail(card, "Travel Date",    bk.getJourneyDate()        != null ? df.format(bk.getJourneyDate()) : "—");
        addDetail(card, "Passenger",      bk.getPassengerName());
        addDetail(card, "Age / Gender",   bk.getAge() + " / " + bk.getGender());
        addDetail(card, "Berth Class",    bk.getSeatType());
        addDetail(card, "Fare",           String.format("Rs. %.2f", bk.getFare()));

        card.add(Box.createVerticalStrut(15));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setBackground(C_PANEL);

        JButton exportBtn = btn("Export Ticket", C_EDGE, Color.WHITE);
        exportBtn.addActionListener(e -> exportTicket(bk));

        if ("CONFIRMED".equalsIgnoreCase(bk.getStatus())) {
            JButton cancelBtn = btn("Cancel Booking", C_RED, Color.WHITE);
            cancelBtn.addActionListener(e -> {
                int opt = JOptionPane.showConfirmDialog(this,
                        "Cancel this booking?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (opt == JOptionPane.YES_OPTION && ctrl.cancelReservation(bk.getReservationId())) {
                    JOptionPane.showMessageDialog(this, "Cancelled successfully.");
                    performSearch();
                }
            });
            actions.add(cancelBtn);
        }

        actions.add(exportBtn);
        card.add(actions);

        return card;
    }

    private void addDetail(JPanel card, String key, String val) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
        row.setBackground(C_PANEL);
        row.setAlignmentX(LEFT_ALIGNMENT);

        JLabel k = new JLabel(key + ":  ");
        k.setFont(new Font("Segoe UI", Font.BOLD, 14));
        k.setForeground(C_DIM);
        k.setPreferredSize(new Dimension(140, 22));

        JLabel v = new JLabel(val);
        v.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        v.setForeground(Color.WHITE);

        row.add(k); row.add(v);
        card.add(row);
    }

    private void exportTicket(Reservation bk) {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File(bk.getReservationId() + "_ticket.txt"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            boolean ok = PDFExporter.exportSingleReservation(bk, fc.getSelectedFile().getAbsolutePath());
            JOptionPane.showMessageDialog(this,
                    ok ? "Ticket saved!" : "Export failed.");
        }
    }

    private JButton btn(String txt, Color bg, Color fg) {
        JButton b = new JButton(txt);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
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
