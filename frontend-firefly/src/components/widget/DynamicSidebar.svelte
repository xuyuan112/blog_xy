<script lang="ts">
/**
 * 侧边栏动态组件 - 从 API 获取数据
 * 支持自定义 API 地址，方便接入第三方后端
 */
import I18nKey from "@i18n/i18nKey";
import { i18n } from "@i18n/translation";
import { onMount } from "svelte";
import { formatDynamicDate } from "@/utils/date-utils";
import { fetchMemos } from "@/utils/memos-adapter";
import { listMoments } from "@/lib/api/moments";
import { url } from "@/utils/url-utils";
import { resolveFileUrl } from "@/utils/file-url";

interface DynamicEntry {
	id: string;
	published: number;
	html: string;
	images?: Array<{ alt: string; src: string; title?: string }>;
	searchText?: string;
	pinned?: boolean;
}

interface ScheduleEntry {
	id: number;
	title: string;
	targetTime?: string;
	type?: string;
}

interface MemosConfig {
	enable: boolean;
	apiUrl: string;
	parent?: string;
}

interface Props {
	apiUrl: string;
	limit: number;
	memos?: MemosConfig;
	schedules?: ScheduleEntry[];
}

let { apiUrl, limit, memos, schedules = [] }: Props = $props();

let entries: DynamicEntry[] = $state([]);
let totalCount = $state(0);
let loading = $state(true);
let error = $state(false);

onMount(async () => {
	const scheduleEntries: DynamicEntry[] = schedules
		.filter((schedule) => schedule.title)
		.map((schedule) => ({
			id: `schedule-${schedule.id}`,
			published: Date.parse(schedule.targetTime || "") || Date.now(),
			html: `<strong>日程</strong> ${escapeHtml(schedule.title)}${schedule.targetTime ? ` · ${escapeHtml(schedule.targetTime)}` : ""}`,
			searchText: `${schedule.title} ${schedule.targetTime || ""}`.toLocaleLowerCase(),
		}));

	let data: DynamicEntry[] = scheduleEntries;
	try {
		if (memos?.enable) {
			data = await fetchMemos(memos.apiUrl, { parent: memos.parent });
		} else {
			const result = await listMoments(1, 100);
			data = result.records.map((entry) => {
				// 后端 createdAt 是 "yyyy-MM-dd HH:mm:ss"（Asia/Shanghai），补 +08:00 解析成 UTC 时间戳
				const shanghaiIso = entry.createdAt ? entry.createdAt.replace(" ", "T") + "+08:00" : "";
				return {
					id: String(entry.id),
					published: shanghaiIso ? new Date(shanghaiIso).getTime() : Date.now(),
					html: entry.content,
					images: (entry.images || []).map((src, index) => ({ src: resolveFileUrl(src), alt: `Moment image ${index + 1}` })),
					searchText: entry.content.toLocaleLowerCase(),
				};
			});
		}

		data = [...data, ...scheduleEntries].sort((a, b) => b.published - a.published);
	} catch {
		// 动态服务不可用时，日程仍应在右侧最新动态中可见。
		data = scheduleEntries;
		error = data.length === 0;
	} finally {
		totalCount = data.length;
		entries = data.slice(0, limit);
		updateCountBadge();
		loading = false;
	}
});

function updateCountBadge() {
	const badge = document.querySelector("[data-dynamic-count]");
	if (badge && totalCount > 0) {
		badge.textContent = `(${totalCount})`;
	}
}

// 从 HTML 中提取纯文本摘要
function getPlainText(html: string): string {
	const div = document.createElement("div");
	div.innerHTML = html;
	return div.textContent?.trim() || "";
}

function escapeHtml(value: string): string {
	return value.replace(/[&<>"']/g, (char) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" })[char] || char);
}

// 格式化日期
// 本地 blog API（apiUrl 以 / 开头）和第三方 API/Memos 使用浏览器本地时区显示
// 其余情况使用 formatDynamicDate（按 UTC 格式化）
function formatDate(timestamp: number): string {
	if (apiUrl.startsWith("/") || apiUrl.startsWith("http") || memos?.enable) {
		return new Date(timestamp).toLocaleDateString("zh-CN", {
			year: "numeric",
			month: "2-digit",
			day: "2-digit",
			hour: "2-digit",
			minute: "2-digit",
		});
	}
	return formatDynamicDate(new Date(timestamp));
}
</script>

<div class="flex flex-col gap-1.5">
	{#if loading}
		<div class="flex justify-center p-3">
			<svg class="size-5 animate-spin text-(--primary)" viewBox="0 0 24 24" fill="none">
				<circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" opacity="0.25"/>
				<path d="M4 12a8 8 0 018-8" stroke="currentColor" stroke-width="3" stroke-linecap="round"/>
			</svg>
		</div>
	{:else if error || entries.length === 0}
		<p class="m-0 p-3 text-center text-sm text-neutral-500">
			{i18n(I18nKey.dynamicEmpty)}
		</p>
	{:else}
		{#each entries as entry (entry.id)}
			{@const text = getPlainText(entry.html)}
			{@const hasImage = (entry.images?.length ?? 0) > 0}
			<a
				href={url(`/dynamic/#dynamic-${entry.id.replace(/[^a-zA-Z0-9_-]/g, "-")}`)}
				class="group flex min-w-0 min-h-16 items-center gap-3 rounded-lg p-2
					text-neutral-700/75 dark:text-neutral-300/75
					hover:bg-(--btn-plain-bg-hover) hover:text-(--primary)
					active:bg-(--btn-plain-bg-active) transition-colors duration-150"
				aria-label={`${i18n(I18nKey.dynamic)}: ${text}`}
			>
				<div class="min-w-0 flex-1">
					<div class="mb-1 flex items-center gap-1 text-xs leading-4 text-(--primary)">
						<svg class="size-4 shrink-0" fill="currentColor" viewBox="0 0 24 24" aria-hidden="true">
							<path d="M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8zm.5-13H11v6l5.25 3.15.75-1.23-4.5-2.67z"/>
						</svg>
						<time datetime={new Date(entry.published).toISOString()}>
							{formatDate(entry.published)}
						</time>
						{#if hasImage || entry.pinned}
							<span class="ml-auto inline-flex items-center gap-1.5">
								{#if hasImage}
									<svg class="size-3.5 shrink-0 text-neutral-500 dark:text-neutral-400" fill="currentColor" viewBox="0 0 24 24" aria-hidden="true">
										<path d="M21 19V5c0-1.1-.9-2-2-2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2zM8.5 13.5l2.5 3L14.5 12l4.5 6H5z"/>
									</svg>
								{/if}
								{#if entry.pinned}
									<span class="inline-flex items-center gap-0.5 text-[10px] px-1 py-0.5 rounded bg-(--primary)/10 text-(--primary) font-medium">
										<svg class="size-3" fill="currentColor" viewBox="0 0 24 24"><path d="M16 12V4h1V2H7v2h1v8l-2 2v2h5.2v6h1.6v-6H18v-2z"/></svg>
										{i18n(I18nKey.pinned)}
									</span>
								{/if}
							</span>
						{/if}
					</div>
					<p class="m-0 line-clamp-3 text-sm leading-[1.35rem]">
						{text}
					</p>
				</div>
			</a>
		{/each}
	{/if}
</div>
