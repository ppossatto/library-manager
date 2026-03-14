CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_authors_name ON authors(name);
CREATE INDEX idx_reservations_dates ON reservations(devolution_date, expected_devolution_date);
CREATE INDEX idx_reservations_status ON reservations(status);
CREATE INDEX idx_users_email ON users(email);