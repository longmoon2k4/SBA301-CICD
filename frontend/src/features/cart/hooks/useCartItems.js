import { useState, useEffect, useMemo, useCallback } from 'react';
import { addItemAPI, updateQuantityAPI, removeItemAPI, clearUnavailableItemsAPI } from '../services/cartService.js';
import { getItemsSubtotal } from '../utils/cartMath.js';

export const isPurchasable = (item) => item.isActive && item.stockQuantity > 0;

export function useCartItems({ setCartAlert }) {
  const [items, setItems] = useState([]);
  const [selectedItemIds, setSelectedItemIds] = useState([]);
  const [isUpdatingItems, setIsUpdatingItems] = useState(false);

  // Lọc bỏ ID đang chọn nếu sản phẩm đó bị xóa hoặc hết hàng
  useEffect(() => {
    setSelectedItemIds((previousIds) =>
      previousIds.filter((id) => items.some((item) => item.id === id && isPurchasable(item))),
    );
  }, [items]);

  // Đồng bộ danh sách ID đã chọn lên sessionStorage
  useEffect(() => {
    sessionStorage.setItem('cart_selected_items', JSON.stringify(selectedItemIds));
  }, [selectedItemIds]);

  // Computed values (khai báo trước các hàm sử dụng chúng)
  const purchasableItems = useMemo(() => items.filter(isPurchasable), [items]);
  const unavailableCount = useMemo(() => items.filter((item) => !isPurchasable(item)).length, [items]);
  const selectedItems = useMemo(() => items.filter((item) => selectedItemIds.includes(item.id)), [items, selectedItemIds]);
  const allPurchasableSelected = useMemo(
    () => purchasableItems.length > 0 && purchasableItems.every((item) => selectedItemIds.includes(item.id)),
    [purchasableItems, selectedItemIds],
  );
  const itemsSubtotal = useMemo(() => getItemsSubtotal(selectedItems), [selectedItems]);

  const changeItemQuantity = useCallback((id, nextQuantity) => {
    if (isUpdatingItems) return;

    const itemToUpdate = items.find((i) => i.id === id);
    if (!itemToUpdate) return;

    const boundedQuantity = Math.max(1, Math.min(nextQuantity, itemToUpdate.stockQuantity));
    if (boundedQuantity === itemToUpdate.quantity) return;

    setIsUpdatingItems(true);

    // Optimistic UI
    setItems((prev) =>
      prev.map((item) => (item.id === id ? { ...item, quantity: boundedQuantity } : item)),
    );

    updateQuantityAPI(id, boundedQuantity)
      .then((res) => {
        if (!res.success) {
          // Rollback nếu API trả failure
          setItems((prev) =>
            prev.map((item) => (item.id === id ? { ...item, quantity: itemToUpdate.quantity } : item)),
          );
          setCartAlert?.({ title: 'Lỗi', message: 'Không thể cập nhật số lượng.', type: 'danger' });
        }
      })
      .catch((err) => {
        console.error('Cập nhật số lượng lỗi:', err);
        setItems((prev) =>
          prev.map((item) => (item.id === id ? { ...item, quantity: itemToUpdate.quantity } : item)),
        );
        setCartAlert?.({ title: 'Lỗi', message: 'Không thể cập nhật số lượng.', type: 'danger' });
      })
      .finally(() => setIsUpdatingItems(false));
  }, [isUpdatingItems, items, setCartAlert]);

  const addToCart = useCallback(async (newItem) => {
    if (!newItem.isActive || newItem.stockQuantity <= 0) {
      setCartAlert?.({
        title: 'Thông báo lỗi',
        message: `Sản phẩm mẫu mã ${newItem.sku} đã hết hàng hoặc ngừng kinh doanh!`,
        type: 'danger',
      });
      return;
    }

    setIsUpdatingItems(true);
    try {
      await addItemAPI(newItem);

      setItems((previousItems) => {
        const existingIndex = previousItems.findIndex((item) => {
          const isSameVariant = item.variantId && newItem.variantId && item.variantId === newItem.variantId;
          const isSameSku = item.sku && newItem.sku && item.sku === newItem.sku;
          const isSameAttributes =
            item.productId === newItem.productId &&
            item.color === newItem.color &&
            item.size === newItem.size;
          return isSameVariant || isSameSku || isSameAttributes;
        });

        if (existingIndex !== -1) {
          const currentItem = previousItems[existingIndex];
          if (currentItem.quantity >= currentItem.stockQuantity) {
            setCartAlert?.({
              title: 'Giới hạn số lượng',
              message: `Mẫu mã ${currentItem.sku} đã đạt giới hạn tồn kho tối đa (${currentItem.stockQuantity} sản phẩm)!`,
              type: 'warning',
            });
            return previousItems;
          }

          setSelectedItemIds((prevSelected) => {
            if (prevSelected.includes(currentItem.id)) return prevSelected;
            return [...prevSelected, currentItem.id];
          });

          return previousItems.map((item, idx) => {
            if (idx === existingIndex) {
              const addedQty = newItem.quantity || 1;
              const newQty = Math.min(item.quantity + addedQty, item.stockQuantity);
              return { ...item, quantity: newQty };
            }
            return item;
          });
        }

        const newId = previousItems.length > 0 ? Math.max(...previousItems.map((i) => i.id)) + 1 : 1001;
        const initialQty = Math.min(newItem.quantity || 1, newItem.stockQuantity);

        const createdItem = {
          id: newId,
          cartItemId: newId,
          ...newItem,
          quantity: initialQty,
        };

        setSelectedItemIds((prevSelected) => [...prevSelected, newId]);
        return [...previousItems, createdItem];
      });
    } catch (error) {
      setCartAlert?.({
        title: 'Lỗi',
        message: 'Thêm vào giỏ hàng thất bại. Vui lòng thử lại.',
        type: 'danger',
      });
    } finally {
      setIsUpdatingItems(false);
    }
  }, [setCartAlert]);

  const removeItem = useCallback(async (itemId) => {
    setIsUpdatingItems(true);
    try {
      await removeItemAPI(itemId);
      setItems((previousItems) => previousItems.filter((item) => item.id !== itemId));
      setSelectedItemIds((previousIds) => previousIds.filter((id) => id !== itemId));
    } catch (error) {
      setCartAlert?.({
        title: 'Lỗi',
        message: 'Không thể xoá sản phẩm. Vui lòng thử lại.',
        type: 'danger',
      });
    } finally {
      setIsUpdatingItems(false);
    }
  }, [setCartAlert]);

  const clearUnavailableItems = useCallback(async () => {
    const unavailableIds = items.filter((item) => !isPurchasable(item)).map((item) => item.id);
    if (unavailableIds.length === 0) return;

    setIsUpdatingItems(true);
    try {
      await clearUnavailableItemsAPI(unavailableIds);
      setItems((previousItems) => previousItems.filter(isPurchasable));
      setSelectedItemIds((previousIds) => previousIds.filter((id) => !unavailableIds.includes(id)));
    } catch (error) {
      setCartAlert?.({
        title: 'Lỗi',
        message: 'Không thể xoá các sản phẩm hết hàng. Vui lòng thử lại.',
        type: 'danger',
      });
    } finally {
      setIsUpdatingItems(false);
    }
  }, [items, setCartAlert]);

  const toggleSelectAll = useCallback(() => {
    if (allPurchasableSelected) {
      setSelectedItemIds([]);
      return;
    }
    setSelectedItemIds(purchasableItems.map((item) => item.id));
  }, [allPurchasableSelected, purchasableItems]);

  const toggleItem = useCallback((itemId) => {
    setSelectedItemIds((previousIds) => {
      if (previousIds.includes(itemId)) {
        return previousIds.filter((id) => id !== itemId);
      }
      return [...previousIds, itemId];
    });
  }, []);

  return {
    items,
    setItems,
    selectedItemIds,
    setSelectedItemIds,
    isUpdatingItems,
    selectedItems,
    purchasableItems,
    unavailableCount,
    allPurchasableSelected,
    itemsSubtotal,
    changeItemQuantity,
    addToCart,
    removeItem,
    clearUnavailableItems,
    toggleSelectAll,
    toggleItem,
  };
}
