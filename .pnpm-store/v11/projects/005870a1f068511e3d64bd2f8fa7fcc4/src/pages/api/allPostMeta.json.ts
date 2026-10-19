import { listArticles } from "@/lib/api/articles";

// Compatibility endpoint for legacy widgets; data is sourced from blog-web.
export async function GET(): Promise<Response> {
  try {
    const page = await listArticles({ page: 1, pageSize: 100 });
    const data = page.records.map((article) => ({
      id: String(article.id),
      title: article.title,
      description: article.description || article.summary || "",
      published: Date.parse(article.publishedAt || "") || 0,
      category: article.categoryName || "",
      password: false,
    }));
    return new Response(JSON.stringify(data), { headers: { "Content-Type": "application/json; charset=utf-8" } });
  } catch {
    return new Response(JSON.stringify([]), { status: 503, headers: { "Content-Type": "application/json; charset=utf-8" } });
  }
}
