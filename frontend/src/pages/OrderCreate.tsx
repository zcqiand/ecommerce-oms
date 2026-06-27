import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api, Product, CreateOrderRequest } from '../api/client';

interface OrderItemInput {
  productId: number;
  quantity: number;
}

const OrderCreate: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [userId, setUserId] = useState<number>(1);
  const [orderItems, setOrderItems] = useState<OrderItemInput[]>([
    { productId: 0, quantity: 1 },
  ]);
  const navigate = useNavigate();

  useEffect(() => {
    loadProducts();
  }, []);

  const loadProducts = async () => {
    try {
      const data = await api.products.getAll();
      setProducts(data);
      if (data.length > 0) {
        setOrderItems([{ productId: data[0].id, quantity: 1 }]);
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load products');
    } finally {
      setLoading(false);
    }
  };

  const handleAddItem = () => {
    setOrderItems([...orderItems, { productId: 0, quantity: 1 }]);
  };

  const handleRemoveItem = (index: number) => {
    if (orderItems.length === 1) {
      alert('至少需要一个商品');
      return;
    }
    const newItems = orderItems.filter((_, i) => i !== index);
    setOrderItems(newItems);
  };

  const handleItemChange = (index: number, field: 'productId' | 'quantity', value: number) => {
    const newItems = [...orderItems];
    newItems[index] = { ...newItems[index], [field]: value };
    setOrderItems(newItems);
  };

  const calculateTotal = (): number => {
    return orderItems.reduce((total, item) => {
      const product = products.find((p) => p.id === item.productId);
      return total + (product ? product.price * item.quantity : 0);
    }, 0);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const validItems = orderItems.filter((item) => item.productId > 0 && item.quantity > 0);
    if (validItems.length === 0) {
      setError('请至少选择一个商品');
      return;
    }

    const request: CreateOrderRequest = {
      userId,
      items: validItems.map((item) => ({
        productId: item.productId,
        quantity: item.quantity,
      })),
    };

    try {
      setSubmitting(true);
      setError(null);
      await api.orders.create(request);
      navigate('/orders');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to create order');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <div>
      <h2>创建订单</h2>

      {error && (
        <div style={{ padding: '12px', backgroundColor: '#ffebee', color: '#c62828', borderRadius: '4px', marginBottom: '16px' }}>
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit} style={{ backgroundColor: 'white', padding: '24px', borderRadius: '8px' }}>
        <div style={{ marginBottom: '16px' }}>
          <label style={{ display: 'block', marginBottom: '4px', fontWeight: 'bold' }}>用户ID</label>
          <input
            type="number"
            value={userId}
            onChange={(e) => setUserId(parseInt(e.target.value) || 1)}
            min="1"
            required
            style={{
              width: '100%',
              padding: '8px 12px',
              border: '1px solid #ddd',
              borderRadius: '4px',
              fontSize: '14px',
            }}
          />
        </div>

        <div style={{ marginBottom: '16px' }}>
          <label style={{ display: 'block', marginBottom: '8px', fontWeight: 'bold' }}>订单商品</label>

          {orderItems.map((item, index) => (
            <div key={index} style={{ display: 'flex', gap: '8px', marginBottom: '8px', alignItems: 'center' }}>
              <select
                value={item.productId}
                onChange={(e) => handleItemChange(index, 'productId', parseInt(e.target.value) || 0)}
                required
                style={{
                  flex: 2,
                  padding: '8px 12px',
                  border: '1px solid #ddd',
                  borderRadius: '4px',
                  fontSize: '14px',
                }}
              >
                <option value={0}>选择商品</option>
                {products.map((product) => (
                  <option key={product.id} value={product.id}>
                    {product.name} - ¥{product.price.toFixed(2)}
                  </option>
                ))}
              </select>

              <input
                type="number"
                value={item.quantity}
                onChange={(e) => handleItemChange(index, 'quantity', parseInt(e.target.value) || 1)}
                min="1"
                required
                style={{
                  flex: 1,
                  padding: '8px 12px',
                  border: '1px solid #ddd',
                  borderRadius: '4px',
                  fontSize: '14px',
                }}
              />

              <button
                type="button"
                onClick={() => handleRemoveItem(index)}
                style={{
                  padding: '8px 12px',
                  backgroundColor: '#f44336',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: 'pointer',
                }}
              >
                删除
              </button>
            </div>
          ))}

          <button
            type="button"
            onClick={handleAddItem}
            style={{
              padding: '8px 16px',
              backgroundColor: '#757575',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer',
              marginTop: '8px',
            }}
          >
            添加商品
          </button>
        </div>

        <div style={{ marginBottom: '24px', padding: '16px', backgroundColor: '#f5f5f5', borderRadius: '4px' }}>
          <strong>订单总额: ¥{calculateTotal().toFixed(2)}</strong>
          <br />
          <small style={{ color: '#666' }}>
            {calculateTotal() >= 1000 ? '需要二级审批（部门经理 + 财务总监）' : '需要一级审批（直属经理）'}
          </small>
        </div>

        <div style={{ display: 'flex', gap: '8px' }}>
          <button
            type="submit"
            disabled={submitting}
            style={{
              padding: '12px 24px',
              backgroundColor: submitting ? '#bdbdbd' : '#1976d2',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: submitting ? 'not-allowed' : 'pointer',
              fontSize: '16px',
            }}
          >
            {submitting ? '创建中...' : '创建订单'}
          </button>

          <button
            type="button"
            onClick={() => navigate('/orders')}
            style={{
              padding: '12px 24px',
              backgroundColor: '#757575',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer',
              fontSize: '16px',
            }}
          >
            取消
          </button>
        </div>
      </form>
    </div>
  );
};

export default OrderCreate;
