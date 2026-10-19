export interface RuntimeConfig {
	apiBaseUrl: string;
	siteUrl: string;
}

// 后端服务端口，与 blog-web-app 的 server.port 保持一致
const API_PORT = "8081";

/**
 * 解析后端 API 基础地址。
 *
 * 优先级：环境变量（部署时指定）> 浏览器动态（跟随当前访问域名）> 本地回环。
 *
 * 这样无论从 localhost 还是局域网 IP（如 http://192.168.x.x:4321）访问前端，
 * 浏览器端请求都会自动指向同一台宿主机的后端端口，局域网访问无需改配置；
 * 服务端渲染（无 window）时回退到 localhost，宿主机直连自己的后端。
 */
export function resolveApiBaseUrl(): string {
	// import.meta.env 仅在 Vite/Astro 环境存在；构建后脚本（tsx/纯 Node）中为
	// undefined，需用可选链兜底，否则会抛 TypeError
	const env = import.meta.env?.PUBLIC_API_BASE_URL;
	if (env) return env.replace(/\/$/, "");
	if (typeof window !== "undefined") {
		const host = window.location?.hostname;
		if (host) return `${window.location.protocol}//${host}:${API_PORT}`;
	}
	return `http://localhost:${API_PORT}`;
}

export const runtimeConfig: RuntimeConfig = {
	apiBaseUrl: (import.meta.env?.PUBLIC_API_BASE_URL || "http://localhost:8081").replace(/\/$/, ""),
	siteUrl: import.meta.env?.PUBLIC_SITE_URL || "http://localhost:4321",
} as const;

export function apiPath(path: string): string {
	const normalized = path.startsWith("/") ? path : `/${path}`;
	return `${resolveApiBaseUrl()}/xuyuan${normalized}`;
}
