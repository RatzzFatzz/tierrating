ALTER TABLE user_media_entry_states ADD COLUMN IF NOT EXISTS dirty BOOLEAN;

UPDATE user_media_entry_states SET dirty = true WHERE dirty IS NULL;

ALTER TABLE user_media_entry_states ALTER COLUMN dirty SET DEFAULT false;
ALTER TABLE user_media_entry_states ALTER COLUMN dirty SET NOT NULL;