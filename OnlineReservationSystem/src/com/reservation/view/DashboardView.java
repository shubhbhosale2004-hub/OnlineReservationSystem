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
 * DashboardView.java - Main hub after traveler sign-in
 * Shows quick-action cards, sidebar navigation, and recent bookings table
 */
public class DashboardView extends JFrame {

    private static final Color C_BG      = new Color(26, 26, 46);
    private static final Color C_PANEL   = new Color(22, 33, 62);
    private static final Color C_ACCENT  = new Color(15, 52, 96);
    private static final Color C_RED     = new Color(233, 69, 96);
    private static final Color C_TXT     = new Color(224, 224, 224);
    private static final Color C_DIM     = new Color(160, 160, 160);

    private User loggedUser;
    private ReservationController bookCtrl;

    public DashboardView(User user) {
        this.loggedUser = user;
        this.bookCtrl = new ReservationController();
        constructLayout();
    }

    private void constructLayout() {
        setTitle("Dashboard – Rail Booking Platform");
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(C_BG);

        /* ---- top bar ---- */
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(18, 18, 38));
        topBar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        topBar.setPreferredSize(new Dimension(0, 55));

        JLabel brandLbl = new JLabel("\u2708  Rail Booking Platform");
        brandLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brandLbl.setForeground(Color.WHITE);

        JLabel greetLbl = new JLabel("Hello, " + loggedUser.getFullName() + "  ");
        greetLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        greetLbl.setForeground(C_DIM);

