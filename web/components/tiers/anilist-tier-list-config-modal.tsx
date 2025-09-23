"use client";
import {useState, useRef, useEffect} from "react";
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
    DialogFooter,
} from "@/components/ui/dialog";
import {Button} from "@/components/ui/button";
import {Input} from "@/components/ui/input";
import {Wrench, X, Plus, Palette, ArrowUpDown} from "lucide-react";
import {HexColorPicker} from "react-colorful";
import {Popover, PopoverContent, PopoverTrigger} from "@/components/ui/popover";
import {Tier} from "@/model/types";
import {useAuth} from "@/contexts/AuthContext";
import {fetchTiers} from "@/components/api/tier-api";
import {Skeleton} from "@/components/ui/skeleton";
import {getDefaultTiers} from "@/model/defaults";

interface AniListTierListConfigModalProps {
    initialTiers?: Tier[];
    type: string;
    onSave: (tiers: Tier[]) => void;
}

// Default tier colors
export const DEFAULT_COLORS = [
    "#FF7F7F", // S - Red
    "#FFBF7F", // A - Orange
    "#FFFF7F", // B - Yellow
    "#7FFF7F", // C - Green
    "#7FBFFF", // D - Blue
    "#BF7FFF", // E - Purple
    "#FF7FBF", // F - Pink
];

