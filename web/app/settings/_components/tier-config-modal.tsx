"use client";
import { useEffect, useMemo, useRef, useState } from "react";
import { Dialog, DialogClose, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { ArrowUpDown, Palette, Plus, Settings, X, XIcon } from "lucide-react";
import { HexColorPicker } from "react-colorful";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Tier } from "@/types/types";
import { useAuth } from "@/contexts/auth-context";
import { getDefaultTiers } from "@/lib/config/default-tiers";
import { TierConfigTableSkeleton } from "@/components/loading-skeletons/tier-config-table-skeleton";
import { useTiersOnDemand, useTiersUpdate } from "@/lib/services/tiers-service";
import { toast } from "sonner";
import * as React from "react";

interface TierConfigModalProps {
	service: string;
	type: string;
	username: string;
	decimals: string;
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

export default function TierConfigModal({ service, type, username, decimals }: TierConfigModalProps) {
	const { token } = useAuth();
	const [isOpen, setIsOpen] = useState(false);
	const { data: tiersData, error: tiersError, isValidating, mutate: refreshTiers} = useTiersOnDemand(!isOpen, username, service, type, token!);
	const { trigger: updateTiers, error, isMutating } = useTiersUpdate(username, service, type, token!);
	const [tiers, setTiers] = useState<Tier[]>(tiersData?.length ? tiersData : getDefaultTiers());
	useEffect(() => {
		// eslint-disable-next-line react-hooks/set-state-in-effect
		setTiers(tiersData?.length ? tiersData : getDefaultTiers());
	}, [tiersData]);

	const addTier = () => {
		if (!tiers) return;

		const newIndex = tiers.length;
		const defaultColor = DEFAULT_COLORS[newIndex % DEFAULT_COLORS.length];
		// Find the lowest score and set new tier below it
		const lowestScore = tiers.length > 0 ? Math.min(...tiers.map((t) => t.score)) : 10;
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
				return { ...tier, [field]: value };
			}
			return tier;
		});
		// If we're updating a score, sort the tiers
		if (field === "score") {
			setTiers(updatedTiers.sort((a, b) => b.score - a.score));
		} else {
			setTiers(updatedTiers);
		}
	};

	const handleSave = () => {
		const sortedTiers = [...tiers].sort((a, b) => b.score - a.score);
		updateTiers({tiers: sortedTiers})
			.catch((error) => {
				toast.error('Something went wrong while saving tiers.');
			});
		setIsOpen(false);
	};

	const handleOpenChange = (open: boolean) => {
		setTimeout(() => setIsOpen(open), open ? 0  : 200);
	}

	const formatNumberInput = (value: string): number => {
		const parsed = parseFloat(value);
		if (isNaN(parsed)) return 0;
		// Ensure we have at most 2 decimal places
		const formatted = Math.round(parsed * 100) / 100;
		return Math.min(10, Math.max(0, formatted)); // Cap between 0 and 10
	};

	const isValidHex = (color: string): boolean => {
		return /^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$/.test(color);
	};

	return (
		<Dialog open={isOpen} onOpenChange={handleOpenChange}>
			<DialogTrigger asChild className="p-0 cursor-pointer">
				<Button variant={"outline"}>
					<Settings />
				</Button>
			</DialogTrigger>
			<DialogContent className="sm:max-w-[650px] max-h-[90vh]">
				<DialogHeader>
					<DialogTitle>Configure tier list</DialogTitle>
					<DialogDescription>
						Configure which <i>score</i> should be assigned to which tier. When dropping an item into a new tier their score
						will be set to <i>adjusted score</i>.
					</DialogDescription>
					<p className="mt-1 font-medium text-sm flex items-center">
						<ArrowUpDown className="h-3 w-3 mr-1" />
						Tiers are automatically sorted by score in descending order.
					</p>
				</DialogHeader>
				<div className="space-y-6 py-4">
					<div className="sm:max-w-fit max-w-[calc(100vw-5rem)] overflow-x-auto">
						<div className="min-w-fit">
							<div className="grid grid-cols-[60px_minmax(120px,_1fr)_100px_120px_40px] gap-4 items-center font-medium text-sm pb-2">
								<div>Color</div>
								<div>Tier Name</div>
								<div>Score</div>
								<div>Adjusted Score</div>
								<div></div>
							</div>
							<div className="max-h-[41vh] pr-1 overflow-y-auto">
								{isValidating ? (
									<TierConfigTableSkeleton />
								) : (
									tiers &&
									tiers.map((tier) => (
										<div
											key={tier.id}
											className="grid grid-cols-[60px_minmax(120px,_1fr)_100px_120px_40px] gap-4 h-10 items-center"
										>
											<div>
												<Popover>
													<PopoverTrigger asChild>
														<Button
															variant="outline"
															className="h-9 w-full p-1 flex justify-between items-center"
														>
															<div className="h-6 w-6 rounded-sm" style={{ backgroundColor: tier.color }} />
															<Palette className="h-4 w-4" />
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
																	if (value.startsWith("#") || value === "") {
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
												/>
											</div>
											<div>
												<Input
													type="number"
													value={tier.score}
													onChange={(e) => updateTier(tier.id, "score", formatNumberInput(e.target.value))}
													step={decimals}
													max="10"
													min="0"
													placeholder="Score"
												/>
											</div>
											<div>
												<Input
													type="number"
													value={tier.adjustedScore}
													onChange={(e) =>
														updateTier(tier.id, "adjustedScore", formatNumberInput(e.target.value))
													}
													step={decimals}
													max="10"
													min="0"
													placeholder="Adjusted Score"
												/>
											</div>
											<Button
												variant="ghost"
												size="icon"
												onClick={() => removeTier(tier.id)}
												disabled={tiers.length <= 1}
												className="w-9 hover:bg-red-100 dark:hover:bg-red-800/30 transition-colors"
											>
												<X className="h-4 w-4" />
											</Button>
										</div>
									))
								)}
							</div>
						</div>
					</div>
					<Button variant="outline" className="w-full flex items-center gap-2 h-9" onClick={addTier} disabled={isMutating}>
						<Plus className="h-4 w-4" /> Add Tier
					</Button>
				</div>
				<DialogFooter className="flex gap-2">
					<Button variant="secondary" onClick={() => setTiers(getDefaultTiers())} className="mr-auto">
						Restore defaults
					</Button>
					<Button onClick={() => refreshTiers()}>Reset</Button>
					<Button variant="outline" onClick={() => setIsOpen(false)}>
						Cancel
					</Button>
					<Button onClick={handleSave} disabled={isMutating}>
						Save Changes
					</Button>
				</DialogFooter>
			</DialogContent>
		</Dialog>
	);
}
