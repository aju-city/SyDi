-- ─────────────────────────────────────────────
-- IPOS-CA STOCK ITEMS SEED DATA (from case study)
-- ─────────────────────────────────────────────

INSERT INTO ipos_ca.stock_items
(item_id, name, description, package_type, unit, units_per_pack, price, quantity, stock_limit)
VALUES
    ('10000001', 'Paracetamol', 'Paracetamol',            'Box',    'Caps', 20,  0.20, 121, 10),
    ('10000002', 'Aspirin',     'Aspirin',                'Box',    'Caps', 20,  1.00, 201, 15),
    ('10000003', 'Analgin',     'Analgin',                'Box',    'Caps', 10,  2.40,  25, 10),
    ('10000004', 'Celebrex',    'Celebrex, caps 100 mg',  'Box',    'Caps', 10, 20.00,  43, 10),
    ('10000005', 'Celebrex',    'Celebrex, caps 200 mg',  'Box',    'Caps', 10, 37.00,  35,  5),
    ('10000006', 'Retin-A',     'Retin-A Tretin, 30 g',   'Box',    'g',    20, 50.00,  28, 10),
    ('10000007', 'Lipitor',     'Lipitor TB, 20 mg',      'Box',    'Caps', 30, 31.00,  10, 10),
    ('10000008', 'Claritin',    'Claritin CR, 60g',       'Box',    'Caps', 20, 39.00,  21, 10),

    ('20000004', 'Iodine',      'Iodine tincture',        'Bottle', 'Ml',  100,  0.60,  35, 10),
    ('20000005', 'Rhynol',      'Rhynol',                 'Bottle', 'Ml',  200,  5.00,  14, 15),

    ('30000001', 'Ospen',       'Ospen',                  'Box',    'Caps', 20, 21.00,  78, 10),
    ('30000002', 'Amopen',      'Amopen',                 'Box',    'Caps', 30, 30.00,  90, 15),

    ('40000001', 'Vitamin C',   'Vitamin C',              'Box',    'Caps', 30,  2.40,  22, 15),
    ('40000002', 'Vitamin B12', 'Vitamin B12',            'Box',    'Caps', 30,  2.60,  43, 15)

    ON DUPLICATE KEY UPDATE
                         name           = VALUES(name),
                         description    = VALUES(description),
                         package_type   = VALUES(package_type),
                         unit           = VALUES(unit),
                         units_per_pack = VALUES(units_per_pack),
                         price          = VALUES(price),
                         quantity       = VALUES(quantity),
                         stock_limit    = VALUES(stock_limit);                                                                                                                       ('P010', 'Allergy Relief', 'Antihistamine tablets', 'Box', 'Tablet', 14, 2.75, 110, 10);