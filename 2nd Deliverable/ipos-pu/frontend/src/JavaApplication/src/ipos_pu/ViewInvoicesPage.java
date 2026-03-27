/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ipos_pu;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.List;

/**
 *
 * @author nuhur
 */
public class ViewInvoicesPage extends javax.swing.JFrame {

    // colour palette
    private static final Color BG      = new Color(0x05080f);
    private static final Color PANEL   = new Color(0x080e1a);
    private static final Color NEON    = new Color(0x2563A8);
    private static final Color NEON_LT = new Color(0x7eb8f7);
    private static final Color GREEN   = new Color(0x4ade80);

    // vat is always 20%
    private static final double VAT_RATE = 0.20;

    private final String username;

    public ViewInvoicesPage(String username) {
        this.username = username;
        initComponents();
        buildUI();
    }

    private void buildUI() {
        setTitle("IPOS-PU \u2014 Invoices");
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

        // clicking the logo goes back to home
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

    // loads all saved orders and renders an invoice card for each one
    private JScrollPane buildContent() {
        JPanel outer = new JPanel();
        outer.setBackground(BG);
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBorder(BorderFactory.createEmptyBorder(28, 32, 32, 32));

        JLabel subLbl = new JLabel("CUSTOMER SERVICES");
        subLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        subLbl.setForeground(NEON);
        subLbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel titleLbl = new JLabel("View Invoices");
        titleLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 22));
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel descLbl = new JLabel("Retail invoices for all orders placed this session.");
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLbl.setForeground(new Color(255, 255, 255, 80));
        descLbl.setAlignmentX(LEFT_ALIGNMENT);

        outer.add(subLbl);
        outer.add(Box.createVerticalStrut(4));
        outer.add(titleLbl);
        outer.add(Box.createVerticalStrut(6));
        outer.add(descLbl);
        outer.add(Box.createVerticalStrut(28));

        List<OrderManager.Order> orders = OrderManager.getOrders();

        if (orders.isEmpty()) {
            outer.add(buildEmptyState());
        } else {
            for (OrderManager.Order order : orders) {
                outer.add(buildInvoiceCard(order));
                outer.add(Box.createVerticalStrut(20));
            }
        }

        outer.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(outer);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    // shown when there are no orders yet
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

        JLabel msg = new JLabel("No invoices yet");
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        msg.setForeground(new Color(255, 255, 255, 80));
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel hint = new JLabel("Invoices are generated when you place an order");
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

