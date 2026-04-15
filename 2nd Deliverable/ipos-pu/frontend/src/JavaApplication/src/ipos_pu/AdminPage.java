/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ipos_pu;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

/**
 *
 * @author nuhur
 */
public class AdminPage extends javax.swing.JFrame {

    // colour palette
    private static final Color BG      = new Color(0x05080f);
    private static final Color PANEL   = new Color(0x080e1a);
    private static final Color NEON    = new Color(0x2563A8);
    private static final Color NEON_LT = new Color(0x7eb8f7);
    private static final Color GREEN   = new Color(0x4ade80);
    private static final Color RED     = new Color(0xf87171);
    private static final Color AMBER   = new Color(0xfbbf24);

    // card layout and the panel it controls for switching between the four sections
    private CardLayout cardLayout;
    private JPanel     mainContent;
    // list of all tab buttons so we can update which one looks selected
    private final List<JButton> tabBtns = new ArrayList<>();
    // keys used to identify each panel in the card layout
    private static final String TAB_PROMOS    = "promos";
    private static final String TAB_SALES     = "sales";
    private static final String TAB_CAMPAIGNS = "campaigns";
    private static final String TAB_ENGAGE    = "engagement";

    // item names loaded from db for the campaign form dropdowns
    private List<String> itemNames = new ArrayList<>();

    public AdminPage() {
        initComponents();
        buildUI();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        pack();
    }// </editor-fold>//GEN-END:initComponents

