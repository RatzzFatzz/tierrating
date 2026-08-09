alter table user_media_entry_states
    add column IF NOT EXISTS dirty bool;

update user_media_entry_states set dirty = true;