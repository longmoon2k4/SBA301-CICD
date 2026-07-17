import { useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { Container, Card, Button } from 'react-bootstrap';
import { clearOrderedItemsAPI } from '../../cart/services/cartService.js';

function MockPaymentReturn() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const vnpResponse = searchParams.get('vnp_ResponseCode');

  const orderCode = searchParams.get('orderCode') || 
                    searchParams.get('vnp_TxnRef') || 
                    searchParams.get('orderId') || 
                    'N/A';

  let status = searchParams.get('status');
  if (!status) {
    if (vnpResponse !== null) {
      status = vnpResponse === '00' ? 'SUCCESS' : 'FAILED';
    } else {
      status = 'FAILED';
    }
  }

  let paymentMethod = searchParams.get('paymentMethod');
  if (!paymentMethod) {
    if (vnpResponse !== null) {
      paymentMethod = 'VNPAY';
    } else {
      paymentMethod = 'UNKNOWN';
    }
  }

  useEffect(() => {
    const storedIdsRaw = sessionStorage.getItem('checkout_selected_items');
    if (!storedIdsRaw) return;

    if (status === 'SUCCESS') {
      try {
        const ids = JSON.parse(storedIdsRaw);

        clearOrderedItemsAPI(ids).catch((err) =>
          console.error('Lỗi khi xoá sản phẩm đã đặt mua khỏi giỏ hàng:', err),
        );

        sessionStorage.removeItem('checkout_selected_items');

        const cartSelectedRaw = sessionStorage.getItem('cart_selected_items');
        if (cartSelectedRaw) {
          const cartSelected = JSON.parse(cartSelectedRaw);
          const nextSelected = cartSelected.filter((id) => !ids.includes(id));
          sessionStorage.setItem('cart_selected_items', JSON.stringify(nextSelected));
        }
      } catch (e) {
        console.error('Lỗi xử lý dọn dẹp giỏ hàng:', e);
      }
    } else {
      sessionStorage.removeItem('checkout_selected_items');
    }
  }, [status]);

  const handleReturnHome = () => {
    navigate('/');
  };

  const handleReorder = () => {
    navigate('/cart');
  };

  const isSuccess = status === 'SUCCESS';

  return (
    <Container className="my-5 d-flex justify-content-center">
      <Card className="shadow border-0 rounded-0 text-center" style={{ maxWidth: '550px', width: '100%' }}>
        <Card.Body className="p-5">
          {isSuccess ? (
            <>
              <div className="mb-4 text-success">
                <svg xmlns="http://www.w3.org/2000/svg" width="72" height="72" fill="currentColor" className="bi bi-check-circle-fill" viewBox="0 0 16 16">
                  <path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0zm-3.97-3.03a.75.75 0 0 0-1.08.022L7.477 9.417 5.384 7.323a.75.75 0 0 0-1.06 1.06L6.97 11.03a.75.75 0 0 0 1.079-.02l3.992-4.99a.75.75 0 0 0-.01-1.05z" />
                </svg>
              </div>
              <h3 className="fw-bold mb-3 text-success">Thanh toán thành công!</h3>
              <p className="text-muted mb-4">
                Đơn hàng <strong>{orderCode}</strong> đã được thanh toán trực tuyến qua <strong>{paymentMethod}</strong>.<br />
                Chúng tôi đang chuẩn bị hàng và sẽ sớm giao đến địa chỉ của bạn.
              </p>
              <Button variant="dark" className="rounded-0 text-uppercase px-4 py-2" onClick={handleReturnHome}>
                Quay về trang chủ
              </Button>
            </>
          ) : (
            <>
              <div className="mb-4 text-danger">
                <svg xmlns="http://www.w3.org/2000/svg" width="72" height="72" fill="currentColor" className="bi bi-exclamation-circle-fill" viewBox="0 0 16 16">
                  <path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0zM8 4a.905.905 0 0 0-.9.995l.35 3.507a.552.552 0 0 0 1.1 0l.35-3.507A.905.905 0 0 0 8 4zm.002 6a1 1 0 1 0 0 2 1 1 0 0 0 0-2z" />
                </svg>
              </div>
              <h3 className="fw-bold mb-3 text-danger">Thanh toán thất bại</h3>
              <p className="text-muted mb-4">
                Giao dịch của bạn đã bị hủy hoặc không thể hoàn tất.<br />
                Mã đơn hàng bị gián đoạn: <strong>{orderCode}</strong>.
              </p>
              <div className="d-flex justify-content-center gap-3">
                <Button variant="outline-dark" className="rounded-0 text-uppercase px-3" onClick={handleReturnHome}>
                  Trang chủ
                </Button>
                <Button variant="dark" className="rounded-0 text-uppercase px-3" onClick={handleReorder}>
                  Thanh toán lại
                </Button>
              </div>
            </>
          )}
        </Card.Body>
      </Card>
    </Container>
  );
}

export default MockPaymentReturn;
