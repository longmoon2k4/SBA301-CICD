import { Button, Stack, Spinner } from 'react-bootstrap';
import { formatVND } from '../../../shared/utils/format';
import { getItemSubtotal } from '../../cart/utils/cartMath.js';

function CheckoutSummary({ items, totals, shippingMethod, address, checkingOut, canCheckout, onPlaceOrder }) {
  return (
    <div className="checkoutx-panel">
      <h2 className="checkoutx-section-title">Tóm tắt đơn hàng</h2>

      {/* Địa chỉ giao hàng */}
      <div className="mb-4 pb-3 border-bottom">
        <h6 className="fw-bold mb-2 text-uppercase small checkoutx-label-spacing">Giao đến</h6>
        {address ? (
          <div>
            <p className="mb-1 fw-bold">{address.recipientName} / {address.phone}</p>
            <p className="mb-0 text-muted small">
              {address.street}, {address.ward}, {address.district}, {address.province}
            </p>
          </div>
        ) : (
          <p className="text-danger small mb-0">Chưa có địa chỉ giao hàng</p>
        )}
      </div>

      {/* Danh sách sản phẩm */}
      <div className="mb-4">
        <h6 className="fw-bold mb-3 text-uppercase small checkoutx-label-spacing">Sản phẩm ({items.length})</h6>
        <div style={{ maxHeight: '300px', overflowY: 'auto', paddingRight: '0.5rem' }}>
          {items.map((item) => (
            <div key={item.id} className="checkoutx-summary-item">
              <img src={item.thumbnail} alt={item.productName} className="checkoutx-summary-thumb" />
              <div className="checkoutx-summary-details">
                <p className="checkoutx-summary-title">{item.productName}</p>
                <p className="checkoutx-summary-meta mb-1">
                  Màu: {item.color} | Size: {item.size}
                </p>
                <div className="d-flex justify-content-between align-items-center mt-auto">
                  <span className="small">SL: x{item.quantity}</span>
                  <span className="checkoutx-summary-price">{formatVND(getItemSubtotal(item))}</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Tổng tiền */}
      <Stack gap={2} className="mb-4">
        <div className="d-flex justify-content-between small">
          <span className="text-muted">Tạm tính</span>
          <span>{formatVND(totals.itemsSubtotal)}</span>
        </div>
        <div className="d-flex justify-content-between small">
          <span className="text-muted">Phí giao hàng ({shippingMethod?.name || 'Chưa chọn'})</span>
          <span>{formatVND(totals.shippingFee)}</span>
        </div>
        {totals.discountAmount > 0 && (
          <div className="d-flex justify-content-between small text-success">
            <span>Ưu đãi giảm giá</span>
            <span>-{formatVND(totals.discountAmount)}</span>
          </div>
        )}
      </Stack>

      {/* Tổng cộng */}
      <div className="d-flex justify-content-between align-items-center mb-4 pt-3 border-top">
        <span className="text-uppercase fw-bold checkoutx-label-spacing">Tổng cộng</span>
        <strong className="fs-4">{formatVND(totals.finalTotal)}</strong>
      </div>

      <Button
        variant="dark"
        size="lg"
        className="w-100 rounded-0 text-uppercase fw-bold checkoutx-label-spacing"
        disabled={checkingOut || !canCheckout}
        onClick={onPlaceOrder}
      >
        {checkingOut ? (
          <>
            <Spinner as="span" animation="border" size="sm" role="status" aria-hidden="true" className="me-2" />
            Đang xử lý...
          </>
        ) : (
          'Đặt Hàng'
        )}
      </Button>
    </div>
  );
}

export default CheckoutSummary;
