<script lang="ts">
	import { currentUser, logout, type User } from "@/lib/api/auth";
	import { onMount } from "svelte";
	let user: User | null = null;
	let busy = false;
	async function load() { try { user = await currentUser(); } catch { user = null; } }
	async function signOut() { if (busy) return; busy = true; try { await logout(); } catch { if (typeof localStorage !== "undefined") { localStorage.removeItem("accessToken"); localStorage.removeItem("refreshToken"); } } finally { window.location.href = "/"; } }
	onMount(() => {
		if (localStorage.getItem("accessToken")) void load();
	});
</script>
{#if user}<span class="user">{user.nickname || user.username}</span><a href="/settings/password/">修改密码</a><button type="button" on:click={signOut} disabled={busy}>退出</button>{:else}<a href="/login/">登录</a>{/if}
<style>:global(.auth-bar){display:flex;align-items:center;gap:.6rem;font-size:.85rem}.auth-bar a,.auth-bar button{color:var(--primary)}.auth-bar button{border:0;background:none;cursor:pointer}.user{color:var(--secondary)}</style>
