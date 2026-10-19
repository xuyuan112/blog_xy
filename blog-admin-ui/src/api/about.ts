import { adminGet, adminPut } from '@/utils/request'

export interface AboutData { nickname?: string; avatar?: string; signature?: string; intro?: string; skills?: unknown[]; milestones?: unknown[] }
export function getAbout() { return adminGet<AboutData>('/about') }
export function updateAbout(data: AboutData) { return adminPut<AboutData>('/about', data) }
