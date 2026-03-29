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
public class RegisterDialog extends javax.swing.JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(RegisterDialog.class.getName());

    /**
     * Creates new form RegisterDialog
     */
    public RegisterDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        styleComponents();
        setLocationRelativeTo(parent);
    }

    private void styleComponents() {
        final Color BG      = new Color(0x05080f);
        final Color NEON    = new Color(0x2563A8);
        final Color NEON_LT = new Color(0x7eb8f7);

        setSize(460, 380);
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
        content.setPreferredSize(new Dimension(320, 300));
        bg.add(content);

        registerText.setText("Create Account");
        registerText.setFont(new Font("Trebuchet MS", Font.BOLD, 30));
        registerText.setForeground(Color.WHITE);
        registerText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Choose your account type");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(255, 255, 255, 75));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        nonCommercialButton.setText("NON-COMMERCIAL ACCOUNT");
        commercialButton.setText("COMMERCIAL ACCOUNT");
        styleButton(nonCommercialButton, true);
        styleButton(commercialButton, false);

        content.add(Box.createVerticalGlue());
        content.add(registerText);
        content.add(Box.createVerticalStrut(8));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(40));
        content.add(nonCommercialButton);
        content.add(Box.createVerticalStrut(14));
        content.add(commercialButton);
        content.add(Box.createVerticalGlue());

        getContentPane().revalidate();
        getContentPane().repaint();
    }

    private void styleButton(JButton btn, boolean primary) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
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

                if (primary) {
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

                g2.setFont(c.getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(btn.getText(),
                    (w - fm.stringWidth(btn.getText())) / 2,
                    (h + fm.getAscent() - fm.getDescent()) / 2);
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

        registerText = new javax.swing.JLabel();
        nonCommercialButton = new javax.swing.JButton();
        commercialButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        registerText.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        registerText.setText("Register Account");

        nonCommercialButton.setText("Non-Commercial Account");
        nonCommercialButton.addActionListener(this::nonCommercialButtonActionPerformed);

        commercialButton.setText("Commercial Account");
        commercialButton.addActionListener(this::commercialButtonActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(104, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(registerText)
                        .addGap(100, 100, 100))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(commercialButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(nonCommercialButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(113, 113, 113))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(registerText)
                .addGap(58, 58, 58)
                .addComponent(nonCommercialButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(commercialButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(96, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void nonCommercialButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nonCommercialButtonActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);

        NonCommercialRegistrationDialog nonCommercial = new NonCommercialRegistrationDialog((java.awt.Frame) this.getParent(), true);
        nonCommercial.setLocationRelativeTo(this);
        nonCommercial.setVisible(true);

    this.setVisible(true);
    }//GEN-LAST:event_nonCommercialButtonActionPerformed

    private void commercialButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_commercialButtonActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);

        CommercialRegistrationDialog commercial = new CommercialRegistrationDialog((java.awt.Frame) this.getParent(), true);
        commercial.setLocationRelativeTo(this);
        commercial.setVisible(true);

        this.setVisible(true);
    }//GEN-LAST:event_commercialButtonActionPerformed

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
                RegisterDialog dialog = new RegisterDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton commercialButton;
    private javax.swing.JButton nonCommercialButton;
    private javax.swing.JLabel registerText;
    // End of variables declaration//GEN-END:variables
}
