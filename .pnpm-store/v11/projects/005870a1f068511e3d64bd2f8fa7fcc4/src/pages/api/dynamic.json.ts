import { apiPath } from "@/config/runtimeConfig";

export async function GET(): Promise<Response> {
	try {
		const response = await fetch(apiPath("/moments?page=1&pageSize=100"));
		const payload = (await response.json()) as { data?: { records?: Array<Record<string, unknown>> } };
		const records = payload.data?.records || [];
		const result = records.map((moment) => ({
			id: String(moment.id ?? ""),
			published: Date.parse(String(moment.createdAt || "")) || Date.now(),
			html: String(moment.content || ""),
			images: Array.isArray(moment.images) ? moment.images.map((src) => ({ src: String(src), alt: "" })) : [],
			searchText: [moment.content, moment.location, moment.mood].filter(Boolean).join(" "),
		}));
		return new Response(JSON.stringify(result), { headers: { "Content-Type": "application/json; charset=utf-8" } });
	} catch {
		return new Response("[]", { status: 200, headers: { "Content-Type": "application/json; charset=utf-8" } });
	}
}
