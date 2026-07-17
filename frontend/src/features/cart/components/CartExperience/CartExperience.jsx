import { Alert, Button, Col, Modal, Row, Spinner, Stack } from 'react-bootstrap';
import { formatDateTime, formatVND } from '../../../../shared/utils/format';
import { useCartExperience } from '../../hooks/useCartExperience.js';
import { Link } from 'react-router-dom';

import CartItemList from './CartItemList.jsx';
import '../../styles/cart.css';

function CartExperience() {
  const {
    loading,
    errorMessage,
    isUnauthenticated,
    items,
    selectedItemIds,
    stockSyncNotice,
    lastSyncedAt,
    purchasableItems,
    unavailableCount,
    allPurchasableSelected,
    itemsSubtotal,
    toggleSelectAll,
    toggleItem,
    changeItemQuantity,
    removeItem,
    clearUnavailableItems,
    reloadCart,
    proceedToCheckout,
    cartAlert,
    setCartAlert,
  } = useCartExperience();

  if (loading) {
    return (
      <div className="cartx-loading">
        <Spinner animation="border" role="status" variant="dark" />
        <p className="mt-3 text-muted">Đang tải dữ liệu...</p>
      </div>
    );
  }

  if (isUnauthenticated) {
    return (
      <section className="cartx-shell">
        <div className="container">
          <header className="cartx-hero">
            <p className="cartx-overline">Your Bag</p>
            <h1 className="cartx-title">Giỏ hàng</h1>
            <p className="cartx-subtitle">
              Miễn phí giao hàng cho đơn từ 1.000.000đ. Trả hàng miễn phí trong 30 ngày.
            </p>
          </header>

          <div className="text-center py-5 my-5 border border-dashed border-dark">
            <div className="mb-4">
              <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" fill="currentColor" className="bi bi-bag-plus text-muted" viewBox="0 0 16 16">
                <path fillRule="evenodd" d="M8 7.5a.5.5 0 0 1 .5.5v1.5H10a.5.5 0 0 1 0 1H8.5V12a.5.5 0 0 1-1 0v-1.5H6a.5.5 0 0 1 0-1h1.5V8a.5.5 0 0 1 .5-.5z" />
                <path d="M8 1a2.5 2.5 0 0 1 2.5 2.5V4h-5v-.5A2.5 2.5 0 0 1 8 1zm3.5 3v-.5a3.5 3.5 0 1 0-7 0V4H1v10a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V4h-3.5zM2 5h12v9a1 1 0 0 1-1 1H3a1 1 0 0 1-1-1V5z" />
              </svg>
            </div>
            <h3 className="fw-bold text-uppercase mb-2" style={{ fontFamily: 'Space Grotesk' }}>Bạn chưa đăng nhập</h3>
            <p className="text-muted mb-4 mx-auto" style={{ maxWidth: '400px' }}>
              Vui lòng đăng nhập để xem giỏ hàng của bạn và tiếp tục thanh toán đơn hàng.
            </p>
            <div className="d-flex justify-content-center gap-3 mt-4">
              <Button as={Link} to="/login" variant="dark" className="rounded-0 text-uppercase fw-bold cartx-label-spacing px-5 py-3">
                Đăng nhập
              </Button>
              <Button as={Link} to="/register" variant="outline-dark" className="rounded-0 text-uppercase fw-bold cartx-label-spacing px-5 py-3">
                Đăng ký
              </Button>
            </div>
          </div>
        </div>
      </section>
    );
  }

  if (errorMessage) {
    return (
      <section className="cartx-shell">
        <div className="container">
          <header className="cartx-hero">
            <p className="cartx-overline">Your Bag</p>
            <h1 className="cartx-title">Giỏ hàng</h1>
            <p className="cartx-subtitle">
              Miễn phí giao hàng cho đơn từ 1.000.000đ. Trả hàng miễn phí trong 30 ngày.
            </p>
          </header>
          <Alert variant="danger" className="mb-0 rounded-0">
            <Stack direction="horizontal" gap={3} className="justify-content-between align-items-center">
              <span>{errorMessage}</span>
              <Button variant="outline-dark" size="sm" className="rounded-0" onClick={reloadCart}>
                Thử lại
              </Button>
            </Stack>
          </Alert>
        </div>
      </section>
    );
  }


  return (
    <section className="cartx-shell">
      <div className="container">
        <header className="cartx-hero">
          <p className="cartx-overline">Your Bag</p>
          <h1 className="cartx-title">Giỏ hàng</h1>
          <p className="cartx-subtitle">
            Miễn phí giao hàng cho đơn từ 1.000.000đ. Trả hàng miễn phí trong 30 ngày.
          </p>
        </header>

        {stockSyncNotice ? (
          <Alert variant={stockSyncNotice.includes('loại') ? 'warning' : 'info'} className="mb-4">
            <Stack className="gap-1">
              <strong>{stockSyncNotice}</strong>
              {lastSyncedAt && (
                <small className="text-muted">
                  Cập nhật: {formatDateTime(lastSyncedAt.toISOString())}
                </small>
              )}
            </Stack>
          </Alert>
        ) : null}

        <Row className="g-5">
          <CartItemList
            items={items}
            selectedItemIds={selectedItemIds}
            purchasableCount={purchasableItems.length}
            unavailableCount={unavailableCount}
            allPurchasableSelected={allPurchasableSelected}
            onToggleSelectAll={toggleSelectAll}
            onReloadCart={reloadCart}
            onClearUnavailable={clearUnavailableItems}
            onToggleItem={toggleItem}
            onChangeQuantity={changeItemQuantity}
            onRemoveItem={removeItem}
          />

          <Col lg={4}>
            <div className="cartx-sticky">
              <div className="cartx-panel">
                <h2 className="cartx-section-title">Tóm tắt giỏ hàng</h2>
                <Stack gap={2} className="mb-4">
                  <div className="d-flex justify-content-between small">
                    <span className="text-muted">Sản phẩm chọn ({selectedItemIds.length})</span>
                    <span>{formatVND(itemsSubtotal)}</span>
                  </div>
                  <div className="d-flex justify-content-between align-items-center mt-3 pt-3 border-top">
                    <span className="text-uppercase fw-bold cartx-label-spacing">Tạm tính</span>
                    <strong className="fs-4">{formatVND(itemsSubtotal)}</strong>
                  </div>
                </Stack>
                <Button
                  variant="dark"
                  size="lg"
                  className="w-100 rounded-0 text-uppercase fw-bold cartx-label-spacing"
                  disabled={selectedItemIds.length === 0}
                  onClick={proceedToCheckout}
                >
                  Tiến hành thanh toán
                </Button>
                <p className="text-center text-muted small mt-3 mb-0">
                  Phí vận chuyển và Khuyến mãi sẽ được tính ở bước sau.
                </p>
              </div>
            </div>
          </Col>
        </Row>

        {process.env.NODE_ENV === 'development' && (
          <div className="mt-5 p-4 bg-light rounded border border-warning border-2">
            <h4 className="text-warning mb-3">🛠️ Khu vực Debug (Chỉ dùng để Test)</h4>
            <p className="text-muted small mb-3">
              Vì trang Sản phẩm do bạn khác làm chưa hoàn thiện, bạn có thể click các nút dưới đây để giả lập việc &quot;Thêm sản phẩm mới&quot; vào giỏ hàng.
            </p>
            <div className="d-flex gap-2 flex-wrap">
              <Button
                variant="outline-primary"
                onClick={async () => {
                  const { addItemAPI } = await import('../../services/cartService.js');
                  await addItemAPI({
                    variantId: 901,
                    productId: 801,
                    productName: 'Áo Khoác Nam Mùa Đông',
                    sku: 'AOK-NAM-L-DEN',
                    size: 'L',
                    color: 'Đen',
                    unitPrice: 550000,
                    quantity: 1,
                    stockQuantity: 10,
                    isActive: true,
                    thumbnail: 'https://images.unsplash.com/photo-1556821840-3a63f95609a7?auto=format&fit=crop&w=640&q=80',
                  });
                  reloadCart();
                  setCartAlert({ title: 'Thành công', message: 'Đã thêm Áo Khoác Nam vào giỏ hàng!', type: 'info' });
                }}
              >
                + Thêm Áo Khoác (550K)
              </Button>
              <Button
                variant="outline-success"
                onClick={async () => {
                  const { addItemAPI } = await import('../../services/cartService.js');
                  await addItemAPI({
                    variantId: 902,
                    productId: 802,
                    productName: 'Giày Thể Thao Sneaker',
                    sku: 'GIAY-SNE-42-TRG',
                    size: '42',
                    color: 'Trắng',
                    unitPrice: 850000,
                    quantity: 1,
                    stockQuantity: 5,
                    isActive: true,
                    thumbnail: 'https://images.unsplash.com/photo-1549298916-b41d501d3772?auto=format&fit=crop&w=640&q=80',
                  });
                  reloadCart();
                  setCartAlert({ title: 'Thành công', message: 'Đã thêm Giày Thể Thao vào giỏ hàng!', type: 'info' });
                }}
              >
                + Thêm Giày Sneaker (850K)
              </Button>
            </div>
          </div>
        )}
      </div>

      <Modal
        show={Boolean(cartAlert)}
        onHide={() => setCartAlert(null)}
        centered
        className="cartx-modal"
        backdropClassName="cartx-modal-backdrop"
      >
        <Modal.Header closeButton className={`cartx-modal-header-${cartAlert?.type || 'default'}`}>
          <Modal.Title>
            {cartAlert?.type === 'danger' && <span className="text-danger me-2">■</span>}
            {cartAlert?.type === 'warning' && <span className="text-warning me-2">▲</span>}
            {cartAlert?.title}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <p className="mb-0 text-center py-3">{cartAlert?.message}</p>
        </Modal.Body>
        <Modal.Footer className="justify-content-center">
          <Button
            variant="dark"
            className="w-100 rounded-0 text-uppercase fw-bold"
            onClick={() => setCartAlert(null)}
          >
            Đã hiểu
          </Button>
        </Modal.Footer>
      </Modal>
    </section>
  );
}

export default CartExperience;