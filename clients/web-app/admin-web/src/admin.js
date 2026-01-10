const express = require('express');
const path = require('path');
const axios = require('axios');
const jwt = require('jsonwebtoken');
const cookieParser = require('cookie-parser');
require('dotenv').config({ path: path.resolve(__dirname, '..', 'config', '.env') });

const router = express.Router();

// Cấu hình URL tới Auth Service (Java Backend)
const AUTH_SERVICE_URL = process.env.AUTH_SERVICE_URL || 'http://localhost:8080';

// Đồng bộ secret/role với authentication.js để đọc JWT trong cookie
const JWT_SECRET_STRING = 'YmljYXAtc2VjcmV0LWtleS1mb3Itand0LWF1dGhlbnRpY2F0aW9uCg==';
const JWT_SECRET = Buffer.from(JWT_SECRET_STRING, 'base64');
const APPLICATION_ROLE = 'ROLE_ADMIN';

// Gắn cookie-parser cho router (phòng trường hợp app cha chưa khai báo)
router.use(cookieParser());

// Lấy JWT từ cookie và gán req.user
const requireAuth = (req, res, next) => {
    const token = req.cookies?.auth_token;
    if (!token) return res.redirect('/login');
    try {
        const decoded = jwt.verify(token, JWT_SECRET);
        req.user = {
            sub: decoded.sub,
            username: decoded.sub,
            email: decoded.email,
            roles: decoded.roles || []
        };
        req.accessToken = token;
        return next();
    } catch (err) {
        return res.redirect('/login');
    }
};

// Kiểm tra role Admin
const requireAdmin = (req, res, next) => {
    if (!req.user) return res.redirect('/login');
    const roles = req.user.roles || [];
    if (!roles.includes('ADMIN') && !roles.includes(APPLICATION_ROLE)) {
        return res.status(403).render('error', { message: 'Bạn không có quyền truy cập trang này!' });
    }
    next();
};

// Shortcut: chuyển /admin về /dashboard
router.get('/admin', requireAuth, (req, res) => res.redirect('/dashboard'));

// =========================================================
// 1. ROUTE: DASHBOARD (Giao diện chính)
// =========================================================
router.get('/dashboard', requireAuth, requireAdmin, async (req, res) => {
    try {
        const headers = { Authorization: `Bearer ${req.accessToken}` };
        const [pendingResponse] = await Promise.all([
            axios.get(`${AUTH_SERVICE_URL}/api/v1/role-requests/pending`, { headers })
        ]);

        const pendingRequests = pendingResponse.data || [];

        res.render('dashboard', {
            user: req.user,
            requests: pendingRequests,
            pendingCount: pendingRequests.length,
            error: null
        });
    } catch (error) {
        console.error('Lỗi Admin Dashboard:', error.message);
        res.render('dashboard', {
            user: req.user,
            requests: [],
            pendingCount: 0,
            error: 'Không thể kết nối tới Service quản lý. Vui lòng thử lại sau.'
        });
    }
});

// =========================================================
// 2. ROUTE: PROXY API (Duyệt/Từ chối)
// =========================================================

// API: Duyệt yêu cầu
router.post('/api/v1/role-requests/:requestId/approve', requireAuth, requireAdmin, async (req, res) => {
    try {
        const { requestId } = req.params;
        const headers = { Authorization: `Bearer ${req.accessToken}` };
        await axios.post(`${AUTH_SERVICE_URL}/api/v1/role-requests/${requestId}/approve`, null, { headers });
        res.status(200).json({ message: 'Duyệt thành công!' });
    } catch (error) {
        console.error('Lỗi duyệt role:', error.response?.data || error.message);
        res.status(500).json({ message: 'Lỗi server khi duyệt yêu cầu.' });
    }
});

// API: Từ chối yêu cầu
router.post('/api/v1/role-requests/:requestId/reject', requireAuth, requireAdmin, async (req, res) => {
    try {
        const { requestId } = req.params;
        const reason = req.body?.reason || 'Không đạt yêu cầu';
        const headers = { 
            Authorization: `Bearer ${req.accessToken}`,
            'Content-Type': 'text/plain'
        };
        // Java Controller nhận @RequestBody String => gửi text/plain
        await axios.post(`${AUTH_SERVICE_URL}/api/v1/role-requests/${requestId}/reject`, reason, { headers });
        res.status(200).json({ message: 'Đã từ chối yêu cầu.' });
    } catch (error) {
        console.error('Lỗi từ chối role:', error.response?.data || error.message);
        res.status(500).json({ message: 'Lỗi server khi từ chối yêu cầu.' });
    }
});

// =========================================================
// 3. CÁC ROUTE PHỤ (Placeholder cho Menu Sidebar)
// =========================================================
router.get('/users', requireAuth, requireAdmin, (req, res) => {
    res.render('admin-users', { user: req.user });
});

router.get('/farms', requireAuth, requireAdmin, (req, res) => {
    res.render('admin-farms', { user: req.user });
});

module.exports = router;