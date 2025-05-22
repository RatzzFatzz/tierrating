export function transformScoreToTier(score?: number): string {
    if (score === undefined) return "unassigned";

    // Divide score by 2
    const normalizedScore = score / 2;

    // Map the normalized score to a tier
    if (normalizedScore >= 5) return "s";
    if (normalizedScore >= 4) return "a";
    if (normalizedScore >= 3) return "b";
    if (normalizedScore >= 2) return "c";
    if (normalizedScore >= 1) return "d";
    if (normalizedScore >= 0) return "f";

    return "unassigned"; // Default for negative scores or other edge cases
}