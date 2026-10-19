import type { NavBarConfig, NavBarLink, NavBarSearchConfig } from "../types/navBarConfig";
import { NavBarSearchMethod } from "../types/navBarConfig";

/** Public navigation backed by blog-web-app modules only. */
const links: NavBarLink[] = [
	{ name: "首页", url: "/", icon: "material-symbols:home" },
	{ name: "文章", url: "/posts/", icon: "material-symbols:article" },
	{ name: "说说", url: "/dynamic/", icon: "material-symbols:dynamic-feed" , pageKey: "dynamic" },
	{ name: "杂谈", url: "/thoughts/", icon: "material-symbols:forum" },
	{ name: "归档", url: "/archive/", icon: "material-symbols:archive" },
	{ name: "相册", url: "/gallery/", icon: "material-symbols:photo-library", pageKey: "gallery" },
	{ name: "友链", url: "/friends/", icon: "material-symbols:link", pageKey: "friends" },
	{ name: "关于", url: "/about/", icon: "material-symbols:person" },
];

export const navBarConfig: NavBarConfig = { links };

export const navBarSearchConfig: NavBarSearchConfig = {
	method: NavBarSearchMethod.PageFind,
};
