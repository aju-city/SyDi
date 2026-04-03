/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ipos_pu;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 *
 * @author nuhur
 */
public class ManageAccountPage extends javax.swing.JFrame {

    // colour palette
    private static final Color BG      = new Color(0x05080f);
    private static final Color PANEL   = new Color(0x080e1a);
    private static final Color NEON    = new Color(0x2563A8);
    private static final Color NEON_LT = new Color(0x7eb8f7);

    private final String username;

    public ManageAccountPage(String username) {
        this.username = username;
        initComponents();
        buildUI();
    }

    private void buildUI() {
        setTitle("IPOS-PU \u2014 Manage Account");
        setSize(1280, 760);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setBackground(BG);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(buildNav(),     BorderLayout.NORTH);
        getContentPane().add(buildContent(), BorderLayout.CENTER);
    }

    // nav bar with brand back button and avatar
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

        JButton backBtn = new JButton("Back to Home");
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

    // page heading and the profile card below it
    private JPanel buildContent() {
        JPanel outer = new JPanel();
        outer.setBackground(BG);
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBorder(BorderFactory.createEmptyBorder(28, 32, 32, 32));

        JLabel subLbl = new JLabel("CUSTOMER SERVICES");
        subLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        subLbl.setForeground(NEON);
        subLbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel titleLbl = new JLabel("Manage Account");
        titleLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 22));
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel descLbl = new JLabel("View your account details and manage your settings.");
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLbl.setForeground(new Color(255, 255, 255, 80));
        descLbl.setAlignmentX(LEFT_ALIGNMENT);

        outer.add(subLbl);
        outer.add(Box.createVerticalStrut(4));
        outer.add(titleLbl);
        outer.add(Box.createVerticalStrut(6));
        outer.add(descLbl);
        outer.add(Box.createVerticalStrut(32));
        outer.add(buildProfileCard());
        outer.add(Box.createVerticalGlue());

        return outer;
    }

    // the profile card with avatar username details and action buttons
    private JPanel buildProfileCard() {
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
        card.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));
        card.setAlignmentX(LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(540, Integer.MAX_VALUE));

        JPanel avatarRow = new JPanel();
        avatarRow.setOpaque(false);
        avatarRow.setLayout(new BoxLayout(avatarRow, BoxLayout.X_AXIS));
        avatarRow.setAlignmentX(LEFT_ALIGNMENT);

        // big avatar circle drawn with the users initials
        JPanel bigAvatar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(NEON);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
                FontMetrics fm = g2.getFontMetrics();
                String initials = getInitials(username);
                g2.drawString(initials,
                    (getWidth()  - fm.stringWidth(initials)) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        bigAvatar.setPreferredSize(new Dimension(60, 60));
        bigAvatar.setMaximumSize(new Dimension(60, 60));
        bigAvatar.setOpaque(false);

        // username and account type label next to the avatar
        JPanel nameBlock = new JPanel();
        nameBlock.setOpaque(false);
        nameBlock.setLayout(new BoxLayout(nameBlock, BoxLayout.Y_AXIS));
        nameBlock.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));

        JLabel nameLbl = new JLabel(username != null && !username.isEmpty() ? username : "Guest");
        nameLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
        nameLbl.setForeground(Color.WHITE);

        JLabel typeLbl = new JLabel("Non-Commercial Account");
        typeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        typeLbl.setForeground(new Color(255, 255, 255, 80));

        nameBlock.add(nameLbl);
        nameBlock.add(Box.createVerticalStrut(4));
        nameBlock.add(typeLbl);

        avatarRow.add(bigAvatar);
        avatarRow.add(nameBlock);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(37, 99, 168, 50));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        JPanel detailsPanel = new JPanel();
        detailsPanel.setOpaque(false);
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setAlignmentX(LEFT_ALIGNMENT);

        // email and username are the same thing in this system since email is used to register
        detailsPanel.add(buildDetailRow("Username", username != null ? username : "—"));
        detailsPanel.add(Box.createVerticalStrut(12));
        detailsPanel.add(buildDetailRow("Email", username != null ? username : "—"));

        JButton changePwdBtn = new JButton("Change Password");
        changePwdBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        changePwdBtn.setForeground(Color.WHITE);
        changePwdBtn.setBackground(NEON);
        changePwdBtn.setBorder(BorderFactory.createEmptyBorder(9, 20, 9, 20));
        changePwdBtn.setFocusPainted(false);
        changePwdBtn.setOpaque(true);
        changePwdBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        changePwdBtn.setAlignmentX(LEFT_ALIGNMENT);
        changePwdBtn.addActionListener(e -> {
            ChangePasswordDialog cpd = new ChangePasswordDialog(this, true, username);
            cpd.setVisible(true);
        });

        JButton signOutBtn = new JButton("Sign Out");
        signOutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        signOutBtn.setForeground(new Color(255, 255, 255, 160));
        signOutBtn.setBackground(new Color(0x0b1220));
        signOutBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(37, 99, 168, 80), 1),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        signOutBtn.setFocusPainted(false);
        signOutBtn.setOpaque(true);
        signOutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signOutBtn.setAlignmentX(LEFT_ALIGNMENT);
        signOutBtn.addActionListener(e -> {
            CartManager.clear();
            OrderManager.clear();
            dispose();
            new LandingPage().setVisible(true);
        });

        JPanel btnRow = new JPanel();
        btnRow.setOpaque(false);
        btnRow.setLayout(new BoxLayout(btnRow, BoxLayout.X_AXIS));
        btnRow.setAlignmentX(LEFT_ALIGNMENT);
        btnRow.add(changePwdBtn);
        btnRow.add(Box.createHorizontalStrut(12));
        btnRow.add(signOutBtn);

        card.add(avatarRow);
        card.add(Box.createVerticalStrut(22));
        card.add(sep);
        card.add(Box.createVerticalStrut(20));
        card.add(detailsPanel);
        card.add(Box.createVerticalStrut(28));
        card.add(btnRow);

        return card;
    }

    // small label and value stacked vertically used for the account details
    private JPanel buildDetailRow(String label, String value) {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbl.setForeground(NEON);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        val.setForeground(new Color(255, 255, 255, 190));

        row.add(lbl);
        row.add(Box.createVerticalStrut(3));
        row.add(val);
        return row;
    }

    // small circle avatar shown in the top right of the nav
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

    // grabs first letter of first and last name or first two chars if single word
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
