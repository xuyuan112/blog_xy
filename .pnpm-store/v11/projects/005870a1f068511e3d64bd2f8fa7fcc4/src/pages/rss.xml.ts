export async function GET(): Promise<Response> {
  return new Response("RSS is unavailable until the dynamic SSR deployment is configured.", {
    status: 503,
    headers: { "Content-Type": "text/plain; charset=utf-8" },
  });
}
