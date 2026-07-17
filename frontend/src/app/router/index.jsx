import { createBrowserRouter } from 'react-router-dom';
import MainLayout from '../layout/MainLayout.jsx';
import { authRoutes } from '../../features/auth/routes';
import { homeRoute } from '../../features/guest/routes';

import ProductList from '../../features/products/pages/ProductList.jsx';
import CartExperience from '../../features/cart/components/CartExperience/CartExperience.jsx';
import CheckoutLayout from '../../features/checkout/components/CheckoutLayout.jsx';
import OrderManagement from '../../features/orders/pages/OrderManagement.jsx';
import Dashboard from '../../features/dashboard/pages/Dashboard.jsx';
import POS from '../../features/pos/pages/POS.jsx';
import AuditLogs from '../../features/audit-logs/pages/AuditLogs.jsx';

import MockVNPay from '../../features/checkout/components/MockVNPay.jsx';
import MockPaymentReturn from '../../features/checkout/components/MockPaymentReturn.jsx';

const teamFeatureRoutes = [
  { path: 'products', element: <ProductList /> },
  { path: 'products/:id', element: <ProductList /> },
  { path: 'cart', element: <CartExperience /> },
  { path: 'checkout', element: <CheckoutLayout /> },
  { path: 'checkout/vnpay-mock', element: <MockVNPay /> },
  { path: 'checkout/payment-return', element: <MockPaymentReturn /> },
  { path: 'admin/dashboard', element: <Dashboard /> },
  { path: 'admin/products', element: <ProductList /> },
  { path: 'admin/orders', element: <OrderManagement /> },
  { path: 'admin/audit-logs', element: <AuditLogs /> },
  { path: 'staff/pos', element: <POS /> },
];

export const router = createBrowserRouter([
  {
    element: <MainLayout />,
    children: [
      homeRoute,
      ...authRoutes,
      ...teamFeatureRoutes,
    ],
  },
]);
