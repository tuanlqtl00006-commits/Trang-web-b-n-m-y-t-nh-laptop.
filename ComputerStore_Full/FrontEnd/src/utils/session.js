// Centralized session storage helper.
//
// WHY sessionStorage instead of localStorage:
// localStorage is shared across every tab of the same browser. If you open
// 3 tabs and log into 3 different accounts (admin / staff / customer) to
// test the site, each login OVERWRITES the previous one in localStorage,
// so refreshing (F5) an earlier tab shows you logged out or logged in as
// the wrong account.
//
// sessionStorage is scoped to a single browser tab: it still survives a
// full page refresh (F5) in that tab, but a new tab (or another tab you
// already had open) gets its own independent copy. That lets you run
// several accounts side by side without them stepping on each other.
//
// NOTE: sessionStorage is NOT shared with a duplicated tab created via
// "Duplicate Tab" in some browsers (that copies it) but IS independent
// for any tab opened via a normal new-tab/new-window action or a typed
// URL, which covers the "3 accounts at once" use case.

const USER_KEY = "user";
const TOKEN_KEY = "token";
const CART_KEY = "cart";

export function getUser() {
  try {
    return JSON.parse(sessionStorage.getItem(USER_KEY) || "null");
  } catch {
    return null;
  }
}

export function setUser(user) {
  sessionStorage.setItem(USER_KEY, JSON.stringify(user));
}

export function getToken() {
  return sessionStorage.getItem(TOKEN_KEY);
}

export function setToken(token) {
  if (token) sessionStorage.setItem(TOKEN_KEY, token);
}

export function clearSession() {
  sessionStorage.removeItem(USER_KEY);
  sessionStorage.removeItem(TOKEN_KEY);
  sessionStorage.removeItem(CART_KEY);
}

export function getCart() {
  try {
    return JSON.parse(sessionStorage.getItem(CART_KEY) || "[]");
  } catch {
    return [];
  }
}

export function setCart(cart) {
  sessionStorage.setItem(CART_KEY, JSON.stringify(cart));
}

export function clearCart() {
  sessionStorage.removeItem(CART_KEY);
}
