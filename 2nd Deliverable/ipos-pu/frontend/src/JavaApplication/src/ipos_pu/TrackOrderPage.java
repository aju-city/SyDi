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
public class TrackOrderPage extends javax.swing.JFrame {

    // colour palette
    private static final Color BG      = new Color(0x05080f);
    private static final Color PANEL   = new Color(0x080e1a);
    private static final Color NEON    = new Color(0x2563A8);
    private static final Color NEON_LT = new Color(0x7eb8f7);
    private static final Color GREEN   = new Color(0x4ade80);

    private final String username;

    public TrackOrderPage(String username) {
        this.username = username;
        initComponents();
        buildUI();
    }

    private void buildUI() {
        setTitle("IPOS-PU — Track Order");
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
                dispose();
                new HomePage(username).setVisible(true);
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

    // small avatar circle drawn in the nav
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

    // loads all orders and shows a tracking card for each one
    private JScrollPane buildContent() {
        JPanel outer = new JPanel();
        outer.setBackground(BG);
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBorder(BorderFactory.createEmptyBorder(28, 32, 32, 32));

        JLabel subLbl = new JLabel("CUSTOMER SERVICES");
        subLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        subLbl.setForeground(NEON);
        subLbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel titleLbl = new JLabel("Track Your Orders");
        titleLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 22));
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel descLbl = new JLabel("Live order status \u2014 received, dispatched and delivered.");
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

        // empty state if no orders have been placed yet
        if (orders.isEmpty()) {
            JPanel emptyPanel = new JPanel(new GridBagLayout());
            emptyPanel.setBackground(PANEL);
            emptyPanel.setPreferredSize(new Dimension(0, 130));
            emptyPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
            emptyPanel.setAlignmentX(LEFT_ALIGNMENT);

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
            emptyPanel.add(stack);
            outer.add(emptyPanel);
        } else {
            for (OrderManager.Order order : orders) {
                outer.add(buildOrderCard(order));
                outer.add(Box.createVerticalStrut(14));
            }
        }

        outer.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(outer);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    // one card per order with order details on the left and the step tracker on the right
    private JPanel buildOrderCard(OrderManager.Order order) {
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
        card.setLayout(new BorderLayout(0, 0));
        card.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        card.setAlignmentX(LEFT_ALIGNMENT);

        // order info stacked on the left side of the card
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel orderIdLbl = new JLabel(order.orderId);
        orderIdLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
        orderIdLbl.setForeground(Color.WHITE);

        JLabel dateLbl = new JLabel("Placed: " + order.date);
        dateLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dateLbl.setForeground(new Color(255, 255, 255, 70));

        JLabel itemsLbl = new JLabel("<html><div style='width:420px'>" + order.itemsSummary + "</div></html>");
        itemsLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        itemsLbl.setForeground(new Color(255, 255, 255, 140));

        JLabel totalLbl = new JLabel("Total: " + order.total);
        totalLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        totalLbl.setForeground(NEON_LT);

        info.add(orderIdLbl);
        info.add(Box.createVerticalStrut(3));
        info.add(dateLbl);
        info.add(Box.createVerticalStrut(8));
        info.add(itemsLbl);
        info.add(Box.createVerticalStrut(6));
        info.add(totalLbl);

        card.add(info,                      BorderLayout.CENTER);
        card.add(buildStepTracker(order.stage), BorderLayout.EAST);
        return card;
    }

    // draws the three step tracker showing which stage the order is at
    private JPanel buildStepTracker(int activeStage) {
        String[] labels = { "Received", "Dispatched", "Delivered" };

        JPanel panel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w      = getWidth();
                int h      = getHeight();
                int dotR   = 9;
                int dotY   = h / 2 - 14;
                int margin = 30;
                int step   = (w - margin * 2) / (labels.length - 1);

                // connecting lines between the dots green if that stage is done
                for (int i = 0; i < labels.length - 1; i++) {
                    int x1   = margin + i * step + dotR;
                    int x2   = margin + (i + 1) * step - dotR;
                    boolean done = activeStage > i;
                    g2.setStroke(new BasicStroke(2f));
                    g2.setColor(done ? GREEN : new Color(37, 99, 168, 60));
                    g2.drawLine(x1, dotY, x2, dotY);
                }

                // each dot is either done active or upcoming with different styles
                for (int i = 0; i < labels.length; i++) {
                    int cx     = margin + i * step;
                    boolean done   = activeStage > i;
                    boolean active = activeStage == i;

                    if (done) {
                        g2.setColor(GREEN);
                        g2.fillOval(cx - dotR, dotY - dotR, dotR * 2, dotR * 2);
                    } else if (active) {
                        g2.setColor(NEON);
                        g2.fillOval(cx - dotR, dotY - dotR, dotR * 2, dotR * 2);
                        g2.setColor(new Color(37, 99, 168, 90));
                        g2.setStroke(new BasicStroke(2f));
                        g2.drawOval(cx - dotR - 3, dotY - dotR - 3, (dotR + 3) * 2, (dotR + 3) * 2);
                    } else {
                        g2.setColor(new Color(37, 99, 168, 35));
                        g2.fillOval(cx - dotR, dotY - dotR, dotR * 2, dotR * 2);
                        g2.setColor(new Color(37, 99, 168, 70));
                        g2.setStroke(new BasicStroke(1f));
                        g2.drawOval(cx - dotR, dotY - dotR, dotR * 2, dotR * 2);
                    }

                    g2.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 10));
                    Color labelColor = done ? GREEN : (active ? NEON_LT : new Color(255, 255, 255, 60));
                    g2.setColor(labelColor);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(labels[i], cx - fm.stringWidth(labels[i]) / 2, dotY + dotR + 16);
                }

                g2.dispose();
            }
        };

        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(300, 80));
        return panel;
    }

    // grabs initials from the username for the avatar
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
