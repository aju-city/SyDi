-- ─────────────────────────────────────────────
-- IPOS-CA STOCK ITEMS SEED DATA (from case study)
-- ─────────────────────────────────────────────

USE ipos_ca;

INSERT INTO stock_items (item_id, name, description, package_type, unit, units_per_pack, price, quantity, stock_limit) VALUES
                                                                                                                           ('P001', 'Aspirin', 'Pain relief tablets', 'Box', 'Tablet', 20, 2.50, 100, 10),
                                                                                                                           ('P002', 'Paracetamol', 'Pain and fever relief', 'Box', 'Tablet', 16, 1.80, 150, 15),
                                                                                                                           ('P003', 'Ibuprofen', 'Anti-inflammatory tablets', 'Box', 'Tablet', 24, 3.20, 120, 10),
                                                                                                                           ('P004', 'Cough Syrup', 'Relief for dry cough', 'Bottle', 'ml', 100, 4.50, 80, 10),
                                                                                                                           ('P005', 'Vitamin C', 'Immune support tablets', 'Bottle', 'Tablet', 30, 5.00, 90, 10),
                                                                                                                           ('P006', 'Antiseptic Cream', 'For cuts and wounds', 'Tube', 'g', 50, 3.75, 70, 8),
                                                                                                                           ('P007', 'Hand Sanitizer', 'Alcohol-based sanitizer', 'Bottle', 'ml', 250, 2.99, 200, 20),
                                                                                                                           ('P008', 'Face Mask Pack', 'Disposable face masks', 'Pack', 'Units', 10, 6.50, 60, 10),
                                                                                                                           ('P009', 'Thermometer', 'Digital thermometer', 'Unit', 'Unit', 1, 9.99, 40, 5),
                                                                                                                           ('P010', 'Allergy Relief', 'Antihistamine tablets', 'Box', 'Tablet', 14, 2.75, 110, 10);