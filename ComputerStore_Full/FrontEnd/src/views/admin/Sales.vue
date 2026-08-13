<template>
  <div>
    <h4 class="fw-bold mb-4"><i class="bi bi-cash-coin me-2"></i>Bán hàng tại quầy</h4>

    <div class="row g-4">
      <!-- Chọn sản phẩm -->
      <div class="col-lg-7">
        <div class="card p-3">
          <input
            v-model="search"
            type="text"
            class="form-control mb-3"
            placeholder="Tìm sản phẩm theo tên..."
          />
          <div class="sales-product-grid">
            <div
              v-for="p in filteredProducts"
              :key="p.id"
              class="sales-product-card"
              :class="{ disabled: p.stock <= 0 }"
              @click="addToCart(p)"
            >
              <img :src="productImage(p)" :alt="p.name" />
              <div class="sales-product-name" :title="p.name">{{ p.name }}</div>
              <div class="sales-product-price">{{ formatPrice(p.price) }}₫</div>
              <div class="small" :class="p.stock > 0 ? 'text-success' : 'text-danger'">
                {{ p.stock > 0 ? `Còn ${p.stock}` : "Hết hàng" }}
              </div>
            </div>
            <div v-if="filteredProducts.length === 0" class="text-muted text-center py-4 w-100">
              Không tìm thấy sản phẩm.
            </div>
          </div>
        </div>
      </div>

      <!-- Giỏ hàng tại quầy -->
      <div class="col-lg-5">
        <div class="card p-3">
          <h6 class="fw-bold mb-3">Hóa đơn</h6>

          <div v-if="cart.length === 0" class="text-muted text-center py-4">
            Chưa chọn sản phẩm nào. Bấm vào sản phẩm bên trái để thêm.
          </div>

          <div v-for="item in cart" :key="item.id" class="cart-line">
            <div class="flex-fill">
              <div class="fw-semibold small">{{ item.name }}</div>
              <div class="text-muted small">{{ formatPrice(item.price) }}₫</div>
            </div>
            <input
              v-model.number="item.quantity"
              type="number"
              min="1"
              :max="item.stock"
              class="form-control form-control-sm cart-qty"
            />
            <button class="btn btn-sm btn-outline-danger" @click="removeFromCart(item.id)">
              <i class="bi bi-x"></i>
            </button>
          </div>

          <hr v-if="cart.length" />

          <div v-if="cart.length" class="d-flex justify-content-between small mb-1">
            <span>Tạm tính</span><span>{{ formatPrice(subtotal) }}₫</span>
          </div>
          <div v-if="cart.length" class="d-flex justify-content-between small mb-2">
            <span>VAT (10%)</span><span>{{ formatPrice(vat) }}₫</span>
          </div>
          <div v-if="cart.length" class="d-flex justify-content-between fw-bold mb-3">
            <span>Tổng cộng</span><span class="price-text">{{ formatPrice(total) }}₫</span>
          </div>

          <div v-if="cart.length">
            <div class="mb-2">
              <label class="form-label">Tên khách hàng</label>
              <input v-model.trim="customer.name" type="text" class="form-control" placeholder="Khách lẻ" />
            </div>
            <div class="mb-2">
              <label class="form-label">Số điện thoại</label>
              <input v-model.trim="customer.phone" type="text" class="form-control" maxlength="10" />
              <div v-if="phoneErr" class="field-error">{{ phoneErr }}</div>
            </div>
            <div class="mb-3">
              <label class="form-label">Phương thức thanh toán</label>
              <select v-model="paymentMethod" class="form-select">
                <option value="Cash">Tiền mặt</option>
                <option value="Credit Card">Thẻ</option>
                <option value="Bank Transfer">Chuyển khoản</option>
              </select>
            </div>
            <button class="btn btn-dark w-100" :disabled="submitting" @click="checkout">
              {{ submitting ? "Đang xử lý..." : "Lập hóa đơn & thanh toán" }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal xem hóa đơn sau khi thanh toán -->
    <div v-if="lastInvoice" class="modal-backdrop-custom" @click.self="lastInvoice = null">
      <div class="modal-box invoice-box" style="width: 520px">
        <div class="invoice-header">
          <div class="fw-bold fs-5">🖥️ ComputerStore</div>
          <div class="text-muted small">123 Nguyễn Văn A, Q.1, TP.HCM | ĐT: 1800-xxxx</div>
          <div class="fw-bold mt-2 text-success fs-6">✅ Thanh toán thành công!</div>
        </div>

        <div class="invoice-divider">— HÓA ĐƠN BÁN HÀNG —</div>

        <div class="invoice-meta">
          <div><span>Mã hóa đơn:</span><b>#{{ lastInvoice.id }}</b></div>
          <div><span>Ngày:</span><b>{{ formatDateFull(lastInvoice.date) }}</b></div>
          <div><span>Nhân viên:</span><b>{{ lastInvoice.staffName }}</b></div>
          <div><span>Khách hàng:</span><b>{{ lastInvoice.customerName }}</b></div>
          <div v-if="lastInvoice.customerPhone"><span>SĐT:</span><b>{{ lastInvoice.customerPhone }}</b></div>
          <div><span>Thanh toán:</span><b>{{ lastInvoice.paymentMethod }}</b></div>
        </div>

        <table class="table table-sm mt-3 mb-2">
          <thead class="table-light">
            <tr>
              <th>Sản phẩm</th>
              <th class="text-center">SL</th>
              <th class="text-end">Đơn giá</th>
              <th class="text-end">Thành tiền</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in lastInvoice.items" :key="item.id">
              <td class="small">{{ item.name }}</td>
              <td class="text-center">{{ item.quantity }}</td>
              <td class="text-end small">{{ formatPrice(item.price) }}₫</td>
              <td class="text-end small fw-semibold">{{ formatPrice(item.price * item.quantity) }}₫</td>
            </tr>
          </tbody>
          <tfoot>
            <tr>
              <td colspan="3" class="text-end text-muted small">Tạm tính</td>
              <td class="text-end small">{{ formatPrice(lastInvoice.subtotal) }}₫</td>
            </tr>
            <tr>
              <td colspan="3" class="text-end text-muted small">VAT (10%)</td>
              <td class="text-end small">{{ formatPrice(lastInvoice.vat) }}₫</td>
            </tr>
            <tr class="table-dark">
              <td colspan="3" class="text-end fw-bold">TỔNG CỘNG</td>
              <td class="text-end fw-bold price-text">{{ formatPrice(lastInvoice.total) }}₫</td>
            </tr>
          </tfoot>
        </table>

        <div class="invoice-footer text-muted small text-center">
          Cảm ơn quý khách đã mua hàng tại ComputerStore!<br />
          Sản phẩm được bảo hành theo chính sách nhà sản xuất.
        </div>

        <div class="d-flex gap-2 justify-content-end mt-3">
          <button class="btn btn-secondary" @click="lastInvoice = null">Đóng</button>
          <button class="btn btn-primary" @click="printInvoice">
            <i class="bi bi-printer me-1"></i>In hóa đơn
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getActiveProducts } from "@/api/product";
import { createOrder, createOrderItem, updateOrderStatus } from "@/api/order";
import { phoneError } from "@/utils/validate";
import { toast } from "@/utils/toast";
import { getUser } from "@/utils/session";

