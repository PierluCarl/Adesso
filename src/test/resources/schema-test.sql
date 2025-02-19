CREATE TABLE IF NOT EXISTS ORDERS (
    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    DESCRIPTION VARCHAR(255) NOT NULL,
    STATUS VARCHAR(50) NOT NULL,
    INSERT_DATE TIMESTAMP NOT NULL,
    UPDATE_DATE TIMESTAMP
);
-- Add the constraint to the STATUS column
ALTER TABLE ORDERS
ADD CONSTRAINT order_status_check CHECK (STATUS IN ('NEW', 'IN_PROGRESS', 'COMPLETED', 'CANCELED'));

-- Add some optional indexes
CREATE INDEX order_status_idx ON ORDERS(STATUS);
CREATE INDEX insert_date_idx ON ORDERS(INSERT_DATE);
