import {Tier, TierlistEntry} from "@/model/types";

export function assignTiersAndGroupEntriesByTier(tiers: Tier[], items: TierlistEntry[]): Map<string, TierlistEntry[]> {
    // Proper order (sorted descending by score) is assured by the server
    let itemsIndex = 0;
    let tiersIndex = 0;
    let positionIndex = 0;

    const entriesByTier = new Map<string, TierlistEntry[]>();
    tiers.forEach(tier => entriesByTier.set(tier.id, []))

    while (itemsIndex < items.length && tiersIndex < tiers.length) {
        if (items[itemsIndex].score >= tiers[tiersIndex].score) {
            items[itemsIndex].tier = tiers[tiersIndex];
            items[itemsIndex].index = positionIndex;

            if (entriesByTier.has(tiers[tiersIndex].id)) {
                const currentEntries = entriesByTier.get(tiers[tiersIndex].id)!;
                entriesByTier.set(tiers[tiersIndex].id, [...currentEntries, items[itemsIndex]]);
            }

            positionIndex++;
            itemsIndex++;
        } else {
            tiersIndex++;
            positionIndex = 0;
        }
    }

    return entriesByTier;
}

export function groupBySingle<T, K>(arr: T[], key: (i: T) => K): Map<K, T> {
    const map = new Map<K, T>();
    arr.forEach(elem => map.set(key(elem), elem));
    return map;
}