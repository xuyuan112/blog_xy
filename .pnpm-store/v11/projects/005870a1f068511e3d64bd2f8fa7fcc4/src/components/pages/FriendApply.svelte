<script lang="ts">
	import { applyFriend } from "@/lib/api/friends";
	let name = "", url = "", avatar = "", description = "", email = "", group = "", status = "", busy = false;
	async function submit() {
		status = "";
		if (!name.trim() || !/^https?:\/\//i.test(url) || !/^\S+@\S+\.\S+$/.test(email)) { status = "请填写有效的网站、地址和邮箱"; return; }
		busy = true;
		try { await applyFriend({ name: name.trim(), url: url.trim(), avatar: avatar.trim() || undefined, description: description.trim() || undefined, email: email.trim(), group: group.trim() || undefined }); status = "申请已提交"; name = url = avatar = description = email = group = ""; } catch { status = "提交失败，请稍后重试"; } finally { busy = false; }
	}
</script>
<section class="apply"><h2>申请友链</h2><form on:submit|preventDefault={submit}><input bind:value={name} placeholder="网站名称" required /><input bind:value={url} type="url" placeholder="网站地址 https://" required /><input bind:value={email} type="email" placeholder="联系邮箱" required /><input bind:value={avatar} type="url" placeholder="头像地址（可选）" /><input bind:value={group} placeholder="分组（可选）" /><textarea bind:value={description} placeholder="网站简介（可选）"></textarea><button type="submit" disabled={busy}>{busy ? "提交中..." : "提交申请"}</button></form>{#if status}<p role="status">{status}</p>{/if}</section>
<style>.apply{margin-top:2rem;padding:1.25rem;border:1px solid var(--line-divider);border-radius:.5rem}.apply h2{margin:0 0 1rem}.apply form{display:grid;gap:.5rem;max-width:36rem}.apply input,.apply textarea{padding:.6rem;border:1px solid var(--line-divider);border-radius:.25rem;background:transparent}.apply textarea{min-height:5rem}.apply button{justify-self:start;padding:.6rem 1rem;background:var(--primary);color:#fff;border-radius:.25rem}.apply button:disabled{opacity:.5}.apply p{color:var(--secondary)}</style>
