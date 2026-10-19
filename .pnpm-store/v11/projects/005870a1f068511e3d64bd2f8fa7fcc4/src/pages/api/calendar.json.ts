import { apiPath } from "@/config/runtimeConfig";

export async function GET(): Promise<Response> {
	try {
		const response = await fetch(apiPath("/articles?page=1&pageSize=100&type=1"));
		return new Response(await response.text(), {
			status: response.status,
			headers: { "Content-Type": "application/json; charset=utf-8" },
		});
	} catch {
		return new Response(JSON.stringify({ code: 200, data: { records: [] } }), {
			status: 200,
			headers: { "Content-Type": "application/json; charset=utf-8" },
		});
	}
}
