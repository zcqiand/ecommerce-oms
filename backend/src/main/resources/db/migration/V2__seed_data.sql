-- V2__seed_data.sql
-- Seed initial data for ecommerce-oms

-- Insert suppliers
INSERT INTO suppliers (name, contact_person, phone, email, address) VALUES
('华东供应链有限公司', '张经理', '13800138001', 'contact@hdgyl.com', '上海市浦东新区张江路100号'),
('华南贸易集团', '李总监', '13900139002', 'sales@hnmy.com', '广州市天河区珠江新城50号');

-- Insert users
INSERT INTO users (username, password, real_name, email, role, department) VALUES
('admin', '$2a$10$XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX', '系统管理员', 'admin@example.com', 'ADMIN', 'IT'),
('zhangsan', '$2a$10$XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX', '张三', 'zhangsan@example.com', 'MANAGER', '销售'),
('lisi', '$2a$10$XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX', '李四', 'lisi@example.com', 'DIRECTOR', '销售'),
('wangwu', '$2a$10$XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX', '王五', 'wangwu@example.com', 'FINANCE_DIRECTOR', '财务');

-- Insert products
INSERT INTO products (sku, name, description, price, supplier_id, category) VALUES
('SKU-001', '无线蓝牙耳机', '高品质降噪无线耳机，续航30小时', 299.00, 1, '电子产品'),
('SKU-002', '机械键盘', '青轴机械键盘，RGB背光', 459.00, 1, '电子产品'),
('SKU-003', '人体工学鼠标', '静音设计，可充电', 189.00, 1, '电子产品'),
('SKU-004', '便携充电宝', '20000mAh大容量，支持快充', 159.00, 2, '配件'),
('SKU-005', 'Type-C数据线', '1米编织线材，快充支持', 49.00, 2, '配件');

-- Insert inventory records
INSERT INTO inventory (product_id, quantity, locked_quantity, warehouse_location) VALUES
(1, 100, 0, 'A-01-01'),
(2, 50, 0, 'A-01-02'),
(3, 80, 0, 'A-02-01'),
(4, 200, 0, 'B-01-01'),
(5, 500, 0, 'B-01-02');
