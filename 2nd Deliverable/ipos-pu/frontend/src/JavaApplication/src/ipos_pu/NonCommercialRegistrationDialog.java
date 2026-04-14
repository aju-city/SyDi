/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package ipos_pu;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.swing.*;

/**
 *
 * @author nuhur
 */
public class NonCommercialRegistrationDialog extends javax.swing.JDialog {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(NonCommercialRegistrationDialog.class.getName());
    private JLabel errorLabel;

    /**
     * Creates new form NonCommercialRegistrationDialog
     */
    public NonCommercialRegistrationDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        styleComponents();
        setLocationRelativeTo(parent);
    }

    private void styleComponents() {
        final Color BG      = new Color(0x05080f);
        final Color NEON    = new Color(0x2563A8);
        final Color NEON_LT = new Color(0x7eb8f7);

        setSize(460, 420);
        setResizable(false);

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

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setPreferredSize(new Dimension(320, 340));
        bg.add(content);

        // Title
        nonCommercialText.setText("Non-Commercial Application");
        nonCommercialText.setFont(new Font("Trebuchet MS", Font.BOLD, 22));
        nonCommercialText.setForeground(Color.WHITE);
        nonCommercialText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Fill in your details to register");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(255, 255, 255, 75));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Email field label
        emailText.setText("EMAIL");
        emailText.setFont(new Font("Segoe UI", Font.BOLD, 10));
        emailText.setForeground(NEON);
        emailText.setAlignmentX(Component.CENTER_ALIGNMENT);

        styleField(emailField, NEON_LT);

        registerButton.setText("REGISTER");
        styleButton(registerButton);

        errorLabel = new JLabel();
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(255, 90, 90));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setVisible(false);

        content.add(Box.createVerticalGlue());
        content.add(nonCommercialText);
        content.add(Box.createVerticalStrut(6));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(40));
        content.add(emailText);
        content.add(Box.createVerticalStrut(6));
        content.add(emailField);
        content.add(Box.createVerticalStrut(10));
        content.add(errorLabel);
        content.add(Box.createVerticalStrut(20));
        content.add(registerButton);
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

        nonCommercialText = new javax.swing.JLabel();
        emailText = new javax.swing.JLabel();
        registerButton = new javax.swing.JButton();
        emailField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        nonCommercialText.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        nonCommercialText.setText("Non-Commercial Application Form");

        emailText.setText("Email:");

        registerButton.setText("Register");
        registerButton.addActionListener(this::registerButtonActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(emailText, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(56, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(nonCommercialText)
                        .addGap(98, 98, 98))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(registerButton, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(159, 159, 159))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(nonCommercialText)
                .addGap(71, 71, 71)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(emailText)
                    .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addComponent(registerButton, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void registerButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            showError("Please enter your email address.");
            return;
        }

        try {
            URL url = new URL("http://localhost:8080/api/non-commercial/register");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = "{ \"email\": \"" + email + "\" }";
            conn.getOutputStream().write(json.getBytes("UTF-8"));

            // Java 8 compatible response reader
            InputStream is = conn.getInputStream();
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;

            while ((length = is.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }

            String response = result.toString("UTF-8");

            JOptionPane.showMessageDialog(this, response);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not connect to server.");
        }
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

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                NonCommercialRegistrationDialog dialog = new NonCommercialRegistrationDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JTextField emailField;
    private javax.swing.JLabel emailText;
    private javax.swing.JLabel nonCommercialText;
    private javax.swing.JButton registerButton;
    // End of variables declaration//GEN-END:variables
}
