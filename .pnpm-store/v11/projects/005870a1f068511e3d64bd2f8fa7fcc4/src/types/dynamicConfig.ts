export type DynamicConfig = {
	title?: string;
	description?: string;
	/** 动态头像和名称的跳转地址，支持站内路径或完整 URL */
	profileUrl?: string;
	showComment?: boolean;
	itemsPerPage?: number;
	// Memos 配置
	memos?: DynamicMemocsConfig;
};

export type DynamicMemocsConfig = {
	/** 是否启用 Memos 数据源 */
	enable: boolean;
	/** Memos 实例地址，如 "https://memos.example.com" */
	apiUrl: string;
	/** Memos 用户标识，如 "users/xiaye"，用于过滤指定用户的动态 */
	parent?: string;
};
