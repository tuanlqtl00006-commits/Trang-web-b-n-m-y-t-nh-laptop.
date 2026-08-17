const WISHLIST_KEY = "computerstore-wishlist";

export function getWishlistIds() {
  try {
    const parsed = JSON.parse(localStorage.getItem(WISHLIST_KEY) || "[]");
    return Array.isArray(parsed) ? parsed.map(Number).filter(Number.isFinite) : [];
  } catch {
    return [];
  }
}

export function setWishlistIds(ids) {
  const normalized = [...new Set(ids.map(Number).filter(Number.isFinite))];
  localStorage.setItem(WISHLIST_KEY, JSON.stringify(normalized));
  window.dispatchEvent(new CustomEvent("wishlist-updated", { detail: normalized }));
  return normalized;
}

export function isWishlisted(id) {
  return getWishlistIds().includes(Number(id));
}

export function toggleWishlist(id) {
  const productId = Number(id);
  const ids = getWishlistIds();
  const next = ids.includes(productId)
    ? ids.filter((itemId) => itemId !== productId)
    : [...ids, productId];
  return setWishlistIds(next);
}

export function clearWishlist() {
  localStorage.removeItem(WISHLIST_KEY);
  window.dispatchEvent(new CustomEvent("wishlist-updated", { detail: [] }));
}
