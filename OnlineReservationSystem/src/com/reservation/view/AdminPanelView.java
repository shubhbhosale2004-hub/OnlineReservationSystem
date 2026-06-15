package com.reservation.view;

import com.reservation.controller.AdminController;
import com.reservation.model.Admin;
import com.reservation.model.Reservation;
import com.reservation.model.User;
import com.reservation.util.PDFExporter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/*
 * AdminPanelView.java - Control center for system administrators
 * Tabbed interface: Bookings | Users | Stats & Reports
 */
public class AdminPanelView extends JFrame {

    private static final Color C_BG    = new Color(26, 26, 46);
    private static final Color C_PANEL = new Color(22, 33, 62);
    private static final Color C_EDGE  = new Color(15, 52, 96);
    private static final Color C_RED   = new Color(233, 69, 96);
    private static final Color C_TXT   = new Color(224, 224, 224);
    private static final Color C_DIM   = new Color(160, 160, 160);
    private static final Color C_INPUT = new Color(30, 30, 55);

    private Admin admin;
    private AdminController ctrl;

    private DefaultTableModel bookingModel, userModel;
    private JTable bookingTable, userTable;

    public AdminPanelView(Admin admin) {
        this.admin = admin;
        this.ctrl = new AdminController();
        layoutUI();
    }

    private void layoutUI() {
        setTitle("Admin Panel – Rail Booking Platform");
        setSize(1200, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 600));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(C_BG);

