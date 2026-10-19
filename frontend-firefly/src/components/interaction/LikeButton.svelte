<script lang="ts">
	import { toggleLike } from "@/lib/api/likes";
	export let targetType: 1 | 2 = 1;
	export let targetId: number;
	export let likeCount = 0;
	let liked = false;
	let busy = false;
	let error = "";
	async function toggle() {
		if (busy) return;
		busy = true; error = "";
		try { const result = await toggleLike(targetType, targetId); liked = result.liked; likeCount = result.likeCount; }
		catch { error = "操作失败，请稍后重试"; }
		finally { busy = false; }
	}
</script>
<div class="like-wrap"><button type="button" on:click={toggle} disabled={busy} aria-pressed={liked}>{liked ? "已赞" : "点赞"} {likeCount}</button>{#if error}<small role="alert">{error}</small>{/if}</div>
<style>.like-wrap{display:flex;align-items:center;gap:.75rem}.like-wrap button{padding:.45rem .8rem;border:1px solid var(--line-divider);border-radius:.25rem;color:var(--primary)}.like-wrap button:disabled{opacity:.5}.like-wrap small{color:var(--secondary)}</style>
