/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ipos_pu;

import java.awt.*;
import java.awt.geom.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Admin dashboard for managing campaigns and viewing reports.
 *
 * @author nuhur
 */
public class AdminPage extends javax.swing.JFrame {

    private static final Color BG      = new Color(0x05080f);
    private static final Color PANEL   = new Color(0x080e1a);
    private static final Color NEON    = new Color(0x2563A8);
    private static final Color NEON_LT = new Color(0x7eb8f7);
    private static final Color GREEN   = new Color(0x4ade80);
    private static final Color RED     = new Color(0xf87171);
    private static final Color AMBER   = new Color(0xfbbf24);

    private CardLayout cardLayout;
    private JPanel     mainContent;
    private final List<JButton> tabBtns = new ArrayList<>();

    private static final String TAB_PROMOS    = "promos";
    private static final String TAB_SALES     = "sales";
    private static final String TAB_CAMPAIGNS = "campaigns";
    private static final String TAB_ENGAGE    = "engagement";

    private List<String> itemNames = new ArrayList<>();

    private static final java.time.format.DateTimeFormatter ADMIN_DATE_FMT =
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String username;

    public AdminPage(String username) {
        this.username = username;
        initComponents();
        buildUI();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Sets up the main admin page layout and loads the four dashboard sections.
     */
    private void buildUI() {
        System.out.println("Logged in admin username = " + username);
        setTitle("IPOS-PU — Admin Panel");
        setSize(1280, 760);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setBackground(BG);
        getContentPane().setLayout(new BorderLayout());

        itemNames = loadItemNames();

        cardLayout  = new CardLayout();
        mainContent = new JPanel(cardLayout);
        mainContent.setBackground(BG);
        mainContent.add(buildPromotionsTab(), TAB_PROMOS);
        mainContent.add(buildSalesReport(),   TAB_SALES);
        mainContent.add(buildCampaigns(),     TAB_CAMPAIGNS);
        mainContent.add(buildEngagement(),    TAB_ENGAGE);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.add(buildTabBar(), BorderLayout.NORTH);
        wrapper.add(mainContent,   BorderLayout.CENTER);

        getContentPane().add(buildNav(), BorderLayout.NORTH);
        getContentPane().add(wrapper,    BorderLayout.CENTER);
    }

    /**
     * Loads stock item names for the campaign form dropdowns.
     */
    private List<String> loadItemNames() {
        List<String> names = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement st = con.prepareStatement("SELECT name FROM ipos_ca.stock_items ORDER BY name");
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) names.add(rs.getString("name"));
        } catch (Exception ex) {
            // Leave dropdowns empty if the database is unavailable.
        }
        return names;
    }

    /**
     * Extracts a simple JSON value by key.
     */
    private String extract(String json, String key) {
        String search = "\"" + key + "\"";
        int start = json.indexOf(search);
        if (start == -1) return null;

        int colon = json.indexOf(":", start);
        if (colon == -1) return null;

        int valueStart = colon + 1;
        while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) {
            valueStart++;
        }

        if (valueStart >= json.length()) return null;

        if (json.charAt(valueStart) == '"') {
            int endQuote = json.indexOf("\"", valueStart + 1);
            if (endQuote == -1) return null;
            return json.substring(valueStart + 1, endQuote);
        }

        int end = valueStart;
        while (end < json.length()
                && json.charAt(end) != ','
                && json.charAt(end) != '}'
                && !Character.isWhitespace(json.charAt(end))) {
            end++;
        }

        return json.substring(valueStart, end);
    }

    /**
     * Builds the top navigation bar shown on the admin page.
     */
    private JPanel buildNav() {
        JPanel nav = new JPanel();
        nav.setBackground(PANEL);
        nav.setPreferredSize(new Dimension(0, 58));
        nav.setLayout(new BoxLayout(nav, BoxLayout.X_AXIS));
        nav.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(37, 99, 168, 90)),
                BorderFactory.createEmptyBorder(0, 20, 0, 20)
        ));

        JLabel brand = new JLabel("<html><span style='font-family:Trebuchet MS;font-size:16px;"
                + "font-weight:bold;color:#ffffff'>IPOS</span><span style='font-family:Trebuchet MS;"
                + "font-size:16px;font-weight:bold;color:#7eb8f7'>-PU</span></html>");

        JLabel adminBadge = new JLabel("ADMIN");
        adminBadge.setFont(new Font("Segoe UI", Font.BOLD, 9));
        adminBadge.setForeground(NEON_LT);
        adminBadge.setOpaque(true);
        adminBadge.setBackground(new Color(37, 99, 168, 40));
        adminBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(37, 99, 168, 100), 1),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));

        JButton signOutBtn = new JButton("Sign Out");
        signOutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        signOutBtn.setForeground(new Color(255, 255, 255, 160));
        signOutBtn.setBackground(new Color(0x0b1220));
        signOutBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(37, 99, 168, 80), 1),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        signOutBtn.setFocusPainted(false);
        signOutBtn.setOpaque(true);
        signOutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signOutBtn.addActionListener(e -> { dispose(); new LandingPage().setVisible(true); });

        nav.add(brand);
        nav.add(Box.createHorizontalStrut(10));
        nav.add(adminBadge);
        nav.add(Box.createHorizontalGlue());
        nav.add(signOutBtn);
        return nav;
    }

    private static class AdminCampaignData {
        Integer campaignId;
        String campaignName;
        String startDatetime;
        String endDatetime;
        String status;
        java.util.List<AdminCampaignItemData> items = new ArrayList<>();
    }

    private static class AdminCampaignItemData {
        Integer campaignItemId;
        String productId;
        String productName;
        Double discountRate;
    }

    /**
     * Sends an update request for a campaign to the backend API.
     */
    private boolean updateCampaignOnBackend(Integer campaignId, String name, String startIso, String endIso, String status) {
        try {
            String json = "{"
                    + "\"campaignId\":" + campaignId + ","
                    + "\"campaignName\":\"" + name + "\","
                    + "\"startDatetime\":\"" + startIso + "\","
                    + "\"endDatetime\":\"" + endIso + "\","
                    + "\"status\":\"" + status + "\""
                    + "}";

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/admin/promotions/campaign?campaignId=" + campaignId))
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("UPDATE CAMPAIGN status = " + response.statusCode());
            System.out.println("UPDATE CAMPAIGN response = " + response.body());

            return response.statusCode() == 200;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Sends an update request for a campaign item discount to the backend API.
     */
    private boolean updateCampaignItemOnBackend(Integer campaignItemId, double discountRate) {
        try {
            String json = "{"
                    + "\"campaignItemId\":" + campaignItemId + ","
                    + "\"discountRate\":" + discountRate
                    + "}";

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/admin/promotions/campaign/item?campaignItemId=" + campaignItemId))
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("UPDATE CAMPAIGN ITEM status = " + response.statusCode());
            System.out.println("UPDATE CAMPAIGN ITEM response = " + response.body());

            return response.statusCode() == 200;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a single campaign item through the backend API.
     */
    private boolean deleteCampaignItemOnBackend(Integer campaignItemId) {
        try {
            URL url = new URL("http://localhost:8080/api/admin/promotions/campaign/item?campaignItemId=" + campaignItemId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");

            int status = conn.getResponseCode();
            String response = "";
            try {
                response = new String(
                        (status >= 200 && status < 300
                                ? conn.getInputStream()
                                : conn.getErrorStream()).readAllBytes()
                );
            } catch (Exception ignored) {}

            System.out.println("DELETE CAMPAIGN ITEM status = " + status);
            System.out.println("DELETE CAMPAIGN ITEM response = " + response);

            return status == 200;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes all items linked to a campaign, then deletes the campaign itself.
     */
    private boolean deleteCampaignFully(Integer campaignId) {
        try {
            AdminCampaignData existing = null;
            for (AdminCampaignData c : loadCampaignsFromBackend()) {
                if (c.campaignId.equals(campaignId)) {
                    existing = c;
                    break;
                }
            }

            if (existing != null) {
                for (AdminCampaignItemData item : existing.items) {
                    deleteCampaignItemOnBackend(item.campaignItemId);
                }
            }

            return deleteCampaignOnBackend(campaignId);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Adds a product and discount entry to a campaign through the backend API.
     */
    private boolean addCampaignItemOnBackend(int campaignId, String productId, double discountRate) {
        try {
            URL url = new URL("http://localhost:8080/api/admin/promotions/campaign/items?campaignId=" + campaignId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = "{"
                    + "\"campaignId\":" + campaignId + ","
                    + "\"productId\":\"" + productId + "\","
                    + "\"discountRate\":" + discountRate
                    + "}";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }

            int status = conn.getResponseCode();
            String response = "";
            try {
                response = new String(
                        (status >= 200 && status < 300
                                ? conn.getInputStream()
                                : conn.getErrorStream()).readAllBytes()
                );
            } catch (Exception ignored) {}

            System.out.println("ADD CAMPAIGN ITEM status = " + status);
            System.out.println("ADD CAMPAIGN ITEM response = " + response);

            return status == 200;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a campaign through the backend API.
     */
    private boolean deleteCampaignOnBackend(Integer campaignId) {
        try {
            URL url = new URL("http://localhost:8080/api/admin/promotions/campaign?campaignId=" + campaignId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");

            int status = conn.getResponseCode();
            String response = "";
            try {
                response = new String(
                        (status >= 200 && status < 300
                                ? conn.getInputStream()
                                : conn.getErrorStream()).readAllBytes()
                );
            } catch (Exception ignored) {}

            System.out.println("DELETE CAMPAIGN status = " + status);
            System.out.println("DELETE CAMPAIGN response = " + response);

            return status == 200;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Creates a new campaign through the backend API and returns its ID.
     */
    private Integer createCampaignOnBackend(String name, String start, String end, String createdBy, String status) {
        try {
            URL url = new URL("http://localhost:8080/api/admin/promotions/campaigns");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = "{"
                    + "\"campaignName\":\"" + name + "\","
                    + "\"startDatetime\":\"" + start + "\","
                    + "\"endDatetime\":\"" + end + "\","
                    + "\"createdBy\":\"" + createdBy + "\","
                    + "\"status\":\"" + status + "\""
                    + "}";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }

            int httpStatus = conn.getResponseCode();
            String response = "";
            try {
                response = new String(
                        (httpStatus >= 200 && httpStatus < 300
                                ? conn.getInputStream()
                                : conn.getErrorStream()).readAllBytes()
                );
            } catch (Exception ignored) {}

            System.out.println("CREATE CAMPAIGN status = " + httpStatus);
            System.out.println("CREATE CAMPAIGN response = " + response);

            if (httpStatus != 200) return null;

            int idx = response.indexOf("\"campaignId\"");
            if (idx != -1) {
                int startIdx = response.indexOf(":", idx) + 1;
                int endIdx = response.indexOf(",", startIdx);
                if (endIdx == -1) endIdx = response.indexOf("}", startIdx);
                return Integer.parseInt(response.substring(startIdx, endIdx).trim());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Loads all campaigns from the backend and fills each one with its item data.
     */
    private List<AdminCampaignData> loadCampaignsFromBackend() {
        List<AdminCampaignData> campaigns = new ArrayList<>();

        try {
            URL url = new URL("http://localhost:8080/api/admin/promotions/campaigns");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int status = conn.getResponseCode();
            String response = "";
            try {
                response = new String(
                        (status >= 200 && status < 300
                                ? conn.getInputStream()
                                : conn.getErrorStream()).readAllBytes()
                );
            } catch (Exception ignored) {}

            System.out.println("ADMIN CAMPAIGNS status = " + status);
            System.out.println("ADMIN CAMPAIGNS response = " + response);

            if (status >= 400) return campaigns;

            int campaignsKey = response.indexOf("\"campaigns\"");
            if (campaignsKey == -1) return campaigns;

            int arrStart = response.indexOf("[", campaignsKey);
            int arrEnd = response.lastIndexOf("]");
            if (arrStart == -1 || arrEnd == -1 || arrEnd <= arrStart) return campaigns;

            String itemsArray = response.substring(arrStart + 1, arrEnd).trim();
            if (itemsArray.isEmpty()) return campaigns;

            String[] objects = itemsArray.split("\\},\\s*\\{");

            for (String obj : objects) {
                String clean = obj;
                if (!clean.startsWith("{")) clean = "{" + clean;
                if (!clean.endsWith("}")) clean = clean + "}";

                AdminCampaignData card = new AdminCampaignData();

                String idStr = extract(clean, "campaignId");
                if (idStr == null) continue;

                card.campaignId = Integer.parseInt(idStr);
                card.campaignName = extract(clean, "campaignName");
                card.startDatetime = extract(clean, "startDatetime");
                card.endDatetime = extract(clean, "endDatetime");
                card.status = extract(clean, "status");

                loadCampaignItemsFromBackend(card);

                campaigns.add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return campaigns;
    }
    /**
     * Loads all items linked to a campaign from the backend API.
     */
    private void loadCampaignItemsFromBackend(AdminCampaignData campaign) {
        if (campaign == null || campaign.campaignId == null) return;

        campaign.items.clear();

        try {
            URL url = new URL("http://localhost:8080/api/admin/promotions/campaign/items?campaignId=" + campaign.campaignId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int status = conn.getResponseCode();
            String response = "";
            try {
                response = new String(
                        (status >= 200 && status < 300
                                ? conn.getInputStream()
                                : conn.getErrorStream()).readAllBytes()
                );
            } catch (Exception ignored) {}

            System.out.println("ADMIN CAMPAIGN ITEMS status = " + status);
            System.out.println("ADMIN CAMPAIGN ITEMS response = " + response);

            if (status >= 400) return;

            int itemsKey = response.indexOf("\"items\"");
            if (itemsKey == -1) return;

            int arrStart = response.indexOf("[", itemsKey);
            int arrEnd = response.lastIndexOf("]");
            if (arrStart == -1 || arrEnd == -1 || arrEnd <= arrStart) return;

            String itemsArray = response.substring(arrStart + 1, arrEnd).trim();
            if (itemsArray.isEmpty()) return;

            String[] objects = itemsArray.split("\\},\\s*\\{");
            for (String obj : objects) {
                String clean = obj;
                if (!clean.startsWith("{")) clean = "{" + clean;
                if (!clean.endsWith("}")) clean = clean + "}";

                String campaignItemIdStr = extract(clean, "campaignItemId");
                String productId = extract(clean, "productId");
                String discountRateStr = extract(clean, "discountRate");

                if (campaignItemIdStr == null || productId == null || discountRateStr == null) continue;

                AdminCampaignItemData item = new AdminCampaignItemData();
                item.campaignItemId = Integer.parseInt(campaignItemIdStr);
                item.productId = productId;
                item.productName = lookupItemNameById(productId);
                if (item.productName == null) item.productName = productId;
                item.discountRate = Double.parseDouble(discountRateStr);

                campaign.items.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Looks up a stock item name using its item ID.
     */
    private String lookupItemNameById(String productId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement st = con.prepareStatement(
                     "SELECT name FROM ipos_ca.stock_items WHERE item_id = ?")) {
            st.setString(1, productId);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return rs.getString("name");
            }
        } catch (Exception ignored) {}
        return null;
    }

    /**
     * Looks up a stock item ID using its item name.
     */
    private String lookupItemIdByName(String itemName) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement st = con.prepareStatement(
                     "SELECT item_id FROM ipos_ca.stock_items WHERE name = ?")) {
            st.setString(1, itemName);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return rs.getString("item_id");
            }
        } catch (Exception ignored) {}
        return null;
    }

    /**
     * Builds the promotions tab, including the create form and campaign list.
     */
    private JScrollPane buildPromotionsTab() {
        JPanel outer = new JPanel();
        outer.setBackground(BG);
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBorder(BorderFactory.createEmptyBorder(28, 32, 32, 32));

        outer.add(buildReportHeader("ADMIN PANEL", "Promotions Management",
                "Create campaigns from scratch with per-item discounts. No limit on number of campaigns."));
        outer.add(Box.createVerticalStrut(20));

        JPanel formHolder = new JPanel();
        formHolder.setOpaque(false);
        formHolder.setLayout(new BoxLayout(formHolder, BoxLayout.Y_AXIS));
        formHolder.setAlignmentX(LEFT_ALIGNMENT);

        JPanel listPanel = new JPanel();
        listPanel.setOpaque(false);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setAlignmentX(LEFT_ALIGNMENT);

        Runnable refreshList = () -> {
            listPanel.removeAll();
            List<AdminCampaignData> all = loadCampaignsFromBackend();
            if (all.isEmpty()) {
                JLabel none = new JLabel("No campaigns created yet. Click '+ New Campaign' to start.");
                none.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                none.setForeground(new Color(255, 255, 255, 55));
                none.setAlignmentX(LEFT_ALIGNMENT);
                listPanel.add(none);
            } else {
                for (AdminCampaignData c : all) {
                    listPanel.add(buildCampaignRow(c, formHolder, listPanel));
                    listPanel.add(Box.createVerticalStrut(10));
                }
            }
            listPanel.revalidate();
            listPanel.repaint();
        };

        Runnable showCreateForm = () -> {
            formHolder.removeAll();
            formHolder.add(buildCampaignForm(null, formHolder, refreshList));
            formHolder.revalidate();
            formHolder.repaint();
        };

        JButton newBtn = new JButton("+ New Campaign");
        newBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        newBtn.setForeground(Color.WHITE);
        newBtn.setBackground(NEON);
        newBtn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        newBtn.setFocusPainted(false);
        newBtn.setOpaque(true);
        newBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        newBtn.addActionListener(e -> showCreateForm.run());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(LEFT_ALIGNMENT);
        btnRow.add(newBtn);

        outer.add(btnRow);
        outer.add(Box.createVerticalStrut(18));
        outer.add(formHolder);
        outer.add(Box.createVerticalStrut(10));

        JLabel listLabel = new JLabel("EXISTING CAMPAIGNS");
        listLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        listLabel.setForeground(new Color(255, 255, 255, 60));
        listLabel.setAlignmentX(LEFT_ALIGNMENT);
        outer.add(listLabel);
        outer.add(Box.createVerticalStrut(10));
        outer.add(listPanel);
        outer.add(Box.createVerticalGlue());

        refreshList.run();

        JScrollPane scroll = new JScrollPane(outer);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    /**
     * Converts a backend date string into dd/MM/yyyy format for display.
     */
    private String toDisplayDate(String dbDateTime) {
        if (dbDateTime == null || dbDateTime.length() < 10) return "";
        String raw = dbDateTime.substring(0, 10);
        String[] parts = raw.split("-");
        if (parts.length != 3) return raw;
        return parts[2] + "/" + parts[1] + "/" + parts[0];
    }

    /**
     * Builds the form used for creating or editing a campaign.
     */
    private JPanel buildCampaignForm(
            AdminCampaignData editCampaign,
            JPanel formHolder,
            Runnable onDone) {

        JPanel form = new JPanel();
        form.setOpaque(true);
        form.setBackground(PANEL);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setAlignmentX(LEFT_ALIGNMENT);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(37, 99, 168, 80), 1),
                BorderFactory.createEmptyBorder(20, 22, 20, 22)
        ));
        form.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel formTitle = new JLabel(editCampaign == null ? "Create New Campaign" : "Edit Campaign");
        formTitle.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
        formTitle.setForeground(Color.WHITE);
        formTitle.setAlignmentX(LEFT_ALIGNMENT);
        form.add(formTitle);
        form.add(Box.createVerticalStrut(16));

        form.add(makeFormLabel("Campaign Name"));
        JTextField nameFld = makeFormField(editCampaign != null ? editCampaign.campaignName : "");
        form.add(nameFld);
        form.add(Box.createVerticalStrut(12));

        JPanel datesRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        datesRow.setOpaque(false);
        datesRow.setAlignmentX(LEFT_ALIGNMENT);

        JLabel startLbl = makeFormLabel("Start Date (dd/MM/yyyy)");
        JTextField startFld = makeFormField(
                editCampaign != null ? toDisplayDate(editCampaign.startDatetime) : "");
        startFld.setPreferredSize(new Dimension(150, 34));
        startFld.setMaximumSize(new Dimension(150, 34));

        JLabel endLbl = makeFormLabel("End Date (dd/MM/yyyy)");
        JTextField endFld = makeFormField(
                editCampaign != null ? toDisplayDate(editCampaign.endDatetime) : "");
        endFld.setPreferredSize(new Dimension(150, 34));
        endFld.setMaximumSize(new Dimension(150, 34));

        JPanel startCol = new JPanel(); startCol.setOpaque(false);
        startCol.setLayout(new BoxLayout(startCol, BoxLayout.Y_AXIS));
        startCol.add(startLbl); startCol.add(Box.createVerticalStrut(4)); startCol.add(startFld);

        JPanel endCol = new JPanel(); endCol.setOpaque(false);
        endCol.setLayout(new BoxLayout(endCol, BoxLayout.Y_AXIS));
        endCol.add(endLbl); endCol.add(Box.createVerticalStrut(4)); endCol.add(endFld);

        datesRow.add(startCol);
        datesRow.add(endCol);
        form.add(datesRow);
        form.add(Box.createVerticalStrut(12));

        form.add(makeFormLabel("Status"));
        String[] statusOpts = { "ACTIVE", "SCHEDULED", "ENDED", "TERMINATED" };
        JComboBox<String> statusBox = new JComboBox<>(statusOpts);
        styleCombo(statusBox);
        if (editCampaign != null) statusBox.setSelectedItem(editCampaign.status);
        form.add(statusBox);
        form.add(Box.createVerticalStrut(16));

        form.add(makeFormLabel("Item Discounts"));
        form.add(Box.createVerticalStrut(6));

        JPanel itemRowsPanel = new JPanel();
        itemRowsPanel.setOpaque(false);
        itemRowsPanel.setLayout(new BoxLayout(itemRowsPanel, BoxLayout.Y_AXIS));
        itemRowsPanel.setAlignmentX(LEFT_ALIGNMENT);

        List<JComboBox<String>> combos = new ArrayList<>();
        List<JTextField> discFlds = new ArrayList<>();

        Runnable[] syncRef = { null };
        syncRef[0] = () -> {
            java.util.Set<String> used = new java.util.HashSet<>();
            for (JComboBox<String> cb : combos) {
                Object sel = cb.getSelectedItem();
                if (sel != null && !sel.toString().startsWith("--")) used.add(sel.toString());
            }
            for (JComboBox<String> cb : combos) {
                String cur = (String) cb.getSelectedItem();
                cb.removeAllItems();
                cb.addItem("-- Select item --");
                for (String n : itemNames) {
                    if (!used.contains(n) || n.equals(cur)) cb.addItem(n);
                }
                if (cur != null) cb.setSelectedItem(cur);
            }
        };

        Runnable[] addRowRef = { null };
        addRowRef[0] = () -> {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
            row.setOpaque(false);
            row.setAlignmentX(LEFT_ALIGNMENT);

            JComboBox<String> combo = new JComboBox<>();
            combo.addItem("-- Select item --");
            for (String n : itemNames) combo.addItem(n);
            styleCombo(combo);
            combo.setPreferredSize(new Dimension(220, 34));
            combo.addActionListener(e -> syncRef[0].run());

            JTextField discFld = makeFormField("");
            discFld.setPreferredSize(new Dimension(90, 34));
            discFld.setMaximumSize(new Dimension(90, 34));

            JLabel pctLbl = new JLabel("% off");
            pctLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            pctLbl.setForeground(new Color(255, 255, 255, 120));

            JButton removeBtn = new JButton("Remove");
            removeBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            removeBtn.setForeground(RED);
            removeBtn.setBackground(new Color(0x0a1020));
            removeBtn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(248, 113, 113, 80), 1),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)
            ));
            removeBtn.setFocusPainted(false);
            removeBtn.setOpaque(true);
            removeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            removeBtn.addActionListener(e -> {
                combos.remove(combo);
                discFlds.remove(discFld);
                itemRowsPanel.remove(row);
                itemRowsPanel.revalidate();
                itemRowsPanel.repaint();
                syncRef[0].run();
            });

            row.add(combo);
            row.add(discFld);
            row.add(pctLbl);
            row.add(removeBtn);

            combos.add(combo);
            discFlds.add(discFld);
            itemRowsPanel.add(row);
            itemRowsPanel.revalidate();
            itemRowsPanel.repaint();
            syncRef[0].run();
        };

        if (editCampaign != null && !editCampaign.items.isEmpty()) {
            for (AdminCampaignItemData entry : editCampaign.items) {
                addRowRef[0].run();
                int last = combos.size() - 1;
                combos.get(last).putClientProperty("campaignItemId", entry.campaignItemId);
                combos.get(last).setSelectedItem(entry.productName);
                discFlds.get(last).setText(String.valueOf((int) Math.round(entry.discountRate)));
            }
            syncRef[0].run();
        }

        form.add(itemRowsPanel);
        form.add(Box.createVerticalStrut(8));

        JButton addItemBtn = new JButton("+ Add Item");
        addItemBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        addItemBtn.setForeground(NEON_LT);
        addItemBtn.setBackground(new Color(0x0a1020));
        addItemBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(37, 99, 168, 80), 1),
                BorderFactory.createEmptyBorder(5, 14, 5, 14)
        ));
        addItemBtn.setFocusPainted(false);
        addItemBtn.setOpaque(true);
        addItemBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addItemBtn.setAlignmentX(LEFT_ALIGNMENT);
        addItemBtn.addActionListener(e -> addRowRef[0].run());
        form.add(addItemBtn);
        form.add(Box.createVerticalStrut(20));

        JButton saveBtn = new JButton(editCampaign == null ? "Save Campaign" : "Update Campaign");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setBackground(NEON);
        saveBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        saveBtn.setFocusPainted(false);
        saveBtn.setOpaque(true);
        saveBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cancelBtn.setForeground(new Color(255, 255, 255, 140));
        cancelBtn.setBackground(new Color(0x0a1020));
        cancelBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(37, 99, 168, 60), 1),
                BorderFactory.createEmptyBorder(7, 18, 7, 18)
        ));
        cancelBtn.setFocusPainted(false);
        cancelBtn.setOpaque(true);
        cancelBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelBtn.addActionListener(e -> {
            formHolder.removeAll();
            formHolder.revalidate();
            formHolder.repaint();
        });

        saveBtn.addActionListener(e -> {
            String cName = nameFld.getText().trim();
            if (cName.isEmpty()) {
                JOptionPane.showMessageDialog(AdminPage.this,
                        "Please enter a campaign name.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            java.time.LocalDate sd = null, ed = null;
            String sText = startFld.getText().trim();
            String eText = endFld.getText().trim();
            if (!sText.isEmpty()) {
                try { sd = java.time.LocalDate.parse(sText, ADMIN_DATE_FMT); }
                catch (java.time.format.DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(AdminPage.this,
                            "Invalid start date — use dd/MM/yyyy format.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            if (!eText.isEmpty()) {
                try { ed = java.time.LocalDate.parse(eText, ADMIN_DATE_FMT); }
                catch (java.time.format.DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(AdminPage.this,
                            "Invalid end date — use dd/MM/yyyy format.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            String status = (String) statusBox.getSelectedItem();

            LinkedHashMap<String, Double> discounts = new LinkedHashMap<>();
            for (int i = 0; i < combos.size(); i++) {
                String item = (String) combos.get(i).getSelectedItem();
                if (item == null || item.startsWith("--")) continue;
                String dText = discFlds.get(i).getText().trim();
                try {
                    double pct = Double.parseDouble(dText);
                    if (pct <= 0 || pct >= 100) continue;
                    discounts.put(item, pct);
                } catch (NumberFormatException ignored) {}
            }

            String startIso = sd.toString() + "T00:00:00";
            String endIso = ed.toString() + "T23:59:59";

            if (editCampaign == null) {
                Integer id = createCampaignOnBackend(
                        cName,
                        startIso,
                        endIso,
                        username,
                        status
                );

                if (id == null) {
                    JOptionPane.showMessageDialog(
                            AdminPage.this,
                            "Failed to create campaign.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                for (int i = 0; i < combos.size(); i++) {
                    String itemName = (String) combos.get(i).getSelectedItem();
                    if (itemName == null || itemName.startsWith("--")) continue;

                    String dText = discFlds.get(i).getText().trim();
                    try {
                        double pct = Double.parseDouble(dText);
                        if (pct <= 0 || pct >= 100) continue;

                        String productId = lookupItemIdByName(itemName);
                        if (productId != null) {
                            addCampaignItemOnBackend(id, productId, pct);
                        }
                    } catch (NumberFormatException ignored) {}
                }

                JOptionPane.showMessageDialog(
                        AdminPage.this,
                        "Campaign created successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } else {
                boolean ok = updateCampaignOnBackend(editCampaign.campaignId, cName, startIso, endIso, status);
                if (!ok) {
                    JOptionPane.showMessageDialog(
                            AdminPage.this,
                            "Failed to update campaign.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                java.util.Set<Integer> seenExistingIds = new java.util.HashSet<>();

                for (int i = 0; i < combos.size(); i++) {
                    String itemName = (String) combos.get(i).getSelectedItem();
                    if (itemName == null || itemName.startsWith("--")) continue;

                    String dText = discFlds.get(i).getText().trim();
                    double pct;
                    try {
                        pct = Double.parseDouble(dText);
                        if (pct <= 0 || pct >= 100) continue;
                    } catch (NumberFormatException ex) {
                        continue;
                    }

                    Object existingIdObj = combos.get(i).getClientProperty("campaignItemId");
                    if (existingIdObj instanceof Integer) {
                        Integer campaignItemId = (Integer) existingIdObj;
                        seenExistingIds.add(campaignItemId);
                        updateCampaignItemOnBackend(campaignItemId, pct);
                    } else {
                        String productId = lookupItemIdByName(itemName);
                        if (productId != null) {
                            addCampaignItemOnBackend(editCampaign.campaignId, productId, pct);
                        }
                    }
                }

                for (AdminCampaignItemData oldItem : editCampaign.items) {
                    if (!seenExistingIds.contains(oldItem.campaignItemId)) {
                        deleteCampaignItemOnBackend(oldItem.campaignItemId);
                    }
                }

                JOptionPane.showMessageDialog(
                        AdminPage.this,
                        "Campaign updated successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

            formHolder.removeAll();
            formHolder.revalidate();
            formHolder.repaint();
            onDone.run();
        });

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionRow.setOpaque(false);
        actionRow.setAlignmentX(LEFT_ALIGNMENT);
        actionRow.add(saveBtn);
        actionRow.add(cancelBtn);
        form.add(actionRow);

        return form;
    }

    /**
     * Builds a single campaign card for the promotions list.
     */
    private JPanel buildCampaignRow(
            AdminCampaignData c,
            JPanel formHolder,
            JPanel listPanel) {

        JPanel row = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PANEL);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(new Color(37, 99, 168, 55));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth()-1f, getHeight()-1f, 10, 10));
                g2.dispose();
            }
        };
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JPanel topLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        topLine.setOpaque(false);
        topLine.setAlignmentX(LEFT_ALIGNMENT);

        JLabel nameLbl = new JLabel(c.campaignName);
        nameLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 15));
        nameLbl.setForeground(Color.WHITE);

        Color statusColor = "ACTIVE".equals(c.status) ? GREEN
                : "ENDED".equals(c.status) ? AMBER : RED;
        JLabel statusBadge = new JLabel(c.status);
        statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 9));
        statusBadge.setForeground(statusColor);
        statusBadge.setOpaque(true);
        statusBadge.setBackground(new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), 30));
        statusBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), 100), 1),
                BorderFactory.createEmptyBorder(2, 7, 2, 7)
        ));

        String dateRange = "";
        if (c.startDatetime != null || c.endDatetime != null) {
            String s = c.startDatetime != null ? toDisplayDate(c.startDatetime) : "open";
            String e = c.endDatetime != null ? toDisplayDate(c.endDatetime) : "open";
            dateRange = s + "  to  " + e;
        }
        JLabel dateLbl = new JLabel(dateRange.isEmpty() ? "No dates set" : dateRange);
        dateLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dateLbl.setForeground(new Color(255, 255, 255, 70));

        topLine.add(nameLbl);
        topLine.add(statusBadge);
        topLine.add(dateLbl);
        row.add(topLine);
        row.add(Box.createVerticalStrut(6));

        StringBuilder itemsSb = new StringBuilder();
        for (AdminCampaignItemData item : c.items) {
            if (itemsSb.length() > 0) itemsSb.append("   |   ");
            itemsSb.append(item.productName)
                    .append(": ")
                    .append((int) Math.round(item.discountRate))
                    .append("% off");
        }

        JLabel itemsLbl = new JLabel(
                "<html><div style='width:700px'>"
                        + (itemsSb.length() > 0 ? itemsSb.toString() : "No item discounts set")
                        + "</div></html>");
        itemsLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        itemsLbl.setForeground(new Color(255, 255, 255, 120));
        itemsLbl.setAlignmentX(LEFT_ALIGNMENT);
        row.add(itemsLbl);
        row.add(Box.createVerticalStrut(8));

        JPanel bottomLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        bottomLine.setOpaque(false);
        bottomLine.setAlignmentX(LEFT_ALIGNMENT);

        JLabel statsLbl = new JLabel("Campaign ID: " + c.campaignId);
        statsLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statsLbl.setForeground(new Color(255, 255, 255, 180));

        JButton editBtn = makeTinyBtn("Edit", NEON_LT, new Color(37, 99, 168, 80));
        JButton deleteBtn = makeTinyBtn("Delete", RED, new Color(248, 113, 113, 60));

        editBtn.addActionListener(e -> {
            Runnable refreshList = () -> {
                listPanel.removeAll();
                List<AdminCampaignData> all = loadCampaignsFromBackend();
                if (all.isEmpty()) {
                    JLabel none = new JLabel("No campaigns created yet.");
                    none.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    none.setForeground(new Color(255, 255, 255, 55));
                    listPanel.add(none);
                } else {
                    for (AdminCampaignData camp : all) {
                        listPanel.add(buildCampaignRow(camp, formHolder, listPanel));
                        listPanel.add(Box.createVerticalStrut(10));
                    }
                }
                listPanel.revalidate();
                listPanel.repaint();
            };
            formHolder.removeAll();
            formHolder.add(buildCampaignForm(c, formHolder, refreshList));
            formHolder.revalidate();
            formHolder.repaint();
        });

        deleteBtn.addActionListener(e -> {
            int res = JOptionPane.showConfirmDialog(
                    AdminPage.this,
                    "Delete campaign \"" + c.campaignName + "\"? This cannot be undone.",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (res == JOptionPane.YES_OPTION) {
                boolean deleted = deleteCampaignFully(c.campaignId);
                if (!deleted) {
                    JOptionPane.showMessageDialog(
                            AdminPage.this,
                            "Failed to delete campaign.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                listPanel.removeAll();
                List<AdminCampaignData> all = loadCampaignsFromBackend();
                if (all.isEmpty()) {
                    JLabel none = new JLabel("No campaigns created yet. Click '+ New Campaign' to start.");
                    none.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    none.setForeground(new Color(255, 255, 255, 55));
                    listPanel.add(none);
                } else {
                    for (AdminCampaignData camp : all) {
                        listPanel.add(buildCampaignRow(camp, formHolder, listPanel));
                        listPanel.add(Box.createVerticalStrut(10));
                    }
                }
                listPanel.revalidate();
                listPanel.repaint();
            }
        });

        bottomLine.add(statsLbl);
        bottomLine.add(editBtn);
        bottomLine.add(deleteBtn);
        row.add(bottomLine);

        return row;
    }

    private JLabel makeFormLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(new Color(255, 255, 255, 120));
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }
    private JTextField makeFormField(String initial) {
        JTextField fld = new JTextField(initial);
        fld.setBackground(new Color(0x0b1220));
        fld.setForeground(new Color(255, 255, 255, 200));
        fld.setCaretColor(Color.WHITE);
        fld.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fld.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(37, 99, 168, 80), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        fld.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        fld.setAlignmentX(LEFT_ALIGNMENT);
        return fld;
    }

    private void styleCombo(JComboBox<String> cb) {
        cb.setBackground(new Color(0x0b1220));
        cb.setForeground(new Color(255, 255, 255, 200));
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cb.setBorder(BorderFactory.createLineBorder(new Color(37, 99, 168, 80), 1));
        cb.setAlignmentX(LEFT_ALIGNMENT);
    }

    private JButton makeTinyBtn(String label, Color fg, Color border) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btn.setForeground(fg);
        btn.setBackground(new Color(0x0a1020));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border, 1),
                BorderFactory.createEmptyBorder(3, 10, 3, 10)
        ));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Builds the tab bar used to switch between admin dashboard sections.
     */
    private JPanel buildTabBar() {
        JPanel bar = new JPanel();
        bar.setBackground(PANEL);
        bar.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(37, 99, 168, 60)));

        String[] labels = { "Promotions", "Sales Report", "Ad Campaigns", "Customer Engagement" };
        String[] keys   = { TAB_PROMOS, TAB_SALES, TAB_CAMPAIGNS, TAB_ENGAGE };

        for (int i = 0; i < labels.length; i++) {
            final String key = keys[i];
            final int    idx = i;
            JButton btn = new JButton(labels[i]);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            btn.setBackground(PANEL);
            btn.setFocusPainted(false);
            btn.setOpaque(true);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            applyTabStyle(btn, i == 0);
            tabBtns.add(btn);
            btn.addActionListener(e -> {
                cardLayout.show(mainContent, key);
                for (int j = 0; j < tabBtns.size(); j++)
                    applyTabStyle(tabBtns.get(j), j == idx);
            });
            bar.add(btn);
        }
        return bar;
    }

    private void applyTabStyle(JButton btn, boolean active) {
        btn.setForeground(active ? Color.WHITE : new Color(255, 255, 255, 100));
        btn.setBorder(BorderFactory.createCompoundBorder(
                active ? BorderFactory.createMatteBorder(0, 0, 2, 0, NEON_LT)
                        : BorderFactory.createEmptyBorder(0, 0, 2, 0),
                BorderFactory.createEmptyBorder(12, 20, 10, 20)
        ));
    }

    /**
     * Builds the sales report view and generates item sales data by date range.
     */
    private JScrollPane buildSalesReport() {
        JPanel outer = new JPanel();
        outer.setBackground(BG);
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBorder(BorderFactory.createEmptyBorder(24, 28, 32, 28));

        outer.add(buildReportHeader("REPORTS", "Sales Report",
                "Items sold across all paid orders. Filter by date range and click Generate."));
        outer.add(Box.createVerticalStrut(18));

        JTextField fromFld = new JTextField("01/03/2025", 10);
        JTextField toFld   = new JTextField(
                new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()), 10);
        styleReportField(fromFld);
        styleReportField(toFld);

        JButton genBtn = new JButton("Generate");
        genBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        genBtn.setForeground(Color.WHITE);
        genBtn.setBackground(NEON);
        genBtn.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        genBtn.setFocusPainted(false);
        genBtn.setOpaque(true);
        genBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel fromLbl = new JLabel("From:");
        fromLbl.setForeground(new Color(255, 255, 255, 120));
        fromLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JLabel toLbl = new JLabel("To:");
        toLbl.setForeground(new Color(255, 255, 255, 120));
        toLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        javax.swing.table.DefaultTableModel salesModel =
                new javax.swing.table.DefaultTableModel(new Object[0][0], new String[]{ "Item ID", "Description", "Sold, packs", "Unit price, £", "Total, £" }) {
                    @Override public boolean isCellEditable(int r, int c) { return false; }
                };
        JTable salesTbl = makeStyledTable(new String[]{ "Item ID", "Description", "Sold, packs", "Unit price, £", "Total, £" }, new Object[0][0]);
        salesTbl.setModel(salesModel);
        reapplyRenderer(salesTbl, 5);

        JPanel filterRow = new JPanel();
        filterRow.setOpaque(false);
        filterRow.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setAlignmentX(LEFT_ALIGNMENT);
        filterRow.add(fromLbl); filterRow.add(fromFld);
        filterRow.add(toLbl);   filterRow.add(toFld);
        filterRow.add(genBtn);
        filterRow.add(makePrintButton(salesTbl));
        outer.add(filterRow);
        outer.add(Box.createVerticalStrut(16));

        JScrollPane tblScroll = new JScrollPane(salesTbl);
        tblScroll.setBorder(BorderFactory.createLineBorder(new Color(37,99,168,40)));
        tblScroll.getViewport().setBackground(PANEL);
        tblScroll.setAlignmentX(LEFT_ALIGNMENT);
        tblScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel totalLbl = new JLabel("Total Revenue: £0.00");
        totalLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        totalLbl.setForeground(GREEN);
        totalLbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel noDataLbl = new JLabel("No sales data found for this period.");
        noDataLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        noDataLbl.setForeground(new Color(255, 255, 255, 70));
        noDataLbl.setAlignmentX(LEFT_ALIGNMENT);
        noDataLbl.setVisible(false);

        outer.add(tblScroll);
        outer.add(Box.createVerticalStrut(4));
        outer.add(noDataLbl);
        outer.add(Box.createVerticalStrut(8));
        outer.add(totalLbl);

        genBtn.addActionListener(ev -> {
            salesModel.setRowCount(0);
            noDataLbl.setVisible(false);

            java.util.Date from = parseDate(fromFld.getText().trim());
            java.util.Date to   = parseDate(toFld.getText().trim());

            if (from == null || to == null) {
                showErrorDialog("Please enter valid dates in dd/MM/yyyy format.");
                return;
            }

            double grandTotal = 0.0;

            String sql =
                    "SELECT " +
                            "    oi.product_id, " +
                            "    COALESCE(si.name, oi.product_description) AS item_name, " +
                            "    SUM(oi.quantity) AS sold_packs, " +
                            "    ROUND(MAX(COALESCE(si.price, oi.unit_price)), 2) AS unit_price, " +
                            "    ROUND(SUM(oi.line_total), 2) AS total_sales " +
                            "FROM ipos_pu.OrderItems oi " +
                            "JOIN ipos_pu.Orders o ON o.order_id = oi.order_id " +
                            "LEFT JOIN ipos_ca.stock_items si ON si.item_id = oi.product_id " +
                            "WHERE o.order_date BETWEEN ? AND ? " +
                            "AND EXISTS ( " +
                            "    SELECT 1 FROM ipos_pu.PaymentTransaction pt " +
                            "    WHERE pt.order_id = o.order_id AND pt.payment_status = 'SUCCESS' " +
                            ") " +
                            "GROUP BY oi.product_id, COALESCE(si.name, oi.product_description) " +
                            "ORDER BY item_name";

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setTimestamp(1, atStartOfDay(from));
                ps.setTimestamp(2, atEndOfDay(to));

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String itemId = rs.getString("product_id");
                        String desc   = rs.getString("item_name");
                        int sold      = rs.getInt("sold_packs");
                        double unit   = rs.getDouble("unit_price");
                        double total  = rs.getDouble("total_sales");

                        salesModel.addRow(new Object[]{
                                itemId,
                                desc,
                                sold,
                                String.format("%.2f", unit),
                                String.format("%.2f", total)
                        });

                        grandTotal += total;
                    }
                }

                totalLbl.setText(String.format("Total Revenue: £%.2f", grandTotal));
                if (salesModel.getRowCount() == 0) noDataLbl.setVisible(true);

            } catch (Exception ex) {
                showErrorDialog("Could not generate sales report: " + ex.getMessage());
            }
        });

        JScrollPane scroll = new JScrollPane(outer);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    /**
     * Builds the campaign report view showing campaign dates, discounts, and sales.
     */
    private JScrollPane buildCampaigns() {
        JPanel outer = new JPanel();
        outer.setBackground(BG);
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBorder(BorderFactory.createEmptyBorder(24, 28, 32, 28));

        outer.add(buildReportHeader("REPORTS", "Ad Campaigns Report",
                "Overview of all promotional campaigns, their schedules, discounts, and campaign-linked sales."));
        outer.add(Box.createVerticalStrut(18));

        JButton refreshBtn = makeRefreshButton();
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnRow.setOpaque(false); btnRow.setAlignmentX(LEFT_ALIGNMENT); btnRow.add(refreshBtn);
        outer.add(btnRow);
        outer.add(Box.createVerticalStrut(12));

        String[] cols = { "Campaign", "Item Discounts", "Start Date", "End Date", "Status", "Items sold" };
        Object[][] rows = buildCampaignRows();
        JTable tbl = makeStyledTable(cols, rows);

        btnRow.add(Box.createHorizontalStrut(10));
        btnRow.add(makePrintButton(tbl));

        JScrollPane tblScroll = new JScrollPane(tbl);
        tblScroll.setBorder(BorderFactory.createLineBorder(new Color(37,99,168,40)));
        tblScroll.getViewport().setBackground(PANEL);
        tblScroll.setAlignmentX(LEFT_ALIGNMENT);
        tblScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        outer.add(tblScroll);

        refreshBtn.addActionListener(ev -> {
            javax.swing.table.DefaultTableModel m = (javax.swing.table.DefaultTableModel) tbl.getModel();
            m.setRowCount(0);
            for (Object[] r : buildCampaignRows()) m.addRow(r);
        });

        JScrollPane scroll = new JScrollPane(outer);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }


    /**
     * Builds the row data used in the campaign report table.
     */
    private Object[][] buildCampaignRows() {
        List<Object[]> rows = new ArrayList<>();

        String sql =
                "SELECT " +
                        "    pc.campaign_id, " +
                        "    pc.campaign_name, " +
                        "    COALESCE(GROUP_CONCAT(DISTINCT CONCAT(si.name, ' (', ROUND(pci.discount_rate, 0), '%)') ORDER BY si.name SEPARATOR ', '), '—') AS item_discounts, " +
                        "    DATE_FORMAT(pc.start_datetime, '%d/%m/%Y') AS start_date, " +
                        "    DATE_FORMAT(pc.end_datetime, '%d/%m/%Y') AS end_date, " +
                        "    pc.status, " +
                        "   COALESCE(SUM(\n" +
                        "    CASE WHEN pt.order_id IS NOT NULL THEN oi.quantity ELSE 0 END\n" +
                        "), 0) AS purchases " +
                        "FROM ipos_pu.PromotionCampaign pc " +
                        "LEFT JOIN ipos_pu.PromotionCampaignItems pci ON pc.campaign_id = pci.campaign_id " +
                        "LEFT JOIN ipos_ca.stock_items si ON si.item_id = pci.product_id " +
                        "LEFT JOIN ipos_pu.OrderItems oi \n" +
                        "    ON oi.campaign_id = pc.campaign_id\n" +
                        "LEFT JOIN ipos_pu.Orders o \n" +
                        "    ON o.order_id = oi.order_id\n" +
                        "LEFT JOIN ipos_pu.PaymentTransaction pt \n" +
                        "    ON pt.order_id = o.order_id \n" +
                        "    AND pt.payment_status = 'SUCCESS' " +
                        "GROUP BY pc.campaign_id, pc.campaign_name, pc.start_datetime, pc.end_datetime, pc.status " +
                        "ORDER BY pc.start_datetime DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int campaignId = rs.getInt("campaign_id");
                String campaignName = rs.getString("campaign_name");
                String itemDiscounts = rs.getString("item_discounts");
                String startDate = rs.getString("start_date");
                String endDate = rs.getString("end_date");
                String status = rs.getString("status");
                int purchases = rs.getInt("purchases");

                rows.add(new Object[]{
                        "Camp " + String.format("%02d", campaignId) + " - " + campaignName,
                        itemDiscounts,
                        startDate,
                        endDate,
                        status,
                        purchases
                });
            }

        } catch (Exception ex) {
            return new Object[0][0];
        }

        Object[][] arr = new Object[rows.size()][6];
        for (int i = 0; i < rows.size(); i++) arr[i] = rows.get(i);
        return arr;
    }

    /**
     * Builds the customer engagement report view.
     */
    private JScrollPane buildEngagement() {
        JPanel outer = new JPanel();
        outer.setBackground(BG);
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBorder(BorderFactory.createEmptyBorder(24, 28, 32, 28));

        outer.add(buildReportHeader("REPORTS", "Customer Engagement Report",
                "Campaign hit counts, purchases, and conversion rate per promotion."));
        outer.add(Box.createVerticalStrut(18));

        JButton refreshBtn = makeRefreshButton();
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnRow.setOpaque(false); btnRow.setAlignmentX(LEFT_ALIGNMENT); btnRow.add(refreshBtn);
        outer.add(btnRow);
        outer.add(Box.createVerticalStrut(12));

        String[] cols = { "Campaign", "Hits", "Purchases", "Conversion Rate" };
        JTable tbl = makeStyledTable(cols, buildEngagementRows());

        btnRow.add(Box.createHorizontalStrut(10));
        btnRow.add(makePrintButton(tbl));

        JScrollPane tblScroll = new JScrollPane(tbl);
        tblScroll.setBorder(BorderFactory.createLineBorder(new Color(37,99,168,40)));
        tblScroll.getViewport().setBackground(PANEL);
        tblScroll.setAlignmentX(LEFT_ALIGNMENT);
        tblScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        outer.add(tblScroll);

        refreshBtn.addActionListener(ev -> {
            javax.swing.table.DefaultTableModel m = (javax.swing.table.DefaultTableModel) tbl.getModel();
            m.setRowCount(0);
            for (Object[] r : buildEngagementRows()) m.addRow(r);
        });

        JScrollPane scroll = new JScrollPane(outer);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    /**
     * Builds the row data used in the engagement report table.
     */
    private Object[][] buildEngagementRows() {
        List<Object[]> rows = new ArrayList<>();

        String sql =
                "SELECT " +
                        "    pc.campaign_id, " +
                        "    pc.campaign_name, " +
                        "    COALESCE(hit_data.hits, 0) AS hits, " +
                        "    COALESCE(purchase_data.purchases, 0) AS purchases " +
                        "FROM ipos_pu.PromotionCampaign pc " +
                        "LEFT JOIN ( " +
                        "    SELECT campaign_id, COUNT(*) AS hits " +
                        "    FROM ipos_pu.ActivityLog " +
                        "   WHERE event_type IN ('CAMPAIGN_VIEW', 'ITEM_VIEW', 'ADD_TO_CART') " +
                        "    GROUP BY campaign_id " +
                        ") hit_data ON hit_data.campaign_id = pc.campaign_id " +
                        "LEFT JOIN ( " +
                        "    SELECT campaign_id, SUM(quantity) AS purchases " +
                        "    FROM ipos_pu.OrderItems " +
                        "    WHERE campaign_id IS NOT NULL " +
                        "    GROUP BY campaign_id " +
                        ") purchase_data ON purchase_data.campaign_id = pc.campaign_id " +
                        "ORDER BY pc.start_datetime DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int campaignId = rs.getInt("campaign_id");
                String campaignName = rs.getString("campaign_name");
                int hits = rs.getInt("hits");
                int purchases = rs.getInt("purchases");
                String conversion = hits > 0
                        ? String.format("%.1f%%", (purchases / (double) hits) * 100.0)
                        : "—";

                rows.add(new Object[]{
                        "Camp " + String.format("%02d", campaignId) + " - " + campaignName,
                        hits,
                        purchases,
                        conversion
                });
            }

        } catch (Exception ex) {
            return new Object[0][0];
        }

        Object[][] arr = new Object[rows.size()][4];
        for (int i = 0; i < rows.size(); i++) arr[i] = rows.get(i);
        return arr;
    }

    /**
     * Builds a reusable header block for the report sections.
     */
    private JPanel buildReportHeader(String sectionLabel, String title, String subtitle) {
        JPanel hdr = new JPanel();
        hdr.setOpaque(false);
        hdr.setLayout(new BoxLayout(hdr, BoxLayout.Y_AXIS));
        hdr.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sectionLbl = new JLabel(sectionLabel);
        sectionLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        sectionLbl.setForeground(NEON);
        sectionLbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 22));
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLbl.setForeground(new Color(255, 255, 255, 80));
        subLbl.setAlignmentX(LEFT_ALIGNMENT);

        hdr.add(sectionLbl);
        hdr.add(Box.createVerticalStrut(4));
        hdr.add(titleLbl);
        hdr.add(Box.createVerticalStrut(6));
        hdr.add(subLbl);
        return hdr;
    }

    /**
     * Creates a table with the shared admin report styling.
     */
    private JTable makeStyledTable(String[] cols, Object[][] rows) {
        javax.swing.table.DefaultTableModel model =
                new javax.swing.table.DefaultTableModel(rows, cols) {
                    @Override public boolean isCellEditable(int r, int c) { return false; }
                };
        JTable tbl = new JTable(model);
        tbl.setBackground(PANEL);
        tbl.setForeground(new Color(255, 255, 255, 200));
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tbl.setRowHeight(28);
        tbl.setGridColor(new Color(37, 99, 168, 25));
        tbl.setShowVerticalLines(false);
        tbl.setIntercellSpacing(new Dimension(0, 0));
        tbl.setSelectionBackground(new Color(37, 99, 168, 60));
        tbl.setSelectionForeground(Color.WHITE);
        tbl.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        javax.swing.table.JTableHeader hdr = tbl.getTableHeader();
        hdr.setBackground(BG);
        hdr.setForeground(NEON_LT);
        hdr.setFont(new Font("Segoe UI", Font.BOLD, 11));
        hdr.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(37, 99, 168, 60)));
        hdr.setReorderingAllowed(false);

        reapplyRenderer(tbl, cols.length);
        return tbl;
    }

    private void reapplyRenderer(JTable tbl, int colCount) {
        javax.swing.table.DefaultTableCellRenderer rend = new javax.swing.table.DefaultTableCellRenderer() {
            @Override public java.awt.Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) {
                    setBackground(row % 2 == 0 ? PANEL : new Color(0x0b1220));
                    setForeground(new Color(255, 255, 255, 200));
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        };
        for (int i = 0; i < colCount; i++) tbl.getColumnModel().getColumn(i).setCellRenderer(rend);
    }

    private JButton makeRefreshButton() {
        JButton btn = new JButton("Refresh");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(NEON_LT);
        btn.setBackground(new Color(0x0a1020));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(37, 99, 168, 80), 1),
                BorderFactory.createEmptyBorder(5, 14, 5, 14)
        ));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton makePrintButton(JTable tbl) {
        JButton btn = new JButton("Print");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(Color.WHITE);
        btn.setBackground(NEON);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(37, 99, 168, 140), 1),
                BorderFactory.createEmptyBorder(5, 14, 5, 14)
        ));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(ev -> showPrintDialog(tbl));
        return btn;
    }

    /**
     * Shows a confirmation dialog before printing a report table.
     */
    private void showPrintDialog(JTable tbl) {
        JDialog dlg = new JDialog(this, "Print Report", true);
        dlg.setUndecorated(true);
        dlg.setSize(380, 200);
        dlg.setLocationRelativeTo(this);

        JPanel root = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0x080e1a));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(new Color(37, 99, 168, 80));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.dispose();
            }
        };
        root.setOpaque(false);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(BorderFactory.createEmptyBorder(28, 32, 24, 32));

        JLabel title = new JLabel("Print Report");
        title.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("This will send the table to your printer.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(255, 255, 255, 110));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        cancelBtn.setForeground(new Color(0x7eb8f7));
        cancelBtn.setBackground(new Color(0x0a1020));
        cancelBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(37, 99, 168, 80), 1),
                BorderFactory.createEmptyBorder(7, 18, 7, 18)));
        cancelBtn.setFocusPainted(false);
        cancelBtn.setOpaque(true);
        cancelBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelBtn.addActionListener(e -> dlg.dispose());

        JButton printBtn = new JButton("Print");
        printBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        printBtn.setForeground(Color.WHITE);
        printBtn.setBackground(NEON);
        printBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(37, 99, 168, 180), 1),
                BorderFactory.createEmptyBorder(7, 18, 7, 18)));
        printBtn.setFocusPainted(false);
        printBtn.setOpaque(true);
        printBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        printBtn.addActionListener(e -> {
            dlg.dispose();
            try {
                tbl.print(JTable.PrintMode.FIT_WIDTH,
                        new java.text.MessageFormat("IPOS-PU Report"),
                        new java.text.MessageFormat("Page {0}"));
            } catch (java.awt.print.PrinterException ex) {
                showErrorDialog("Print failed: " + ex.getMessage());
            }
        });

        btnRow.add(cancelBtn);
        btnRow.add(printBtn);

        root.add(title);
        root.add(Box.createVerticalStrut(8));
        root.add(sub);
        root.add(Box.createVerticalGlue());
        root.add(Box.createVerticalStrut(24));
        root.add(btnRow);

        dlg.setContentPane(root);
        dlg.getRootPane().setDefaultButton(printBtn);
        dlg.setVisible(true);
    }

    /**
     * Shows a styled error dialog for user-facing validation or runtime issues.
     */
    private void showErrorDialog(String message) {
        JDialog dlg = new JDialog(this, "Error", true);
        dlg.setUndecorated(true);
        dlg.setSize(360, 160);
        dlg.setLocationRelativeTo(this);

        JPanel root = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0x080e1a));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(new Color(248, 113, 113, 100));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.dispose();
            }
        };
        root.setOpaque(false);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(BorderFactory.createEmptyBorder(24, 28, 20, 28));

        JLabel msg = new JLabel("<html>" + message + "</html>");
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        msg.setForeground(new Color(0xf87171));
        msg.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton ok = new JButton("OK");
        ok.setFont(new Font("Segoe UI", Font.BOLD, 11));
        ok.setForeground(Color.WHITE);
        ok.setBackground(NEON);
        ok.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(37,99,168,180),1),
                BorderFactory.createEmptyBorder(6,18,6,18)));
        ok.setFocusPainted(false);
        ok.setOpaque(true);
        ok.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        ok.addActionListener(ev -> dlg.dispose());

        javax.swing.JPanel inner = new javax.swing.JPanel();
        inner.setLayout(new javax.swing.BoxLayout(inner, javax.swing.BoxLayout.Y_AXIS));
        inner.setBackground(new java.awt.Color(0x0a1018));
        inner.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(37, 99, 168, 80), 1),
                javax.swing.BorderFactory.createEmptyBorder(20, 24, 20, 24)));
        msg.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        inner.add(msg);
        inner.add(javax.swing.Box.createVerticalStrut(16));
        javax.swing.JPanel btnRow = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));
        btnRow.setOpaque(false);
        btnRow.add(ok);
        inner.add(btnRow);

        dlg.setContentPane(inner);
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private void styleReportField(javax.swing.JTextField f) {
        f.setBackground(new java.awt.Color(0x0b1220));
        f.setForeground(new java.awt.Color(255, 255, 255, 160));
        f.setCaretColor(NEON_LT);
        f.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        f.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(37, 99, 168, 80), 1),
                javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    }

    private java.util.Date parseDate(String text) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            return sdf.parse(text);
        } catch (Exception ex) {
            return null;
        }
    }

    private Timestamp atStartOfDay(java.util.Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return new Timestamp(cal.getTimeInMillis());
    }

    private Timestamp atEndOfDay(java.util.Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return new Timestamp(cal.getTimeInMillis());
    }
}