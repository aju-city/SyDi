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
public class LoginDialog extends javax.swing.JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(LoginDialog.class.getName());
    private JLabel errorLabel;

    /**
     * Creates new form LoginDialog
     */
    public LoginDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        styleComponents();
        setLocationRelativeTo(parent);
    }

    private void styleComponents() {
        final Color BG      = new Color(0x05080f);
        final Color NEON    = new Color(0x2563A8);
        final Color NEON_LT = new Color(0x7eb8f7);

        setSize(460, 440);
        setResizable(false);

        //following landing page theme
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
                g2.setPaint(new RadialGradientPaint(w/2f, h/2f, Math.max(w,h)/2f,
                    new float[]{0f,1f}, new Color[]{new Color(37,99,168,40), new Color(0,0,0,0)}));
                g2.fillRect(0, 0, w, h);
                g2.dispose();
            }
        };
        bg.setLayout(new GridBagLayout());
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(bg, BorderLayout.CENTER);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setPreferredSize(new Dimension(320, 360));
        bg.add(content);

        // Title and subtitle
        loginText.setText("Welcome Back");
        loginText.setFont(new Font("Trebuchet MS", Font.BOLD, 30));
        loginText.setForeground(Color.WHITE);
        loginText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Sign in to continue");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(255, 255, 255, 75));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Field labels
        usernameText.setText("USERNAME");
        usernameText.setFont(new Font("Segoe UI", Font.BOLD, 10));
        usernameText.setForeground(NEON);
        usernameText.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordText.setText("PASSWORD");
        passwordText.setFont(new Font("Segoe UI", Font.BOLD, 10));
        passwordText.setForeground(NEON);
        passwordText.setAlignmentX(Component.CENTER_ALIGNMENT);


        styleField(usernameField, NEON_LT);
        styleField(passwordField, NEON_LT);
        signInButton.setText("SIGN IN");
        styleButton(signInButton);

        // Inline error label
        errorLabel = new JLabel();
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(255, 90, 90));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setVisible(false);

        content.add(Box.createVerticalGlue());
        content.add(loginText);
        content.add(Box.createVerticalStrut(6));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(36));
        content.add(usernameText);
        content.add(Box.createVerticalStrut(6));
        content.add(usernameField);
        content.add(Box.createVerticalStrut(20));
        content.add(passwordText);
        content.add(Box.createVerticalStrut(6));
        content.add(passwordField);
        content.add(Box.createVerticalStrut(10));
        content.add(errorLabel);
        content.add(Box.createVerticalStrut(16));
        content.add(signInButton);
        content.add(Box.createVerticalGlue());

        getContentPane().revalidate();
        getContentPane().repaint();
    }

    private void styleField(JTextField field, Color focusColor) {
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
            BorderFactory.createMatteBorder(0, 0, 2, 0, focusColor),
            BorderFactory.createEmptyBorder(8, 12, 7, 12)
        );
        field.setBorder(normal);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) { field.setBorder(focused); }
            @Override public void focusLost(java.awt.event.FocusEvent e)   { field.setBorder(normal); }
        });
    }

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
                g2.fillRoundRect(0, 0, w, h/2, arc, arc);
                int layers = hover || pressed ? 6 : 3;
                for (int i = 1; i <= layers; i++) {
                    g2.setColor(new Color(37, 99, 168, Math.max(0, (hover||pressed?55:30) - i*8)));
                    g2.setStroke(new BasicStroke(i));
                    g2.drawRoundRect(-i, -i, w+i*2-1, h+i*2-1, arc+i, arc+i);
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

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        usernameText = new javax.swing.JLabel();
        usernameField = new javax.swing.JTextField();
        passwordText = new javax.swing.JLabel();
        loginText = new javax.swing.JLabel();
        signInButton = new javax.swing.JButton();
        passwordField = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(350, 260));

        usernameText.setText("Username:");

        passwordText.setText("Password:");

        loginText.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        loginText.setText("Login");

        signInButton.setText("Sign In");
        signInButton.addActionListener(this::signInButtonActionPerformed);

        passwordField.addActionListener(this::passwordFieldActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(passwordText)
                    .addComponent(usernameText))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(usernameField)
                    .addComponent(passwordField, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(147, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(loginText)
                        .addGap(138, 138, 138))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(signInButton)
                        .addGap(132, 132, 132))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(loginText)
                .addGap(41, 41, 41)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(usernameText))
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(passwordText)
                    .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addComponent(signInButton)
                .addContainerGap(52, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void signInButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signInButtonActionPerformed
        // TODO add your handling code here:
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() && password.isEmpty()) {
            showError("Please enter your username and password.");
            return;
        }
        if (username.isEmpty()) {
            showError("Please enter your username.");
            return;
        }
        if (password.isEmpty()) {
            showError("Please enter your password.");
            return;
        }

        // Temporary success flow until database is connected
        errorLabel.setVisible(false);
        this.dispose();
        new HomePage(username).setVisible(true);
    }//GEN-LAST:event_signInButtonActionPerformed

    private void passwordFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passwordFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_passwordFieldActionPerformed

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

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginDialog dialog = new LoginDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel loginText;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel passwordText;
    private javax.swing.JButton signInButton;
    private javax.swing.JTextField usernameField;
    private javax.swing.JLabel usernameText;
    // End of variables declaration//GEN-END:variables
}
