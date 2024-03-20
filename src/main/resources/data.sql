ALTER TABLE candidate ADD COLUMN IF NOT EXISTS basic_info_embeddings vector(384);
ALTER TABLE candidate ADD COLUMN IF NOT EXISTS work_experiences_embeddings vector(384);
ALTER TABLE candidate ADD COLUMN IF NOT EXISTS education_embeddings vector(384);
