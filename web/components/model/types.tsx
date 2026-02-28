export interface TierlistEntry {
    id: string
    score: number
    title: string
    cover: string
    tier: Tier
    index: number
}

export interface Tier {
    id: string;
    name: string;
    score: number;
    adjustedScore: number;
    color: string;
}
