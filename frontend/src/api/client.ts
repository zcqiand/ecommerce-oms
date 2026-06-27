const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

export interface ApiResponse<T> {
  success: boolean;
  code: string;
  message?: string;
  data: T;
}

export interface Product {
  id: number;
  sku: string;
  name: string;
  description?: string;
  price: number;
  category?: string;
  supplierId?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface Order {
  id: number;
  orderNumber: string;
  userId: number;
  status: OrderStatus;
  totalAmount: number;
  approvalLevel: number;
  approvalNotes?: string;
  submittedAt?: string;
  paidAt?: string;
  shippedAt?: string;
  completedAt?: string;
  cancelledAt?: string;
  cancellationReason?: string;
  createdAt?: string;
  items?: OrderItem[];
}

export type OrderStatus = 'DRAFT' | 'SUBMITTED' | 'APPROVED' | 'REJECTED' | 'PAID' | 'SHIPPED' | 'COMPLETED' | 'CANCELLED';

export interface OrderItem {
  id: number;
  orderId: number;
  productId: number;
  quantity: number;
  unitPrice: number;
  subtotal: number;
}

export interface CreateOrderRequest {
  userId: number;
  items: {
    productId: number;
    quantity: number;
  }[];
}

export interface Inventory {
  id: number;
  productId: number;
  quantity: number;
  lockedQuantity: number;
  warehouseLocation?: string;
  availableQuantity: number;
}

async function fetchApi<T>(endpoint: string, options?: RequestInit): Promise<T> {
  const url = `${API_BASE_URL}${endpoint}`;
  const response = await fetch(url, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options?.headers,
    },
  });

  const result: ApiResponse<T> = await response.json();

  if (!result.success) {
    throw new Error(result.message || 'API request failed');
  }

  return result.data;
}

export const api = {
  products: {
    getAll: () => fetchApi<Product[]>('/api/products'),
    getById: (id: number) => fetchApi<Product>(`/api/products/${id}`),
    getBySku: (sku: string) => fetchApi<Product>(`/api/products/sku/${sku}`),
    create: (product: Omit<Product, 'id'>) =>
      fetchApi<Product>('/api/products', {
        method: 'POST',
        body: JSON.stringify(product),
      }),
    update: (id: number, product: Partial<Product>) =>
      fetchApi<Product>(`/api/products/${id}`, {
        method: 'PUT',
        body: JSON.stringify(product),
      }),
    delete: (id: number) =>
      fetchApi<void>(`/api/products/${id}`, { method: 'DELETE' }),
  },

  orders: {
    getAll: () => fetchApi<Order[]>('/api/orders'),
    getById: (id: number) => fetchApi<Order>(`/api/orders/${id}`),
    getByNumber: (orderNumber: string) => fetchApi<Order>(`/api/orders/number/${orderNumber}`),
    create: (request: CreateOrderRequest) =>
      fetchApi<Order>('/api/orders', {
        method: 'POST',
        body: JSON.stringify(request),
      }),
    submit: (id: number) =>
      fetchApi<Order>(`/api/orders/${id}/submit`, { method: 'POST' }),
    approve: (id: number, notes?: string) =>
      fetchApi<Order>(`/api/orders/${id}/approve?notes=${notes || ''}`, { method: 'POST' }),
    reject: (id: number, reason: string) =>
      fetchApi<Order>(`/api/orders/${id}/reject?reason=${encodeURIComponent(reason)}`, { method: 'POST' }),
    pay: (id: number) =>
      fetchApi<Order>(`/api/orders/${id}/pay`, { method: 'POST' }),
    ship: (id: number) =>
      fetchApi<Order>(`/api/orders/${id}/ship`, { method: 'POST' }),
    complete: (id: number) =>
      fetchApi<Order>(`/api/orders/${id}/complete`, { method: 'POST' }),
    cancel: (id: number, reason?: string) =>
      fetchApi<Order>(`/api/orders/${id}/cancel?reason=${reason || ''}`, { method: 'POST' }),
  },

  inventory: {
    getAll: () => fetchApi<Inventory[]>('/api/inventory'),
    getByProductId: (productId: number) => fetchApi<Inventory>(`/api/inventory/product/${productId}`),
    lock: (productId: number, quantity: number) =>
      fetchApi<void>(`/api/inventory/lock?productId=${productId}&quantity=${quantity}`, { method: 'POST' }),
    unlock: (productId: number, quantity: number) =>
      fetchApi<void>(`/api/inventory/unlock?productId=${productId}&quantity=${quantity}`, { method: 'POST' }),
  },
};