    // builds one full invoice card for a given order
    private JPanel buildInvoiceCard(OrderManager.Order order) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PANEL);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(new Color(37, 99, 168, 60));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 12, 12));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        card.setAlignmentX(LEFT_ALIGNMENT);

        // customer address on the left merchant address on the right
        JPanel addrRow = new JPanel(new BorderLayout());
        addrRow.setOpaque(false);
        addrRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JLabel customerAddr = new JLabel(
            "<html>" + username + ",<br>"
            + "Delivery address on file<br>"
            + "— to be populated from account —"
            + "</html>");
        customerAddr.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        customerAddr.setForeground(new Color(255, 255, 255, 160));

        JLabel merchantAddr = new JLabel(
            "<html><div style='text-align:right'>"
            + "InfoPharma Ltd.,<br>"
            + "1 Pharma Way,<br>"
            + "London, EC1A 1BB<br>"
            + "Phone: 020 7000 0000"
            + "</div></html>",
            SwingConstants.RIGHT);
        merchantAddr.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        merchantAddr.setForeground(new Color(255, 255, 255, 160));

        addrRow.add(customerAddr, BorderLayout.WEST);
        addrRow.add(merchantAddr, BorderLayout.EAST);

        JLabel dateLbl = new JLabel(order.date, SwingConstants.RIGHT);
        dateLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLbl.setForeground(new Color(255, 255, 255, 110));
        dateLbl.setAlignmentX(LEFT_ALIGNMENT);
        dateLbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));

        JLabel greetLbl = new JLabel("Dear " + username + ",");
        greetLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        greetLbl.setForeground(new Color(255, 255, 255, 160));

        JLabel invoiceNoLbl = new JLabel("INVOICE NO.:  " + order.orderId, SwingConstants.CENTER);
        invoiceNoLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
        invoiceNoLbl.setForeground(Color.WHITE);
        invoiceNoLbl.setAlignmentX(LEFT_ALIGNMENT);
        invoiceNoLbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel acctLbl = new JLabel("Account No:  " + username.toUpperCase().replaceAll("\\s+", "") + "001");
        acctLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        acctLbl.setForeground(new Color(255, 255, 255, 120));

        JPanel table = buildItemsTable(order);

        JLabel thankLbl = new JLabel(
            "<html>Thank you for your valued custom. "
            + "We look forward to receiving your payment in due course.</html>");
        thankLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        thankLbl.setForeground(new Color(255, 255, 255, 120));
        thankLbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel sinsLbl = new JLabel("Yours sincerely,");
        sinsLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sinsLbl.setForeground(new Color(255, 255, 255, 80));

        JLabel sigLbl = new JLabel("InfoPharma Ltd.");
        sigLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 13));
        sigLbl.setForeground(NEON_LT);

        card.add(addrRow);
        card.add(Box.createVerticalStrut(10));
        card.add(dateLbl);
        card.add(Box.createVerticalStrut(14));
        card.add(greetLbl);
        card.add(Box.createVerticalStrut(12));
        card.add(invoiceNoLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(acctLbl);
        card.add(Box.createVerticalStrut(14));
        card.add(table);
        card.add(Box.createVerticalStrut(18));
        card.add(thankLbl);
        card.add(Box.createVerticalStrut(12));
        card.add(sinsLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(sigLbl);

        return card;
    }

    // builds the line items table with subtotal vat and total at the bottom
    private JPanel buildItemsTable(OrderManager.Order order) {
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setAlignmentX(LEFT_ALIGNMENT);

        Color headerBg = new Color(0x0a1018);
        Color borderCol = new Color(37, 99, 168, 70);

        String[] colNames = { "Item", "Qty", "Unit Price", "Amount" };
        int[]    colW     = { 5, 1, 1, 1 }; // relative weights via GridBagLayout

        JPanel header = buildTableRow(colNames, null, headerBg, true, borderCol);
        wrapper.add(header);

        double subtotal = 0;
        List<CartManager.CartItem> items = order.items;
        for (int i = 0; i < items.size(); i++) {
            CartManager.CartItem it = items.get(i);
            double lineTotal = it.unitPrice * it.qty;
            subtotal += lineTotal;
            String[] cells = {
                it.name,
                String.valueOf(it.qty),
                String.format("\u00a3%.2f", it.unitPrice),
                String.format("\u00a3%.2f", lineTotal)
            };
            // alternating row background for readabilty
            Color rowBg = i % 2 == 0 ? PANEL : new Color(0x0a1018);
            wrapper.add(buildTableRow(cells, null, rowBg, false, borderCol));
        }

        double vat   = Math.round(subtotal * VAT_RATE * 100.0) / 100.0;
        double total = subtotal + vat;

        wrapper.add(buildSummaryRow("Subtotal",
            String.format("\u00a3%.2f", subtotal), borderCol));
        wrapper.add(buildSummaryRow(
            String.format("VAT @ %.0f%%", VAT_RATE * 100),
            String.format("\u00a3%.2f", vat), borderCol));

        JPanel totalRow = buildSummaryRow("Amount Due",
            String.format("\u00a3%.2f", total), borderCol);
        totalRow.setBackground(new Color(0x0a1018));
        for (Component c : totalRow.getComponents()) {
            if (c instanceof JLabel) {
                ((JLabel) c).setFont(new Font("Segoe UI", Font.BOLD, 13));
                ((JLabel) c).setForeground(GREEN);
            }
        }
        wrapper.add(totalRow);

        return wrapper;
    }

    private JPanel buildTableRow(String[] cells, Color[] colors,
                                 Color bg, boolean isHeader, Color borderCol) {
        JPanel row = new JPanel(new GridLayout(1, cells.length, 0, 0));
        row.setBackground(bg);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        row.setAlignmentX(LEFT_ALIGNMENT);

        for (int i = 0; i < cells.length; i++) {
            JLabel lbl = new JLabel(cells[i],
                i == 0 ? SwingConstants.LEFT : SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI",
                isHeader ? Font.BOLD : Font.PLAIN, isHeader ? 9 : 12));
            lbl.setForeground(isHeader ? NEON
                : (colors != null ? colors[i] : new Color(255, 255, 255, 190)));
            lbl.setBackground(bg);
            lbl.setOpaque(true);
            lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, borderCol),
                BorderFactory.createEmptyBorder(0, isHeader ? 8 : 12, 0, 8)
            ));
            row.add(lbl);
        }
        return row;
    }

    // summary row used for subtotal vat and total at the bottom of the table
    private JPanel buildSummaryRow(String label, String value, Color borderCol) {
        JPanel row = new JPanel(new GridLayout(1, 4, 0, 0));
        row.setBackground(PANEL);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        row.setAlignmentX(LEFT_ALIGNMENT);

        // two blank cells take up the first columns to push the label and value to the right
        for (int i = 0; i < 2; i++) {
            JLabel blank = new JLabel();
            blank.setBackground(PANEL);
            blank.setOpaque(true);
            blank.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderCol));
            row.add(blank);
        }

        JLabel lbl = new JLabel(label, SwingConstants.LEFT);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(255, 255, 255, 160));
        lbl.setBackground(PANEL);
        lbl.setOpaque(true);
        lbl.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, borderCol),
            BorderFactory.createEmptyBorder(0, 12, 0, 8)
        ));

        JLabel val = new JLabel(value, SwingConstants.CENTER);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        val.setForeground(new Color(255, 255, 255, 190));
        val.setBackground(PANEL);
        val.setOpaque(true);
        val.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, borderCol),
            BorderFactory.createEmptyBorder(0, 8, 0, 8)
        ));

        row.add(lbl);
        row.add(val);
        return row;
    }

    // small circle avatar for the nav using the users initials
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

    // grabs first letters of the name for the avatar initials
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
