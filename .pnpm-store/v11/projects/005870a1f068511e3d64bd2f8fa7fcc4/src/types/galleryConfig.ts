// Backend album metadata shape used by API adapters/utilities. This is not local seed data.
export type GalleryAlbum = {
	id: string;
	name: string;
	description?: string;
	date?: string;
	location?: string;
	tags?: string[];
	cover?: string;
	password?: string;
	passwordHint?: string;
};

// Gallery configuration is intentionally limited to presentation settings; album data comes from the API.
export type GalleryConfig = {
	columnWidth?: number;
};
