<script lang="ts">
	import { login } from "@/lib/api/auth";
	import { apiErrorMessage } from "@/lib/api/client";
	let username = "", password = "", error = "", busy = false;
	async function submit() { if (!username.trim() || !password) { error = "请输入账号和密码"; return; } busy = true; error = ""; try { await login({ username: username.trim(), password }); window.location.href = "/"; } catch (cause) { error = apiErrorMessage(cause, "登录失败，请检查账号和密码"); } finally { busy = false; } }
</script>
<form on:submit|preventDefault={submit}><label>账号<input bind:value={username} autocomplete="username" required /></label><label>密码<input bind:value={password} type="password" autocomplete="current-password" required /></label>{#if error}<p role="alert">{error}</p>{/if}<button type="submit" disabled={busy}>{busy ? "登录中..." : "登录"}</button><a href="/register/">注册账号</a></form>
<style>form{display:grid;gap:1rem;max-width:24rem;margin:auto;padding:1.5rem;border:1px solid var(--line-divider);border-radius:.5rem}label{display:grid;gap:.35rem}input{padding:.6rem;border:1px solid var(--line-divider);border-radius:.25rem;background:transparent}button{padding:.6rem;background:var(--primary);color:#fff;border-radius:.25rem}button:disabled{opacity:.5}p{margin:0;color:#c33}a{color:var(--primary);text-align:center}</style>
