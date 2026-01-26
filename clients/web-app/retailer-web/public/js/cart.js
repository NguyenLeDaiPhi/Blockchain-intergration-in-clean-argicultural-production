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
  // TĂNG
  if (e.target.classList.contains("plus")) {
    const row = e.target.closest(".cart-item");
    const qtyEl = row.querySelector(".qty");
    qtyEl.innerText = Number(qtyEl.innerText) + 1;

    syncCartItemsFromDOM();
    updateGrandTotal();
  }

  // GIẢM
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

  // XOÁ
  if (e.target.classList.contains("remove-btn")) {
    e.target.closest(".cart-item").remove();
    syncCartItemsFromDOM();
    updateGrandTotal();
  }
});

/************************************
 * ADD TO CART (from marketplace)
 ************************************/
document.addEventListener("click", (e) => {
  if (e.target.classList.contains("add-to-cart-btn")) {
    const btn = e.target;

    const product = {
      id: Number(btn.dataset.id),
      name: btn.dataset.name,
      price: Number(btn.dataset.price),
    };

    fetch("/cart/add", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ product }),
    })
      .then((res) => {
        if (!res.ok) throw new Error("exists");
        return res.json();
      })
      .then(() => {
        alert("✅ Đã thêm vào giỏ hàng");
      })
      .catch(() => {
        alert("⚠️ Sản phẩm đã có trong giỏ");
      });
  }
});

/************************************
 * ORDER POPUP
 ************************************/
document.querySelector(".btn-checkout")?.addEventListener("click", () => {
  document.getElementById("orderTotal").innerText = updateGrandTotal();
  document.getElementById("orderOverlay")?.classList.add("show");
});

function closeOrder() {
  document.getElementById("orderOverlay")?.classList.remove("show");
}

/**
 * Tạo order khi nhập địa chỉ và click đặt hàng
 */
async function handleCreateOrder() {
  const addressInput = document.getElementById("shippingAddress");
  const shippingAddress = addressInput?.value.trim();

  if (!shippingAddress) {
    alert("Vui lòng nhập địa chỉ giao hàng");
    addressInput.focus();
    return;
  }

  // Kiểm tra giỏ hàng
  if (!window.cartItems.length) {
    alert("Giỏ hàng trống");
    return;
  }

  try {
    // Tạo order trực tiếp
    const res = await fetch("http://localhost:8000/api/orders", {
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
      console.error("Error:", text);
      throw new Error("Tạo đơn hàng thất bại");
    }

    const order = await res.json();
    
    // Đóng popup
    document.getElementById("orderOverlay").classList.remove("show");
    
    // Hiển thị thông báo thành công
    alert("✅ Đặt hàng thành công!");
    
    // Redirect đến trang chi tiết đơn hàng
    window.location.href = `/orders/${order.id || order.orderId}`;

  } catch (err) {
    console.error(err);
    alert("Lỗi: " + (err.message || "Không thể tạo đơn hàng. Vui lòng thử lại."));
  }
}
