// Shared validation helpers.
// Email: standard format, must contain @ and a domain with a dot (full address).
// Phone: exactly 10 digits, Vietnamese mobile numbers start with 0.

export const EMAIL_REGEX = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
export const PHONE_REGEX = /^0\d{9}$/;

export function isValidEmail(email) {
  return typeof email === "string" && EMAIL_REGEX.test(email.trim());
}

export function isValidPhone(phone) {
  return typeof phone === "string" && PHONE_REGEX.test(phone.trim());
}

export function emailError(email) {
  if (!email || !email.trim()) return "Vui lòng nhập email!";
  if (!isValidEmail(email)) return "Email không đúng định dạng (VD: ten@example.com)!";
  return "";
}

export function phoneError(phone) {
  if (!phone || !phone.trim()) return "Vui lòng nhập số điện thoại!";
  if (!isValidPhone(phone)) return "Số điện thoại phải gồm đúng 10 chữ số và bắt đầu bằng 0!";
  return "";
}
