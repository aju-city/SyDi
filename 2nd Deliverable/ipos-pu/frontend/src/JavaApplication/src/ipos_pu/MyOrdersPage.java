/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ipos_pu;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.List;

/**
 *
 * @author nuhur
 */
public class MyOrdersPage extends javax.swing.JFrame {

    // colour palette
    private static final Color BG      = new Color(0x05080f);
    private static final Color PANEL   = new Color(0x080e1a);
    private static final Color NEON    = new Color(0x2563A8);
    private static final Color NEON_LT = new Color(0x7eb8f7);
    private static final Color GREEN   = new Color(0x4ade80);

    private final String username;

    public MyOrdersPage(String username) {
        this.username = username;
        initComponents();
        buildUI();
    }

    private void buildUI() {
        setTitle("IPOS-PU \u2014 My Orders");
        setSize(1280, 760);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setBackground(BG);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(buildNav(),     BorderLayout.NORTH);
        getContentPane().add(buildContent(), BorderLayout.CENTER);
    }

    // nav bar
    private JPanel buildNav() {
        JPanel nav = new JPanel();
        nav.setBackground(PANEL);
        nav.setPreferredSize(new Dimension(0, 58));
        nav.setLayout(new BoxLayout(nav, BoxLayout.X_AXIS));
        nav.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(37, 99, 168, 90)),
            BorderFactory.createEmptyBorder(0, 20, 0, 20)
        ));

        JLabel brand = new JLabel("<html><span style='font-family:Trebuchet MS;font-size:16px;"
            + "font-weight:bold;color:#ffffff'>IPOS</span><span style='font-family:Trebuchet MS;"
            + "font-size:16px;font-weight:bold;color:#7eb8f7'>-PU</span></html>");
        brand.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        brand.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                dispose(); new HomePage(username).setVisible(true);
            }
        });

        JLabel welcomeLbl = new JLabel("Hi, " + (username != null && !username.isEmpty() ? username : "Guest"));
        welcomeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        welcomeLbl.setForeground(new Color(255, 255, 255, 110));

        JButton backBtn = new JButton("\u2190 Back to Home");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        backBtn.setForeground(new Color(255, 255, 255, 160));
        backBtn.setBackground(new Color(0x0b1220));
        backBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(37, 99, 168, 80), 1),
            BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        backBtn.setFocusPainted(false);
        backBtn.setOpaque(true);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> { dispose(); new HomePage(username).setVisible(true); });

        nav.add(brand);
        nav.add(Box.createHorizontalStrut(16));
        nav.add(welcomeLbl);
        nav.add(Box.createHorizontalGlue());
        nav.add(backBtn);
        nav.add(Box.createHorizontalStrut(12));
        nav.add(makeAvatarPanel());
        return nav;
    }

    // loads orders from the manager and shows them in a table or the empty state
    private JScrollPane buildContent() {
        JPanel outer = new JPanel();
        outer.setBackground(BG);
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBorder(BorderFactory.createEmptyBorder(28, 32, 32, 32));

        JLabel subLbl = new JLabel("CUSTOMER SERVICES");
        subLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        subLbl.setForeground(NEON);
        subLbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel titleLbl = new JLabel("My Orders");
        titleLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 22));
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel descLbl = new JLabel("All orders placed during this session.");
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLbl.setForeground(new Color(255, 255, 255, 80));
        descLbl.setAlignmentX(LEFT_ALIGNMENT);

        outer.add(subLbl);
        outer.add(Box.createVerticalStrut(4));
        outer.add(titleLbl);
        outer.add(Box.createVerticalStrut(6));
        outer.add(descLbl);
        outer.add(Box.createVerticalStrut(24));

        List<OrderManager.Order> orders = OrderManager.getOrders();

        if (orders.isEmpty()) {
            outer.add(buildEmptyState());
        } else {
            outer.add(buildOrderTable(orders));
        }

        outer.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(outer);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    // shown when theres no orders yet
    private JPanel buildEmptyState() {
        JPanel empty = new JPanel(new GridBagLayout());
        empty.setBackground(PANEL);
        empty.setPreferredSize(new Dimension(0, 130));
        empty.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        empty.setAlignmentX(LEFT_ALIGNMENT);

        JPanel stack = new JPanel();
        stack.setOpaque(false);
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));

        JLabel icon = new JLabel("[ ]");
        icon.setFont(new Font("Segoe UI", Font.BOLD, 22));
        icon.setForeground(new Color(37, 99, 168, 80));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msg = new JLabel("No orders placed yet");
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        msg.setForeground(new Color(255, 255, 255, 80));
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel hint = new JLabel("Orders you place this session will appear here");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hint.setForeground(new Color(255, 255, 255, 45));
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);

        stack.add(icon);
        stack.add(Box.createVerticalStrut(6));
        stack.add(msg);
        stack.add(Box.createVerticalStrut(4));
        stack.add(hint);
        empty.add(stack);
        return empty;
    }

    // builds the orders table with a header row and one row per order
    private JPanel buildOrderTable(List<OrderManager.Order> orders) {
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setAlignmentX(LEFT_ALIGNMENT);

        JPanel header = new JPanel(new GridLayout(1, 5, 0, 0));
        header.setBackground(new Color(0x0a1018));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        header.setAlignmentX(LEFT_ALIGNMENT);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(37, 99, 168, 60)),
            BorderFactory.createEmptyBorder(0, 16, 0, 16)
        ));

        for (String col : new String[]{"ORDER ID", "DATE", "ITEMS", "TOTAL", "STATUS"}) {
            JLabel lbl = new JLabel(col);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
            lbl.setForeground(NEON);
            lbl.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
            header.add(lbl);
        }

        wrapper.add(header);

        String[] stageNames = { "Received", "Dispatched", "Delivered" };

        for (int i = 0; i < orders.size(); i++) {
            OrderManager.Order o = orders.get(i);
            Color rowBg = i % 2 == 0 ? PANEL : new Color(0x0a1018);

            JPanel row = new JPanel(new GridLayout(1, 5, 0, 0));
            row.setBackground(rowBg);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
            row.setAlignmentX(LEFT_ALIGNMENT);

            // status column colour changes based on how far along the order is
            String stageName  = stageNames[Math.min(o.stage, stageNames.length - 1)];
            Color  stageColor = o.stage == 2 ? GREEN : (o.stage == 1 ? NEON_LT : new Color(255, 255, 255, 160));

            String[] cells = { o.orderId, o.date, o.itemsSummary, o.total, stageName };

            for (int c = 0; c < cells.length; c++) {
                JLabel cell = new JLabel(
                    "<html><div style='width:160px;overflow:hidden'>" + cells[c] + "</div></html>");
                cell.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                cell.setForeground(c == 4 ? stageColor : new Color(255, 255, 255, 190));
                cell.setBackground(rowBg);
                cell.setOpaque(true);
                cell.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 8));
                row.add(cell);
            }

            wrapper.add(row);
        }

        // thin line at the bottom of the table
        JPanel border = new JPanel();
        border.setBackground(new Color(37, 99, 168, 40));
        border.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        border.setAlignmentX(LEFT_ALIGNMENT);
        wrapper.add(border);

        return wrapper;
    }

    // small circle avatar for the nav
    private JPanel makeAvatarPanel() {
        String initials = getInitials(username);
        JPanel avatar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(NEON);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(initials,
                    (getWidth()  - fm.stringWidth(initials)) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(34, 34));
        avatar.setMaximumSize(new Dimension(34, 34));
        avatar.setOpaque(false);
        return avatar;
    }

    // grabs first letters from the name for the avatar initials
    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "G";
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2)
            return ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
        return name.substring(0, Math.min(2, name.length())).toUpperCase();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
