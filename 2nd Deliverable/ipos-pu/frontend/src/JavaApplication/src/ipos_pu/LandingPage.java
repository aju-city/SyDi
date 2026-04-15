/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ipos_pu;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
/**
 *
 * @author nuhur
 */
public class LandingPage extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(LandingPage.class.getName());
    private static final Color NAV_DARK    = new Color(0x0F1E33);
    private static final Color NAV_MID     = new Color(0x1A3557);
    private static final Color NAV_ACCENT  = new Color(0x2563A8);
    private static final Color WHITE       = Color.WHITE;
    private static final Color WHITE_DIM   = new Color(255, 255, 255, 180);
    private static final Color WHITE_FAINT = new Color(255, 255, 255, 60);

    /**
     * Creates new form LandingPage
     */
    public LandingPage() {
        initComponents();
        styleComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }
private void styleComponents() {
    final Color BG      = new Color(0x05080f);
    final Color NEON    = new Color(0x2563A8);
    final Color NEON_LT = new Color(0x7eb8f7);

    mainPanel = new JPanel() {
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
                new float[]{0f, 1f}, new Color[]{new Color(37, 99, 168, 28), new Color(0, 0, 0, 0)}));
            g2.fillRect(0, 0, w, h);

            int m = 40, s = 90;
            g2.setPaint(new Color(37, 99, 168, 110));
            g2.setStroke(new BasicStroke(1f));
            g2.drawLine(m, m, m + s, m);         g2.drawLine(m, m, m, m + s);
            g2.drawLine(w-m-s, m, w-m, m);       g2.drawLine(w-m, m, w-m, m+s);
            g2.drawLine(m, h-m, m+s, h-m);       g2.drawLine(m, h-m-s, m, h-m);
            g2.drawLine(w-m-s, h-m, w-m, h-m);   g2.drawLine(w-m, h-m-s, w-m, h-m);

            g2.dispose();
        }
    };
    mainPanel.setLayout(new GridBagLayout());
    getContentPane().setLayout(new BorderLayout());
    getContentPane().removeAll();
    getContentPane().add(mainPanel, BorderLayout.CENTER);

    contentPanel.setOpaque(false);
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    contentPanel.setPreferredSize(new Dimension(800, 720));
    contentPanel.removeAll();
    mainPanel.add(contentPanel);

    JPanel accent = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int cx = getWidth() / 2, cy = getHeight() / 2;
            for (int x : new int[]{-22, 0, 22}) {
                g2.setColor(new Color(37, 99, 168, 70));
                g2.fillOval(cx + x - 8, cy - 8, 16, 16);
                g2.setColor(NEON_LT);
                g2.fillOval(cx + x - 5, cy - 5, 10, 10);
            }
            g2.dispose();
        }
    };
    accent.setOpaque(false);
    accent.setPreferredSize(new Dimension(100, 36));
    accent.setMaximumSize(new Dimension(100, 36));
    accent.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel eyebrow = new JLabel("IPOS · PU SYSTEM");
    eyebrow.setFont(new Font("Segoe UI", Font.BOLD, 18));
    eyebrow.setForeground(NEON);
    eyebrow.setAlignmentX(Component.CENTER_ALIGNMENT);

    titleLabel.setText("SyDi Online");
    titleLabel.setFont(new Font("Trebuchet MS", Font.BOLD, 110));
    titleLabel.setForeground(WHITE);
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel titleLine2 = new JLabel("Platform") {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            g2.setPaint(new GradientPaint(0, 0, NEON_LT, fm.stringWidth("Platform"), 0, NEON));
            g2.drawString("Platform", 0, fm.getAscent());
            g2.dispose();
        }
    };
    titleLine2.setFont(new Font("Trebuchet MS", Font.BOLD, 110));
    titleLine2.setForeground(NEON_LT);
    titleLine2.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel tagline = new JLabel("Secure ordering for registered businesses and individuals.");
    tagline.setFont(new Font("Segoe UI", Font.PLAIN, 22));
    tagline.setForeground(new Color(255, 255, 255, 90));
    tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

    loginButton.setText("SIGN IN");
    registerButton.setText("CREATE ACCOUNT");
    styleButton(loginButton, true);
    styleButton(registerButton, false);

    JButton guestButton = new JButton("CONTINUE AS GUEST");
    styleButton(guestButton, false);
    guestButton.addActionListener(e -> {
        try {
            String token = CartManager.createGuestCartOnBackend();
            CartManager.guestToken = token;

            dispose();
            new HomePage("Guest").setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Unable to start guest session.\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    });

    contentPanel.add(Box.createVerticalGlue());
    contentPanel.add(accent);
    contentPanel.add(Box.createVerticalStrut(26));
    contentPanel.add(eyebrow);
    contentPanel.add(Box.createVerticalStrut(22));
    contentPanel.add(titleLabel);
    contentPanel.add(Box.createVerticalStrut(4));
    contentPanel.add(titleLine2);
    contentPanel.add(Box.createVerticalStrut(28));
    contentPanel.add(tagline);
    contentPanel.add(Box.createVerticalStrut(80));
    contentPanel.add(loginButton);
    contentPanel.add(Box.createVerticalStrut(22));
    contentPanel.add(registerButton);
    contentPanel.add(Box.createVerticalStrut(22));
    contentPanel.add(guestButton);
    contentPanel.add(Box.createVerticalGlue());

    getContentPane().revalidate();
    getContentPane().repaint();
}

    private void styleButton(JButton btn, boolean primary) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setPreferredSize(new Dimension(520, 66));
        btn.setMaximumSize(new Dimension(520, 66));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setRolloverEnabled(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, javax.swing.JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = 8, w = c.getWidth(), h = c.getHeight();
                boolean hover   = btn.getModel().isRollover();
                boolean pressed = btn.getModel().isPressed();

                if (primary) {
                    g2.setColor(pressed ? new Color(0x1a4f8a) : new Color(0x2563A8));
                    g2.fillRoundRect(0, 0, w, h, arc, arc);
                    g2.setColor(new Color(255, 255, 255, pressed ? 8 : 22));
                    g2.fillRoundRect(0, 0, w, h / 2, arc, arc);
                    int layers = hover || pressed ? 6 : 3;
                    for (int i = 1; i <= layers; i++) {
                        g2.setColor(new Color(37, 99, 168, Math.max(0, (hover||pressed ? 55:30) - i*8)));
                        g2.setStroke(new BasicStroke(i));
                        g2.drawRoundRect(-i, -i, w+i*2-1, h+i*2-1, arc+i, arc+i);
                    }
                    g2.setColor(Color.WHITE);
                } else {
                    if (hover || pressed) {
                        g2.setColor(new Color(37, 99, 168, pressed ? 55 : 28));
                        g2.fillRoundRect(0, 0, w, h, arc, arc);
                    }
                    g2.setColor(new Color(37, 99, 168, hover || pressed ? 255 : 160));
                    g2.setStroke(new BasicStroke(hover ? 1.8f : 1.2f));
                    g2.drawRoundRect(1, 1, w-2, h-2, arc, arc);
                    if (hover || pressed) {
                        for (int i = 1; i <= 4; i++) {
                            g2.setColor(new Color(37, 99, 168, Math.max(0, 45 - i*10)));
                            g2.setStroke(new BasicStroke(i));
                            g2.drawRoundRect(-i, -i, w+i*2-1, h+i*2-1, arc+i, arc+i);
                        }
                    }
                    g2.setColor(hover ? Color.WHITE : new Color(0x7eb8f7));
                }

                FontMetrics fm = g2.getFontMetrics(c.getFont());
                g2.setFont(c.getFont());
                int tx = (w - fm.stringWidth(btn.getText())) / 2;
                int ty = (h + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(btn.getText(), tx, ty);
                g2.dispose();
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        contentPanel = new javax.swing.JPanel();
        registerButton = new javax.swing.JButton();
        loginButton = new javax.swing.JButton();
        titleLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("IPOS-PU");

        contentPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        contentPanel.setPreferredSize(new java.awt.Dimension(700, 500));

        registerButton.setText("Create Account");
        registerButton.addActionListener(this::registerButtonActionPerformed);

        loginButton.setText("Sign In");
        loginButton.addActionListener(this::loginButtonActionPerformed);

        titleLabel.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        titleLabel.setText("SyDi Online Platform");

        javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addGap(0, 117, Short.MAX_VALUE)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(loginButton, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(registerButton, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(194, 194, 194))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                        .addComponent(titleLabel)
                        .addGap(109, 109, 109))))
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                .addComponent(titleLabel)
                .addGap(125, 125, 125)
                .addComponent(loginButton, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(71, 71, 71)
                .addComponent(registerButton, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(100, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(contentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(contentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginButtonActionPerformed
        LoginDialog login = new LoginDialog(this, true);
        login.setVisible(true);
    }//GEN-LAST:event_loginButtonActionPerformed

    private void registerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registerButtonActionPerformed
        // TODO add your handling code here:
        RegisterDialog register = new RegisterDialog(this, true);
        register.setVisible(true);
    }//GEN-LAST:event_registerButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new LandingPage().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel contentPanel;
    private javax.swing.JButton loginButton;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton registerButton;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables
}
