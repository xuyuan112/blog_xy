#!/usr/bin/env node
/* ------------------------------------------------------------------
 * serve.cjs - 零依赖静态 SPA 服务器 + /xuyuan 反向代理
 * 用途：把同目录下的 dist/ 文件夹对外提供 HTTP 服务；
 *       并把 /xuyuan/* 路径转发到后端 blog-web-admin (默认 127.0.0.1:8080)
 * 用法：node serve.cjs [port]
 * 默认监听 0.0.0.0:5174
 *
 * 为什么用 Node 原生：避免依赖 node_modules，
 * 服务器只需要 dist/ + serve.cjs + package.json 即可运行。
 * ------------------------------------------------------------------ */
'use strict';

const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = parseInt(process.argv[2] || process.env.PORT || '5174', 10);
const HOST = process.env.HOST || '0.0.0.0';
const ROOT = path.join(__dirname, 'dist');

// 后端地址，默认本机 8080；可通过环境变量 BACKEND_URL 覆盖
const BACKEND_URL = (process.env.BACKEND_URL || 'http://127.0.0.1:8080').replace(/\/$/, '');
const BACKEND = new URL(BACKEND_URL);
const BACKEND_HOST = BACKEND.hostname;
const BACKEND_PORT = parseInt(BACKEND.port || '80', 10);

const MIME = {
  '.html': 'text/html; charset=utf-8',
  '.js':  'application/javascript; charset=utf-8',
  '.mjs': 'application/javascript; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.json':'application/json; charset=utf-8',
  '.svg': 'image/svg+xml',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.jpeg':'image/jpeg',
  '.gif': 'image/gif',
  '.webp':'image/webp',
  '.ico': 'image/x-icon',
  '.woff':'font/woff',
  '.woff2':'font/woff2',
  '.ttf': 'font/ttf',
  '.eot': 'application/vnd.ms-fontobject',
  '.txt': 'text/plain; charset=utf-8',
  '.map': 'application/json; charset=utf-8'
};

if (!fs.existsSync(ROOT)) {
  console.error('[serve] 错误：dist/ 目录不存在，请先 npm run build 或者把 dist/ 上传到这里');
  process.exit(1);
}

const indexHtml = path.join(ROOT, 'index.html');
if (!fs.existsSync(indexHtml)) {
  console.error('[serve] 错误：dist/index.html 不存在，请检查 dist/ 是否完整');
  process.exit(1);
}

function safeJoin(root, requested) {
  const resolved = path.normalize(path.join(root, requested));
  if (!resolved.startsWith(root)) return null; // 防 .. 路径逃逸
  return resolved;
}

function pipeProxy(req, res) {
  return new Promise((resolve, reject) => {
    const proxyReq = http.request({
      hostname: BACKEND_HOST,
      port: BACKEND_PORT,
      path: req.url,
      method: req.method,
      headers: {
        ...req.headers,
        'host': `${BACKEND_HOST}:${BACKEND_PORT}`
      },
      timeout: 30000
    }, (proxyRes) => {
      res.writeHead(proxyRes.statusCode, proxyRes.headers);
      proxyRes.pipe(res);
      proxyRes.on('end', resolve);
    });

    proxyReq.on('error', (err) => {
      if (!res.headersSent) {
        res.writeHead(502, { 'Content-Type': 'application/json; charset=utf-8' });
        res.end(JSON.stringify({ code: 502, msg: `后端代理失败: ${err.message}` }));
      }
      reject(err);
    });

    proxyReq.on('timeout', () => {
      proxyReq.destroy();
      if (!res.headersSent) {
        res.writeHead(504, { 'Content-Type': 'application/json; charset=utf-8' });
        res.end(JSON.stringify({ code: 504, msg: '后端响应超时' }));
      }
      reject(new Error('backend timeout'));
    });

    req.pipe(proxyReq);
  });
}

http.createServer(async (req, res) => {
  try {
    const urlPath = decodeURIComponent((req.url || '/').split('?')[0]);

    // ---- /xuyuan/* 反向代理到后端 8080 ----
    if (urlPath.startsWith('/xuyuan/')) {
      await pipeProxy(req, res);
      return;
    }

    // ---- 静态文件 ----
    let requested = urlPath === '/' ? '/index.html' : urlPath;
    let filePath = safeJoin(ROOT, requested);

    if (!filePath || !fs.existsSync(filePath) || fs.statSync(filePath).isDirectory()) {
      // SPA fallback: Vue Router history 模式
      filePath = indexHtml;
    }

    const ext = path.extname(filePath).toLowerCase();
    res.setHeader('Content-Type', MIME[ext] || 'application/octet-stream');
    if (ext === '.html' || urlPath === '/' || urlPath.endsWith('/')) {
      res.setHeader('Cache-Control', 'no-cache');
    } else {
      res.setHeader('Cache-Control', 'public, max-age=31536000, immutable');
    }

    fs.createReadStream(filePath)
      .on('error', () => {
        res.writeHead(500, { 'Content-Type': 'text/plain; charset=utf-8' });
        res.end('Internal Server Error');
      })
      .pipe(res);
  } catch (err) {
    if (!res.headersSent) {
      res.writeHead(500, { 'Content-Type': 'text/plain; charset=utf-8' });
      res.end('Internal Server Error');
    }
  }
}).listen(PORT, HOST, () => {
  console.log(`[serve] listening http://${HOST}:${PORT}  (root: ${ROOT}, backend: ${BACKEND_URL})`);
});

process.on('SIGTERM', () => process.exit(0));
process.on('SIGINT', () => process.exit(0));
