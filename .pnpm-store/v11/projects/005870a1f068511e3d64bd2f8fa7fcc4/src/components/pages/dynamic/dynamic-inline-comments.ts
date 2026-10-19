/**
 * 动态列表行内的评论挂件：
 * - 默认折叠，只显示"评论"按钮
 * - 点击展开时按需 dynamic import `BackendComments.svelte`，调用 Svelte 5 的 `mount()`
 *   把评论组件挂到面板里
 * - 关闭折叠 / 自定义元素被卸载时调用 `unmount()` 销毁实例
 *
 * 后端评论接口（/xuyuan/comments）由 backend BackendComments 直接走，与第三方评论
 * 系统（commentConfig.type）解耦，所以即使 type === "none" 也能评论。
 *
 * 注意：Svelte 5 客户端用 `mount(component, { target, props })`，返回值是组件的 exports，
 *       不能用 `new Component(...)`。props 在 mount 调用时锁定，要响应式更新需要重新 mount。
 */
interface MountedInstance {
	component: Record<string, unknown>;
	dispose: () => void;
}

export function registerDynamicInlineComments(): void {
	if (customElements.get("dynamic-inline-comments")) return;

	// 异步加载竞态守护：同一次切换请求可能被新的切换打断，避免覆盖
	let loadToken = 0;

	class DynamicInlineComments extends HTMLElement {
		private instance: MountedInstance | null = null;

		connectedCallback() {
			if (this.dataset.ready) return;
			this.querySelector("[data-comment-toggle]")?.addEventListener(
				"click",
				() => this.toggle(),
			);
			this.dataset.ready = "true";
		}

		disconnectedCallback() {
			this.unmountInstance();
		}

		private async toggle() {
			const panel = this.querySelector<HTMLElement>("[data-comment-panel]");
			if (!panel) return;
			const willOpen = panel.hidden;
			panel.hidden = !willOpen;
			this.dataset.expanded = String(willOpen);
			if (willOpen) {
				await this.open(panel);
			} else {
				this.unmountInstance();
			}
		}

		private async open(panel: HTMLElement) {
			// 已经挂载 → 不用重 mount
			if (this.instance) return;

			const targetId = Number(this.dataset.targetId || "0");
			const targetType = Number(this.dataset.targetType || "2");
			if (!targetId) {
				panel.innerHTML =
					'<p class="dynamic-comment-error">评论加载失败：目标 ID 无效</p>';
				return;
			}

			const token = ++loadToken;
			panel.replaceChildren();
			// 占位：网络比较慢时给一个过渡文案，避免看起来像没反应
			const placeholder = document.createElement("p");
			placeholder.className = "dynamic-comment-loading";
			placeholder.textContent = "正在加载评论...";
			panel.append(placeholder);

			try {
				// 动态 import：让 BackendComments 走独立 chunk 不污染主包
				const [{ mount, unmount }, mod] = await Promise.all([
					import("svelte"),
					import("@/components/comment/BackendComments.svelte"),
				]);
				if (token !== loadToken) return; // 已被新的切换打断，丢弃

				const BackendComments = mod.default;
				if (typeof BackendComments !== "function") {
					throw new Error("BackendComments 不可用");
				}

				// 清掉占位后挂载。Svelte 5 用 mount(component, { target, props }) 返回组件 exports
				panel.replaceChildren();
				const component = mount(BackendComments, {
					target: panel,
					props: {
						targetType: targetType as 1 | 2,
						targetId,
					},
				});

				this.instance = {
					component,
					dispose: () => {
						try {
							unmount(component);
						} catch (cause) {
							console.warn("Failed to unmount inline comment", cause);
						}
					},
				};
			} catch (cause) {
				console.error("Inline comment mount failed", cause);
				if (token !== loadToken) return;
				panel.innerHTML =
					'<p class="dynamic-comment-error">评论加载失败，请稍后重试</p>';
			}
		}

		private unmountInstance() {
			if (this.instance) {
				this.instance.dispose();
				this.instance = null;
			}
			const panel = this.querySelector<HTMLElement>("[data-comment-panel]");
			if (panel) panel.replaceChildren();
		}
	}

	customElements.define("dynamic-inline-comments", DynamicInlineComments);
}
