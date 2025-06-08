"use client"

import type {RatingItem} from "@/model/types";
import {useState} from "react";
import {TierList} from "@/components/tier-list";

export default function TierListPage({itemList, title}: {itemList: RatingItem[], title: string}) {
    const [items, setItems] = useState<RatingItem[]>(itemList);

    return (
        <main className="container mx-auto py-8 px-4">
            <h1 className="text-3xl font-bold mb-6">{title}</h1>
            <TierList items={items} setItems={setItems}/>
        </main>
    )
}