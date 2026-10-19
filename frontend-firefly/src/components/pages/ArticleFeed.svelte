<script lang="ts">
	import { onMount } from "svelte";
	import { listArticles, type ArticleListItem } from "@/lib/api/articles";
	import { siteConfig } from "@/config";
	import { resolveFileUrl } from "@/utils/file-url";
	let { loadAll = false, initialArticles = [], initialPage = 1, initialPages = 0, initialTotal = 0 }: { loadAll?: boolean; initialArticles?: ArticleListItem[]; initialPage?: number; initialPages?: number; initialTotal?: number } = $props();
	let articles: ArticleListItem[] = initialArticles, loading = initialArticles.length === 0, failed = false, page = initialPage, pages = initialPages, total = initialTotal;
	let categoryId: number | undefined, tagId: number | undefined;

	// ── 列表/网格布局切换（与顶部显示设置 DisplaySettings 的 layoutChange 事件联动）──
	type Layout = "list" | "grid";
	let layout = $state<Layout>("list");
	// 网格卡片最小宽度，取自站点配置 postListLayout.grid.columnWidth（与模板 post-grid-auto 一致）
	const gridMinWidth = siteConfig.postListLayout?.grid?.columnWidth || 320;

	function resolveLayout(): Layout {
		if (typeof window === "undefined") return "list";
		// 低于 380px 强制网格模式（与模板 DisplaySettings/checkScreenSize 逻辑一致）
		if (window.innerWidth < 380) return "grid";
		const saved = localStorage.getItem("postListLayout");
		if (saved === "list" || saved === "grid") return saved;
		const mobileDefault = siteConfig.postListLayout?.mobileDefaultMode || "grid";
		const desktopDefault = siteConfig.postListLayout?.defaultMode || "list";
		return window.innerWidth < 780 ? mobileDefault : desktopDefault;
	}

	function onLayoutChange(event: Event) {
		const next = (event as CustomEvent<{ layout: Layout }>).detail?.layout;
		layout = next === "grid" || next === "list" ? next : resolveLayout();
	}

	function onResize() {
		// 超窄屏强制网格；其余情况尊重用户/存储的选择
		if (window.innerWidth < 380 && layout !== "grid") layout = "grid";
	}

	function readQuery() { const q = new URLSearchParams(window.location.search), p = Number(q.get("page")), c = Number(q.get("categoryId")), t = Number(q.get("tagId")); page = Number.isInteger(p) && p > 0 ? p : 1; categoryId = Number.isInteger(c) && c > 0 ? c : undefined; tagId = Number.isInteger(t) && t > 0 ? t : undefined; }
	async function load() { loading = true; failed = false; try {
		const params = { pageSize: 10, type: 1, ...(categoryId ? { categoryId } : {}), ...(tagId ? { tagId } : {}) };
		const first = await listArticles({ ...params, page });
		if (loadAll && page === 1 && first.pages > 1) {
			const rest = await Promise.all(Array.from({ length: first.pages - 1 }, (_, index) => listArticles({ ...params, page: index + 2 })));
			articles = [first.records, ...rest.map((result) => result.records)].flat();
			pages = 1;
		} else { articles = first.records; pages = first.pages; }
		total = first.total;
	} catch { failed = true; } finally { loading = false; } }
	function goTo(nextPage: number) { if (nextPage < 1 || (pages > 0 && nextPage > pages)) return; const q = new URLSearchParams(window.location.search); if (nextPage === 1) q.delete("page"); else q.set("page", String(nextPage)); window.location.href = `${window.location.pathname}${q.toString() ? `?${q}` : ""}`; }
	onMount(() => {
		readQuery();
		if (initialArticles.length === 0) load();
		// 初始化布局并订阅顶部显示设置的切换事件
		layout = resolveLayout();
		window.addEventListener("layoutChange", onLayoutChange);
		window.addEventListener("resize", onResize);
		return () => {
			window.removeEventListener("layoutChange", onLayoutChange);
			window.removeEventListener("resize", onResize);
		};
	});
</script>
{#if loading}<div class="api-state" role="status">Loading articles...</div>{:else if failed}<div class="api-state" role="alert">Failed to load articles. <button type="button" on:click={load}>Retry</button></div>{:else if articles.length === 0}<div class="api-state" role="status">No published articles.</div>{:else}<section class="api-article-list {layout}" style="--grid-min:{gridMinWidth}px" aria-label="Articles">{#each articles as article}<article class="article-card card-base">{#if article.cover}<img src={resolveFileUrl(article.cover)} alt="" loading="lazy" />{/if}<div class="article-body"><a class="article-title" href={`/posts/${article.id}/`}>{article.title}</a>{#if article.summary || article.description}<p>{article.summary || article.description}</p>{/if}<div class="article-meta">{#if article.categoryName}<span>{article.categoryName}</span>{/if}{#if article.publishedAt}<time datetime={article.publishedAt}>{article.publishedAt}</time>{/if}</div></div></article>{/each}</section>{#if pages > 1 && !loadAll}<nav class="pagination" aria-label="Article pagination"><button type="button" disabled={page <= 1} on:click={() => goTo(page - 1)}>Previous</button><span>{page} / {pages} pages, {total} articles</span><button type="button" disabled={page >= pages} on:click={() => goTo(page + 1)}>Next</button></nav>{/if}{/if}
<style>.api-article-list{display:grid;gap:1rem;transition:all .5s ease-in-out}.api-article-list.list{grid-template-columns:minmax(0,1fr)}.api-article-list.grid{grid-template-columns:repeat(auto-fill,minmax(var(--grid-min,320px),1fr))}.api-article-list.grid .article-card{flex-direction:column}.api-article-list.grid .article-card img{width:100%;aspect-ratio:16/9}.article-card{display:flex;gap:1rem;padding:1rem;overflow:hidden}.article-card img{width:10rem;aspect-ratio:16/10;object-fit:cover;border-radius:.5rem;flex:none}.article-body{min-width:0;display:grid;gap:.5rem;align-content:center}.article-title{font-size:1.25rem;font-weight:700;color:var(--primary)}.article-body p{margin:0;color:var(--secondary);display:-webkit-box;-webkit-line-clamp:2;-webkit-box-orient:vertical;overflow:hidden}.article-meta{display:flex;gap:.75rem;color:var(--secondary);font-size:.85rem}.api-state{padding:2.5rem 1rem;text-align:center;color:var(--secondary)}.api-state button,.pagination button{padding:.4rem .75rem;border:1px solid var(--line-divider);border-radius:.25rem;color:var(--primary)}.pagination{display:flex;justify-content:center;align-items:center;gap:.75rem;margin-top:1.25rem;color:var(--secondary)}.pagination button:disabled{opacity:.45;cursor:not-allowed}@media(max-width:640px){.api-article-list.list .article-card img{width:7rem}.pagination{font-size:.85rem;gap:.4rem}}</style>
