import {Tier} from "@/components/model/types"; // Make sure to install this package: npm install uuid

export function getDefaultTiers(): Tier[] {
    return [
        {
            id: crypto.randomUUID(),
            name: "S",
            score: 10.0,
            adjustedScore: 10.0,
            color: "#FF7F7F"
        },
        {
            id: crypto.randomUUID(),
            name: "A+",
            score: 9.0,
            adjustedScore: 9.0,
            color: "#FF9E7F"
        },
        {
            id: crypto.randomUUID(),
            name: "A",
            score: 8.0,
            adjustedScore: 8.0,
            color: "#FFBF7F"
        },
        {
            id: crypto.randomUUID(),
            name: "B+",
            score: 7.0,
            adjustedScore: 7.0,
            color: "#e4e449"
        },
        {
            id: crypto.randomUUID(),
            name: "B",
            score: 6.0,
            adjustedScore: 6.0,
            color: "#aae371"
        },
        {
            id: crypto.randomUUID(),
            name: "C+",
            score: 5.0,
            adjustedScore: 5.0,
            color: "#7FDFBF"
        },
        {
            id: crypto.randomUUID(),
            name: "C",
            score: 4.0,
            adjustedScore: 4.0,
            color: "#7FBFFF"
        },
        {
            id: crypto.randomUUID(),
            name: "D",
            score: 3.0,
            adjustedScore: 3.0,
            color: "#9F7FFF"
        },
        {
            id: crypto.randomUUID(),
            name: "E",
            score: 2.0,
            adjustedScore: 2.0,
            color: "#BF7FFF"
        },
        {
            id: crypto.randomUUID(),
            name: "F",
            score: 1.0,
            adjustedScore: 1.0,
            color: "#FF7FBF"
        },
        {
            id: crypto.randomUUID(),
            name: "Unassigned",
            score: 0.0,
            adjustedScore: 0.0,
            color: "#E6E6FF"
        }
    ];
}