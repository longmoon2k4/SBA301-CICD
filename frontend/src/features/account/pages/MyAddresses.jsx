import { useState, useEffect } from 'react';
import { Container, Row, Col, Button, Badge, Spinner, Alert, Modal } from 'react-bootstrap';
import { GeoAlt, PlusLg, HouseDoor, CheckCircleFill, Trash, ExclamationTriangle } from 'react-bootstrap-icons';
import { getAddressesAPI, addAddressAPI, deleteAddressAPI } from '../../checkout/services/checkoutService.js';
import AddressFormModal from '../../checkout/components/AddressFormModal.jsx';

export default function MyAddresses() {
  const [addresses, setAddresses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showAddModal, setShowAddModal] = useState(false);

  // Custom Delete Confirm Modal State
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [deleting, setDeleting] = useState(false);

  // Custom Notice/Alert Modal State
  const [noticeModal, setNoticeModal] = useState({ show: false, title: '', message: '', type: 'danger' });

  useEffect(() => {
    fetchAddresses();
  }, []);

  const fetchAddresses = async () => {
    try {
      setLoading(true);
      setError('');
      const res = await getAddressesAPI();
      setAddresses(res.data || []);
    } catch (err) {
      console.error('Lỗi khi tải sổ địa chỉ:', err);
      setError(err.response?.data?.message || 'Không thể tải danh sách địa chỉ.');
    } finally {
      setLoading(false);
    }
  };

  const handleSaveAddress = async (formData) => {
    try {
      const res = await addAddressAPI(formData);
      if (res.data) {
        setShowAddModal(false);
        fetchAddresses();
      }
    } catch (err) {
      console.error('Lỗi khi thêm địa chỉ mới:', err);
      setNoticeModal({
        show: true,
        title: 'Lỗi thêm địa chỉ',
        message: err.response?.data?.message || err.message || 'Không thể thêm địa chỉ mới.',
        type: 'danger',
      });
    }
  };

  const handleConfirmDelete = async () => {
    if (!deleteTarget) return;
    try {
      setDeleting(true);
      await deleteAddressAPI(deleteTarget.id);
      setDeleteTarget(null);
      fetchAddresses();
    } catch (err) {
      console.error('Lỗi khi xóa địa chỉ:', err);
      const msg = err.response?.data?.message || err.message || 'Không thể xóa địa chỉ này.';
      setDeleteTarget(null);
      setNoticeModal({
        show: true,
        title: 'Không thể xóa địa chỉ',
        message: msg,
        type: 'danger',
      });
    } finally {
      setDeleting(false);
    }
  };

  if (loading) {
    return (
      <Container className="d-flex flex-column align-items-center justify-content-center py-5 my-5" style={{ minHeight: '60vh' }}>
        <Spinner animation="border" variant="dark" size="lg" className="mb-3" />
        <p className="text-muted fw-bold text-uppercase" style={{ letterSpacing: '0.1em' }}>Đang tải sổ địa chỉ...</p>
      </Container>
    );
  }

  return (
    <div className="bg-light py-5" style={{ minHeight: '80vh' }}>
      <Container>
        {/* Title Banner */}
        <div className="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4 pb-2 border-bottom border-dark border-3">
          <div>
            <h1 className="fw-bold text-uppercase mb-1" style={{ fontFamily: 'Space Grotesk, sans-serif', letterSpacing: '0.02em' }}>
              <GeoAlt className="me-2 mb-1" /> Sổ địa chỉ
            </h1>
            <p className="text-muted mb-0">Quản lý danh sách địa chỉ nhận hàng của bạn</p>
          </div>
          <Button
            variant="dark"
            className="rounded-0 fw-bold text-uppercase border-2 d-inline-flex align-items-center gap-2"
            onClick={() => setShowAddModal(true)}
          >
            <PlusLg /> Thêm địa chỉ mới
          </Button>
        </div>

        {error && (
          <Alert variant="danger" className="rounded-0 border-2 mb-4">
            {error}
          </Alert>
        )}

        {/* Empty State */}
        {addresses.length === 0 ? (
          <div className="checkoutx-panel p-5 border border-dark border-3 bg-white text-center my-4" style={{ boxShadow: '8px 8px 0px #000' }}>
            <HouseDoor className="fs-1 text-muted mb-3" />
            <h4 className="fw-bold text-uppercase mb-2">Chưa có địa chỉ nào</h4>
            <p className="text-muted mb-4">Bạn chưa lưu địa chỉ nhận hàng nào. Hãy thêm địa chỉ đầu tiên để thuận tiện cho việc thanh toán.</p>
            <Button
              variant="dark"
              className="rounded-0 text-uppercase fw-bold px-4 py-2"
              onClick={() => setShowAddModal(true)}
            >
              <PlusLg className="me-1" /> Thêm địa chỉ mới
            </Button>
          </div>
        ) : (
          /* Address List Grid */
          <Row className="g-4">
            {addresses.map((addr) => (
              <Col key={addr.id} xs={12} md={6}>
                <div
                  className="checkoutx-panel p-4 border border-dark border-3 bg-white h-100 d-flex flex-column justify-content-between"
                  style={{ boxShadow: '6px 6px 0px #000' }}
                >
                  <div>
                    <div className="d-flex align-items-center justify-content-between mb-3 pb-2 border-bottom border-dark border-2">
                      <span className="fw-bold fs-5" style={{ fontFamily: 'Space Grotesk, sans-serif' }}>
                        {addr.recipientName}
                      </span>
                      <div className="d-flex align-items-center gap-2">
                        {addr.isDefault && (
                          <Badge bg="dark" className="rounded-0 text-uppercase px-2 py-1 d-inline-flex align-items-center gap-1">
                            <CheckCircleFill size={12} /> Mặc định
                          </Badge>
                        )}
                        <Button
                          variant="outline-danger"
                          size="sm"
                          className="rounded-0 p-1 d-inline-flex align-items-center justify-content-center"
                          title="Xóa địa chỉ"
                          onClick={() => setDeleteTarget(addr)}
                        >
                          <Trash size={14} />
                        </Button>
                      </div>
                    </div>

                    <p className="mb-2 text-dark">
                      <strong>Số điện thoại:</strong> {addr.phone}
                    </p>
                    <p className="mb-0 text-muted">
                      <strong>Địa chỉ:</strong> {addr.street}, {addr.ward}, {addr.district}, {addr.province}
                    </p>
                  </div>
                </div>
              </Col>
            ))}
          </Row>
        )}
      </Container>

      {/* Modal Add Address */}
      <AddressFormModal
        show={showAddModal}
        onHide={() => setShowAddModal(false)}
        onSave={handleSaveAddress}
      />

      {/* Custom Delete Confirmation Modal */}
      <Modal
        show={Boolean(deleteTarget)}
        onHide={() => !deleting && setDeleteTarget(null)}
        centered
        backdrop="static"
      >
        <div className="border border-dark border-3 bg-white" style={{ boxShadow: '8px 8px 0px #000' }}>
          <Modal.Header closeButton={!deleting} className="border-bottom border-dark border-2 bg-light">
            <Modal.Title className="fw-bold text-uppercase" style={{ fontFamily: 'Space Grotesk, sans-serif' }}>
              Xác nhận xóa địa chỉ
            </Modal.Title>
          </Modal.Header>
          <Modal.Body className="p-4 text-center">
            <div className="text-danger mb-3">
              <ExclamationTriangle size={54} />
            </div>
            <h5 className="fw-bold mb-3">Bạn có chắc chắn muốn xóa địa chỉ này?</h5>
            {deleteTarget && (
              <div className="p-3 bg-light border border-dark border-2 text-start mb-2">
                <p className="fw-bold mb-1">{deleteTarget.recipientName} - {deleteTarget.phone}</p>
                <p className="text-muted small mb-0">
                  {deleteTarget.street}, {deleteTarget.ward}, {deleteTarget.district}, {deleteTarget.province}
                </p>
              </div>
            )}
            <p className="text-muted small mb-0 mt-2">Hành động này không thể hoàn tác sau khi thực hiện.</p>
          </Modal.Body>
          <Modal.Footer className="border-top border-dark border-2 d-flex justify-content-end gap-2 bg-light">
            <Button
              variant="outline-dark"
              className="rounded-0 text-uppercase fw-bold px-4"
              onClick={() => setDeleteTarget(null)}
              disabled={deleting}
            >
              Hủy
            </Button>
            <Button
              variant="danger"
              className="rounded-0 text-uppercase fw-bold px-4 border-2"
              onClick={handleConfirmDelete}
              disabled={deleting}
            >
              {deleting ? (
                <>
                  <Spinner animation="border" size="sm" className="me-2" />
                  Đang xóa...
                </>
              ) : (
                'Xác nhận xóa'
              )}
            </Button>
          </Modal.Footer>
        </div>
      </Modal>

      {/* Custom Alert/Notice Modal */}
      <Modal
        show={noticeModal.show}
        onHide={() => setNoticeModal({ ...noticeModal, show: false })}
        centered
      >
        <div className="border border-dark border-3 bg-white" style={{ boxShadow: '8px 8px 0px #000' }}>
          <Modal.Header closeButton className="border-bottom border-dark border-2 bg-light">
            <Modal.Title className="fw-bold text-uppercase" style={{ fontFamily: 'Space Grotesk, sans-serif' }}>
              {noticeModal.title}
            </Modal.Title>
          </Modal.Header>
          <Modal.Body className="p-4 text-center">
            <div className={`text-${noticeModal.type} mb-3`}>
              <ExclamationTriangle size={48} />
            </div>
            <p className="mb-0 fs-6 fw-bold text-dark">{noticeModal.message}</p>
          </Modal.Body>
          <Modal.Footer className="border-top border-dark border-2 bg-light">
            <Button
              variant="dark"
              className="w-100 rounded-0 text-uppercase fw-bold py-2"
              onClick={() => setNoticeModal({ ...noticeModal, show: false })}
            >
              Đã hiểu
            </Button>
          </Modal.Footer>
        </div>
      </Modal>
    </div>
  );
}
