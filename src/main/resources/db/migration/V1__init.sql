-- USERS TABLE
CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       phone VARCHAR(20) UNIQUE NOT NULL,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL CHECK (role IN ('user', 'admin', 'super_admin')),
                       is_suspended BOOLEAN DEFAULT FALSE,
                       created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- POSTS TABLE
CREATE TABLE posts (
                       id UUID PRIMARY KEY,
                       user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                       content TEXT NOT NULL,
                       repost_of UUID REFERENCES posts(id),
                       type VARCHAR(20) NOT NULL CHECK (type IN ('text', 'repost')),
                       is_hidden BOOLEAN DEFAULT FALSE,
                       created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- FOLLOWS TABLE
CREATE TABLE follows (
                         follower_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                         following_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                         PRIMARY KEY (follower_id, following_id)
);

-- LIKES TABLE
CREATE TABLE likes (
                       user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                       post_id UUID NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
                       PRIMARY KEY (user_id, post_id)
);

-- CONTACTS TABLE
CREATE TABLE contacts (
                          id UUID PRIMARY KEY,
                          user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                          phone_number VARCHAR(20) NOT NULL,
                          created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                          UNIQUE (user_id, phone_number)
);

-- INDEXES
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_contacts_phone ON contacts(phone_number);
CREATE INDEX idx_posts_user_id ON posts(user_id);
CREATE INDEX idx_posts_repost_of ON posts(repost_of);
CREATE INDEX idx_follows_follower_id ON follows(follower_id);
CREATE INDEX idx_likes_post_id ON likes(post_id);

-- TRIGGER FUNCTION FOR updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- TRIGGERS TO AUTO-UPDATE updated_at
CREATE TRIGGER trigger_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_posts_updated_at
    BEFORE UPDATE ON posts
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
