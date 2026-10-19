/** Stable frontend models and boundary mappers for blog-web responses. */
export const numericId = (value: unknown): number => {
	const id = Number(value);
	return Number.isFinite(id) ? id : 0;
};

export const apiIdPath = (value: unknown): string =>
	encodeURIComponent(String(numericId(value)));

export const textValue = (value: unknown): string =>
	typeof value === "string" ? value : value == null ? "" : String(value);

export const optionalText = (value: unknown): string | undefined => {
	const text = textValue(value).trim();
	return text || undefined;
};

export const booleanValue = (value: unknown): boolean =>
	value === true || value === 1 || value === "true";

export function mapArticle<T extends { id: unknown }>(
	raw: T,
): T & { id: number } {
	return { ...raw, id: numericId(raw.id) };
}

export function mapMoment<T extends { id: unknown }>(
	raw: T,
): T & { id: number } {
	return { ...raw, id: numericId(raw.id) };
}

export function mapGallery<T extends { id: unknown }>(
	raw: T,
): T & { id: number } {
	return { ...raw, id: numericId(raw.id) };
}

export function mapPhoto<T extends { id: unknown; galleryId?: unknown }>(
	raw: T,
): T & { id: number; galleryId?: number } {
	return {
		...raw,
		id: numericId(raw.id),
		galleryId: raw.galleryId == null ? undefined : numericId(raw.galleryId),
	};
}

export function mapFriend<T extends { id?: unknown }>(
	raw: T,
): T & { id: number } {
	const value = raw as T & Record<string, unknown>;
	return {
		...raw,
		id: numericId(value.id),
		name: textValue(value.name),
		url: textValue(value.url),
		avatar: optionalText(value.avatar),
		description: optionalText(value.description),
		group: optionalText(value.group ?? value.groupName),
	} as T & { id: number };
}

export function mapComment<
	T extends { id?: unknown; targetId?: unknown; targetType?: unknown },
>(raw: T): T & { id: number; targetId: number; targetType: number } {
	const value = raw as T & Record<string, unknown>;
	return {
		...raw,
		id: numericId(value.id),
		targetId: numericId(value.targetId),
		targetType: numericId(value.targetType),
		content: textValue(value.content),
		nickname: textValue(value.nickname),
		avatar: optionalText(value.avatar),
		createdAt: optionalText(value.createdAt),
	} as T & { id: number; targetId: number; targetType: number };
}
