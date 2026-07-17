import { useState, useEffect, useCallback } from 'react';
import { getCartSnapshot, updateQuantityAPI } from '../services/cartService.js';
import { useCartItems, isPurchasable } from './useCartItems.js';
import { useNavigate } from 'react-router-dom';
import { getAuthState, clearAuthState } from '../../../shared/utils/auth.js';

export { isPurchasable };

export function useCartExperience() {
  const [loading, setLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState('');
  const [isUnauthenticated, setIsUnauthenticated] = useState(false);
  const [cartAlert, setCartAlert] = useState(null);
  const [stockSyncNotice, setStockSyncNotice] = useState('');
  const [lastSyncedAt, setLastSyncedAt] = useState(null);
  const navigate = useNavigate();

  const cartItems = useCartItems({ setCartAlert });
  const isUpdating = cartItems.isUpdatingItems;

  const refreshCartSnapshot = useCallback(async () => {
    const snapshot = await getCartSnapshot();

    let adjustedCount = 0;
    const adjustedItems = await Promise.all(
      snapshot.items.map(async (item) => {
        if (item.quantity > item.stockQuantity && item.stockQuantity > 0) {
          adjustedCount++;
          try {
            await updateQuantityAPI(item.id, item.stockQuantity);
          } catch (err) {
            console.error(`Không thể đồng bộ số lượng sản phẩm ${item.id}`, err);
          }
          return { ...item, quantity: item.stockQuantity };
        }
        return item;
      }),
    );

    const availableItemIds = new Set(adjustedItems.filter(isPurchasable).map((item) => item.id));
    const nextSelectedIds = cartItems.selectedItemIds.filter((id) => availableItemIds.has(id));
    const removedCount = cartItems.selectedItemIds.length - nextSelectedIds.length;

    cartItems.setItems(adjustedItems);
    cartItems.setSelectedItemIds(nextSelectedIds);
    setLastSyncedAt(new Date());
    setStockSyncNotice(
      removedCount > 0 || adjustedCount > 0
        ? `Đã cập nhật tồn kho. ${removedCount} sản phẩm bị loại, ${adjustedCount} sản phẩm được điều chỉnh số lượng.`
        : 'Tồn kho đã được đồng bộ lại từ máy chủ.',
    );

    return { removedCount, adjustedCount, nextSelectedIds };
  }, [cartItems]);

  useEffect(() => {
    let mounted = true;

    async function loadData() {
      setLoading(true);
      setErrorMessage('');
      setIsUnauthenticated(false);

      const { isAuthenticated } = getAuthState();
      if (!isAuthenticated) {
        if (mounted) {
          setIsUnauthenticated(true);
          setLoading(false);
        }
        return;
      }

      try {
        const snapshot = await getCartSnapshot();
        if (!mounted) return;

        cartItems.setItems(snapshot.items);

        const availableIds = snapshot.items.filter(isPurchasable).map((item) => item.id);
        const storedSelected = sessionStorage.getItem('cart_selected_items');
        if (storedSelected) {
          const parsedSelected = JSON.parse(storedSelected);
          const validSelected = parsedSelected.filter((id) => availableIds.includes(id));
          cartItems.setSelectedItemIds(validSelected);
        } else {
          cartItems.setSelectedItemIds(availableIds);
        }

        setLastSyncedAt(new Date());
        setStockSyncNotice('Tồn kho đã được tải mới từ máy chủ.');
      } catch (_error) {
        const status = _error.response?.status;
        if (status === 401 || status === 403) {
          clearAuthState();
          if (mounted) setIsUnauthenticated(true);
        } else {
          setErrorMessage('Không thể tải giỏ hàng. Vui lòng thử lại.');
        }
      } finally {
        if (mounted) {
          setLoading(false);
        }
      }
    }

    loadData();

    return () => {
      mounted = false;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps -- Chỉ chạy 1 lần khi mount, không cần theo dõi cartItems
  }, []);

  const proceedToCheckout = useCallback(async () => {
    const { removedCount, adjustedCount, nextSelectedIds } = await refreshCartSnapshot();

    if (nextSelectedIds.length === 0) {
      setCartAlert({
        title: 'Giỏ hàng trống',
        message: 'Bạn chưa chọn sản phẩm nào để thanh toán.',
        type: 'warning',
      });
      return;
    }

    if (removedCount > 0 || adjustedCount > 0) {
      setCartAlert({
        title: 'Tồn kho thay đổi',
        message: 'Một số sản phẩm đã thay đổi số lượng. Vui lòng kiểm tra lại trước khi tiếp tục.',
        type: 'warning',
      });
      return;
    }

    sessionStorage.setItem('checkout_selected_items', JSON.stringify(nextSelectedIds));
    navigate('/checkout');
  }, [refreshCartSnapshot, navigate]);

  const reloadCart = useCallback(async () => {
    setLoading(true);
    setErrorMessage('');
    setIsUnauthenticated(false);

    const { isAuthenticated } = getAuthState();
    if (!isAuthenticated) {
      setIsUnauthenticated(true);
      setLoading(false);
      return;
    }

    try {
      await refreshCartSnapshot();
    } catch (_error) {
      const status = _error.response?.status;
      if (status === 401 || status === 403) {
        clearAuthState();
        setIsUnauthenticated(true);
      } else {
        setErrorMessage('Tải lại giỏ hàng thất bại.');
      }
    } finally {
      setLoading(false);
    }
  }, [refreshCartSnapshot]);

  return {
    loading,
    isUpdating,
    errorMessage,
    isUnauthenticated,
    cartAlert,
    setCartAlert,
    stockSyncNotice,
    lastSyncedAt,

    items: cartItems.items,
    selectedItemIds: cartItems.selectedItemIds,
    purchasableItems: cartItems.purchasableItems,
    unavailableCount: cartItems.unavailableCount,
    allPurchasableSelected: cartItems.allPurchasableSelected,
    itemsSubtotal: cartItems.itemsSubtotal,

    toggleSelectAll: cartItems.toggleSelectAll,
    toggleItem: cartItems.toggleItem,
    changeItemQuantity: cartItems.changeItemQuantity,
    addToCart: cartItems.addToCart,
    removeItem: cartItems.removeItem,
    clearUnavailableItems: cartItems.clearUnavailableItems,
    reloadCart,
    proceedToCheckout,
  };
}