export default {
  name: "AdminSales",
  data() {
    return {
      products: [],
      search: "",
      cart: [],
      customer: { name: "", phone: "" },
      paymentMethod: "Cash",
      submitting: false,
      phoneErr: "",
      lastInvoice: null,
    };
  },
  computed: {
    filteredProducts() {
      const kw = this.search.trim().toLowerCase();
      if (!kw) return this.products;
      return this.products.filter((p) => p.name.toLowerCase().includes(kw));
    },
    subtotal() {
      return this.cart.reduce((s, i) => s + i.price * i.quantity, 0);
    },
    vat() {
      return Math.round(this.subtotal * 0.1);
    },
    total() {
      return this.subtotal + this.vat;
    },
  },
  async mounted() {
    const res = await getActiveProducts();
    this.products = res.data;
  },
  methods: {
    productImage(p) {
      return p.image ? encodeURI(p.image) : "";
    },
    addToCart(p) {
      if (p.stock <= 0) return;
      const existing = this.cart.find((i) => i.id === p.id);
      if (existing) {
        if (existing.quantity < p.stock) existing.quantity++;
      } else {
        this.cart.push({ id: p.id, name: p.name, price: p.price, image: p.image, stock: p.stock, quantity: 1 });
      }
    },
    removeFromCart(id) {
      this.cart = this.cart.filter((i) => i.id !== id);
    },
    formatPrice(price) {
      if (!price) return "0";
      return price.toLocaleString("vi-VN");
    },
    formatDateFull(date) {
      if (!date) return "—";
      return new Date(date).toLocaleString("vi-VN", {
        day: "2-digit", month: "2-digit", year: "numeric",
        hour: "2-digit", minute: "2-digit",
      });
    },
    async checkout() {
      this.phoneErr = "";
      if (this.customer.phone) {
        const err = phoneError(this.customer.phone);
        if (err) {
          this.phoneErr = err;
          return;
        }
      }
      this.submitting = true;
      try {
        const subtotal = this.subtotal;
        const vat = this.vat;
        const total = this.total;
        const cartSnapshot = [...this.cart];

        const orderRes = await createOrder({
          customerName: this.customer.name || "Khách lẻ",
          phone: this.customer.phone || "",
          address: "Mua trực tiếp tại cửa hàng",
          paymentMethod: this.paymentMethod,
          subtotal,
          vat,
          total,
          orderDate: new Date().toISOString(),
        });
        const orderId = orderRes.data.id;

        await Promise.all(
          cartSnapshot.map((item) =>
            createOrderItem({
              order: { id: orderId },
              product: { id: item.id },
              productName: item.name,
              quantity: item.quantity,
              price: item.price,
              image: item.image,
            })
          )
        );

        // Bán trực tiếp tại quầy: xác nhận đơn ngay (khách đã thanh toán tại chỗ).
        await updateOrderStatus(orderId, "CONFIRMED");

        const user = getUser();
        // Hiển thị hóa đơn để nhân viên in
        this.lastInvoice = {
          id: orderId,
          date: new Date().toISOString(),
          staffName: user?.fullname || user?.email || "Nhân viên",
          customerName: this.customer.name || "Khách lẻ",
          customerPhone: this.customer.phone,
          paymentMethod: this.paymentMethod,
          items: cartSnapshot,
          subtotal,
          vat,
          total,
        };

        toast.success(`Đã lập hóa đơn #${orderId} thành công!`);
        this.cart = [];
        this.customer = { name: "", phone: "" };
        const res = await getActiveProducts();
        this.products = res.data;
      } catch (e) {
        toast.error(e.response?.data?.message || "Không thể lập hóa đơn, vui lòng thử lại!");
      } finally {
        this.submitting = false;
      }
    },
    printInvoice() {
      if (!this.lastInvoice) return;
      const inv = this.lastInvoice;
      const rows = inv.items
        .map(
          (it) =>
            `<tr>
              <td>${it.name}</td>
              <td style="text-align:center">${it.quantity}</td>
              <td style="text-align:right">${this.formatPrice(it.price)}₫</td>
              <td style="text-align:right;font-weight:600">${this.formatPrice(it.price * it.quantity)}₫</td>
            </tr>`
        )
        .join("");

      const html = `
        <!DOCTYPE html>
        <html lang="vi">
        <head>
          <meta charset="UTF-8" />
          <title>Hóa đơn #${inv.id} — ComputerStore</title>
          <style>
            body { font-family: Arial, sans-serif; font-size: 13px; margin: 20px; color: #111; }
            .header { text-align: center; border-bottom: 2px solid #000; padding-bottom: 12px; margin-bottom: 12px; }
            .header h2 { margin: 0 0 4px; font-size: 20px; }
            .header p { margin: 2px 0; color: #555; }
            .title { text-align: center; font-weight: bold; font-size: 15px; letter-spacing: 1px; margin: 10px 0; }
            .meta { margin-bottom: 12px; }
            .meta div { display: flex; justify-content: space-between; padding: 2px 0; border-bottom: 1px dashed #ddd; }
            .meta span { color: #555; }
            table { width: 100%; border-collapse: collapse; }
            th, td { border: 1px solid #ddd; padding: 6px 8px; }
            th { background: #f5f5f5; font-weight: bold; }
            tfoot tr:last-child td { font-weight: bold; background: #111; color: #fff; }
            .footer { text-align: center; margin-top: 16px; color: #555; font-size: 12px; border-top: 1px solid #ddd; padding-top: 10px; }
            @media print { .no-print { display: none; } }
          </style>
        </head>
        <body>
          <div class="header">
            <h2>🖥️ ComputerStore</h2>
            <p>123 Nguyễn Văn A, Q.1, TP.HCM &nbsp;|&nbsp; ĐT: 1800-xxxx</p>
          </div>
          <div class="title">— HÓA ĐƠN BÁN HÀNG —</div>
          <div class="meta">
            <div><span>Mã hóa đơn</span><b>#${inv.id}</b></div>
            <div><span>Ngày</span><b>${this.formatDateFull(inv.date)}</b></div>
            <div><span>Nhân viên</span><b>${inv.staffName}</b></div>
            <div><span>Khách hàng</span><b>${inv.customerName}</b></div>
            ${inv.customerPhone ? `<div><span>Số điện thoại</span><b>${inv.customerPhone}</b></div>` : ""}
            <div><span>Thanh toán</span><b>${inv.paymentMethod}</b></div>
          </div>
          <table>
            <thead>
              <tr>
                <th>Sản phẩm</th>
                <th style="text-align:center">SL</th>
                <th style="text-align:right">Đơn giá</th>
                <th style="text-align:right">Thành tiền</th>
              </tr>
            </thead>
            <tbody>${rows}</tbody>
            <tfoot>
              <tr>
                <td colspan="3" style="text-align:right;color:#555">Tạm tính</td>
                <td style="text-align:right">${this.formatPrice(inv.subtotal)}₫</td>
              </tr>
              <tr>
                <td colspan="3" style="text-align:right;color:#555">VAT (10%)</td>
                <td style="text-align:right">${this.formatPrice(inv.vat)}₫</td>
              </tr>
              <tr>
                <td colspan="3" style="text-align:right">TỔNG CỘNG</td>
                <td style="text-align:right">${this.formatPrice(inv.total)}₫</td>
              </tr>
            </tfoot>
          </table>
          <div class="footer">
            Cảm ơn quý khách đã mua hàng tại ComputerStore!<br/>
            Sản phẩm được bảo hành theo chính sách nhà sản xuất.
          </div>
          <script>window.onload = function() { window.print(); }<\/script>
        </body>
        </html>`;

      const win = window.open("", "_blank", "width=600,height=800");
      if (win) {
        win.document.write(html);
        win.document.close();
      }
    },
  },
};
</script>

