const axios = require("axios");

// Sử dụng biến môi trường hoặc mặc định là kong-gateway (trong Docker) hoặc localhost (local dev)
// Trong Docker, luôn dùng kong-gateway:8000
const API_GATEWAY_URL = process.env.API_GATEWAY_BASE_URL || 
                        (process.env.DOCKER_ENV === "true" ? "http://kong-gateway:8000" : "http://localhost:8000");

console.log("🔧 API Gateway URL:", API_GATEWAY_URL);
console.log("🔧 DOCKER_ENV:", process.env.DOCKER_ENV);
console.log("🔧 API_GATEWAY_BASE_URL:", process.env.API_GATEWAY_BASE_URL);

const api = axios.create({
  baseURL: API_GATEWAY_URL,
  timeout: 10000,
  withCredentials: true,
});

module.exports = api;
