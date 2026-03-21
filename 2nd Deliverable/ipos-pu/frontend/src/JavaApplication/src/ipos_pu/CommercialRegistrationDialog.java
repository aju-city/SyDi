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
public class CommercialRegistrationDialog extends javax.swing.JDialog {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(CommercialRegistrationDialog.class.getName());
    private JLabel errorLabel;

    /**
     * Creates new form CommercialRegistrationDialog
     */
    public CommercialRegistrationDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        directorsPanel.setLayout(new javax.swing.BoxLayout(directorsPanel, javax.swing.BoxLayout.Y_AXIS));
        directorsScrollPane.setVisible(false);
        styleComponents();
        setLocationRelativeTo(parent);
    }

    private void styleComponents() {
        final Color BG      = new Color(0x05080f);
        final Color NEON    = new Color(0x2563A8);
        final Color NEON_LT = new Color(0x7eb8f7);

        setSize(580, 720);
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
        bg.setLayout(new BorderLayout());
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(bg, BorderLayout.CENTER);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JScrollPane scroll = new JScrollPane(content);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        bg.add(scroll, BorderLayout.CENTER);

        // Title
        jLabel1.setText("Commercial Application");
        jLabel1.setFont(new Font("Trebuchet MS", Font.BOLD, 26));
        jLabel1.setForeground(Color.WHITE);
        jLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Fill in your company details to register");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(255, 255, 255, 75));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Field labels
        styleFieldLabel(jLabel2, "COMPANY NAME", NEON);
        styleFieldLabel(jLabel3, "COMPANIES HOUSE ID", NEON);
        styleFieldLabel(jLabel4, "TYPE OF BUSINESS", NEON);
        styleFieldLabel(jLabel5, "BUSINESS ADDRESS", NEON);
        styleFieldLabel(jLabel6, "EMAIL", NEON);

        // Text fields
        styleField(jTextField3, NEON_LT);
        styleField(jTextField2, NEON_LT);
        styleField(jTextField4, NEON_LT);
        styleField(jTextField6, NEON_LT);

        // Business address text area
        jTextArea1.setBackground(new Color(8, 16, 30));
        jTextArea1.setForeground(Color.WHITE);
        jTextArea1.setCaretColor(Color.WHITE);
        jTextArea1.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jScrollPane1.setOpaque(false);
        jScrollPane1.getViewport().setBackground(new Color(8, 16, 30));
        jScrollPane1.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(37, 99, 168, 100)));
        jScrollPane1.setPreferredSize(new Dimension(440, 80));
        jScrollPane1.setMaximumSize(new Dimension(440, 80));
        jScrollPane1.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Notification preference
        styleFieldLabel(jLabel9, "NOTIFICATION PREFERENCE", NEON);

        for (JCheckBox cb : new JCheckBox[]{jCheckBox1, jCheckBox2}) {
            cb.setOpaque(false);
            cb.setForeground(Color.WHITE);
            cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            cb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        JPanel notifRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        notifRow.setOpaque(false);
        notifRow.setMaximumSize(new Dimension(440, 36));
        notifRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        notifRow.add(jCheckBox1);
        notifRow.add(jCheckBox2);

        // Directors section header
        jLabel7.setText("COMPANY DIRECTORS");
        jLabel7.setFont(new Font("Segoe UI", Font.BOLD, 11));
        jLabel7.setForeground(new Color(126, 184, 247, 180));
        jLabel7.setAlignmentX(Component.CENTER_ALIGNMENT);

        styleFieldLabel(jLabel8, "NUMBER OF DIRECTORS", NEON);

        // Spinner
        directorSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField spinnerEditor = ((JSpinner.DefaultEditor) directorSpinner.getEditor()).getTextField();
        spinnerEditor.setBackground(new Color(8, 16, 30));
        spinnerEditor.setForeground(Color.WHITE);
        spinnerEditor.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel spinnerRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        spinnerRow.setOpaque(false);
        spinnerRow.setMaximumSize(new Dimension(380, 42));
        spinnerRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        spinnerRow.add(jLabel8);
        spinnerRow.add(directorSpinner);

        // Directors scroll pane
        directorsScrollPane.setOpaque(false);
        directorsScrollPane.getViewport().setBackground(new Color(8, 16, 30));
        directorsScrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(37, 99, 168, 60)));
        directorsScrollPane.setPreferredSize(new Dimension(440, 140));
        directorsScrollPane.setMaximumSize(new Dimension(440, 140));
        directorsScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        directorsPanel.setBackground(new Color(8, 16, 30));

        // Error + button
        errorLabel = new JLabel();
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(255, 90, 90));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setVisible(false);

        jButton1.setText("SUBMIT APPLICATION");
        styleButton(jButton1);

        // Assemble layout
        content.add(jLabel1);
        content.add(Box.createVerticalStrut(6));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(30));
        content.add(jLabel2);
        content.add(Box.createVerticalStrut(6));
        content.add(jTextField3);
        content.add(Box.createVerticalStrut(16));
        content.add(jLabel3);
        content.add(Box.createVerticalStrut(6));
        content.add(jTextField2);
        content.add(Box.createVerticalStrut(16));
        content.add(jLabel4);
        content.add(Box.createVerticalStrut(6));
        content.add(jTextField4);
        content.add(Box.createVerticalStrut(16));
        content.add(jLabel5);
        content.add(Box.createVerticalStrut(6));
        content.add(jScrollPane1);
        content.add(Box.createVerticalStrut(16));
        content.add(jLabel6);
        content.add(Box.createVerticalStrut(6));
        content.add(jTextField6);
        content.add(Box.createVerticalStrut(28));
        content.add(jLabel9);
        content.add(Box.createVerticalStrut(10));
        content.add(notifRow);
        content.add(Box.createVerticalStrut(30));
        content.add(jLabel7);
        content.add(Box.createVerticalStrut(16));
        content.add(spinnerRow);
        content.add(Box.createVerticalStrut(16));
        content.add(directorsScrollPane);
        content.add(Box.createVerticalStrut(20));
        content.add(errorLabel);
        content.add(Box.createVerticalStrut(10));
        content.add(jButton1);
        content.add(Box.createVerticalStrut(10));

        getContentPane().revalidate();
        getContentPane().repaint();
    }

    private void styleFieldLabel(JLabel label, String text, Color color) {
        label.setText(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 10));
        label.setForeground(color);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void styleField(JTextField field, Color focusColor) {
        field.setBackground(new Color(8, 16, 30));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(440, 42));
        field.setMaximumSize(new Dimension(440, 42));
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
        btn.setPreferredSize(new Dimension(440, 50));
        btn.setMaximumSize(new Dimension(440, 50));
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

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        directorSpinner = new javax.swing.JSpinner();
        directorsScrollPane = new javax.swing.JScrollPane();
        directorsPanel = new javax.swing.JPanel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jLabel9 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Commercial Application Form");

        jLabel2.setText("Company Name:");

        jLabel3.setText("Companies House ID:");

        jLabel4.setText("Type of Business:");

        jButton1.setText("Submit Application");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        jTextField2.addActionListener(this::jTextField2ActionPerformed);

        jTextField3.addActionListener(this::jTextField3ActionPerformed);

        jTextField4.addActionListener(this::jTextField4ActionPerformed);

        jLabel5.setText("Business Address:");

        jTextField6.addActionListener(this::jTextField6ActionPerformed);

        jLabel6.setText("Email:");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Company Directors");

        jLabel8.setText("Number of Directors:");

        directorSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 5, 1));
        directorSpinner.addChangeListener(this::directorSpinnerStateChanged);

        directorsScrollPane.setBackground(new java.awt.Color(255, 255, 255));

        directorsPanel.setLayout(new javax.swing.BoxLayout(directorsPanel, javax.swing.BoxLayout.Y_AXIS));
        directorsScrollPane.setViewportView(directorsPanel);

        jCheckBox1.setText("Email");

        jCheckBox2.setText("Mail");

        jLabel9.setText("Notifications:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(162, 162, 162)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(directorsScrollPane)
                            .addComponent(directorSpinner)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(24, 24, 24)
                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(87, 87, 87)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jCheckBox1)
                                        .addGap(39, 39, 39)
                                        .addComponent(jCheckBox2))
                                    .addComponent(jLabel7)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(139, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(230, 230, 230))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel1)
                .addGap(57, 57, 57)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox1)
                    .addComponent(jCheckBox2)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(directorSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(18, 18, 18)
                .addComponent(directorsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your han wling code here:
        String companyName = jTextField3.getText().trim();
        String companyId   = jTextField2.getText().trim();
        String email       = jTextField6.getText().trim();

        if (companyName.isEmpty() || companyId.isEmpty() || email.isEmpty()) {
            showError("Please complete all required fields.");
            return;
        }

        errorLabel.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jTextField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField4ActionPerformed

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField6ActionPerformed

    private void directorSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_directorSpinnerStateChanged
        // TODO add your handling code here:
        int directors = (int) directorSpinner.getValue();

        directorsPanel.removeAll();

        if (directors == 0) {
            directorsScrollPane.setVisible(false);
            directorsPanel.revalidate();
            directorsPanel.repaint();
            pack();
            return;
        }

        directorsScrollPane.setVisible(true);

        java.awt.Color darkBg    = new java.awt.Color(8, 16, 30);
        java.awt.Color neonBlue  = new java.awt.Color(0x2563A8);
        java.awt.Color neonLight = new java.awt.Color(0x7eb8f7);

        for (int i = 1; i <= directors; i++) {

            javax.swing.JLabel directorTitle = new javax.swing.JLabel("DIRECTOR " + i);
            directorTitle.setForeground(neonLight);
            directorTitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 10));
            directorTitle.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 6, 4, 0));
            directorsPanel.add(directorTitle);

            for (String[] pair : new String[][]{{"First Name", "15"}, {"Last Name", "15"}, {"Phone Number", "15"}}) {
                javax.swing.JLabel lbl = new javax.swing.JLabel(pair[0]);
                lbl.setForeground(java.awt.Color.WHITE);
                lbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));

                javax.swing.JTextField field = new javax.swing.JTextField(Integer.parseInt(pair[1]));
                field.setBackground(darkBg);
                field.setForeground(java.awt.Color.WHITE);
                field.setCaretColor(java.awt.Color.WHITE);
                field.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
                field.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(37, 99, 168, 100)),
                    javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 8)
                ));

                javax.swing.JPanel row = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 4));
                row.setBackground(darkBg);
                row.add(lbl);
                row.add(field);
                directorsPanel.add(row);
            }

            directorsPanel.add(javax.swing.Box.createVerticalStrut(8));
        }

        directorsPanel.revalidate();
        directorsPanel.repaint();
        pack();
        
    }//GEN-LAST:event_directorSpinnerStateChanged

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
                CommercialRegistrationDialog dialog = new CommercialRegistrationDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JSpinner directorSpinner;
    private javax.swing.JPanel directorsPanel;
    private javax.swing.JScrollPane directorsScrollPane;
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField6;
    // End of variables declaration//GEN-END:variables
}
