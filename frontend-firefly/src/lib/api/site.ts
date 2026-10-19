import { get } from "./client";
import { numericId, optionalText, textValue } from "./models";

export interface Skill {
	name: string;
	level: number;
	category?: string;
}
export interface Milestone {
	year: string;
	title: string;
	description?: string;
}
export interface About {
	nickname?: string;
	avatar?: string;
	signature?: string;
	intro?: string;
	skills: Skill[];
	milestones: Milestone[];
}
export interface Notice {
	id: number;
	content?: string;
	updatedAt?: string;
}
export interface Word {
	content?: string;
	source?: string;
}
export interface Weather {
	city?: string;
	temp?: string;
	weather?: string;
	icon?: string;
}
export interface Schedule {
	id: number;
	title: string;
	targetTime?: string;
	type?: "countdown" | "schedule" | string;
}
export interface SiteWidgets {
	notice?: Notice;
	words?: Word;
	weather?: Weather;
	schedules?: Schedule[];
}
export interface SiteStats {
	articleCount: number;
	momentCount: number;
	categoryCount: number;
	tagCount: number;
	commentCount: number;
	viewCount: number;
}

type RecordDto = Record<string, unknown>;

const asRecord = (value: unknown): RecordDto =>
	value && typeof value === "object" && !Array.isArray(value)
		? (value as RecordDto)
		: {};
const mapSkill = (raw: unknown): Skill => {
	const value = asRecord(raw);
	return {
		name: textValue(value.name),
		level: numericId(value.level),
		category: optionalText(value.category),
	};
};
const mapMilestone = (raw: unknown): Milestone => {
	const value = asRecord(raw);
	return {
		year: textValue(value.year),
		title: textValue(value.title),
		description: optionalText(value.description),
	};
};
const mapAbout = (raw: unknown): About => {
	const value = asRecord(raw);
	return {
		nickname: optionalText(value.nickname),
		avatar: optionalText(value.avatar),
		signature: optionalText(value.signature),
		intro: optionalText(value.intro),
		skills: Array.isArray(value.skills) ? value.skills.map(mapSkill) : [],
		milestones: Array.isArray(value.milestones)
			? value.milestones.map(mapMilestone)
			: [],
	};
};
const mapNotice = (raw: unknown): Notice => {
	const value = asRecord(raw);
	return {
		id: numericId(value.id),
		content: optionalText(value.content),
		updatedAt: optionalText(value.updatedAt),
	};
};
const mapWord = (raw: unknown): Word => {
	const value = asRecord(raw);
	return {
		content: optionalText(value.content),
		source: optionalText(value.source),
	};
};
const mapWeather = (raw: unknown): Weather => {
	const value = asRecord(raw);
	return {
		city: optionalText(value.city),
		temp: optionalText(value.temp),
		weather: optionalText(value.weather),
		icon: optionalText(value.icon),
	};
};
const mapSchedule = (raw: unknown): Schedule => {
	const value = asRecord(raw);
	return {
		id: numericId(value.id),
		title: textValue(value.title),
		targetTime: optionalText(value.targetTime),
		type: optionalText(value.type),
	};
};
const mapSiteStats = (raw: unknown): SiteStats => {
	const value = asRecord(raw);
	return {
		articleCount: numericId(value.articleCount),
		momentCount: numericId(value.momentCount),
		categoryCount: numericId(value.categoryCount),
		tagCount: numericId(value.tagCount),
		commentCount: numericId(value.commentCount),
		viewCount: numericId(value.viewCount),
	};
};

export const getAbout = async (): Promise<About> =>
	mapAbout(await get<unknown>("/about"));
export const getNotice = async (): Promise<Notice> =>
	mapNotice(await get<unknown>("/site/notice"));
export const getRandomWord = async (): Promise<Word> =>
	mapWord(await get<unknown>("/site/words/random"));
export const getWeather = async (): Promise<Weather> =>
	mapWeather(await get<unknown>("/site/weather"));
export const getSchedules = async (): Promise<Schedule[]> => {
	const value = await get<unknown>("/site/schedules");
	return Array.isArray(value) ? value.map(mapSchedule) : [];
};
export const getSiteStats = async (): Promise<SiteStats> =>
	mapSiteStats(await get<unknown>("/site/stats"));
