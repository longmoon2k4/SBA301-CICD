import { useState } from 'react';
import { Form, Button, Row, Col } from 'react-bootstrap';
import AddressFormModal from './AddressFormModal.jsx';

function AddressSelection({ addresses, selectedAddressId, selectedAddress, onChange, onAddAddress }) {
  const [showModal, setShowModal] = useState(false);

  const handleSaveAddress = (newAddress) => {
    if (onAddAddress) {
      onAddAddress(newAddress);
    }
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <span className="text-muted small">Chọn địa chỉ nhận hàng</span>
        <Button variant="outline-dark" size="sm" className="rounded-0" onClick={() => setShowModal(true)}>
          + Thêm địa chỉ mới
        </Button>
      </div>
      
      <Form.Select
        value={selectedAddressId ?? ''}
        onChange={(event) => onChange(Number(event.target.value))}
        className="mb-3 rounded-0"
      >
        {addresses.map((address) => (
          <option key={address.id} value={address.id}>
            {address.recipientName} - {address.district} {address.isDefault ? '(Mặc định)' : ''}
          </option>
        ))}
      </Form.Select>

      {selectedAddress ? (
        <div className="checkoutx-address mt-2 p-3 border bg-light">
          <p className="mb-1 fw-bold">{selectedAddress.recipientName} / {selectedAddress.phone}</p>
          <p className="mb-0 text-muted small">
            {selectedAddress.street}, {selectedAddress.ward && `${selectedAddress.ward}, `}
            <br/>
            {selectedAddress.district}, {selectedAddress.province}
          </p>
        </div>
      ) : null}

      <AddressFormModal 
        show={showModal} 
        onHide={() => setShowModal(false)} 
        onSave={handleSaveAddress} 
      />
    </div>
  );
}

export default AddressSelection;
