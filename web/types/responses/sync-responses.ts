export type SyncType = 'PUSH' | 'PULL';

export interface SyncStatus {
	type: SyncType;
	status: string;
	startedAt: string;
	completedAt: string;
}

export interface SyncStatusResponse {
	current: SyncStatus;
	lastPull: SyncStatus;
	lastPush: SyncStatus;
}