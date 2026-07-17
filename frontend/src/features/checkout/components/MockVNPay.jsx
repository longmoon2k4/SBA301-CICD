import { useSearchParams, useNavigate } from 'react-router-dom';
import { Container, Card, Button, Stack } from 'react-bootstrap';
import { formatVND } from '../../../shared/utils/format';

function MockVNPay() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const orderCode = searchParams.get('orderCode') || 'N/A';
  const amount = Number(searchParams.get('amount') || 0);

  const handlePayment = (status) => {
    navigate(`/checkout/payment-return?orderCode=${orderCode}&status=${status}&paymentMethod=VNPAY`);
  };

  return (
    <Container className="my-5 d-flex justify-content-center">
      <Card className="shadow-lg border-0 rounded-0" style={{ maxWidth: '480px', width: '100%' }}>
        <Card.Header className="text-white text-center py-3 border-0" style={{ backgroundColor: '#005baa' }}>
          <h4 className="mb-0 fw-bold">CỔNG THANH TOÁN VNPAY</h4>
        </Card.Header>
        <Card.Body className="p-4 text-center">
          <div className="mb-4">
            <span className="text-muted small d-block">Mã đơn hàng</span>
            <strong className="fs-5 text-dark">{orderCode}</strong>
          </div>

          <div className="mb-4 bg-light p-3 border">
            <span className="text-muted small d-block mb-1">Số tiền thanh toán</span>
            <h3 className="text-danger fw-bold mb-0">{formatVND(amount)}</h3>
          </div>

          <div className="my-4">
            {/* Realistically looking QR code */}
            <img 
              src="https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=https://vnpay.vn" 
              alt="Mã QR VNPay" 
              className="img-fluid border p-2 bg-white"
              style={{ width: '200px', height: '200px' }}
            />
            <p className="text-muted small mt-2">Quét mã QR bằng ứng dụng Ngân hàng của bạn để thanh toán</p>
          </div>

          <hr className="my-4" />

          <Stack gap={2}>
            <Button 
              variant="success" 
              className="rounded-0 py-2 text-uppercase fw-bold"
              onClick={() => handlePayment('SUCCESS')}
            >
              Giả lập Thanh toán Thành công
            </Button>
            <Button 
              variant="outline-danger" 
              className="rounded-0 py-2 text-uppercase fw-bold"
              onClick={() => handlePayment('CANCELLED')}
            >
              Hủy giao dịch
            </Button>
          </Stack>
        </Card.Body>
        <Card.Footer className="bg-white text-center py-3 border-top text-muted small">
          VNPay Secure Payment Gateway &copy; {new Date().getFullYear()}
        </Card.Footer>
      </Card>
    </Container>
  );
}

export default MockVNPay;
