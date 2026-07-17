/**
 * Tính số tiền được giảm giá dựa trên loại Voucher.
 * @param {Object} voucher - Thông tin mã giảm giá (type, amount, maxDiscount)
 * @param {number} itemsSubtotal - Tổng tiền hàng (chưa phí ship)
 * @param {number} shippingFee - Phí giao hàng
 * @returns {number} Số tiền được giảm
 */
export function getDiscountAmount({ voucher, itemsSubtotal, shippingFee }) {
  if (!voucher) {
    return 0;
  }

  if (voucher.type === 'fixed') {
    return Math.min(voucher.amount, itemsSubtotal + shippingFee);
  }

  if (voucher.type === 'percent') {
    const percentDiscount = Math.round((itemsSubtotal * voucher.amount) / 100);
    if (voucher.maxDiscount) {
      return Math.min(percentDiscount, voucher.maxDiscount);
    }
    return percentDiscount;
  }

  if (voucher.type === 'shipping') {
    return Math.min(voucher.amount, shippingFee);
  }

  return 0;
}

/**
 * Tổng hợp chi phí cuối cùng của đơn hàng.
 * @param {number} itemsSubtotal - Tổng tiền hàng
 * @param {number} shippingFee - Phí ship
 * @param {number} discountAmount - Tổng tiền được giảm
 * @returns {{ itemsSubtotal, shippingFee, discountAmount, finalTotal }}
 */
export function getCartTotals({ itemsSubtotal, shippingFee, discountAmount }) {
  const finalTotal = Math.max(itemsSubtotal + shippingFee - discountAmount, 0);

  return {
    itemsSubtotal,
    shippingFee,
    discountAmount,
    finalTotal,
  };
}
