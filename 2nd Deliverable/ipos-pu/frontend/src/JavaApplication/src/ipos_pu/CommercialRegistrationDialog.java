/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package ipos_pu;

import java.awt.*;

import java.util.List;
import java.util.ArrayList;

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
        commercialFormText.setText("Commercial Application");
        commercialFormText.setFont(new Font("Trebuchet MS", Font.BOLD, 26));
        commercialFormText.setForeground(Color.WHITE);
        commercialFormText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Fill in your company details to register");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(255, 255, 255, 75));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Field labels
        styleFieldLabel(companyNameText, "COMPANY NAME", NEON);
        styleFieldLabel(companyIDText, "COMPANIES HOUSE ID", NEON);
        styleFieldLabel(businessTypeText, "TYPE OF BUSINESS", NEON);
        styleFieldLabel(businessAddressText, "BUSINESS ADDRESS", NEON);
        styleFieldLabel(emailText, "EMAIL", NEON);

        // Text fields
        styleField(companyNameField, NEON_LT);
        styleField(companyIDField, NEON_LT);
        styleField(BusinessTypeField, NEON_LT);
        styleField(emailField, NEON_LT);

        // Business address text area
        jTextArea1.setBackground(new Color(8, 16, 30));
        jTextArea1.setForeground(Color.WHITE);
        jTextArea1.setCaretColor(Color.WHITE);
        jTextArea1.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addressScrollPane.setOpaque(false);
        addressScrollPane.getViewport().setBackground(new Color(8, 16, 30));
        addressScrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(37, 99, 168, 100)));
        addressScrollPane.setPreferredSize(new Dimension(440, 80));
        addressScrollPane.setMaximumSize(new Dimension(440, 80));
        addressScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Notification preference
        styleFieldLabel(notificationText, "NOTIFICATION PREFERENCE", NEON);

        for (JCheckBox cb : new JCheckBox[]{emailCheckBox, mailCheckBox}) {
            cb.setOpaque(false);
            cb.setForeground(Color.WHITE);
            cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            cb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        JPanel notifRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        notifRow.setOpaque(false);
        notifRow.setMaximumSize(new Dimension(440, 36));
        notifRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        notifRow.add(emailCheckBox);
        notifRow.add(mailCheckBox);

        // Directors section header
        companyDirectorText.setText("COMPANY DIRECTORS");
        companyDirectorText.setFont(new Font("Segoe UI", Font.BOLD, 11));
        companyDirectorText.setForeground(new Color(126, 184, 247, 180));
        companyDirectorText.setAlignmentX(Component.CENTER_ALIGNMENT);

        styleFieldLabel(directorNameText, "NUMBER OF DIRECTORS", NEON);

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
        spinnerRow.add(directorNameText);
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

        submitButton.setText("SUBMIT APPLICATION");
        styleButton(submitButton);

        // Assemble layout
        content.add(commercialFormText);
        content.add(Box.createVerticalStrut(6));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(30));
        content.add(companyNameText);
        content.add(Box.createVerticalStrut(6));
        content.add(companyNameField);
        content.add(Box.createVerticalStrut(16));
        content.add(companyIDText);
        content.add(Box.createVerticalStrut(6));
        content.add(companyIDField);
        content.add(Box.createVerticalStrut(16));
        content.add(businessTypeText);
        content.add(Box.createVerticalStrut(6));
        content.add(BusinessTypeField);
        content.add(Box.createVerticalStrut(16));
        content.add(businessAddressText);
        content.add(Box.createVerticalStrut(6));
        content.add(addressScrollPane);
        content.add(Box.createVerticalStrut(16));
        content.add(emailText);
        content.add(Box.createVerticalStrut(6));
        content.add(emailField);
        content.add(Box.createVerticalStrut(28));
        content.add(notificationText);
        content.add(Box.createVerticalStrut(10));
        content.add(notifRow);
        content.add(Box.createVerticalStrut(30));
        content.add(companyDirectorText);
        content.add(Box.createVerticalStrut(16));
        content.add(spinnerRow);
        content.add(Box.createVerticalStrut(16));
        content.add(directorsScrollPane);
        content.add(Box.createVerticalStrut(20));
        content.add(errorLabel);
        content.add(Box.createVerticalStrut(10));
        content.add(submitButton);
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

        commercialFormText = new javax.swing.JLabel();
        companyNameText = new javax.swing.JLabel();
        companyIDText = new javax.swing.JLabel();
        businessTypeText = new javax.swing.JLabel();
        submitButton = new javax.swing.JButton();
        companyIDField = new javax.swing.JTextField();
        companyNameField = new javax.swing.JTextField();
        BusinessTypeField = new javax.swing.JTextField();
        businessAddressText = new javax.swing.JLabel();
        emailField = new javax.swing.JTextField();
        emailText = new javax.swing.JLabel();
        addressScrollPane = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        companyDirectorText = new javax.swing.JLabel();
        directorNameText = new javax.swing.JLabel();
        directorSpinner = new javax.swing.JSpinner();
        directorsScrollPane = new javax.swing.JScrollPane();
        directorsPanel = new javax.swing.JPanel();
        emailCheckBox = new javax.swing.JCheckBox();
        mailCheckBox = new javax.swing.JCheckBox();
        notificationText = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        commercialFormText.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        commercialFormText.setText("Commercial Application Form");

        companyNameText.setText("Company Name:");

        companyIDText.setText("Companies House ID:");

        businessTypeText.setText("Type of Business:");

        submitButton.setText("Submit Application");
        submitButton.addActionListener(this::submitButtonActionPerformed);

        companyIDField.addActionListener(this::companyIDFieldActionPerformed);

        companyNameField.addActionListener(this::companyNameFieldActionPerformed);

        BusinessTypeField.addActionListener(this::BusinessTypeFieldActionPerformed);

        businessAddressText.setText("Business Address:");

        emailField.addActionListener(this::emailFieldActionPerformed);

        emailText.setText("Email:");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        addressScrollPane.setViewportView(jTextArea1);

        companyDirectorText.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        companyDirectorText.setText("Company Directors");

        directorNameText.setText("Number of Directors:");

        directorSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 5, 1));
        directorSpinner.addChangeListener(this::directorSpinnerStateChanged);

        directorsScrollPane.setBackground(new java.awt.Color(255, 255, 255));

        directorsPanel.setLayout(new javax.swing.BoxLayout(directorsPanel, javax.swing.BoxLayout.Y_AXIS));
        directorsScrollPane.setViewportView(directorsPanel);

        emailCheckBox.setText("Email");

        mailCheckBox.setText("Mail");

        notificationText.setText("Notifications:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(162, 162, 162)
                        .addComponent(commercialFormText))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(directorNameText, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(directorsScrollPane)
                            .addComponent(directorSpinner)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(businessTypeText, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(24, 24, 24)
                                .addComponent(BusinessTypeField, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(businessAddressText, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(addressScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(companyIDText)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(companyIDField, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(companyNameText, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(companyNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(notificationText, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(87, 87, 87)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(emailCheckBox)
                                        .addGap(39, 39, 39)
                                        .addComponent(mailCheckBox))
                                    .addComponent(companyDirectorText)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(emailText, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(139, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(submitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(230, 230, 230))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(commercialFormText)
                .addGap(57, 57, 57)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(companyNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(companyNameText))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(companyIDText)
                    .addComponent(companyIDField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BusinessTypeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(businessTypeText))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(businessAddressText)
                    .addComponent(addressScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(emailText)
                    .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(emailCheckBox)
                    .addComponent(mailCheckBox)
                    .addComponent(notificationText))
                .addGap(18, 18, 18)
                .addComponent(companyDirectorText)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(directorSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(directorNameText))
                .addGap(18, 18, 18)
                .addComponent(directorsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(submitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {
        System.out.println("SUBMIT CLICKED");

        // Read company fields
        String companyName   = companyNameField.getText().trim();
        String companyId     = companyIDField.getText().trim();
        String businessType  = BusinessTypeField.getText().trim();
        String address       = jTextArea1.getText().trim();
        String email         = emailField.getText().trim();
        String phone         = "";   // company phone number

        // Basic validation
        if (companyName.isEmpty() || companyId.isEmpty() || email.isEmpty()) {
            showError("Please complete all required fields.");
            return;
        }

        // Collect directors
        int count = (int) directorSpinner.getValue();
        List<Director> directors = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            JTextField first = (JTextField) findComponentByName(directorsPanel, "director" + i + "_firstname");
            JTextField last  = (JTextField) findComponentByName(directorsPanel, "director" + i + "_lastname");
            JTextField phoneField = (JTextField) findComponentByName(directorsPanel, "director" + i + "_phonenumber");

            directors.add(new Director(
                    first.getText().trim(),
                    last.getText().trim(),
                    phoneField.getText().trim()
            ));
        }

        // Build request object
        CommercialApplicationRequest req = new CommercialApplicationRequest();
        req.companyName   = companyName;
        req.regNumber     = companyId;
        req.businessType  = businessType;
        req.address       = address;
        req.email         = email;
        req.phone         = phone;
        req.directors     = directors;

        // Convert to JSON
        // build JSON manually — no Gson needed
        StringBuilder jsonSb = new StringBuilder();
        jsonSb.append("{");
        jsonSb.append("\"companyName\":\"").append(req.companyName).append("\",");
        jsonSb.append("\"regNumber\":\"").append(req.regNumber).append("\",");
        jsonSb.append("\"businessType\":\"").append(req.businessType).append("\",");
        jsonSb.append("\"address\":\"").append(req.address).append("\",");
        jsonSb.append("\"email\":\"").append(req.email).append("\",");
        jsonSb.append("\"phone\":\"").append(req.phone).append("\",");
        jsonSb.append("\"directors\":[");
        if (req.directors != null) {
            for (int di = 0; di < req.directors.size(); di++) {
                ipos_pu.Director d = req.directors.get(di);
                if (di > 0) jsonSb.append(",");
                jsonSb.append("{\"name\":\"").append(d.getFullName()).append("\",\"phone\":\"").append(d.getPhone()).append("\"}");
            }
        }
        jsonSb.append("]}");
        String json = jsonSb.toString();

        try {
            // Send to backend
            String response = HttpClient.postJson(
                    "http://localhost:8080/api/commercial-application",
                    json
            );

            JOptionPane.showMessageDialog(this, "Application submitted:\n" + response);
            this.dispose();

        } catch (Exception ex) {
            showError("Submission failed: " + ex.getMessage());
        }
    }//GEN-LAST:event_submitButtonActionPerformed

    private Component findComponentByName(Container container, String name) {
        for (Component c : container.getComponents()) {
            if (name.equals(c.getName())) {
                return c;
            }
            if (c instanceof Container) {
                Component child = findComponentByName((Container) c, name);
                if (child != null) return child;
            }
        }
        return null;
    }

    private void companyIDFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_companyIDFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_companyIDFieldActionPerformed

    private void companyNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_companyNameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_companyNameFieldActionPerformed

    private void BusinessTypeFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BusinessTypeFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BusinessTypeFieldActionPerformed

    private void emailFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emailFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_emailFieldActionPerformed

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

            for (String[] pair : new String[][]{
                    {"First Name", "15"},
                    {"Last Name", "15"},
                    {"Phone Number", "15"}
            }) {
                JLabel lbl = new JLabel(pair[0]);
                lbl.setForeground(Color.WHITE);
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));

                JTextField field = new JTextField(Integer.parseInt(pair[1]));
                field.setBackground(darkBg);
                field.setForeground(Color.WHITE);
                field.setCaretColor(Color.WHITE);
                field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(37, 99, 168, 100)),
                        BorderFactory.createEmptyBorder(4, 8, 4, 8)
                ));

                // Assign the correct name so submitButtonActionPerformed can find it
                if (pair[0].equals("First Name")) {
                    field.setName("director" + i + "_firstname");
                } else if (pair[0].equals("Last Name")) {
                    field.setName("director" + i + "_lastname");
                } else if (pair[0].equals("Phone Number")) {
                    field.setName("director" + i + "_phonenumber");
                }

                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
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
    private javax.swing.JTextField BusinessTypeField;
    private javax.swing.JScrollPane addressScrollPane;
    private javax.swing.JLabel businessAddressText;
    private javax.swing.JLabel businessTypeText;
    private javax.swing.JLabel commercialFormText;
    private javax.swing.JLabel companyDirectorText;
    private javax.swing.JTextField companyIDField;
    private javax.swing.JLabel companyIDText;
    private javax.swing.JTextField companyNameField;
    private javax.swing.JLabel companyNameText;
    private javax.swing.JLabel directorNameText;
    private javax.swing.JSpinner directorSpinner;
    private javax.swing.JPanel directorsPanel;
    private javax.swing.JScrollPane directorsScrollPane;
    private javax.swing.JCheckBox emailCheckBox;
    private javax.swing.JTextField emailField;
    private javax.swing.JLabel emailText;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JCheckBox mailCheckBox;
    private javax.swing.JLabel notificationText;
    private javax.swing.JButton submitButton;
    // End of variables declaration//GEN-END:variables
}
