insert into security.menu (id, name, value, link, icon, sort_index) values 
(1, 'Administrative', 'administrative', '', 'ico-adm', 1),
(2, 'Financial', 'financial', '', 'ico-finan', 2),
(3, 'System', 'system', '', 'ico-sys', 3);

insert into security.menu (id, name, value, link, icon, sort_index, removed) values 
(4, 'Reserved1', 'reserved1', '', 'ico-blank', 4, true),
(5, 'Reserved2', 'reserved2', '', 'ico-blank', 5, true),
(6, 'Reserved3', 'reserved3', '', 'ico-blank', 6, true),
(7, 'Reserved4', 'reserved4', '', 'ico-blank', 7, true),
(8, 'Reserved5', 'reserved5', '', 'ico-blank', 8, true),
(9, 'Reserved6', 'reserved6', '', 'ico-blank', 9, true);

insert into security.menu (fk_parent, id, name, value, link, icon) values 
(1, 10, 'Colaborator', 'colaborator', '/administrative/colaborator', 'ico-colab'),
(1, 11, 'Payroll', 'payroll', '/administrative/folha', 'ico-payroll'),
(1, 12, 'Task', 'task', '/administrative/task', 'ico-task'),

(2, 20, 'Cashier', 'cashier', '/financial/cashier', 'ico-cashier'),
(2, 21, 'Receivable', 'receivable', '/financial/receivable', 'ico-receivable'),
(2, 22, 'Payable', 'payable', '/financial/payable', 'ico-payable'),
(2, 23, 'Stock', 'stock', '/financial/stock', 'ico-stock'),
(2, 24, 'Sale', 'sale', '/financial/sale', 'ico-sale'),

(3, 30, 'Report', 'report', '/system/report', 'ico-report'),
(3, 31, 'Settings', 'settings', '/system/settings', 'ico-settings'),
(3, 32, 'Import', 'import', '/system/importacao', 'ico-import');




