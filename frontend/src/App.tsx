import React from 'react';
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import ProductList from './pages/ProductList';
import OrderList from './pages/OrderList';
import OrderCreate from './pages/OrderCreate';

const App: React.FC = () => {
  return (
    <BrowserRouter>
      <div style={{ minHeight: '100vh', backgroundColor: '#f5f5f5' }}>
        <header style={{ backgroundColor: '#1976d2', color: 'white', padding: '16px' }}>
          <div style={{ maxWidth: '1200px', margin: '0 auto', display: 'flex', alignItems: 'center', gap: '24px' }}>
            <h1 style={{ margin: 0, fontSize: '20px' }}>电商订单管理系统</h1>
            <nav style={{ display: 'flex', gap: '16px' }}>
              <Link to="/" style={{ color: 'white', textDecoration: 'none' }}>商品管理</Link>
              <Link to="/orders" style={{ color: 'white', textDecoration: 'none' }}>订单列表</Link>
              <Link to="/orders/create" style={{ color: 'white', textDecoration: 'none' }}>创建订单</Link>
            </nav>
          </div>
        </header>

        <main style={{ maxWidth: '1200px', margin: '24px auto', padding: '0 16px' }}>
          <Routes>
            <Route path="/" element={<ProductList />} />
            <Route path="/orders" element={<OrderList />} />
            <Route path="/orders/create" element={<OrderCreate />} />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  );
};

export default App;