        /* ---- header ---- */
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(18, 18, 38));
        bar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        bar.setPreferredSize(new Dimension(0, 55));

        JLabel brand = new JLabel("\u2699  Administration Panel");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brand.setForeground(Color.WHITE);

        JLabel info = new JLabel("Admin: " + admin.getUsername() + "  ");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        info.setForeground(C_DIM);

        JButton logoutBtn = makeBtn("Sign Out", C_RED, Color.WHITE);
        logoutBtn.setPreferredSize(new Dimension(100, 32));
        logoutBtn.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Sign out?", "Confirm",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                new LoginView().setVisible(true); dispose();
            }
        });

        JPanel rightPart = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPart.setOpaque(false);
        rightPart.add(info); rightPart.add(logoutBtn);

        bar.add(brand,     BorderLayout.WEST);
        bar.add(rightPart, BorderLayout.EAST);

        /* ---- tabs ---- */
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabs.setBackground(C_BG); tabs.setForeground(Color.WHITE);

        tabs.addTab("All Bookings", buildBookingsTab());
        tabs.addTab("All Users",    buildUsersTab());
        tabs.addTab("Statistics",   buildStatsTab());

        root.add(bar,  BorderLayout.NORTH);
        root.add(tabs, BorderLayout.CENTER);
        setContentPane(root);
    }

    /* ========== BOOKINGS TAB ========== */

    private JPanel buildBookingsTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(C_BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        String[] cols = {"Booking ID","From","To","Date","Passenger","Age",
                         "Gender","Berth","Fare","Status"};
        bookingModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        bookingTable = styledTable(bookingModel);
        refreshBookings();

        JScrollPane sp = new JScrollPane(bookingTable);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(C_PANEL);

        // Search bar
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        top.setBackground(C_BG);

        JTextField searchFld = new JTextField(20);
        searchFld.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchFld.setBackground(C_INPUT); searchFld.setForeground(Color.WHITE);
        searchFld.setCaretColor(Color.WHITE);
        searchFld.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_EDGE, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        JButton searchBtn = makeBtn("Search", C_EDGE, Color.WHITE);
        searchBtn.addActionListener(e -> {
            String q = searchFld.getText().trim();
            if (q.isEmpty()) { refreshBookings(); return; }
            bookingModel.setRowCount(0);
            List<Reservation> res = ctrl.searchReservations(q);
            populateBookings(res);
        });

        JButton refreshBtn = makeBtn("\u21BB Refresh", C_EDGE, Color.WHITE);
        refreshBtn.addActionListener(e -> { searchFld.setText(""); refreshBookings(); });

        JButton deleteBtn = makeBtn("Delete Selected", C_RED, Color.WHITE);
        deleteBtn.addActionListener(e -> deleteSelectedBooking());

        JButton exportBtn = makeBtn("Export Report", new Color(76, 175, 80), Color.WHITE);
        exportBtn.addActionListener(e -> exportReport());

        top.add(searchFld); top.add(searchBtn);
        top.add(refreshBtn); top.add(deleteBtn); top.add(exportBtn);

        p.add(top, BorderLayout.NORTH);
        p.add(sp,  BorderLayout.CENTER);
        return p;
    }

    private void refreshBookings() {
        bookingModel.setRowCount(0);
        populateBookings(ctrl.getAllReservations());
    }

    private void populateBookings(List<Reservation> list) {
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        if (list != null) for (Reservation bk : list) {
            bookingModel.addRow(new Object[]{
                bk.getReservationId(),
                bk.getSourceStation()      != null ? bk.getSourceStation() : "—",
                bk.getDestinationStation() != null ? bk.getDestinationStation() : "—",
                bk.getJourneyDate()        != null ? df.format(bk.getJourneyDate()) : "—",
                bk.getPassengerName(), bk.getAge(), bk.getGender(),
                bk.getSeatType(), String.format("%.2f", bk.getFare()), bk.getStatus()
            });
        }
        if (bookingModel.getRowCount() == 0)
            bookingModel.addRow(new Object[]{"No records","—","—","—","—","—","—","—","—","—"});
    }

    private void deleteSelectedBooking() {
        int row = bookingTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a booking."); return; }
        String code = (String) bookingModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Permanently delete " + code + "?",
                "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (ctrl.deleteReservation(code)) { JOptionPane.showMessageDialog(this, "Deleted."); refreshBookings(); }
            else JOptionPane.showMessageDialog(this, "Deletion failed.");
        }
    }

    private void exportReport() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("reservation_report.txt"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            List<Reservation> all = ctrl.getAllReservations();
            boolean ok = PDFExporter.exportReservationReport(all, fc.getSelectedFile().getAbsolutePath());
            JOptionPane.showMessageDialog(this, ok ? "Report exported!" : "Export failed.");
        }
    }

    /* ========== USERS TAB ========== */

    private JPanel buildUsersTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(C_BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        String[] cols = {"ID", "Username", "Full Name", "Email", "Phone", "Registered"};
        userModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        userTable = styledTable(userModel);
        refreshUsers();

        JScrollPane sp = new JScrollPane(userTable);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(C_PANEL);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        btns.setBackground(C_BG);

        JButton refBtn = makeBtn("\u21BB Refresh", C_EDGE, Color.WHITE);
        refBtn.addActionListener(e -> refreshUsers());

        JButton delBtn = makeBtn("Remove Selected User", C_RED, Color.WHITE);
        delBtn.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a user."); return; }
            int uid = (int) userModel.getValueAt(row, 0);
            String name = (String) userModel.getValueAt(row, 1);
            if (JOptionPane.showConfirmDialog(this, "Delete user '" + name + "'?",
                    "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (ctrl.deleteUser(uid)) { JOptionPane.showMessageDialog(this, "User removed."); refreshUsers(); }
                else JOptionPane.showMessageDialog(this, "Removal failed.");
            }
        });

        btns.add(refBtn); btns.add(delBtn);

        p.add(btns, BorderLayout.NORTH);
        p.add(sp,   BorderLayout.CENTER);
        return p;
    }

    private void refreshUsers() {
        userModel.setRowCount(0);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        List<User> users = ctrl.getAllUsers();
        if (users != null) for (User u : users) {
            userModel.addRow(new Object[]{
                u.getId(), u.getUsername(), u.getFullName(), u.getEmail(),
                u.getPhone() != null ? u.getPhone() : "—",
                u.getCreatedAt() != null ? df.format(u.getCreatedAt()) : "—"
            });
        }
        if (userModel.getRowCount() == 0)
            userModel.addRow(new Object[]{"—","No users","—","—","—","—"});
    }

    /* ========== STATS TAB ========== */

    private JPanel buildStatsTab() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(C_BG);
        p.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        Map<String, Object> st = ctrl.getReservationStats();

        JLabel title = new JLabel("System Overview");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(LEFT_ALIGNMENT);
        p.add(title); p.add(Box.createVerticalStrut(20));

        JPanel cards = new JPanel(new GridLayout(1, 4, 15, 0));
        cards.setBackground(C_BG);
        cards.setAlignmentX(LEFT_ALIGNMENT);
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        cards.add(statCard("Total Bookings",   String.valueOf(st.get("totalReservations")),     C_EDGE));
        cards.add(statCard("Active",           String.valueOf(st.get("confirmedReservations")), new Color(76, 175, 80)));
        cards.add(statCard("Cancelled",        String.valueOf(st.get("cancelledReservations")), C_RED));
        cards.add(statCard("Revenue",          String.format("Rs. %.2f", (double) st.get("totalRevenue")), new Color(33, 150, 243)));

        p.add(cards);
        p.add(Box.createVerticalStrut(25));

        JLabel usersTitle = new JLabel("Registered Users: " + ctrl.getAllUsers().size());
        usersTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        usersTitle.setForeground(C_TXT);
        usersTitle.setAlignmentX(LEFT_ALIGNMENT);
        p.add(usersTitle);

        p.add(Box.createVerticalGlue());
        return p;
    }

    private JPanel statCard(String label, String value, Color accent) {
        JPanel c = new JPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBackground(C_PANEL);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accent, 2),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        JLabel vLbl = new JLabel(value);
        vLbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
        vLbl.setForeground(accent);
        vLbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel kLbl = new JLabel(label);
        kLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        kLbl.setForeground(C_DIM);
        kLbl.setAlignmentX(LEFT_ALIGNMENT);

        c.add(vLbl); c.add(Box.createVerticalStrut(5)); c.add(kLbl);
        return c;
    }

    /* --- shared helpers --- */

    private JTable styledTable(DefaultTableModel mdl) {
        JTable t = new JTable(mdl);
        t.setBackground(C_PANEL); t.setForeground(C_TXT);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setRowHeight(32); t.setGridColor(C_EDGE);
        t.setSelectionBackground(C_EDGE);
        t.setSelectionForeground(Color.WHITE);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        t.getTableHeader().setBackground(C_EDGE);
        t.getTableHeader().setForeground(Color.WHITE);
        return t;
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
