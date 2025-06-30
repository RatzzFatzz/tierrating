"use client"

import type {RatingItem} from "@/model/types";
import {useState} from "react";
import {TierList} from "@/components/tier-list";

export default function TierListPage({itemList, title}: {itemList: RatingItem[], title: string}) {
    const [items, setItems] = useState<RatingItem[]>(itemList);

    return (
        <div className="max-w-[1514px] w-full mx-auto content-center px-4">
            <h1 className="text-3xl font-bold mb-6">{title}</h1>
            <TierList items={items} setItems={setItems}/>
        </div>
    )
}