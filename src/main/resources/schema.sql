CREATE TABLE IF NOT EXISTS ORDERS (
    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    DESCRIPTION VARCHAR(255) NOT NULL,
    STATUS VARCHAR(50) NOT NULL,
    INSERT_DATE TIMESTAMP NOT NULL,
    UPDATE_DATE TIMESTAMP
);

-- Add a comment to the table
COMMENT ON TABLE ORDERS IS 'Table containing the orders';

-- Add comments to the columns
COMMENT ON COLUMN ORDERS.ID IS 'Unique identifier of the order';
COMMENT ON COLUMN ORDERS.DESCRIPTION IS 'Description of the order';
COMMENT ON COLUMN ORDERS.STATUS IS 'Status of the order (e.g., NEW, IN_PROGRESS, COMPLETED, CANCELED)';
COMMENT ON COLUMN ORDERS.INSERT_DATE IS 'Insertion date of the order';
COMMENT ON COLUMN ORDERS.UPDATE_DATE IS 'Update date of the order';

-- Add the constraint to the STATUS column
ALTER TABLE ORDERS
ADD CONSTRAINT order_status_check CHECK (STATUS IN ('NEW', 'IN_PROGRESS', 'COMPLETED', 'CANCELED'));

-- Add some optional indexes
CREATE INDEX order_status_idx ON ORDERS(STATUS);
CREATE INDEX insert_date_idx ON ORDERS(INSERT_DATE);
