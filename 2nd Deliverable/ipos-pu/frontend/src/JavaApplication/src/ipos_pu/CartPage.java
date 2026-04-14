/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ipos_pu;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;


public class CartPage extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
        java.util.logging.Logger.getLogger(CartPage.class.getName());


    private static final Color BG      = new Color(0x05080f);
    private static final Color PANEL   = new Color(0x080e1a);
    private static final Color NEON    = new Color(0x2563A8);
    private static final Color NEON_LT = new Color(0x7eb8f7);
    private static final Color GREEN   = new Color(0x4ade80);


    private String username;
    private DefaultTableModel cartModel;
    private JLabel subtotalLbl, totalLbl;
    private JPanel paymentCard, successCard;
    private JTable table;


    public CartPage(String username) {
        this.username = username;

        String[] cols = {"ITEM_ID", "PRODUCT", "UNIT PRICE", "QTY", "TOTAL", ""};
        cartModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        initComponents();
        buildUI();
        attachCartListeners();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        loadCartItemsFromBackend();
    }

    public CartPage() { this("Guest"); }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /** @param args the command line arguments */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info :
                    javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new CartPage().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables


    private String extract(String json, String key) {
        String search = "\"" + key + "\"";
        int start = json.indexOf(search);
        if (start == -1) return "";

        int colon = json.indexOf(":", start);
        if (colon == -1) return "";

        int valueStart = colon + 1;

        // Skip whitespace
        while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) {
            valueStart++;
        }

        // Quoted string
        if (json.charAt(valueStart) == '"') {
            int endQuote = json.indexOf("\"", valueStart + 1);
            return json.substring(valueStart + 1, endQuote);
        }

        // Number (supports negative and decimals)
        int end = valueStart;
        while (end < json.length() &&
                (Character.isDigit(json.charAt(end)) ||
                        json.charAt(end) == '-' ||
                        json.charAt(end) == '.')) {
            end++;
        }

        return json.substring(valueStart, end);
    }



    private void loadCartItemsFromBackend() {
        try {
            String token = CartManager.guestToken;
            String urlStr = "http://localhost:8080/api/cart/get?guestToken=" + token;

            // --- 1. HTTP GET request ---
            java.net.URL url = new java.net.URL(urlStr);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            java.io.BufferedReader reader =
                    new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();

            String json = sb.toString();
            System.out.println("DEBUG cart JSON = " + json);

            // --- 2. Clear table ---
            cartModel.setRowCount(0);

            // --- 3. Parse manually: find the items array ---
            int start = json.indexOf("[");
            int end   = json.lastIndexOf("]");
            if (start == -1 || end == -1) return;

            String itemsArray = json.substring(start + 1, end).trim();
            if (itemsArray.isEmpty()) return;

            // Split objects: "},{"
            String[] objects = itemsArray.split("\\},\\{");

            for (String obj : objects) {
                String o = obj.replace("{", "").replace("}", "").trim();

                String name     = extract(o, "name");
                String priceStr = extract(o, "price");
                String qtyStr   = extract(o, "qty");
                String itemId = extract(o, "itemId");

                double price = Double.parseDouble(priceStr);
                int qty      = Integer.parseInt(qtyStr);

                cartModel.addRow(new Object[]{
                        itemId,     // hidden column
                        name,
                        String.format("£%.2f", price),
                        qty,
                        String.format("£%.2f", price * qty),
                        "Remove"
                });
            }

            updateTotals();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Unable to load cart items.\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshCartUI() {
        getContentPane().removeAll();
        buildUI();
        revalidate();
        repaint();
    }

    private void buildUI() {
        setTitle("IPOS-PU \u00b7 Cart");
        setSize(1280, 760);
        setMinimumSize(new Dimension(1000, 620));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        getContentPane().setLayout(new BorderLayout());

        getContentPane().add(buildNav(), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(buildContent());
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        getContentPane().add(scroll, BorderLayout.CENTER);

        revalidate();
        repaint();
    }


    private JPanel buildNav() {
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.X_AXIS));
        nav.setBackground(PANEL);
        nav.setPreferredSize(new Dimension(0, 58));
        nav.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(37, 99, 168, 90)),
            BorderFactory.createEmptyBorder(0, 20, 0, 20)
        ));

        JLabel brand = new JLabel();
        brand.setText("<html><span style='font-family:Trebuchet MS;font-size:16px;"
            + "font-weight:bold;color:#ffffff'>IPOS</span>"
            + "<span style='font-family:Trebuchet MS;font-size:16px;"
            + "font-weight:bold;color:#7eb8f7'>-PU</span></html>");
        brand.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        brand.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                dispose();
                new HomePage(username).setVisible(true);
            }
        });
        nav.add(brand);
        nav.add(Box.createHorizontalStrut(10));

        JLabel hi = new JLabel("Hi, " + (username != null ? username : "Guest"));
        hi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hi.setForeground(new Color(255, 255, 255, 110));
        nav.add(hi);
        nav.add(Box.createHorizontalStrut(16));

        JButton backBtn = new JButton("Back to Home");
        styleNavBtn(backBtn);
        backBtn.addActionListener(e -> { dispose(); new HomePage(username).setVisible(true); });
        nav.add(backBtn);

        nav.add(Box.createHorizontalGlue());

        JButton signOut = new JButton("Sign Out");
        signOut.setBackground(NEON);
        signOut.setForeground(Color.WHITE);
        signOut.setFont(new Font("Segoe UI", Font.BOLD, 12));
        signOut.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        signOut.setFocusPainted(false);
        signOut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signOut.setOpaque(true);
        signOut.addActionListener(e -> { CartManager.clear(); OrderManager.clear(); dispose(); new LandingPage().setVisible(true); });
        nav.add(signOut);
        nav.add(Box.createHorizontalStrut(12));
        nav.add(makeAvatarPanel());

        return nav;
    }

    private JPanel buildContent() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG);
        outer.setBorder(BorderFactory.createEmptyBorder(24, 28, 28, 28));

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));

        JLabel pageLbl = new JLabel("YOUR CART");
        pageLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        pageLbl.setForeground(NEON);
        pageLbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel pageTitle = new JLabel("Review & Checkout");
        pageTitle.setFont(new Font("Trebuchet MS", Font.BOLD, 22));
        pageTitle.setForeground(Color.WHITE);
        pageTitle.setAlignmentX(LEFT_ALIGNMENT);

        heading.add(pageLbl);
        heading.add(Box.createVerticalStrut(4));
        heading.add(pageTitle);
        heading.add(Box.createVerticalStrut(20));
        outer.add(heading, BorderLayout.NORTH);

        JPanel columns = new JPanel(new GridBagLayout());
        columns.setBackground(BG);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(buildCartSection());
        left.add(Box.createVerticalStrut(16));
        left.add(buildDeliverySection());
        left.add(Box.createVerticalGlue());

        GridBagConstraints gLeft = new GridBagConstraints();
        gLeft.gridx = 0; gLeft.gridy = 0;
        gLeft.weightx = 1.4; gLeft.weighty = 1.0;
        gLeft.fill = GridBagConstraints.BOTH;
        gLeft.insets = new Insets(0, 0, 0, 16);
        columns.add(left, gLeft);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.add(buildSummarySection());
        right.add(Box.createVerticalStrut(16));

        paymentCard = buildPaymentSection();
        right.add(paymentCard);

        successCard = buildSuccessCard();
        successCard.setVisible(false);
        right.add(successCard);

        GridBagConstraints gRight = new GridBagConstraints();
        gRight.gridx = 1; gRight.gridy = 0;
        gRight.weightx = 1.0; gRight.weighty = 1.0;
        gRight.fill = GridBagConstraints.BOTH;
        columns.add(right, gRight);

        outer.add(columns, BorderLayout.CENTER);
        return outer;
    }

    private JPanel buildCartSection() {
        JPanel card = makeCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(sectionLabel("CART ITEMS"));
        card.add(Box.createVerticalStrut(12));

        table = new JTable(cartModel);

        table.setBackground(PANEL);
        table.setForeground(new Color(255, 255, 255, 200));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(44);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(37, 99, 168, 40));
        table.setSelectionForeground(Color.WHITE);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(0x0a1018));
        header.setForeground(NEON);
        header.setFont(new Font("Segoe UI", Font.BOLD, 9));
        header.setPreferredSize(new Dimension(0, 34));
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(37, 99, 168, 60)));

        DefaultTableCellRenderer cr = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setBackground(r % 2 == 0 ? PANEL : new Color(0x0a1018));
                setForeground(new Color(255, 255, 255, 200));
                setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
                if (sel) setBackground(new Color(37, 99, 168, 40));
                if (c == 3) { setForeground(NEON_LT); setFont(getFont().deriveFont(Font.BOLD)); }
                return this;
            }
        };
        for (int i = 0; i < 4; i++) table.getColumnModel().getColumn(i).setCellRenderer(cr);

        table.getColumnModel().getColumn(3).setCellRenderer((t, v, sel, foc, r, c) -> {
            int qty = v instanceof Integer ? (Integer) v : 0;
            Color rowBg = r % 2 == 0 ? PANEL : new Color(0x0a1018);
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 8));
            p.setBackground(rowBg);

            JLabel minus = new JLabel("-");
            minus.setFont(new Font("Segoe UI", Font.BOLD, 13));
            minus.setForeground(NEON_LT);
            minus.setOpaque(true);
            minus.setBackground(new Color(0x0b1220));
            minus.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(37, 99, 168, 100), 1),
                BorderFactory.createEmptyBorder(2, 7, 2, 7)));
            minus.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel qtyLbl = new JLabel(String.valueOf(qty), SwingConstants.CENTER);
            qtyLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            qtyLbl.setForeground(Color.WHITE);
            qtyLbl.setPreferredSize(new Dimension(26, 20));

            JLabel plus = new JLabel("+");
            plus.setFont(new Font("Segoe UI", Font.BOLD, 13));
            plus.setForeground(NEON_LT);
            plus.setOpaque(true);
            plus.setBackground(new Color(0x0b1220));
            plus.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(37, 99, 168, 100), 1),
                BorderFactory.createEmptyBorder(2, 7, 2, 7)));
            plus.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            p.add(minus);
            p.add(qtyLbl);
            p.add(plus);
            return p;
        });
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setMaxWidth(130);


        table.getColumnModel().getColumn(5).setCellRenderer((t, v, sel, foc, r, c) -> {
            JButton btn = new JButton("Remove");
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            btn.setForeground(new Color(0xf87171));
            btn.setBackground(r % 2 == 0 ? PANEL : new Color(0x0a1018));
            btn.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            btn.setFocusPainted(false);
            btn.setOpaque(false);
            btn.setContentAreaFilled(false);
            JPanel wrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
            wrap.setBackground(r % 2 == 0 ? PANEL : new Color(0x0a1018));
            wrap.add(btn);
            return wrap;
        });
        table.getColumnModel().getColumn(5).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setMaxWidth(90);

        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(37, 99, 168, 50), 1));
        sp.getViewport().setBackground(PANEL);
        sp.setAlignmentX(LEFT_ALIGNMENT);
        sp.setPreferredSize(new Dimension(0, 210));
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        card.add(sp);

        return card;
    }


    private void sendQtyUpdateToBackend(String token, String itemId, int delta) throws Exception {
        String json = "[{\"guestToken\":\"" + token + "\",\"itemId\":\"" + itemId + "\",\"qty\":" + delta + "}]";

        java.net.URL url = new java.net.URL("http://localhost:8080/api/cart/add");
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (java.io.OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
        }

        if (conn.getResponseCode() >= 400) {
            throw new Exception("Backend rejected update (stock limit or unavailable)");
        }
    }

    private void sendRemoveToBackend(String token, String itemId) throws Exception {
        String json = "[{\"guestToken\":\"" + token + "\",\"itemId\":\"" + itemId + "\"}]";

        java.net.URL url = new java.net.URL("http://localhost:8080/api/cart/remove");
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (java.io.OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
        }

        if (conn.getResponseCode() >= 400) {
            throw new Exception("Failed to remove item");
        }
    }


    private JPanel buildDeliverySection() {
        JPanel card = makeCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(sectionLabel("DELIVERY ADDRESS"));
        card.add(Box.createVerticalStrut(12));

        JPanel row1 = twoColRow();
        row1.add(labeledField("FIRST NAME", "e.g. John"));
        row1.add(labeledField("LAST NAME",  "e.g. Smith"));
        card.add(row1);
        card.add(Box.createVerticalStrut(10));

        JPanel addrLine = labeledField("ADDRESS LINE 1", "e.g. 12 Pharmacy Road");
        addrLine.setAlignmentX(LEFT_ALIGNMENT);
        addrLine.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        card.add(addrLine);
        card.add(Box.createVerticalStrut(10));

        JPanel row2 = twoColRow();
        row2.add(labeledField("CITY",     "e.g. London"));
        row2.add(labeledField("POSTCODE", "e.g. EC1A 1BB"));
        card.add(row2);
        return card;
    }

    private JPanel buildSummarySection() {
        JPanel card = makeCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(sectionLabel("ORDER SUMMARY"));
        card.add(Box.createVerticalStrut(12));

        double sub   = calcSubtotal();
        double total = sub;

        subtotalLbl = summaryRow(card, "Subtotal", String.format("\u00a3%.2f", sub), false);
                      summaryRow(card, "Delivery", "Free",                           false);

        card.add(Box.createVerticalStrut(8));
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(37, 99, 168, 60));
        sep.setAlignmentX(LEFT_ALIGNMENT);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        card.add(sep);
        card.add(Box.createVerticalStrut(10));

        totalLbl = new JLabel("Total   \u00a3" + String.format("%.2f", total));
        totalLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        totalLbl.setForeground(Color.WHITE);
        totalLbl.setAlignmentX(LEFT_ALIGNMENT);
        card.add(totalLbl);
        return card;
    }
    private JPanel buildPaymentSection() {
        boolean guest = "Guest".equals(username);
        JPanel card = makeCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(sectionLabel("PAYMENT DETAILS"));
        card.add(Box.createVerticalStrut(12));

        // guest-only fields
        JTextField emailField   = styledField("Email address");
        JTextField addressField = styledField("Delivery address");
        if (guest) {
            card.add(fieldBlock("EMAIL", emailField));         card.add(Box.createVerticalStrut(10));
            card.add(fieldBlock("DELIVERY ADDRESS", addressField)); card.add(Box.createVerticalStrut(14));
        }

        JTextField   nameField   = styledField("Name on card");
        JTextField   cardField   = styledField("Card number (16 digits)");
        JTextField   expiryField = styledField("MM / YY");
        JPasswordField cvvField  = styledPasswordField();

        card.add(fieldBlock("NAME ON CARD",  nameField));   card.add(Box.createVerticalStrut(10));
        card.add(fieldBlock("CARD NUMBER",   cardField));   card.add(Box.createVerticalStrut(10));

        JPanel expCvvRow = twoColRow();
        expCvvRow.add(fieldBlock("EXPIRY DATE", expiryField));
        expCvvRow.add(fieldBlock("CVV",          cvvField));
        card.add(expCvvRow);
        card.add(Box.createVerticalStrut(6));

        JLabel errLbl = new JLabel(" ");
        errLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        errLbl.setForeground(new Color(0xf87171));
        errLbl.setAlignmentX(LEFT_ALIGNMENT);
        card.add(errLbl);
        card.add(Box.createVerticalStrut(8));

        JButton placeBtn = new JButton("PLACE ORDER");
        placeBtn.setBackground(NEON);
        placeBtn.setForeground(Color.WHITE);
        placeBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        placeBtn.setBorder(BorderFactory.createEmptyBorder(14, 0, 14, 0));
        placeBtn.setFocusPainted(false);
        placeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        placeBtn.setOpaque(true);
        placeBtn.setAlignmentX(LEFT_ALIGNMENT);
        placeBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        placeBtn.addActionListener(e -> {
            String name  = nameField.getText().trim();
            String cnum  = cardField.getText().replaceAll("\\s", "");
            String exp   = expiryField.getText().trim();
            String cvv   = new String(cvvField.getPassword()).trim();

            if (cartModel.getRowCount() == 0) {
                errLbl.setText("Your cart is empty."); return;
            }
            if (guest) {
                String em   = emailField.getText().trim();
                String addr = addressField.getText().trim();
                if (em.isEmpty() || em.equals("Email address") || !em.contains("@")) {
                    errLbl.setText("Please enter a valid email address."); return;
                }
                if (addr.isEmpty() || addr.equals("Delivery address")) {
                    errLbl.setText("Please enter your delivery address."); return;
                }
            }
            if (name.isEmpty() || name.equals("Name on card")) {
                errLbl.setText("Please enter the name on your card."); return;
            }
            if (cnum.length() != 16 || !cnum.matches("\\d+")) {
                errLbl.setText("Please enter a valid 16-digit card number."); return;
            }
            if (!exp.matches("\\d{2}\\s*/\\s*\\d{2}")) {
                errLbl.setText("Please enter expiry as MM / YY."); return;
            }
            if (cvv.length() != 3 || !cvv.matches("\\d+")) {
                errLbl.setText("Please enter a valid 3-digit CVV."); return;
            }

            OrderManager.placeOrder(CartManager.getItems(), calcSubtotal());
            // if a promo was active record how many items were purchased under it before we clear the cart
            String activeP = PromoManager.getUserActivePromo();
            if (activeP != null) {
                int totalQty = 0;
                for (CartManager.CartItem it : CartManager.getItems()) totalQty += it.qty;
                PromoManager.recordPurchase(activeP, totalQty);
                PromoManager.setUserActivePromo(null);
            }
            CartManager.clear();
            paymentCard.setVisible(false);
            successCard.setVisible(true);
            successCard.revalidate();
        });
        card.add(placeBtn);

        JLabel secure = new JLabel("Card details stored securely per brief requirements");
        secure.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        secure.setForeground(new Color(255, 255, 255, 55));
        secure.setAlignmentX(CENTER_ALIGNMENT);
        card.add(Box.createVerticalStrut(8));
        card.add(secure);
        return card;
    }

    private JPanel buildSuccessCard() {
        JPanel card = makeCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JPanel tick = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(74, 222, 128, 28));
                g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                g2.setColor(GREEN);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(1, 1, getWidth()-3, getHeight()-3);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
                g2.setColor(GREEN);
                FontMetrics fm = g2.getFontMetrics();
                String check = "OK";
                g2.drawString(check,
                    (getWidth()  - fm.stringWidth(check)) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        tick.setOpaque(false);
        tick.setPreferredSize(new Dimension(56, 56));
        tick.setMaximumSize(new Dimension(56, 56));
        tick.setAlignmentX(CENTER_ALIGNMENT);

        JLabel title = new JLabel("Order Placed!");
        title.setFont(new Font("Trebuchet MS", Font.BOLD, 20));
        title.setForeground(GREEN);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Your order has been received and is being processed.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(255, 255, 255, 130));
        sub.setAlignmentX(CENTER_ALIGNMENT);

        String ref = "ORD-" + (System.currentTimeMillis() % 100000);
        JLabel refLbl = new JLabel("Order Ref: " + ref);
        refLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        refLbl.setForeground(NEON_LT);
        refLbl.setAlignmentX(CENTER_ALIGNMENT);
        refLbl.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(37, 99, 168, 70), 1),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));

        String noteText = "Guest".equals(username)
            ? "Your order has been placed. A confirmation will be sent to the email you provided."
            : "A confirmation email has been sent to your registered address.<br>Use <b>Track Order</b> on the home page to follow your delivery.";
        JLabel emailNote = new JLabel(
            "<html><div style='text-align:center;color:rgba(255,255,255,0.5)'>"
            + noteText + "</div></html>");
        emailNote.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        emailNote.setForeground(new Color(255, 255, 255, 80));
        emailNote.setAlignmentX(CENTER_ALIGNMENT);

        JButton homeBtn = new JButton("Back to Home");
        homeBtn.setBackground(NEON);
        homeBtn.setForeground(Color.WHITE);
        homeBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        homeBtn.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        homeBtn.setFocusPainted(false);
        homeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        homeBtn.setOpaque(true);
        homeBtn.setAlignmentX(CENTER_ALIGNMENT);
        homeBtn.addActionListener(e -> { dispose(); new HomePage(username).setVisible(true); });

        card.add(Box.createVerticalStrut(14));
        card.add(tick);
        card.add(Box.createVerticalStrut(14));
        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(sub);
        card.add(Box.createVerticalStrut(18));
        card.add(refLbl);
        card.add(Box.createVerticalStrut(14));
        card.add(emailNote);
        card.add(Box.createVerticalStrut(22));
        card.add(homeBtn);
        card.add(Box.createVerticalStrut(14));
        return card;
    }

    private double calcSubtotal() {
        double sum = 0.0;
        if (cartModel == null) return 0.0;

        for (int i = 0; i < cartModel.getRowCount(); i++) {
            Object val = cartModel.getValueAt(i, 4); // TOTAL column
            if (val == null) continue;
            String totalStr = val.toString().replace("£", "").trim();
            try {
                sum += Double.parseDouble(totalStr);
            } catch (NumberFormatException ignored) {}
        }
        return sum;
    }


    private void updateTotals() {
        if (subtotalLbl == null) return;
        double sub = calcSubtotal();
        subtotalLbl.setText(String.format("\u00a3%.2f", sub));
        totalLbl.setText("Total   \u00a3" + String.format("%.2f", sub));
    }


    private JPanel makeCard() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PANEL);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(new Color(37, 99, 168, 50));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth()-1f, getHeight()-1f, 10, 10));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setAlignmentX(LEFT_ALIGNMENT);
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        return card;
    }


    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(NEON);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }


    private JLabel summaryRow(JPanel parent, String key, String val, boolean green) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        JLabel kLbl = new JLabel(key);
        kLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        kLbl.setForeground(new Color(255, 255, 255, 160));
        JLabel vLbl = new JLabel(val);
        vLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        vLbl.setForeground(green ? GREEN : new Color(255, 255, 255, 160));
        row.add(kLbl, BorderLayout.WEST);
        row.add(vLbl, BorderLayout.EAST);
        parent.add(row);
        parent.add(Box.createVerticalStrut(4));
        return vLbl;
    }


    private JPanel twoColRow() {
        JPanel p = new JPanel(new GridLayout(1, 2, 12, 0));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        return p;
    }


    private JPanel labeledField(String label, String placeholder) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(NEON);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        JTextField field = styledField(placeholder);
        p.add(lbl);
        p.add(Box.createVerticalStrut(5));
        p.add(field);
        return p;
    }


    private JPanel fieldBlock(String label, JTextField field) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentX(LEFT_ALIGNMENT);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(NEON);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        p.add(lbl);
        p.add(Box.createVerticalStrut(5));
        p.add(field);
        return p;
    }


    private JTextField styledField(String placeholder) {
        JTextField f = new JTextField();
        f.setBackground(new Color(0x0b1220));
        f.setForeground(new Color(255, 255, 255, 80));
        f.setCaretColor(NEON_LT);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        f.setAlignmentX(LEFT_ALIGNMENT);
        f.setText(placeholder);
        Border normal  = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(37, 99, 168, 80), 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10));
        Border focused = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(NEON_LT, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10));
        f.setBorder(normal);
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                f.setBorder(focused);
                if (f.getText().equals(placeholder)) { f.setText(""); f.setForeground(Color.WHITE); }
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                f.setBorder(normal);
                if (f.getText().isEmpty()) { f.setText(placeholder); f.setForeground(new Color(255,255,255,80)); }
            }
        });
        return f;
    }


    private JPasswordField styledPasswordField() {
        JPasswordField f = new JPasswordField();
        f.setBackground(new Color(0x0b1220));
        f.setForeground(Color.WHITE);
        f.setCaretColor(NEON_LT);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        f.setAlignmentX(LEFT_ALIGNMENT);
        Border normal  = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(37, 99, 168, 80), 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10));
        Border focused = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(NEON_LT, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10));
        f.setBorder(normal);
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) { f.setBorder(focused); }
            @Override public void focusLost(java.awt.event.FocusEvent e)   { f.setBorder(normal);  }
        });
        return f;
    }

    private void styleNavBtn(JButton btn) {
        btn.setBackground(new Color(0x0b1220));
        btn.setForeground(new Color(200, 200, 200));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(37, 99, 168, 80), 1),
            BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
    }

    private JPanel makeAvatarPanel() {
        String initials = getInitials(username);
        JPanel av = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(NEON);
                g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(initials,
                    (getWidth()  - fm.stringWidth(initials)) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        av.setOpaque(false);
        av.setPreferredSize(new Dimension(36, 36));
        av.setMaximumSize(new Dimension(36, 36));
        av.setMinimumSize(new Dimension(36, 36));
        return av;
    }

    private void attachCartListeners() {
        if (table == null) return;

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                int row = table.rowAtPoint(e.getPoint());
                if (row < 0) return;

                String itemId = cartModel.getValueAt(row, 0).toString();
                String token = CartManager.guestToken;

                if (col == 3) {
                    Rectangle cellRect = table.getCellRect(row, col, false);
                    int localX = e.getX() - cellRect.x;
                    boolean isMinus = localX < cellRect.width * 0.34;
                    boolean isPlus  = localX > cellRect.width * 0.66;
                    if (!isMinus && !isPlus) return;

                    int delta = isPlus ? 1 : -1;

                    try {
                        sendQtyUpdateToBackend(token, itemId, delta);
                        loadCartItemsFromBackend();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

                else if (col == 5) {
                    try {
                        sendRemoveToBackend(token, itemId);
                        loadCartItemsFromBackend();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    private JTable findCartTable() {
        // Your cart table is always inside the first JScrollPane in the content
        for (Component c : getContentPane().getComponents()) {
            if (c instanceof JScrollPane sp) {
                if (sp.getViewport().getView() instanceof JTable t) {
                    return t;
                }
            }
        }
        return null;
    }

    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "G";
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2)
            return ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
        return name.substring(0, Math.min(2, name.length())).toUpperCase();
    }
}
