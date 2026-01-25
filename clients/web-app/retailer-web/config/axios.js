const axios = require("axios");

const api = axios.create({
  baseURL: process.env.API_GATEWAY_BASE_URL, // KONG GATEWAY
  timeout: 10000,
});

module.exports = api;