<style scoped>
.sales-product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 10px;
  max-height: 520px;
  overflow-y: auto;
}
.sales-product-card {
  border: 1.5px solid var(--border-soft);
  border-radius: var(--radius-sm);
  padding: 8px;
  text-align: center;
  cursor: pointer;
  transition: all 0.15s ease;
  background: #fff;
}
.sales-product-card:hover {
  border-color: var(--brand-primary);
  box-shadow: var(--shadow-sm);
}
.sales-product-card.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
.sales-product-card img {
  width: 100%;
  height: 70px;
  object-fit: contain;
  margin-bottom: 6px;
}
.sales-product-name {
  font-size: 0.78rem;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.sales-product-price {
  font-size: 0.78rem;
  color: var(--brand-accent);
  font-weight: 700;
}
.cart-line {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 0;
  border-bottom: 1px solid #f0f0f0;
}
.cart-qty {
  width: 60px;
}

/* Hóa đơn modal */
.invoice-box {
  max-height: 90vh;
  overflow-y: auto;
}
.invoice-header {
  text-align: center;
  border-bottom: 2px solid #dee2e6;
  padding-bottom: 10px;
  margin-bottom: 10px;
}
.invoice-divider {
  text-align: center;
  font-weight: bold;
  letter-spacing: 2px;
  margin: 8px 0;
  color: #555;
  font-size: 0.85rem;
}
.invoice-meta > div {
  display: flex;
  justify-content: space-between;
  padding: 3px 0;
  border-bottom: 1px dashed #dee2e6;
  font-size: 0.9rem;
}
.invoice-meta > div span {
  color: #6c757d;
}
.invoice-footer {
  border-top: 1px solid #dee2e6;
  padding-top: 10px;
}
</style>
