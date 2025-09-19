CREATE TABLE IF NOT EXISTS hot_news (
    id BIGSERIAL PRIMARY KEY,
    source_url VARCHAR(500) NOT NULL,
    title VARCHAR(500) NOT NULL,
    content TEXT,
    published_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_hot_news_published_at ON hot_news(published_at);
CREATE INDEX IF NOT EXISTS idx_hot_news_title ON hot_news(title);