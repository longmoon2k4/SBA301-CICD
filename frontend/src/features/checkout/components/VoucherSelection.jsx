import { Form, Stack, Button } from 'react-bootstrap';

function VoucherSelection({ voucherInput, voucherNotice, onInputChange, onApply }) {
  return (
    <div>
      <Stack direction="horizontal" gap={2}>
        <Form.Control
          placeholder="Nhập mã ưu đãi..."
          value={voucherInput}
          onChange={(event) => onInputChange(event.target.value)}
          className="rounded-0"
        />
        <Button variant="outline-dark" className="rounded-0 text-uppercase fw-bold text-nowrap" onClick={onApply}>
          Áp dụng
        </Button>
      </Stack>
      {voucherNotice ? (
        <p className="small mb-0 mt-2 text-muted">
          {voucherNotice}
        </p>
      ) : null}
    </div>
  );
}

export default VoucherSelection;
