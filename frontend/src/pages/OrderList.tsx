import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api, Order, OrderStatus } from '../api/client';

const statusLabels: Record<OrderStatus, string> = {
  DRAFT: '草稿',
  SUBMITTED: '已提交',
  APPROVED: '已批准',
  REJECTED: '已拒绝',
  PAID: '已支付',
  SHIPPED: '已发货',
  COMPLETED: '已完成',
  CANCELLED: '已取消',
};

const statusColors: Record<OrderStatus, string> = {
  DRAFT: '#9e9e9e',
  SUBMITTED: '#ff9800',
  APPROVED: '#4caf50',
  REJECTED: '#f44336',
  PAID: '#2196f3',
  SHIPPED: '#9c27b0',
  COMPLETED: '#4caf50',
  CANCELLED: '#f44336',
};

const OrderList: React.FC = () => {
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    loadOrders();
  }, []);

  const loadOrders = async () => {
    try {
      setLoading(true);
      const data = await api.orders.getAll();
      setOrders(data);
      setError(null);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load orders');
    } finally {
      setLoading(false);
    }
  };

  const handleStatusAction = async (id: number, action: 'submit' | 'pay' | 'ship' | 'complete' | 'cancel', reason?: string) => {
    try {
      switch (action) {
        case 'submit':
          await api.orders.submit(id);
          break;
        case 'pay':
          await api.orders.pay(id);
          break;
        case 'ship':
          await api.orders.ship(id);
          break;
        case 'complete':
          await api.orders.complete(id);
          break;
        case 'cancel':
          await api.orders.cancel(id, reason);
          break;
      }
      await loadOrders();
    } catch (err) {
      setError(err instanceof Error ? err.message : `Failed to ${action} order`);
    }
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div style={{ color: 'red' }}>Error: {error}</div>;
  }

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
        <h2>订单列表</h2>
        <div style={{ display: 'flex', gap: '8px' }}>
          <button
            onClick={loadOrders}
            style={{
              padding: '8px 16px',
              backgroundColor: '#757575',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer',
            }}
          >
            刷新
          </button>
          <button
            onClick={() => navigate('/orders/create')}
            style={{
              padding: '8px 16px',
              backgroundColor: '#1976d2',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer',
            }}
          >
            创建订单
          </button>
        </div>
      </div>

      <table style={{ width: '100%', borderCollapse: 'collapse', backgroundColor: 'white' }}>
        <thead>
          <tr style={{ backgroundColor: '#f5f5f5' }}>
            <th style={thStyle}>订单号</th>
            <th style={thStyle}>金额</th>
            <th style={thStyle}>状态</th>
            <th style={thStyle}>审批级别</th>
            <th style={thStyle}>创建时间</th>
            <th style={thStyle}>操作</th>
          </tr>
        </thead>
        <tbody>
          {orders.map((order) => (
            <tr key={order.id} style={{ borderBottom: '1px solid #eee' }}>
              <td style={tdStyle}>{order.orderNumber}</td>
              <td style={tdStyle}>¥{order.totalAmount.toFixed(2)}</td>
              <td style={tdStyle}>
                <span
                  style={{
                    padding: '4px 8px',
                    borderRadius: '4px',
                    backgroundColor: statusColors[order.status],
                    color: 'white',
                    fontSize: '12px',
                  }}
                >
                  {statusLabels[order.status]}
                </span>
              </td>
              <td style={tdStyle}>{order.approvalLevel === 1 ? '一级' : '二级'}</td>
              <td style={tdStyle}>{order.createdAt ? new Date(order.createdAt).toLocaleString() : '-'}</td>
              <td style={tdStyle}>
                <div style={{ display: 'flex', gap: '4px', flexWrap: 'wrap' }}>
                  {order.status === 'DRAFT' && (
                    <button
                      onClick={() => handleStatusAction(order.id, 'submit')}
                      style={actionButtonStyle('#4caf50')}
                    >
                      提交
                    </button>
                  )}
                  {order.status === 'APPROVED' && (
                    <button
                      onClick={() => handleStatusAction(order.id, 'pay')}
                      style={actionButtonStyle('#2196f3')}
                    >
                      支付
                    </button>
                  )}
                  {order.status === 'PAID' && (
                    <button
                      onClick={() => handleStatusAction(order.id, 'ship')}
                      style={actionButtonStyle('#9c27b0')}
                    >
                      发货
                    </button>
                  )}
                  {order.status === 'SHIPPED' && (
                    <button
                      onClick={() => handleStatusAction(order.id, 'complete')}
                      style={actionButtonStyle('#4caf50')}
                    >
                      完成
                    </button>
                  )}
                  {['DRAFT', 'SUBMITTED', 'APPROVED'].includes(order.status) && (
                    <button
                      onClick={() => {
                        const reason = prompt('请输入取消原因:');
                        if (reason) handleStatusAction(order.id, 'cancel', reason);
                      }}
                      style={actionButtonStyle('#f44336')}
                    >
                      取消
                    </button>
                  )}
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {orders.length === 0 && (
        <div style={{ textAlign: 'center', padding: '32px', color: '#666' }}>
          暂无订单数据
        </div>
      )}
    </div>
  );
};

const thStyle: React.CSSProperties = {
  padding: '12px',
  textAlign: 'left',
  borderBottom: '2px solid #ddd',
};

const tdStyle: React.CSSProperties = {
  padding: '12px',
};

const actionButtonStyle = (backgroundColor: string): React.CSSProperties => ({
  padding: '4px 8px',
  backgroundColor,
  color: 'white',
  border: 'none',
  borderRadius: '4px',
  cursor: 'pointer',
  fontSize: '12px',
});

export default OrderList;
