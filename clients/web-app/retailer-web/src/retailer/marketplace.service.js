const api = require("../../config/axios");

const getMarketplaceProducts = async (keyword = "", token) => {
  try {
    let url = "/api/fetch-marketplace-products";

    if (keyword && keyword.trim() !== "") {
      url += `?name=${encodeURIComponent(keyword.trim())}`;
    }

    console.log("🔍 Fetching marketplace products from:", url);
    console.log("🔍 Token present:", !!token);

    const res = await api.get(url, {
      headers: token ? {
        Authorization: `Bearer ${token}`,
      } : {},
    });

    console.log("✅ Marketplace API response:", res.data?.length || 0, "products");
    return res.data || [];
  } catch (err) {
    console.error("❌ Marketplace API error:", err.message);
    console.error("❌ Error details:", err.response?.data || err.response?.status || "No response");
    return [];
  }
};

module.exports = {
  getMarketplaceProducts,
};
