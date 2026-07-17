import api from '../../../shared/services/axios.js';

const BASE_CHECKOUT = '/my/checkout';

/**
 * API: Thêm một địa chỉ giao hàng mới.
 * Nếu isDefault = true, gỡ isDefault của tất cả địa chỉ cũ.
 * @param {Object} addressData - Thông tin địa chỉ
 * @returns {{ success: boolean, data: Object }}
 */
export async function addAddressAPI(addressData) {
  const response = await api.post(`${BASE_CHECKOUT}/addresses`, addressData);
  return response.data; // { success, data: address }
}

/**
 * API: Lấy danh sách địa chỉ giao hàng của người dùng.
 * @returns {{ success: boolean, data: Object[] }}
 */
export async function getAddressesAPI() {
  const response = await api.get(`${BASE_CHECKOUT}/addresses`);
  return response.data; // { success, data: [...] }
}

/**
 * API: Lấy danh sách phương thức vận chuyển.
 * @returns {{ success: boolean, data: Object[] }}
 */
export async function getShippingMethodsAPI() {
  const response = await api.get(`${BASE_CHECKOUT}/shipping-methods`);
  return response.data; // { success, data: [...] }
}

/**
 * API: Kiểm tra và áp dụng mã giảm giá.
 * @param {string} code - Mã voucher
 * @returns {{ success: boolean, data: Object }}
 * @throws {Error} Nếu mã không hợp lệ
 */
export async function applyVoucherAPI(code) {
  try {
    const response = await api.post(`${BASE_CHECKOUT}/vouchers/apply`, { code });
    return response.data; // { success, data: voucher }
  } catch (err) {
    const message = err.response?.data?.message
      || 'Mã giảm giá không hợp lệ hoặc đã hết hạn.';
    throw new Error(message);
  }
}