export default function AniListTierListConfigModal({
                                                       initialTiers = [],
                                                       type,
                                                       onSave,
                                                   }: AniListTierListConfigModalProps) {
    const [isOpen, setIsOpen] = useState(false);
    const [tiers, setTiers] = useState<Tier[]>([]);
    const [queryRunning, setQueryRunning] = useState(false);
    const {token, isLoading, isAuthenticated, logout} = useAuth();
    const dataFetched = useRef(false);

    // Only fetch data when the modal is opened
    useEffect(() => {
        if (isOpen && !dataFetched.current && !isLoading && isAuthenticated) {
            setQueryRunning(true);
            fetchTiers(token, "anilist", type, logout)
                .then((data: Tier[]) => {
                    setTiers(data && data.length > 0 ? data : getDefaultTiers());
                    dataFetched.current = true;
                })
                .catch((error) => console.error(error))
                .finally(() => setQueryRunning(false));
        }
    }, [isOpen, isLoading, isAuthenticated, token, type, logout]);

    // Sort tiers whenever they change
    useEffect(() => {
        if (!tiers || tiers.length === 0) return;

        const sortedTiers = [...tiers].sort((a, b) => b.score - a.score);
        // Only update if the order has changed to avoid infinite loops
        if (JSON.stringify(sortedTiers.map(t => t.id)) !== JSON.stringify(tiers.map(t => t.id))) {
            setTiers(sortedTiers);
        }
    }, [tiers]);

    const restoreDefaults = () => {
        setTiers(getDefaultTiers());
    }

    const addTier = () => {
        if (!tiers) return;

        const newIndex = tiers.length;
        const defaultColor = DEFAULT_COLORS[newIndex % DEFAULT_COLORS.length];
        // Find the lowest score and set new tier below it
        const lowestScore = tiers.length > 0
            ? Math.min(...tiers.map(t => t.score))
            : 10;
        const newScore = Math.max(0, lowestScore - 2);
        const newTier = {
            id: crypto.randomUUID(),
            name: "",
            score: newScore,
            adjustedScore: newScore,
            color: defaultColor,
        };
        setTiers([...tiers, newTier].sort((a, b) => b.score - a.score));
    };

    const removeTier = (id: string) => {
        setTiers(tiers.filter((tier) => tier.id !== id));
    };

    const updateTier = (id: string, field: keyof Tier, value: string | number) => {
        const updatedTiers = tiers.map((tier) => {
            if (tier.id === id) {
                return {...tier, [field]: value};
            }
            return tier;
        });
        // If we're updating a score, sort the tiers
        if (field === 'score') {
            setTiers(updatedTiers.sort((a, b) => b.score - a.score));
        } else {
            setTiers(updatedTiers);
        }
    };

    const handleSave = () => {
        // Ensure tiers are sorted before saving
        const sortedTiers = [...tiers].sort((a, b) => b.score - a.score);
        onSave(sortedTiers);
        setIsOpen(false);
    };

    const handleOpenChange = (open: boolean) => {
        setIsOpen(open);

        // If opening the dialog, reset the data fetched flag if it was previously closed
        if (open && !isOpen) {
            dataFetched.current = false;
        }

        // Reset to initial tiers if dialog is closed without saving
        if (!open) {
            if (initialTiers.length > 0) {
                setTiers(initialTiers);
            }
        }
    };

    // Trigger the dialog to open
    const handleTriggerClick = () => {
        setIsOpen(true);
    };

    // Helper function to format and validate number inputs
    const formatNumberInput = (value: string): number => {
        const parsed = parseFloat(value);
        if (isNaN(parsed)) return 0;
        // Ensure we have at most 2 decimal places
        const formatted = Math.round(parsed * 100) / 100;
        return Math.min(10, Math.max(0, formatted)); // Cap between 0 and 10
    };

    // Helper function to validate hex color
    const isValidHex = (color: string): boolean => {
        return /^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$/.test(color);
    };

    // Skeleton tier rows
    const renderSkeletonTiers = () => {
        return Array(5).fill(0).map((_, index) => (
            <div
                key={`skeleton-${index}`}
                className="grid grid-cols-[60px_1fr_100px_120px_40px] gap-4 items-center"
            >
                {Array(4).fill(0).map((_, index2) => (
                    // eslint-disable-next-line react/jsx-key
                    <div key={`skeleton-${index}-${index2}`}>
                        <Skeleton className="h-9 w-full"/>
                    </div>
                ))}
                <div>
                    <Skeleton className="h-9 w-9"/>
                </div>
            </div>
        ));
    };

    return (
        <Dialog open={isOpen} onOpenChange={handleOpenChange}>
            <DialogTrigger className="p-1.5" onClick={handleTriggerClick}>
                <Wrench/>
            </DialogTrigger>
            <DialogContent className="sm:max-w-[650px] max-h-[80vh] overflow-y-auto">
                <DialogHeader>
                    <DialogTitle>Configure tier list</DialogTitle>
                    <DialogDescription>
                        Configure which score should be assigned to which tier. Also set a score value and color for
                        each tier,
                        which will be set when dropping an element to that tier.
                        These changes will be sent back to the corresponding service provider.
                    </DialogDescription>
                    <p className="mt-1 font-medium text-sm flex items-center">
                        <ArrowUpDown className="h-3 w-3 mr-1"/>
                        Tiers are automatically sorted by score in descending order.
                    </p>
                </DialogHeader>
                <div className="space-y-6 py-4">
                    <div className="grid grid-cols-[60px_1fr_100px_120px_40px] gap-4 items-center font-medium text-sm">
                        <div>Color</div>
                        <div>Tier Name</div>
                        <div>Score</div>
                        <div>Adjusted Score</div>
                        <div></div>
                    </div>

                    {queryRunning ? (
                        // Skeleton loading state
                        renderSkeletonTiers()
                    ) : (
                        // Actual tier data
                        tiers && tiers.map((tier) => (
                            <div
                                key={tier.id}
                                className="grid grid-cols-[60px_1fr_100px_120px_40px] gap-4 items-center"
                            >
                                <div>
                                    <Popover>
                                        <PopoverTrigger asChild>
                                            <Button
                                                variant="outline"
                                                className="h-9 w-full p-1 flex justify-between items-center"
                                            >
                                                <div
                                                    className="h-6 w-6 rounded-sm"
                                                    style={{backgroundColor: tier.color}}
                                                />
                                                <Palette className="h-4 w-4"/>
                                            </Button>
                                        </PopoverTrigger>
                                        <PopoverContent className="w-auto p-3" align="start">
                                            <div className="space-y-3">
                                                <HexColorPicker
                                                    color={tier.color}
                                                    onChange={(color) => updateTier(tier.id, "color", color)}
                                                />
                                                <Input
                                                    value={tier.color}
                                                    onChange={(e) => {
                                                        const value = e.target.value;
                                                        if (value.startsWith('#') || value === '') {
                                                            updateTier(tier.id, "color", value);
                                                        }
                                                    }}
                                                    onBlur={(e) => {
                                                        if (!isValidHex(e.target.value)) {
                                                            updateTier(tier.id, "color", tier.color || "#000000");
                                                        }
                                                    }}
                                                    className="h-8 mt-2"
                                                    placeholder="#RRGGBB"
                                                />
                                            </div>
                                        </PopoverContent>
                                    </Popover>
                                </div>
                                <div>
                                    <Input
                                        value={tier.name}
                                        onChange={(e) => updateTier(tier.id, "name", e.target.value)}
                                        placeholder="Tier name"
                                        className="h-9"
                                    />
                                </div>
                                <div>
                                    <Input
                                        type="number"
                                        value={tier.score}
                                        onChange={(e) =>
                                            updateTier(
                                                tier.id,
                                                "score",
                                                formatNumberInput(e.target.value)
                                            )
                                        }
                                        step="0.01"
                                        max="10"
                                        min="0"
                                        placeholder="Score"
                                        className="h-9"
                                    />
                                </div>
                                <div>
                                    <Input
                                        type="number"
                                        value={tier.adjustedScore}
                                        onChange={(e) =>
                                            updateTier(
                                                tier.id,
                                                "adjustedScore",
                                                formatNumberInput(e.target.value)
                                            )
                                        }
                                        step="0.01"
                                        max="10"
                                        min="0"
                                        placeholder="Adjusted Score"
                                        className="h-9"
                                    />
                                </div>
                                <Button
                                    variant="ghost"
                                    size="icon"
                                    onClick={() => removeTier(tier.id)}
                                    disabled={tiers.length <= 1}
                                    className="h-9 w-9 hover:bg-red-100 dark:hover:bg-red-800/30 transition-colors"
                                >
                                    <X className="h-4 w-4"/>
                                </Button>
                            </div>
                        ))
                    )}

                    <Button
                        variant="outline"
                        className="w-full flex items-center gap-2 h-9"
                        onClick={addTier}
                        disabled={queryRunning}
                    >
                        <Plus className="h-4 w-4"/> Add Tier
                    </Button>
                </div>
                <DialogFooter className="flex gap-2">
                    <Button variant="secondary" onClick={restoreDefaults} className="mr-auto">
                        Restore defaults
                    </Button>
                    <Button variant="outline" onClick={() => setIsOpen(false)}>
                        Cancel
                    </Button>
                    <Button onClick={handleSave} disabled={queryRunning}>
                        Save Changes
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    );
}