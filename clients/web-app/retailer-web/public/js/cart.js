/************************************
 * INIT CART ITEMS
 ************************************/
window.cartItems = [];

function syncCartItemsFromDOM() {
  window.cartItems = [];
  document.querySelectorAll(".cart-item").forEach(item => {
    window.cartItems.push({
      id: Number(item.dataset.id),
      price: Number(item.dataset.price),
      quantity: Number(item.querySelector(".qty").innerText)
    });
  });
}

/************************************
 * TOTAL
 ************************************/
function updateGrandTotal() {
  let total = 0;

  document.querySelectorAll(".cart-item").forEach(row => {
    const price = Number(row.dataset.price || 0);
    const qty = Number(row.querySelector(".qty")?.innerText || 0);

    const itemTotal = price * qty;
    row.querySelector(".item-total").innerText = itemTotal + " VND";

    total += itemTotal;
  });

  const totalEl = document.getElementById("grandTotal");
  if (totalEl) totalEl.innerText = total;

  return total;
}

document.addEventListener("DOMContentLoaded", () => {
  syncCartItemsFromDOM();
  updateGrandTotal();
});

/************************************
 * FIX + / − BUTTON
 ************************************/
document.addEventListener("click", e => {
  if (e.target.classList.contains("plus")) {
    const row = e.target.closest(".cart-item");
    const qtyEl = row.querySelector(".qty");
    qtyEl.innerText = Number(qtyEl.innerText) + 1;

    syncCartItemsFromDOM();
    updateGrandTotal();
  }

  if (e.target.classList.contains("minus")) {
    const row = e.target.closest(".cart-item");
    const qtyEl = row.querySelector(".qty");
    const current = Number(qtyEl.innerText);

    if (current > 1) {
      qtyEl.innerText = current - 1;
      syncCartItemsFromDOM();
      updateGrandTotal();
    }
  }

  if (e.target.classList.contains("remove-btn")) {
    e.target.closest(".cart-item").remove();
    syncCartItemsFromDOM();
    updateGrandTotal();
  }
});

/************************************
 * CHECKOUT - THANH TOÁN TIỀN MẶT KHI NHẬN HÀNG
 ************************************/
document.querySelector(".btn-checkout")?.addEventListener("click", () => {
  // Hiển thị popup nhập địa chỉ
  document.getElementById("paymentTotal").innerText = updateGrandTotal();
  document.getElementById("paymentOverlay")?.classList.add("show");
});

function closePayment() {
  document.getElementById("paymentOverlay")?.classList.remove("show");
}

/**
 * TẠO ĐƠN HÀNG - THANH TOÁN TIỀN MẶT KHI NHẬN HÀNG
 */
async function handlePaymentNext() {
  const addressInput = document.getElementById("shippingAddress");
  const shippingAddress = addressInput?.value.trim();

  if (!shippingAddress) {
    alert("Vui lòng nhập địa chỉ giao hàng");
    addressInput.focus();
    return;
  }

  if (!window.cartItems.length) {
    alert("Giỏ hàng trống");
    return;
  }

  try {
    // Tạo order trực tiếp (không cần payment)
    // Dùng relative path để đi qua retailer-web proxy
    const res = await fetch("/api/orders", {
      method: "POST",
      credentials: "include",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        items: window.cartItems.map(i => ({
          productId: i.id,
          quantity: i.quantity
        })),
        shippingAddress: shippingAddress
      })
    });

    if (!res.ok) {
      const text = await res.text();
      console.error(text);
      throw new Error("Tạo đơn hàng thất bại");
    }

    const order = await res.json();

    // Xóa giỏ hàng
    window.cartItems = [];
    document.querySelectorAll(".cart-item").forEach(item => item.remove());
    updateGrandTotal();

    // Đóng popup
    closePayment();

    // Thông báo thành công
    alert("Đặt hàng thành công! Bạn sẽ thanh toán tiền mặt khi nhận hàng.");

    // Redirect đến trang chi tiết đơn hàng
    window.location.href = `/orders/${order.id}`;

  } catch (err) {
    console.error(err);
    alert("Không thể tạo đơn hàng. Vui lòng thử lại.");
  }
}