        private void buildUI() {
        setTitle("IPOS-PU \u2014 Admin Panel");
        setSize(1280, 760);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setBackground(BG);
        getContentPane().setLayout(new BorderLayout());

        // load item names from db once when admin page opens
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

    // loads all item names from the stock_items table so the campaign form dropdowns are populated
    private List<String> loadItemNames() {
        List<String> names = new ArrayList<>();
        try (java.sql.Connection con = DBConnection.getConnection();
             java.sql.Statement st = con.createStatement();
             java.sql.ResultSet rs = st.executeQuery("SELECT name FROM ipos_ca.stock_items ORDER BY name")) {
            while (rs.next()) names.add(rs.getString("name"));
        } catch (java.sql.SQLException ex) {
            // db might not be available, form will show empty dropdown
        }
        return names;
    }

    // top nav with brand badge and sign out button
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

    private JScrollPane buildPromotionsTab() {
        JPanel outer = new JPanel();
        outer.setBackground(BG);
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBorder(BorderFactory.createEmptyBorder(28, 32, 32, 32));

        outer.add(buildReportHeader("ADMIN PANEL", "Promotions Management",
            "Create campaigns from scratch with per-item discounts. No limit on number of campaigns."));
        outer.add(Box.createVerticalStrut(20));

        // the form panel — hidden until new campaign or edit is clicked
        JPanel formHolder = new JPanel();
        formHolder.setOpaque(false);
        formHolder.setLayout(new BoxLayout(formHolder, BoxLayout.Y_AXIS));
        formHolder.setAlignmentX(LEFT_ALIGNMENT);

        // campaign list panel — rebuilt whenever a campaign is added/edited/deleted
        JPanel listPanel = new JPanel();
        listPanel.setOpaque(false);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setAlignmentX(LEFT_ALIGNMENT);

        // callback to rebuild the list
        Runnable refreshList = () -> {
            listPanel.removeAll();
            List<PromoManager.Campaign> all = PromoManager.getCampaigns();
            if (all.isEmpty()) {
                JLabel none = new JLabel("No campaigns created yet. Click '+ New Campaign' to start.");
                none.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                none.setForeground(new Color(255, 255, 255, 55));
                none.setAlignmentX(LEFT_ALIGNMENT);
                listPanel.add(none);
            } else {
                for (PromoManager.Campaign c : all) {
                    listPanel.add(buildCampaignRow(c, formHolder, listPanel));
                    listPanel.add(Box.createVerticalStrut(10));
                }
            }
            listPanel.revalidate();
            listPanel.repaint();
        };

        // helper that shows a blank create form in formHolder
        Runnable showCreateForm = () -> {
            formHolder.removeAll();
            formHolder.add(buildCampaignForm(null, formHolder, refreshList));
            formHolder.revalidate();
            formHolder.repaint();
        };

        // + New Campaign button
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

        // populate on first load
        refreshList.run();

        JScrollPane scroll = new JScrollPane(outer);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    // builds the create/edit campaign form
    // editCampaign is null when creating a new one
    private JPanel buildCampaignForm(
            PromoManager.Campaign editCampaign,
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

        // campaign name
        form.add(makeFormLabel("Campaign Name"));
        JTextField nameFld = makeFormField(editCampaign != null ? editCampaign.name : "");
        form.add(nameFld);
        form.add(Box.createVerticalStrut(12));

        // dates row
        JPanel datesRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        datesRow.setOpaque(false);
        datesRow.setAlignmentX(LEFT_ALIGNMENT);

        JLabel startLbl = makeFormLabel("Start Date (dd/MM/yyyy)");
        JTextField startFld = makeFormField(
            editCampaign != null && editCampaign.startDate != null
                ? editCampaign.startDate.format(PromoManager.DATE_FMT) : "");
        startFld.setPreferredSize(new Dimension(150, 34));
        startFld.setMaximumSize(new Dimension(150, 34));

        JLabel endLbl = makeFormLabel("End Date (dd/MM/yyyy)");
        JTextField endFld = makeFormField(
            editCampaign != null && editCampaign.endDate != null
                ? editCampaign.endDate.format(PromoManager.DATE_FMT) : "");
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

        // status dropdown
        form.add(makeFormLabel("Status"));
        String[] statusOpts = { "ACTIVE", "ENDED", "TERMINATED" };
        JComboBox<String> statusBox = new JComboBox<>(statusOpts);
        styleCombo(statusBox);
        if (editCampaign != null) statusBox.setSelectedItem(editCampaign.status);
        form.add(statusBox);
        form.add(Box.createVerticalStrut(16));

        // item discount rows
        form.add(makeFormLabel("Item Discounts"));
        form.add(Box.createVerticalStrut(6));

        JPanel itemRowsPanel = new JPanel();
        itemRowsPanel.setOpaque(false);
        itemRowsPanel.setLayout(new BoxLayout(itemRowsPanel, BoxLayout.Y_AXIS));
        itemRowsPanel.setAlignmentX(LEFT_ALIGNMENT);

        List<JComboBox<String>> combos = new ArrayList<>();
        List<JTextField> discFlds = new ArrayList<>();

        // syncCombos: keeps each combobox showing only items not already picked by another row
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

        // adds a new item discount row to the form, optionally pre-filled
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

        // if editing, pre-fill existing item rows
        if (editCampaign != null && !editCampaign.itemDiscounts.isEmpty()) {
            for (java.util.Map.Entry<String, Double> entry : editCampaign.itemDiscounts.entrySet()) {
                addRowRef[0].run();
                // set the item and discount on the row just added
                int last = combos.size() - 1;
                combos.get(last).setSelectedItem(entry.getKey());
                discFlds.get(last).setText(String.valueOf((int) Math.round(entry.getValue())));
            }
            syncRef[0].run();
        }

        form.add(itemRowsPanel);
        form.add(Box.createVerticalStrut(8));

        // add item row button
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

        // save / cancel buttons
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
            // validate name
            String cName = nameFld.getText().trim();
            if (cName.isEmpty()) {
                JOptionPane.showMessageDialog(AdminPage.this,
                    "Please enter a campaign name.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // parse dates (optional)
            java.time.LocalDate sd = null, ed = null;
            String sText = startFld.getText().trim();
            String eText = endFld.getText().trim();
            if (!sText.isEmpty()) {
                try { sd = java.time.LocalDate.parse(sText, PromoManager.DATE_FMT); }
                catch (java.time.format.DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(AdminPage.this,
                        "Invalid start date — use dd/MM/yyyy format.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            if (!eText.isEmpty()) {
                try { ed = java.time.LocalDate.parse(eText, PromoManager.DATE_FMT); }
                catch (java.time.format.DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(AdminPage.this,
                        "Invalid end date — use dd/MM/yyyy format.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            String status = (String) statusBox.getSelectedItem();

            // build item discount map — skip rows with no item or invalid discount
            LinkedHashMap<String, Double> discounts = new LinkedHashMap<>();
            for (int i = 0; i < combos.size(); i++) {
                String item = (String) combos.get(i).getSelectedItem();
                if (item == null || item.startsWith("--")) continue;
                String dText = discFlds.get(i).getText().trim();
                try {
                    double pct = Double.parseDouble(dText);
                    if (pct <= 0 || pct >= 100) continue; // ignore bad values
                    discounts.put(item, pct);
                } catch (NumberFormatException ignored) {}
            }

            if (editCampaign == null) {
                // create new campaign
                PromoManager.Campaign c = new PromoManager.Campaign(
                    PromoManager.generateId(), cName, sd, ed, status);
                c.itemDiscounts.putAll(discounts);
                PromoManager.addCampaign(c);
            } else {
                // update existing campaign in place
                editCampaign.name      = cName;
                editCampaign.startDate = sd;
                editCampaign.endDate   = ed;
                editCampaign.status    = status;
                editCampaign.itemDiscounts.clear();
                editCampaign.itemDiscounts.putAll(discounts);
            }

            // hide form and refresh the list
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

    // builds a single campaign display row with edit, terminate and delete buttons
    private JPanel buildCampaignRow(
            PromoManager.Campaign c,
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

        // top line: name + status badge + dates
        JPanel topLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        topLine.setOpaque(false);
        topLine.setAlignmentX(LEFT_ALIGNMENT);

        JLabel nameLbl = new JLabel(c.name);
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
        if (c.startDate != null || c.endDate != null) {
            String s = c.startDate != null ? c.startDate.format(PromoManager.DATE_FMT) : "open";
            String e = c.endDate   != null ? c.endDate.format(PromoManager.DATE_FMT)   : "open";
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

        // items line: shows per-item discounts
        StringBuilder itemsSb = new StringBuilder();
        for (java.util.Map.Entry<String, Double> entry : c.itemDiscounts.entrySet()) {
            if (itemsSb.length() > 0) itemsSb.append("   |   ");
            itemsSb.append(entry.getKey())
                   .append(": ").append((int) Math.round(entry.getValue())).append("% off");
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

        // stats + action buttons
        JPanel bottomLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        bottomLine.setOpaque(false);
        bottomLine.setAlignmentX(LEFT_ALIGNMENT);

        JLabel statsLbl = new JLabel("Hits: " + c.hits + "   Purchases: " + c.purchases);
        statsLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statsLbl.setForeground(new Color(255, 255, 255, 180));

        JButton editBtn = makeTinyBtn("Edit", NEON_LT, new Color(37, 99, 168, 80));
        JButton deleteBtn = makeTinyBtn("Delete", RED, new Color(248, 113, 113, 60));

        editBtn.addActionListener(e -> {
            // show edit form pre-filled with this campaign's data
            Runnable refreshList = () -> {
                listPanel.removeAll();
                List<PromoManager.Campaign> all = PromoManager.getCampaigns();
                if (all.isEmpty()) {
                    JLabel none = new JLabel("No campaigns created yet.");
                    none.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    none.setForeground(new Color(255, 255, 255, 55));
                    listPanel.add(none);
                } else {
                    for (PromoManager.Campaign camp : all) {
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
            int res = JOptionPane.showConfirmDialog(AdminPage.this,
                "Delete campaign \"" + c.name + "\"? This cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (res == JOptionPane.YES_OPTION) {
                PromoManager.removeCampaign(c.id);
                // rebuild list directly
                listPanel.removeAll();
                List<PromoManager.Campaign> all = PromoManager.getCampaigns();
                if (all.isEmpty()) {
                    JLabel none = new JLabel("No campaigns created yet. Click '+ New Campaign' to start.");
                    none.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    none.setForeground(new Color(255, 255, 255, 55));
                    listPanel.add(none);
                } else {
                    for (PromoManager.Campaign camp : all) {
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

    private JScrollPane buildSalesReport() {
        JPanel outer = new JPanel();
        outer.setBackground(BG);
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBorder(BorderFactory.createEmptyBorder(24, 28, 32, 28));

        outer.add(buildReportHeader("REPORTS", "Sales Report",
            "Items sold across all orders. Filter by date range and click Generate."));
        outer.add(Box.createVerticalStrut(18));

        JTextField fromFld = new JTextField("01/03/2026", 10);
        JTextField toFld   = new JTextField(
            new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()), 10);
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
            new javax.swing.table.DefaultTableModel(new Object[0][0], new String[]{ "Product", "Qty Sold", "Unit Price", "Line Total" }) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
        JTable salesTbl = makeStyledTable(new String[]{ "Product", "Qty Sold", "Unit Price", "Line Total" }, new Object[0][0]);
        salesTbl.setModel(salesModel);
        reapplyRenderer(salesTbl, 4);

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

        JLabel totalLbl = new JLabel("Total Revenue: \u00a30.00");
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
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            java.util.Date from = null, to = null;
            try { from = sdf.parse(fromFld.getText().trim()); } catch (Exception ex) { }
            try { to   = sdf.parse(toFld.getText().trim()); }   catch (Exception ex) { }

            java.util.LinkedHashMap<String, Object[]> agg = new java.util.LinkedHashMap<>();
            for (OrderManager.Order ord : OrderManager.getOrders()) {
                java.util.Date ordDate = null;
                try { ordDate = sdf.parse(ord.date); } catch (Exception ex) { }
                if (ordDate == null) continue;
                if (from != null && ordDate.before(from)) continue;
                if (to   != null && ordDate.after(to))   continue;
                for (CartManager.CartItem it : ord.items) {
                    Object[] r = agg.getOrDefault(it.name,
                        new Object[]{ it.name, 0, it.unitPrice, 0.0 });
                    r[1] = (int)r[1] + it.qty;
                    r[3] = Math.round(((double)r[3] + it.unitPrice * it.qty) * 100.0) / 100.0;
                    agg.put(it.name, r);
                }
            }
            double grandTotal = 0;
            for (Object[] r : agg.values()) {
                r[2] = String.format("\u00a3%.2f", r[2]);
                r[3] = String.format("\u00a3%.2f", r[3]);
                grandTotal += Double.parseDouble(r[3].toString().replace("\u00a3",""));
                salesModel.addRow(r);
            }
            totalLbl.setText(String.format("Total Revenue: \u00a3%.2f", grandTotal));
            if (salesModel.getRowCount() == 0) noDataLbl.setVisible(true);
        });

        JScrollPane scroll = new JScrollPane(outer);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JScrollPane buildCampaigns() {
        JPanel outer = new JPanel();
        outer.setBackground(BG);
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBorder(BorderFactory.createEmptyBorder(24, 28, 32, 28));

        outer.add(buildReportHeader("REPORTS", "Ad Campaigns Report",
            "Overview of all promotional campaigns, their schedules, and sales."));
        outer.add(Box.createVerticalStrut(18));

        JButton refreshBtn = makeRefreshButton();
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnRow.setOpaque(false); btnRow.setAlignmentX(LEFT_ALIGNMENT); btnRow.add(refreshBtn);
        outer.add(btnRow);
        outer.add(Box.createVerticalStrut(12));

        String[] cols = { "Campaign", "Item Discounts", "Start Date", "End Date", "Status", "Purchases" };
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

    private Object[][] buildCampaignRows() {
        List<PromoManager.Campaign> all = PromoManager.getCampaigns();
        Object[][] rows = new Object[all.size()][6];
        for (int i = 0; i < all.size(); i++) {
            PromoManager.Campaign c = all.get(i);
            StringBuilder sb = new StringBuilder();
            for (java.util.Map.Entry<String, Double> e : c.itemDiscounts.entrySet()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(e.getKey()).append(" (").append((int)Math.round(e.getValue())).append("%)");
            }
            rows[i][0] = c.name;
            rows[i][1] = sb.length() > 0 ? sb.toString() : "\u2014";
            rows[i][2] = c.startDate != null ? c.startDate.format(PromoManager.DATE_FMT) : "\u2014";
            rows[i][3] = c.endDate   != null ? c.endDate.format(PromoManager.DATE_FMT)   : "\u2014";
            rows[i][4] = c.status;
            rows[i][5] = c.purchases;
        }
        return rows;
    }

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

    private Object[][] buildEngagementRows() {
        List<PromoManager.Campaign> all = PromoManager.getCampaigns();
        Object[][] rows = new Object[all.size()][4];
        for (int i = 0; i < all.size(); i++) {
            PromoManager.Campaign c = all.get(i);
            int h = c.hits, p = c.purchases;
            String conv = h > 0 ? String.format("%.1f%%", (p / (double) h) * 100.0) : "\u2014";
            rows[i][0] = c.name;
            rows[i][1] = h;
            rows[i][2] = p;
            rows[i][3] = conv;
        }
        return rows;
    }

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

    // applies the dark row renderer after a model swap
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

}
