CREATE TABLE IF NOT EXISTS reclamations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(30) NOT NULL,
    resolution_notes TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS reclamation_comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reclamation_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    comment_text TEXT NOT NULL,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (reclamation_id) REFERENCES reclamations(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reclamation_attachments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reclamation_id BIGINT NOT NULL,
    file_url VARCHAR(255) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    uploaded_at DATETIME NOT NULL,
    FOREIGN KEY (reclamation_id) REFERENCES reclamations(id) ON DELETE CASCADE
);
