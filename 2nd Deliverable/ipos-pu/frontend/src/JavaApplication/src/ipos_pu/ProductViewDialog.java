package ipos_pu;

import java.awt.*;
import javax.swing.*;

public class ProductViewDialog extends JDialog {

    private static final Color BG     = new Color(0x05080f);
    private static final Color NEON   = new Color(0x2563A8);

    public ProductViewDialog(Frame parent, String name, String description,
                             String packageType, String unit,
                             String unitsPack, String price, String stock) {
        super(parent, name, true);
        setSize(480, 360);
        setResizable(false);
        setLocationRelativeTo(parent);
        buildUI(name, description, packageType, unit, unitsPack, price, stock);
    }

    private void buildUI(String name, String description, String packageType,
                         String unit, String unitsPack, String price, String stock) {

        JPanel bg = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setColor(BG);
                g2.fillRect(0, 0, w, h);
                g2.setColor(new Color(37, 99, 168, 5));
                for (int y = 0; y < h; y += 4) g2.drawLine(0, y, w, y);
                g2.setPaint(new RadialGradientPaint(w / 2f, h / 2f, Math.max(w, h) / 2f,
                    new float[]{0f, 1f}, new Color[]{new Color(37, 99, 168, 30), new Color(0, 0, 0, 0)}));
                g2.fillRect(0, 0, w, h);
                g2.dispose();
            }
        };
        bg.setLayout(new BoxLayout(bg, BoxLayout.Y_AXIS));
        bg.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        JLabel eyebrow = new JLabel("PRODUCT DETAILS");
        eyebrow.setFont(new Font("Segoe UI", Font.BOLD, 9));
        eyebrow.setForeground(NEON);
        eyebrow.setAlignmentX(LEFT_ALIGNMENT);

        JLabel nameLbl = new JLabel(name);
        nameLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 20));
        nameLbl.setForeground(Color.WHITE);
        nameLbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel descLbl = new JLabel("<html><div style='width:380px'>" + description + "</div></html>");
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLbl.setForeground(new Color(255, 255, 255, 160));
        descLbl.setAlignmentX(LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(37, 99, 168, 60));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        JPanel detailsGrid = new JPanel(new GridLayout(0, 2, 12, 8));
        detailsGrid.setOpaque(false);
        detailsGrid.setAlignmentX(LEFT_ALIGNMENT);
        detailsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        for (String[] pair : new String[][]{
                {"Package Type", packageType},
                {"Unit",         unit},
                {"Units / Pack", unitsPack},
                {"Price",        price},
                {"Availability", stock}
        }) {
            JLabel key = new JLabel(pair[0].toUpperCase());
            key.setFont(new Font("Segoe UI", Font.BOLD, 9));
            key.setForeground(NEON);

            Color valColor = new Color(255, 255, 255, 190);
            if ("Availability".equals(pair[0])) {
                if ("IN STOCK".equals(pair[1]))       valColor = new Color(0x4ade80);
                else if ("LOW STOCK".equals(pair[1])) valColor = new Color(0xfbbf24);
                else if ("NO STOCK".equals(pair[1]))  valColor = new Color(0xf87171);
            }
            JLabel val = new JLabel(pair[1]);
            val.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            val.setForeground(valColor);

            detailsGrid.add(key);
            detailsGrid.add(val);
        }

        JButton closeBtn = new JButton("CLOSE");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        closeBtn.setPreferredSize(new Dimension(120, 38));
        closeBtn.setMaximumSize(new Dimension(120, 38));
        closeBtn.setAlignmentX(LEFT_ALIGNMENT);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.setBackground(new Color(0x0b1220));
        closeBtn.setForeground(new Color(255, 255, 255, 160));
        closeBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(37, 99, 168, 100), 1),
            BorderFactory.createEmptyBorder(8, 18, 8, 18)
        ));
        closeBtn.addActionListener(e -> dispose());

        bg.add(eyebrow);
        bg.add(Box.createVerticalStrut(6));
        bg.add(nameLbl);
        bg.add(Box.createVerticalStrut(12));
        bg.add(descLbl);
        bg.add(Box.createVerticalStrut(18));
        bg.add(sep);
        bg.add(Box.createVerticalStrut(16));
        bg.add(detailsGrid);
        bg.add(Box.createVerticalGlue());
        bg.add(Box.createVerticalStrut(18));
        bg.add(closeBtn);

        getContentPane().add(bg);
    }
}