        JButton exitBtn = navBtn("Logout");
        exitBtn.setBackground(C_RED);
        exitBtn.setPreferredSize(new Dimension(90, 32));
        exitBtn.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Sign out now?",
                    "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                new LoginView().setVisible(true); dispose();
            }
        });

        JPanel rightBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightBar.setOpaque(false);
        rightBar.add(greetLbl); rightBar.add(exitBtn);

        topBar.add(brandLbl, BorderLayout.WEST);
        topBar.add(rightBar, BorderLayout.EAST);

        /* ---- sidebar ---- */
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(C_PANEL);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel menuTitle = new JLabel("  Menu");
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        menuTitle.setForeground(C_RED);
        menuTitle.setAlignmentX(LEFT_ALIGNMENT);
        sidebar.add(menuTitle); sidebar.add(Box.createVerticalStrut(20));

        String[][] links = {
            {"\u2795  New Booking",       "book"},
            {"\u2709  Booking History",    "history"},
            {"\uD83D\uDD0D  Find Booking", "find"},
            {"\u2699  Account Info",       "account"}
        };
        for (String[] lk : links) {
            JButton nb = navBtn(lk[0]);
            nb.setMaximumSize(new Dimension(200, 40));
            nb.setAlignmentX(LEFT_ALIGNMENT);
            final String act = lk[1];
            nb.addActionListener(e -> navigate(act));
            sidebar.add(nb); sidebar.add(Box.createVerticalStrut(8));
        }
        sidebar.add(Box.createVerticalGlue());

        /* ---- content ---- */
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(C_BG);
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Welcome banner
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(C_ACCENT);
        banner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(20, 60, 110), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));
        banner.setPreferredSize(new Dimension(0, 100));

        JLabel bannerTxt = new JLabel("<html><b style='font-size:16px'>Welcome back, "
                + loggedUser.getFullName() + "!</b><br>"
                + "<span style='font-size:12px;color:#aaa'>Manage your upcoming journeys from this dashboard.</span></html>");
        bannerTxt.setForeground(Color.WHITE);
        banner.add(bannerTxt, BorderLayout.WEST);

        // Action tiles
        JPanel tiles = new JPanel(new GridLayout(1, 3, 15, 0));
        tiles.setBackground(C_BG);
        tiles.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        tiles.add(tile("\u2708", "Book Now",      "Start a new booking",       "book"));
        tiles.add(tile("\u2709", "My Journeys",   "View past & upcoming trips","history"));
        tiles.add(tile("\uD83D\uDD0D", "Search",  "Locate a booking by ID",   "find"));

        // Recent bookings table
        JPanel tblWrap = new JPanel(new BorderLayout());
        tblWrap.setBackground(C_PANEL);
        tblWrap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_ACCENT, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel tblHead = new JLabel("Recent Bookings");
        tblHead.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tblHead.setForeground(Color.WHITE);
        tblHead.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        String[] cols = {"Booking ID", "Destination", "Date", "Status"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        try {
            List<Reservation> recs = bookCtrl.getUserReservations(loggedUser.getId());
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            int shown = 0;
            if (recs != null) for (Reservation r : recs) {
                if (shown >= 5) break;
                mdl.addRow(new Object[]{
                    r.getReservationId(),
                    r.getDestinationStation() != null ? r.getDestinationStation() : "—",
                    r.getJourneyDate() != null ? df.format(r.getJourneyDate()) : "—",
                    r.getStatus()
                });
                shown++;
            }
        } catch (Exception ignored) {}

        if (mdl.getRowCount() == 0)
            mdl.addRow(new Object[]{"No bookings yet", "—", "—", "—"});

        JTable tbl = new JTable(mdl);
        tbl.setBackground(C_PANEL); tbl.setForeground(C_TXT);
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbl.setRowHeight(35); tbl.setGridColor(C_ACCENT);
        tbl.setSelectionBackground(C_ACCENT); tbl.setSelectionForeground(Color.WHITE);
        tbl.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tbl.getTableHeader().setBackground(C_ACCENT);
        tbl.getTableHeader().setForeground(Color.WHITE);

        JScrollPane sp = new JScrollPane(tbl);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(C_PANEL);

        tblWrap.add(tblHead, BorderLayout.NORTH);
        tblWrap.add(sp,      BorderLayout.CENTER);

        JPanel mid = new JPanel(new BorderLayout());
        mid.setBackground(C_BG);
        mid.add(tiles,   BorderLayout.NORTH);
        mid.add(tblWrap, BorderLayout.CENTER);

        content.add(banner, BorderLayout.NORTH);
        content.add(mid,    BorderLayout.CENTER);

        shell.add(topBar,  BorderLayout.NORTH);
        shell.add(sidebar, BorderLayout.WEST);
        shell.add(content, BorderLayout.CENTER);
        setContentPane(shell);
    }

    private void navigate(String dest) {
        switch (dest) {
            case "book":    new ReservationFormView(loggedUser).setVisible(true); dispose(); break;
            case "history": new HistoryView(loggedUser).setVisible(true); dispose(); break;
            case "find":    new SearchView(loggedUser).setVisible(true); dispose(); break;
            case "account": showAccountInfo(); break;
        }
    }

    private void showAccountInfo() {
        String info = String.format("Name: %s\nLogin: %s\nMail: %s\nPhone: %s",
                loggedUser.getFullName(), loggedUser.getUsername(),
                loggedUser.getEmail(),
                loggedUser.getPhone() != null ? loggedUser.getPhone() : "N/A");
        JOptionPane.showMessageDialog(this, info, "Account Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel tile(String icon, String heading, String detail, String dest) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(C_PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_ACCENT, 1),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel ic = new JLabel(icon);
        ic.setFont(new Font("Segoe UI", Font.PLAIN, 28)); ic.setForeground(C_RED);
        ic.setAlignmentX(LEFT_ALIGNMENT);

        JLabel h = new JLabel(heading);
        h.setFont(new Font("Segoe UI", Font.BOLD, 15)); h.setForeground(Color.WHITE);
        h.setAlignmentX(LEFT_ALIGNMENT);

        JLabel d = new JLabel(detail);
        d.setFont(new Font("Segoe UI", Font.PLAIN, 11)); d.setForeground(C_DIM);
        d.setAlignmentX(LEFT_ALIGNMENT);

        p.add(ic); p.add(Box.createVerticalStrut(8));
        p.add(h);  p.add(Box.createVerticalStrut(3));
        p.add(d);

        p.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { navigate(dest); }
            public void mouseEntered(MouseEvent e) { p.setBackground(C_ACCENT); }
            public void mouseExited(MouseEvent e)  { p.setBackground(C_PANEL); }
        });
        return p;
    }

    private JButton navBtn(String txt) {
        JButton b = new JButton(txt);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(Color.WHITE); b.setBackground(C_PANEL);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(C_ACCENT); }
            public void mouseExited(MouseEvent e)  { b.setBackground(C_PANEL); }
        });
        return b;
    }
}
