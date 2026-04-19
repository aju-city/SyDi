/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ipos_pu;

import java.awt.*;
import java.awt.geom.*;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/**
 * Cart page for reviewing items and completing checkout.
 * @author nuhur
 */
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
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField addressLine1Field;
    private JTextField cityField;
    private JTextField postcodeField;

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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

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

    /**
     * Extracts a simple JSON value by key.
     */
    private String extract(String json, String key) {
        String search = "\"" + key + "\"";
        int start = json.indexOf(search);
        if (start == -1) return "";

        int colon = json.indexOf(":", start);
        if (colon == -1) return "";

        int valueStart = colon + 1;

        while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) {
            valueStart++;
        }

        if (json.charAt(valueStart) == '"') {
            int endQuote = json.indexOf("\"", valueStart + 1);
            return json.substring(valueStart + 1, endQuote);
        }

        int end = valueStart;
        while (end < json.length() &&
                (Character.isDigit(json.charAt(end)) ||
                        json.charAt(end) == '-' ||
                        json.charAt(end) == '.')) {
            end++;
        }

        return json.substring(valueStart, end);
    }

    /**
     * Loads cart items from the backend and updates the table totals.
     */
    private void loadCartItemsFromBackend() {
        cartModel.setRowCount(0);

        try {
            boolean isGuest = "Guest".equals(username);
            String query;

            if (isGuest) {
                if (CartManager.guestToken == null || CartManager.guestToken.trim().isEmpty()) {
                    updateTotals();
                    return;
                }
                query = "guestToken=" + CartManager.guestToken;
            } else {
                if (CartManager.memberEmail == null || CartManager.memberEmail.trim().isEmpty()) {
                    updateTotals();
                    return;
                }
                query = "memberEmail=" + CartManager.memberEmail;
            }

            URL url = new URL("http://localhost:8080/api/cart/get?" + query);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int status = conn.getResponseCode();
            String response = "";
            try {
                response = new String(
                        (status >= 200 && status < 300 ? conn.getInputStream() : conn.getErrorStream()).readAllBytes()
                );
            } catch (Exception ignored) {}

            System.out.println("LOAD CART status = " + status);
            System.out.println("LOAD CART response = " + response);
            System.out.println("LOAD CART query = " + query);

            if (status >= 400) {
                throw new Exception("Failed to load cart. HTTP " + status + " -> " + response);
            }

            int itemsStart = response.indexOf("[");
            int itemsEnd = response.lastIndexOf("]");
            if (itemsStart == -1 || itemsEnd == -1 || itemsEnd <= itemsStart) {
                updateTotals();
                return;
            }

            String itemsArray = response.substring(itemsStart + 1, itemsEnd).trim();
            if (itemsArray.isEmpty()) {
                updateTotals();
                return;
            }

            java.util.Map<String, Double> promoPrices = loadPromoPrices();
            boolean loyalty = loyaltyApplies();

            String[] objects = itemsArray.split("\\},\\s*\\{");
            for (String obj : objects) {
                String clean = obj;
                if (!clean.startsWith("{")) clean = "{" + clean;
                if (!clean.endsWith("}")) clean = clean + "}";

                String itemId = extract(clean, "itemId");
                String name = extract(clean, "name");
                String priceStr = extract(clean, "price");
                String qtyStr = extract(clean, "qty");

                double price = 0.0;
                int qty = 0;

                try { price = Double.parseDouble(priceStr); } catch (Exception ignored) {}
                try { qty = Integer.parseInt(qtyStr); } catch (Exception ignored) {}

                Double promoPrice = promoPrices.get(itemId);
                if (promoPrice != null) {
                    price = promoPrice;
                }

                if (loyalty) {
                    price = Math.round(price * 0.9 * 100.0) / 100.0;
                }

                cartModel.addRow(new Object[]{
                        itemId,
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
            updateTotals();
        }
    }

    /**
     * Rebuilds the page after cart changes.
     */
    private void refreshCartUI() {
        getContentPane().removeAll();
        buildUI();
        revalidate();
        repaint();
    }

    /**
     * Builds the main cart page layout.
     */
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

    /**
     * Builds the top navigation bar for the cart page.
     */
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

    /**
     * Builds the main cart content, including cart, delivery, summary, and payment sections.
     */
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

    /**
     Builds the cart items section, including the table and item action controls.
     */
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


    /**
     Sends a cart quantity update to the backend for a member or guest cart.
     */
    private void sendQtyUpdateToBackend(String identifier, String itemId, int delta, boolean isGuest) throws Exception {
        String json = isGuest
                ? "{\"guestToken\":\"" + identifier + "\",\"itemId\":\"" + itemId + "\",\"qty\":" + delta + "}"
                : "{\"memberEmail\":\"" + identifier + "\",\"itemId\":\"" + itemId + "\",\"qty\":" + delta + "}";

        URL url = new URL("http://localhost:8080/api/cart/add");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
        }

        if (conn.getResponseCode() >= 400) {
            throw new Exception("Backend rejected update (stock limit or unavailable)");
        }
    }


    /**
     Sends a remove-item request to the backend for a member or guest cart.
     */
    private void sendRemoveToBackend(String identifier, String itemId, boolean isGuest) throws Exception {
        String json = isGuest
                ? "{\"guestToken\":\"" + identifier + "\",\"itemId\":\"" + itemId + "\"}"
                : "{\"memberEmail\":\"" + identifier + "\",\"itemId\":\"" + itemId + "\"}";

        URL url = new URL("http://localhost:8080/api/cart/remove");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
        }

        if (conn.getResponseCode() >= 400) {
            throw new Exception("Failed to remove item");
        }
    }


    /**
     Builds the delivery address section for member checkout.
     */
    private JPanel buildDeliverySection() {
        JPanel card = makeCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(sectionLabel("DELIVERY ADDRESS"));
        card.add(Box.createVerticalStrut(12));

        JPanel row1 = twoColRow();
        firstNameField = styledField("e.g. John");
        lastNameField  = styledField("e.g. Smith");
        row1.add(fieldBlock("FIRST NAME", firstNameField));
        row1.add(fieldBlock("LAST NAME",  lastNameField));
        card.add(row1);
        card.add(Box.createVerticalStrut(10));

        addressLine1Field = styledField("e.g. 12 Pharmacy Road");
        JPanel addrLine = fieldBlock("ADDRESS LINE 1", addressLine1Field);
        addrLine.setAlignmentX(LEFT_ALIGNMENT);
        addrLine.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        card.add(addrLine);
        card.add(Box.createVerticalStrut(10));

        JPanel row2 = twoColRow();
        cityField = styledField("e.g. London");
        postcodeField = styledField("e.g. EC1A 1BB");
        row2.add(fieldBlock("CITY",     cityField));
        row2.add(fieldBlock("POSTCODE", postcodeField));
        card.add(row2);
        return card;
    }

    /**
     Builds the order summary section using the current cart totals.
     */
    private JPanel buildSummarySection() {
        JPanel card = makeCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(sectionLabel("ORDER SUMMARY"));
        card.add(Box.createVerticalStrut(12));

        double sub   = calcSubtotal();
        double total = sub;

        subtotalLbl = summaryRow(card, "Subtotal", String.format("£%.2f", sub), false);
        summaryRow(card, "Delivery", "Free", false);

        if (loyaltyApplies()) {
            summaryRow(card, "Loyalty Discount", "10% applied", true);
        }

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

    /**
     Builds the payment section and handles checkout validation and submission.
     */
    private JPanel buildPaymentSection() {
        boolean guest = "Guest".equals(username);
        JPanel card = makeCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(sectionLabel("PAYMENT DETAILS"));
        card.add(Box.createVerticalStrut(12));

        // Guest users must provide an email address and delivery address at checkout.
        JTextField emailField   = styledField("Email address");
        JTextField addressField = styledField("Delivery address");
        if (guest) {
            card.add(fieldBlock("EMAIL", emailField));
            card.add(Box.createVerticalStrut(10));
            card.add(fieldBlock("DELIVERY ADDRESS", addressField));
            card.add(Box.createVerticalStrut(14));
        }

        JTextField nameField   = styledField("Name on card");
        JTextField cardField   = styledField("Card number (16 digits)");
        JTextField expiryField = styledField("MM / YY");
        JPasswordField cvvField  = styledPasswordField();

        card.add(fieldBlock("NAME ON CARD",  nameField));
        card.add(Box.createVerticalStrut(10));
        card.add(fieldBlock("CARD NUMBER",   cardField));
        card.add(Box.createVerticalStrut(10));

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
                errLbl.setText("Your cart is empty.");
                return;
            }

            String customerEmail;
            String deliveryAddress;

            if (guest) {
                String em   = emailField.getText().trim();
                String addr = addressField.getText().trim();

                if (em.isEmpty() || em.equals("Email address") || !em.contains("@")) {
                    errLbl.setText("Please enter a valid email address.");
                    return;
                }
                if (addr.isEmpty() || addr.equals("Delivery address")) {
                    errLbl.setText("Please enter your delivery address.");
                    return;
                }

                customerEmail = em;
                deliveryAddress = addr;

            } else {
                String first = firstNameField != null ? firstNameField.getText().trim() : "";
                String last  = lastNameField != null ? lastNameField.getText().trim() : "";
                String line1 = addressLine1Field != null ? addressLine1Field.getText().trim() : "";
                String city  = cityField != null ? cityField.getText().trim() : "";
                String post  = postcodeField != null ? postcodeField.getText().trim() : "";

                if (first.isEmpty() || first.equals("e.g. John")) {
                    errLbl.setText("Please enter your first name.");
                    return;
                }
                if (last.isEmpty() || last.equals("e.g. Smith")) {
                    errLbl.setText("Please enter your last name.");
                    return;
                }
                if (line1.isEmpty() || line1.equals("e.g. 12 Pharmacy Road")) {
                    errLbl.setText("Please enter your delivery address.");
                    return;
                }
                if (city.isEmpty() || city.equals("e.g. London")) {
                    errLbl.setText("Please enter your city.");
                    return;
                }
                if (post.isEmpty() || post.equals("e.g. EC1A 1BB")) {
                    errLbl.setText("Please enter your postcode.");
                    return;
                }

                customerEmail = CartManager.memberEmail;
                deliveryAddress = first + " " + last + ", " + line1 + ", " + city + ", " + post;
            }

            if (name.isEmpty() || name.equals("Name on card")) {
                errLbl.setText("Please enter the name on your card.");
                return;
            }
            if (cnum.length() != 16 || !cnum.matches("\\d+")) {
                errLbl.setText("Please enter a valid 16-digit card number.");
                return;
            }
            if (!exp.matches("\\d{2}\\s*/\\s*\\d{2}")) {
                errLbl.setText("Please enter expiry as MM / YY.");
                return;
            }
            if (cvv.length() != 3 || !cvv.matches("\\d+")) {
                errLbl.setText("Please enter a valid 3-digit CVV.");
                return;
            }

            try {
                boolean isGuest = guest;
                String identifier = isGuest ? CartManager.guestToken : CartManager.memberEmail;
                Integer campaignId = CartManager.activeCampaignId;

                String maskedCard = "**** **** **** " + cnum.substring(cnum.length() - 4);

                StringBuilder json = new StringBuilder("{");
                if (isGuest) {
                    json.append("\"guestToken\":\"").append(identifier).append("\"");
                } else {
                    json.append("\"memberEmail\":\"").append(identifier).append("\"");
                }
                json.append(",\"customerEmail\":\"").append(customerEmail).append("\"");
                json.append(",\"deliveryAddress\":\"").append(deliveryAddress).append("\"");
                json.append(",\"paymentMethod\":\"").append("Visa").append("\"");
                json.append(",\"maskedCardNumber\":\"").append(maskedCard).append("\"");
                json.append(",\"processorReference\":\"").append("PU-").append(System.currentTimeMillis()).append("\"");
                json.append(",\"amount\":").append(String.format(java.util.Locale.US, "%.2f", calcSubtotal()));

                if (campaignId != null) {
                    json.append(",\"campaignId\":").append(campaignId);
                }

                json.append("}");
                URL url = new URL("http://localhost:8080/api/checkout");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.toString().getBytes());
                }

                int status = conn.getResponseCode();
                String response = "";
                try {
                    response = new String(
                            (status >= 200 && status < 300 ? conn.getInputStream() : conn.getErrorStream()).readAllBytes()
                    );
                } catch (Exception ignored) {}

                if (status != 200) {
                    errLbl.setText("Checkout failed. Please try again.");
                    System.out.println("Checkout error: " + response);
                    return;
                }

                if (guest) {
                    CartManager.clear();
                    CartManager.guestToken = null;
                } else {
                    CartManager.clearItemsOnly();
                }
                CartManager.activeCampaignId = null;

                paymentCard.setVisible(false);
                successCard.setVisible(true);
                successCard.revalidate();

            } catch (Exception ex) {
                ex.printStackTrace();
                errLbl.setText("Checkout failed. Please try again.");
            }
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

    /**
     Builds the success card shown after a completed order.
     */
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

    /**
     Calculates the current cart subtotal from the total column values.
     */
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


    /**
     Updates the subtotal and total labels after a cart change.
     */
    private void updateTotals() {
        if (subtotalLbl == null) return;
        double sub = calcSubtotal();
        subtotalLbl.setText(String.format("\u00a3%.2f", sub));
        totalLbl.setText("Total   \u00a3" + String.format("%.2f", sub));
    }


    /**
     Creates a rounded card panel used for cart page sections.
     */
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


    /**
     Creates a section heading label for the cart page.
     */
    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(NEON);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }


    /**
     Adds a summary row to the order summary section and returns the value label.
     */
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


    /**
     Creates a two-column row layout for grouped form fields.
     */
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


    /**
     Creates a labeled field block for forms on the cart page.
     */
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


    /**
     Creates a styled text field with placeholder behaviour.
     */
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
        Border focused = javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(37, 99, 168, 200), 1),
                javax.swing.BorderFactory.createEmptyBorder(6, 10, 6, 10));
        f.setBorder(normal);
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                if (f.getText().equals(placeholder)) {
                    f.setText("");
                    f.setForeground(java.awt.Color.WHITE);
                }
                f.setBorder(focused);
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                if (f.getText().isEmpty()) {
                    f.setText(placeholder);
                    f.setForeground(new java.awt.Color(255, 255, 255, 80));
                }
                f.setBorder(normal);
            }
        });
        return f;
    }

    /**
     Attaches cart table listeners for quantity changes and item removal.
     */
    private void attachCartListeners() {
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                int row = table.rowAtPoint(e.getPoint());
                if (row < 0) return;
                String itemId  = cartModel.getValueAt(row, 0).toString();
                String prodName = cartModel.getValueAt(row, 1).toString();
                boolean isGuest  = "Guest".equals(username);
                String identifier = isGuest ? CartManager.guestToken : CartManager.memberEmail;
                if (col == 5) {
                    CartManager.removeItem(prodName);
                    cartModel.removeRow(row);
                    updateTotals();
                    try { sendRemoveToBackend(identifier, itemId, isGuest); } catch (Exception ex) { /* best effort */ }
                } else if (col == 3) {
                    java.awt.Rectangle rect = table.getCellRect(row, col, false);
                    int relX  = e.getX() - rect.x;
                    int third = rect.width / 3;
                    int qty = cartModel.getValueAt(row, 3) instanceof Integer
                            ? (Integer) cartModel.getValueAt(row, 3) : 0;

                    if (relX < third) {
                        // minus button
                        if (qty <= 1) {
                            CartManager.removeItem(prodName);
                            cartModel.removeRow(row);
                            try { sendRemoveToBackend(identifier, itemId, isGuest); } catch (Exception ex) { /* best effort */ }
                        } else {
                            qty--;
                            CartManager.setQty(prodName, qty);
                            double price = parsePrice(cartModel.getValueAt(row, 2).toString());
                            cartModel.setValueAt(qty, row, 3);
                            cartModel.setValueAt(String.format("\u00a3%.2f", price * qty), row, 4);
                            try { sendQtyUpdateToBackend(identifier, itemId, -1, isGuest); } catch (Exception ex) { /* best effort */ }
                        }
                    } else if (relX > rect.width - third) {
                        // plus button
                        int limit = Integer.MAX_VALUE;
                        for (CartManager.CartItem it : CartManager.getItems()) {
                            if (it.name.equals(prodName)) { limit = it.stockLimit; break; }
                        }
                        if (qty >= limit) return;
                        qty++;
                        CartManager.setQty(prodName, qty);
                        double price = parsePrice(cartModel.getValueAt(row, 2).toString());
                        cartModel.setValueAt(qty, row, 3);
                        cartModel.setValueAt(String.format("\u00a3%.2f", price * qty), row, 4);
                        try { sendQtyUpdateToBackend(identifier, itemId, 1, isGuest); } catch (Exception ex) { /* best effort */ }
                    }
                    updateTotals();
                }
            }
        });
    }

    /**
     Parses a price string into a numeric value.
     */
    private double parsePrice(String s) {
        try { return Double.parseDouble(s.replace("\u00a3", "").trim()); }
        catch (NumberFormatException e) { return 0.0; }
    }

    /**
     Loads promotional prices for items in the active campaign.
     */
    private java.util.Map<String, Double> loadPromoPrices() {
        java.util.Map<String, Double> promoPrices = new java.util.HashMap<>();

        if (CartManager.activeCampaignId == null) return promoPrices;

        try {
            URL url = new URL("http://localhost:8080/api/promotions/products?campaignId=" + CartManager.activeCampaignId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int status = conn.getResponseCode();
            String response = "";
            try {
                response = new String(
                        (status >= 200 && status < 300 ? conn.getInputStream() : conn.getErrorStream()).readAllBytes()
                );
            } catch (Exception ignored) {}

            System.out.println("CART PROMO PRODUCTS status = " + status);
            System.out.println("CART PROMO PRODUCTS response = " + response);

            if (status >= 400) return promoPrices;

            int productsKey = response.indexOf("\"products\"");
            if (productsKey == -1) return promoPrices;

            int arrStart = response.indexOf("[", productsKey);
            int arrEnd = response.lastIndexOf("]");
            if (arrStart == -1 || arrEnd == -1 || arrEnd <= arrStart) return promoPrices;

            String itemsArray = response.substring(arrStart + 1, arrEnd).trim();
            if (itemsArray.isEmpty()) return promoPrices;

            String[] objects = itemsArray.split("\\},\\s*\\{");
            for (String obj : objects) {
                String clean = obj;
                if (!clean.startsWith("{")) clean = "{" + clean;
                if (!clean.endsWith("}")) clean = clean + "}";

                String productId = extract(clean, "productId");
                String discountedPriceStr = extract(clean, "discountedPrice");

                if (productId == null || discountedPriceStr == null) continue;

                try {
                    promoPrices.put(productId, Double.parseDouble(discountedPriceStr));
                } catch (Exception ignored) {}
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return promoPrices;
    }


    private void styleNavBtn(javax.swing.JButton btn) {
        btn.setBackground(new java.awt.Color(0x0b1220));
        btn.setForeground(new java.awt.Color(255, 255, 255, 160));
        btn.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        btn.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(37, 99, 168, 80), 1),
                javax.swing.BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        btn.setFocusPainted(false);
        btn.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        btn.setOpaque(true);
    }

    /**
     Creates the user avatar panel shown in the navigation area.
     */
    private javax.swing.JPanel makeAvatarPanel() {
        String name = username != null ? username : "G";
        String initials;
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2)
            initials = ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
        else
            initials = name.substring(0, Math.min(2, name.length())).toUpperCase();
        final String ini = initials;
        javax.swing.JPanel av = new javax.swing.JPanel() {
            @Override protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(NEON);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.setColor(java.awt.Color.WHITE);
                g2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
                java.awt.FontMetrics fm = g2.getFontMetrics();
                g2.drawString(ini, (getWidth() - fm.stringWidth(ini)) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        av.setOpaque(false);
        av.setPreferredSize(new java.awt.Dimension(34, 34));
        av.setMinimumSize(new java.awt.Dimension(34, 34));
        av.setMaximumSize(new java.awt.Dimension(34, 34));
        return av;
    }

    /**
     Checks whether the loyalty discount applies to the current order.
     */
    private boolean loyaltyApplies() {
        return !"Guest".equals(username)
                && CartManager.memberEmail != null
                && !CartManager.memberEmail.trim().isEmpty()
                && OrderManager.isLoyaltyOrder(CartManager.memberEmail);
    }

    /**
     Creates a styled password field for payment entry.
     */
    private javax.swing.JPasswordField styledPasswordField() {
        javax.swing.JPasswordField f = new javax.swing.JPasswordField();
        f.setBackground(new java.awt.Color(0x0b1220));
        f.setForeground(new java.awt.Color(255, 255, 255, 160));
        f.setCaretColor(NEON_LT);
        f.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        f.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 38));
        f.setAlignmentX(LEFT_ALIGNMENT);
        f.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(37, 99, 168, 80), 1),
                javax.swing.BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return f;
    }

}