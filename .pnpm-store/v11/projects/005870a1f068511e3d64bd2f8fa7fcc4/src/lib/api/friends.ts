import { get, post } from "./client";
import { mapFriend, numericId, textValue } from "./models";

export interface Friend {
	id: number;
	name: string;
	url: string;
	avatar?: string;
	description?: string;
}
export interface FriendGroup {
	name: string;
	friends: Friend[];
}
export interface FriendList {
	total: number;
	groups: FriendGroup[];
}
export const listFriends = async (): Promise<FriendList> => {
	const value = await get<Partial<FriendList>>("/friends");
	return {
		total: numericId(value.total),
		groups: (value.groups || []).map((group) => ({
			name: textValue(group?.name),
			friends: Array.isArray(group?.friends)
				? group.friends.map(mapFriend)
				: [],
		})),
	};
};
export const randomFriend = async (): Promise<Friend> =>
	mapFriend(await get<Friend>("/friends/random"));
export const applyFriend = (payload: {
	name: string;
	url: string;
	avatar?: string;
	description?: string;
	email: string;
	group?: string;
}): Promise<void> => post<void>("/friends/apply", payload);
