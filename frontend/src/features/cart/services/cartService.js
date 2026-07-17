import api from '../../../shared/services/axios.js';

const BASE = '/my/cart';

/**
 * API: Lấy toàn bộ snapshot giỏ hàng hiện tại từ backend.
 * Backend trả về: { cartId, userId, channel, items: [...] }
 * @returns {Object} Dữ liệu giỏ hàng
 */
export async function getCartSnapshot() {
  const response = await api.get(BASE);
  // BE trả trực tiếp MyCartResponse (không bọc ApiResponse)
  return response.data;
}

/**
 * API: Thêm một sản phẩm vào giỏ hàng.
 * Nếu sản phẩm đã tồn tại (theo variantId), tăng số lượng.
 * @param {Object} itemPayload - Cần có: { variantId, quantity }
 * @returns {{ success: boolean, data: Object }}
 */
export async function addItemAPI(itemPayload) {
  const response = await api.post(`${BASE}/items`, {
    variantId: itemPayload.variantId,
    quantity: itemPayload.quantity || 1,
  });
  return response.data; // { success, data: cartItem }
}

/**
 * API: Cập nhật số lượng của một sản phẩm trong giỏ.
 * @param {number} itemId - ID của cart item
 * @param {number} quantity - Số lượng mới
 * @returns {{ success: boolean, data?: Object }}
 */
export async function updateQuantityAPI(itemId, quantity) {
  const response = await api.put(`${BASE}/items/${itemId}`, { quantity });
  // BE trả ApiResponse { success, data: cartItem }
  return { success: response.data.success, data: response.data.data };
}

/**
 * API: Xóa một sản phẩm khỏi giỏ hàng.
 * @param {number} itemId - ID của cart item cần xóa
 * @returns {{ success: boolean }}
 */
export async function removeItemAPI(itemId) {
  await api.delete(`${BASE}/items/${itemId}`);
  return { success: true };
}

/**
 * API: Xóa tất cả sản phẩm hết hàng hoặc ngừng kinh doanh.
 * @returns {{ success: boolean }}
 */
export async function clearUnavailableItemsAPI() {
  await api.delete(`${BASE}/items/unavailable`);
  return { success: true };
}

/**
 * API: Xóa các sản phẩm đã được đặt mua thành công khỏi giỏ hàng.
 * @param {number[]} orderedItemIds - Danh sách ID vừa mua
 * @returns {{ success: boolean }}
 */
export async function clearOrderedItemsAPI(orderedItemIds) {
  if (!orderedItemIds || orderedItemIds.length === 0) {
    return { success: true };
  }
  await api.delete(`${BASE}/items/batch`, { data: { ids: orderedItemIds } });
  return { success: true };
}
