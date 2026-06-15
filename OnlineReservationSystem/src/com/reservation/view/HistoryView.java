package com.reservation.view;

import com.reservation.controller.ReservationController;
import com.reservation.model.Reservation;
import com.reservation.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.List;

/*
 * HistoryView.java - Displays all bookings belonging to the signed-in traveler
 * Supports cancellation via context action button
 */
public class HistoryView extends JFrame {

    private static final Color C_BG    = new Color(26, 26, 46);
    private static final Color C_PANEL = new Color(22, 33, 62);
    private static final Color C_EDGE  = new Color(15, 52, 96);
    private static final Color C_RED   = new Color(233, 69, 96);
    private static final Color C_TXT   = new Color(224, 224, 224);

    private User currentUser;
    private ReservationController ctrl;
    private JTable dataTable;
    private DefaultTableModel tableModel;

    public HistoryView(User user) {
        this.currentUser = user;
        this.ctrl = new ReservationController();
        buildLayout();
    }

    private void buildLayout() {
        setTitle("My Bookings – Rail Booking Platform");
        setSize(1050, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 500));

        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(C_BG);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(18, 18, 38));
        topBar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        topBar.setPreferredSize(new Dimension(0, 55));

        JButton backBtn = makeBtn("\u2190 Dashboard", C_EDGE, Color.WHITE);
        backBtn.setPreferredSize(new Dimension(160, 32));
        backBtn.addActionListener(e -> { new DashboardView(currentUser).setVisible(true); dispose(); });

        JLabel heading = new JLabel("\u2709  My Booking History");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        heading.setForeground(Color.WHITE);

        topBar.add(backBtn, BorderLayout.WEST);
        topBar.add(heading, BorderLayout.CENTER);

        // Table
        String[] cols = {"Booking ID", "From", "To", "Date", "Passenger", "Age",
                         "Gender", "Berth", "Fare (Rs.)", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        dataTable = new JTable(tableModel);
        dataTable.setBackground(C_PANEL); dataTable.setForeground(C_TXT);
        dataTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dataTable.setRowHeight(35); dataTable.setGridColor(C_EDGE);
        dataTable.setSelectionBackground(C_EDGE);
        dataTable.setSelectionForeground(Color.WHITE);
        dataTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        dataTable.getTableHeader().setBackground(C_EDGE);
        dataTable.getTableHeader().setForeground(Color.WHITE);

        loadBookings();

        JScrollPane sp = new JScrollPane(dataTable);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(C_PANEL);

        JPanel tableWrap = new JPanel(new BorderLayout());
        tableWrap.setBackground(C_BG);
        tableWrap.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        tableWrap.add(sp, BorderLayout.CENTER);

        // Bottom action bar
        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionBar.setBackground(C_BG);

        JButton cancelBtn = makeBtn("Cancel Selected", C_RED, Color.WHITE);
        cancelBtn.setPreferredSize(new Dimension(160, 36));
        cancelBtn.addActionListener(e -> cancelSelected());

        JButton refreshBtn = makeBtn("\u21BB Refresh", C_EDGE, Color.WHITE);
        refreshBtn.setPreferredSize(new Dimension(120, 36));
        refreshBtn.addActionListener(e -> loadBookings());

        actionBar.add(cancelBtn);
        actionBar.add(refreshBtn);

        shell.add(topBar,    BorderLayout.NORTH);
        shell.add(tableWrap, BorderLayout.CENTER);
        shell.add(actionBar, BorderLayout.SOUTH);
        setContentPane(shell);
    }

    private void loadBookings() {
        tableModel.setRowCount(0);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        try {
            List<Reservation> list = ctrl.getUserReservations(currentUser.getId());
            if (list != null) for (Reservation bk : list) {
                tableModel.addRow(new Object[]{
                    bk.getReservationId(),
                    bk.getSourceStation()      != null ? bk.getSourceStation()      : "—",
                    bk.getDestinationStation() != null ? bk.getDestinationStation() : "—",
                    bk.getJourneyDate()        != null ? df.format(bk.getJourneyDate()) : "—",
                    bk.getPassengerName(),
                    bk.getAge(),
                    bk.getGender(),
                    bk.getSeatType(),
                    String.format("%.2f", bk.getFare()),
                    bk.getStatus()
                });
            }
        } catch (Exception ex) { ex.printStackTrace(); }

        if (tableModel.getRowCount() == 0)
            tableModel.addRow(new Object[]{"No bookings found", "—","—","—","—","—","—","—","—","—"});
    }

    private void cancelSelected() {
        int row = dataTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a booking first."); return; }

        String code   = (String) tableModel.getValueAt(row, 0);
        String status = (String) tableModel.getValueAt(row, 9);

        if ("CANCELLED".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "This booking is already cancelled."); return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "Cancel booking " + code + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            boolean ok = ctrl.cancelReservation(code);
            if (ok) { JOptionPane.showMessageDialog(this, "Booking cancelled."); loadBookings(); }
            else    JOptionPane.showMessageDialog(this, "Cancellation failed.");
        }
    }

    private JButton makeBtn(String txt, Color bg, Color fg) {
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
