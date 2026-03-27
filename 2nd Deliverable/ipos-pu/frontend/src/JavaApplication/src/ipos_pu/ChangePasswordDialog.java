/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package ipos_pu;

import java.awt.*;
import javax.swing.*;

/**
 *
 * @author nuhur
 */
public class ChangePasswordDialog extends javax.swing.JDialog {

    // colour palette
    private static final Color BG      = new Color(0x05080f);
    private static final Color NEON    = new Color(0x2563A8);
    private static final Color NEON_LT = new Color(0x7eb8f7);

    private final String username;
    private JPasswordField newPassField;
    private JPasswordField confirmPassField;
    private JLabel errorLabel;

    public ChangePasswordDialog(java.awt.Frame parent, boolean modal, String username) {
        super(parent, modal);
        this.username = username;
        buildUI();
        setLocationRelativeTo(parent);
    }

    // sets up the dialog layout and form fields
    private void buildUI() {
        setTitle("Set New Password");
        setSize(460, 480);
        setResizable(false);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        // background with a subtle scanline effect and radial glow in the centre
        JPanel bg = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setColor(BG);
                g2.fillRect(0, 0, w, h);
                g2.setColor(new Color(37, 99, 168, 5));
                for (int y = 0; y < h; y += 4) g2.drawLine(0, y, w, y);
                g2.setPaint(new RadialGradientPaint(w / 2f, h / 2f, Math.max(w, h) / 2f,
                    new float[]{0f, 1f}, new Color[]{new Color(37, 99, 168, 40), new Color(0, 0, 0, 0)}));
                g2.fillRect(0, 0, w, h);
                g2.dispose();
            }
        };
        bg.setLayout(new GridBagLayout());
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(bg, BorderLayout.CENTER);

        // form content centred in the dialog
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setPreferredSize(new Dimension(320, 400));
        bg.add(content);

        JLabel title = new JLabel("Set New Password");
        title.setFont(new Font("Trebuchet MS", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Your first login — please choose a new password");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(255, 255, 255, 75));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel newPassLbl = new JLabel("NEW PASSWORD");
        newPassLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        newPassLbl.setForeground(NEON);
        newPassLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel confirmLbl = new JLabel("CONFIRM PASSWORD");
        confirmLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        confirmLbl.setForeground(NEON);
        confirmLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        newPassField    = new JPasswordField();
        confirmPassField = new JPasswordField();
        styleField(newPassField);
        styleField(confirmPassField);

        errorLabel = new JLabel();
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(255, 90, 90));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setVisible(false);

        JButton confirmBtn = new JButton("CONFIRM");
        styleButton(confirmBtn);
        confirmBtn.addActionListener(e -> handleConfirm());

        content.add(Box.createVerticalGlue());
        content.add(title);
        content.add(Box.createVerticalStrut(6));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(36));
        content.add(newPassLbl);
        content.add(Box.createVerticalStrut(6));
        content.add(newPassField);
        content.add(Box.createVerticalStrut(20));
        content.add(confirmLbl);
        content.add(Box.createVerticalStrut(6));
        content.add(confirmPassField);
        content.add(Box.createVerticalStrut(10));
        content.add(errorLabel);
        content.add(Box.createVerticalStrut(16));
        content.add(confirmBtn);
        content.add(Box.createVerticalGlue());
    }

    // validates the two fields and opens the home page if everything checks out
    private void handleConfirm() {
        String newPass     = new String(newPassField.getPassword()).trim();
        String confirmPass = new String(confirmPassField.getPassword()).trim();

        if (newPass.isEmpty()) {
            showError("Please enter a new password.");
            return;
        }
        // cant keep the temp password that was used to first log in
        if (newPass.equals("test")) {
            showError("You cannot keep the temporary password.");
            return;
        }
        if (newPass.length() < 6) {
            showError("Password must be at least 6 characters.");
            return;
        }
        if (!newPass.equals(confirmPass)) {
            showError("Passwords do not match.");
            return;
        }

        java.awt.Window owner = getOwner();
        dispose();
        if (owner != null) owner.dispose();
        new HomePage(username).setVisible(true);
    }

    // shows the red error label with the given message
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    // styles a password field with a focus highlight on the bottom border
    private void styleField(JPasswordField field) {
        field.setBackground(new Color(8, 16, 30));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(320, 42));
        field.setMaximumSize(new Dimension(320, 42));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);

        javax.swing.border.Border normal = BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(37, 99, 168, 100)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        );
        javax.swing.border.Border focused = BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, NEON_LT),
            BorderFactory.createEmptyBorder(8, 12, 7, 12)
        );
        field.setBorder(normal);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) { field.setBorder(focused); }
            @Override public void focusLost(java.awt.event.FocusEvent e)   { field.setBorder(normal); }
        });
    }

    // custom painted button with a glow effect on hover
    private void styleButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(320, 50));
        btn.setMaximumSize(new Dimension(320, 50));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setRolloverEnabled(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = 8, w = c.getWidth(), h = c.getHeight();
                boolean hover   = btn.getModel().isRollover();
                boolean pressed = btn.getModel().isPressed();
                g2.setColor(pressed ? new Color(0x1a4f8a) : new Color(0x2563A8));
                g2.fillRoundRect(0, 0, w, h, arc, arc);
                g2.setColor(new Color(255, 255, 255, pressed ? 8 : 22));
                g2.fillRoundRect(0, 0, w, h / 2, arc, arc);
                int layers = hover || pressed ? 6 : 3;
                for (int i = 1; i <= layers; i++) {
                    g2.setColor(new Color(37, 99, 168, Math.max(0, (hover || pressed ? 55 : 30) - i * 8)));
                    g2.setStroke(new BasicStroke(i));
                    g2.drawRoundRect(-i, -i, w + i * 2 - 1, h + i * 2 - 1, arc + i, arc + i);
                }
                g2.setColor(Color.WHITE);
                g2.setFont(c.getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(btn.getText(),
                    (w - fm.stringWidth(btn.getText())) / 2,
                    (h + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        });
    }
}
