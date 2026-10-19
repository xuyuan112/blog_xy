/**
 * 图片/文件 URL 处理工具
 *
 * 后端上传图片时把 MinIO 内网完整地址（如 http://192.168.10.103:9000/blog/article/xxx.png）
 * 写进了数据库。局域网/跨网段设备（手机等）直连该内网地址会失败，导致图片加载不出来。
 *
 * 这里的 resolveFileUrl 把这类"内网/私有网段"的绝对地址统一转成走后端同源代理的相对路径
 * （/xuyuan/file/{bucket}/{object}），浏览器请求时自动带上当前访问域名，跨网段也能正常加载。
 */
function isPrivateHost(host: string): boolean {
	if (!host) return false;
	if (host === "localhost" || host === "127.0.0.1") return true;
	// IPv4 私有网段：10.x / 172.16-31.x / 192.168.x
	if (/^10\./.test(host)) return true;
	if (/^172\.(1[6-9]|2\d|3[01])\./.test(host)) return true;
	if (/^192\.168\./.test(host)) return true;
	return false;
}

/**
 * 把图片 URL 规范化：
 * - 相对路径 / data: / blob: 原样返回
 * - 内网/私有网段绝对地址 → 转成 /xuyuan/file/... 相对路径（走后端代理）
 * - 外网公共地址原样返回
 */
export function resolveFileUrl(src?: string | null): string {
	if (!src) return "";
	if (
		src.startsWith("/") ||
		src.startsWith("data:") ||
		src.startsWith("blob:")
	) {
		return src;
	}
	if (!/^https?:\/\//i.test(src)) return src;
	try {
		const u = new URL(src);
		if (isPrivateHost(u.hostname)) {
			return `/xuyuan/file${u.pathname}${u.search}`;
		}
	} catch {
		// 无法解析的 URL 原样返回
	}
	return src;
}
