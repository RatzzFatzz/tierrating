package at.pcgamingfreaks.mapper;

public class ScoreToTierMapper {
    public static String map(Integer score) {
        if (score == null) return "unassigned";

        var normalizedScore = score;
        if (normalizedScore >= 10) return "s";
        if (normalizedScore >= 8) return "a";
        if (normalizedScore >= 6) return "b";
        if (normalizedScore >= 4) return "c";
        if (normalizedScore >= 2) return "d";
        if (normalizedScore > 0) return "f";

        return "unassigned";
    }
}
