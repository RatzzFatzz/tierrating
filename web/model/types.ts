export interface RatingItem {
    id: string
    score: number
    title: string
    cover: string
    tier: string
}

export interface DragItem {
    type: string
    id: string
    tier: string
    index: number
}

export interface User {
    id: number
    name: string
}