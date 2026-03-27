/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ipos_pu;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

/**
 *
 * @author nuhur
 */
public class AdminPage extends javax.swing.JFrame {

    // colour palette
    private static final Color BG      = new Color(0x05080f);
    private static final Color PANEL   = new Color(0x080e1a);
    private static final Color NEON    = new Color(0x2563A8);
    private static final Color NEON_LT = new Color(0x7eb8f7);
    private static final Color GREEN   = new Color(0x4ade80);
    private static final Color RED     = new Color(0xf87171);

    private JLabel counterLbl;
    // each card registers a refresh function here so we can update all of them at once after a toggle
    private final List<Runnable> cardRefreshers = new ArrayList<>();

    public AdminPage() {
        initComponents();
        buildUI();
    }

    private void buildUI() {
        setTitle("IPOS-PU \u2014 Admin Panel");
        setSize(1280, 760);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setBackground(BG);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(buildNav(),     BorderLayout.NORTH);
        getContentPane().add(buildContent(), BorderLayout.CENTER);
    }

    // top nav with brand badge and sign out button
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

        JLabel adminBadge = new JLabel("ADMIN");
        adminBadge.setFont(new Font("Segoe UI", Font.BOLD, 9));
        adminBadge.setForeground(NEON_LT);
        adminBadge.setOpaque(true);
        adminBadge.setBackground(new Color(37, 99, 168, 40));
        adminBadge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(37, 99, 168, 100), 1),
            BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));

        JButton signOutBtn = new JButton("Sign Out");
        signOutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        signOutBtn.setForeground(new Color(255, 255, 255, 160));
        signOutBtn.setBackground(new Color(0x0b1220));
        signOutBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(37, 99, 168, 80), 1),
            BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        signOutBtn.setFocusPainted(false);
        signOutBtn.setOpaque(true);
        signOutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signOutBtn.addActionListener(e -> { dispose(); new LandingPage().setVisible(true); });

        nav.add(brand);
        nav.add(Box.createHorizontalStrut(10));
        nav.add(adminBadge);
        nav.add(Box.createHorizontalGlue());
        nav.add(signOutBtn);
        return nav;
    }

    // scrollable content with the counter and all the promo cards in rows of 3
    private JScrollPane buildContent() {
        JPanel outer = new JPanel();
        outer.setBackground(BG);
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBorder(BorderFactory.createEmptyBorder(28, 32, 32, 32));

        JLabel subLbl = new JLabel("ADMIN PANEL");
        subLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        subLbl.setForeground(NEON);
        subLbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel titleLbl = new JLabel("Promotions Management");
        titleLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 22));
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel descLbl = new JLabel(
            "Enabled promotions appear on the customer storefront. Maximum 3 active at once.");
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLbl.setForeground(new Color(255, 255, 255, 80));
        descLbl.setAlignmentX(LEFT_ALIGNMENT);

        counterLbl = new JLabel();
        counterLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        counterLbl.setAlignmentX(LEFT_ALIGNMENT);
        refreshCounter();

        outer.add(subLbl);
        outer.add(Box.createVerticalStrut(4));
        outer.add(titleLbl);
        outer.add(Box.createVerticalStrut(6));
        outer.add(descLbl);
        outer.add(Box.createVerticalStrut(10));
        outer.add(counterLbl);
        outer.add(Box.createVerticalStrut(24));

        // lay the promo cards out in rows of 3
        PromoManager.PromoConfig[] all = PromoManager.ALL_PROMOS;
        for (int rowStart = 0; rowStart < all.length; rowStart += 3) {
            JPanel row = new JPanel(new GridLayout(1, 3, 14, 0));
            row.setOpaque(false);
            row.setAlignmentX(LEFT_ALIGNMENT);
            for (int j = rowStart; j < rowStart + 3; j++) {
                if (j < all.length) {
                    row.add(buildAdminCard(all[j]));
                } else {
                    JPanel filler = new JPanel();
                    filler.setOpaque(false);
                    row.add(filler);
                }
            }
            outer.add(row);
            outer.add(Box.createVerticalStrut(14));
        }

        outer.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(outer);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    // builds one promo card with a toggle button and visual state based on whether its enabled
    private JPanel buildAdminCard(PromoManager.PromoConfig cfg) {
        final boolean[] on = { PromoManager.isEnabled(cfg.code) };

        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (on[0]) {
                    GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0x0d1b33),
                        getWidth(), getHeight(), new Color(0x080e1a));
                    g2.setPaint(gp);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                    RadialGradientPaint rg = new RadialGradientPaint(
                        new Point(getWidth(), 0), 90,
                        new float[]{0f, 1f},
                        new Color[]{new Color(37, 99, 168, 50), new Color(0, 0, 0, 0)});
                    g2.setPaint(rg);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(new Color(37, 99, 168, 76));
                } else {
                    g2.setColor(new Color(0x07090f));
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                    g2.setColor(new Color(255, 255, 255, 18));
                }
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth()-1f, getHeight()-1f, 12, 12));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 14, 18));

        JLabel tagLbl = new JLabel(cfg.tag);
        tagLbl.setFont(new Font("Segoe UI", Font.BOLD, 9));

        JLabel nameLbl = new JLabel(cfg.name);
        nameLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 14));

        JLabel descLbl = new JLabel(
            "<html><div style='width:160px'>" + cfg.desc + "</div></html>");
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        JLabel statusLbl = new JLabel(on[0] ? "ENABLED" : "DISABLED");
        statusLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));

        JButton toggleBtn = new JButton(on[0] ? "Disable" : "Enable");
        toggleBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        toggleBtn.setForeground(Color.WHITE);
        toggleBtn.setBorder(BorderFactory.createEmptyBorder(5, 14, 5, 14));
        toggleBtn.setFocusPainted(false);
        toggleBtn.setOpaque(true);
        toggleBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel ctrlRow = new JPanel();
        ctrlRow.setOpaque(false);
        ctrlRow.setLayout(new BoxLayout(ctrlRow, BoxLayout.X_AXIS));
        ctrlRow.setAlignmentX(LEFT_ALIGNMENT);
        ctrlRow.add(statusLbl);
        ctrlRow.add(Box.createHorizontalGlue());
        ctrlRow.add(toggleBtn);

        card.add(tagLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(nameLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(descLbl);
        card.add(Box.createVerticalGlue());
        card.add(Box.createVerticalStrut(10));
        card.add(sep);
        card.add(Box.createVerticalStrut(8));
        card.add(ctrlRow);

        // updates labels and buttons to match the current enabled state of this card
        Runnable refresh = () -> {
            on[0] = PromoManager.isEnabled(cfg.code);
            boolean maxHit = PromoManager.getEnabledCount() >= PromoManager.MAX_ENABLED;

            tagLbl.setForeground(on[0] ? NEON_LT   : new Color(255, 255, 255, 40));
            nameLbl.setForeground(on[0] ? Color.WHITE : new Color(255, 255, 255, 50));
            descLbl.setForeground(on[0] ? new Color(255, 255, 255, 115) : new Color(255, 255, 255, 30));
            sep.setForeground(on[0] ? new Color(37, 99, 168, 60) : new Color(255, 255, 255, 15));

            statusLbl.setText(on[0] ? "ENABLED" : "DISABLED");
            statusLbl.setForeground(on[0] ? GREEN : RED);

            if (on[0]) {
                toggleBtn.setText("Disable");
                toggleBtn.setBackground(new Color(0x7f1d1d));
                toggleBtn.setEnabled(true);
            } else {
                toggleBtn.setText(maxHit ? "Max reached" : "Enable");
                toggleBtn.setBackground(maxHit ? new Color(0x1a1f2e) : NEON);
                toggleBtn.setEnabled(!maxHit);
            }
            card.repaint();
        };
        // run once immediately to set the initial appearance
        refresh.run();
        cardRefreshers.add(refresh);

        // when toggled update the counter and refresh every card so max reached state is applied everywhere
        toggleBtn.addActionListener(e -> {
            boolean applied = PromoManager.setEnabled(cfg.code, !on[0]);
            if (applied) {
                refreshCounter();
                cardRefreshers.forEach(Runnable::run);
            }
        });

        return card;
    }

    // updates the counter label at the top showing how many promos are currently on
    private void refreshCounter() {
        if (counterLbl == null) return;
        int count = PromoManager.getEnabledCount();
        counterLbl.setText(count + " / " + PromoManager.MAX_ENABLED + " promotions active");
        counterLbl.setForeground(count == PromoManager.MAX_ENABLED ? GREEN : NEON_LT);
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
