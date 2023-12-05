CREATE USER invoiceapp WITH PASSWORD 'invoice45app';

GRANT USAGE on schema public to invoiceapp;
GRANT SELECT, UPDATE, INSERT, DELETE ON ALL TABLES IN SCHEMA public TO invoiceapp;

-- insert roles
INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');

-- dummy items
insert into items (name, price) values ('item one', 10000);
insert into items (name, price) values ('item two', 20000);
insert into items (name, price) values ('item three', 90000);

-- dummy invoice
insert into invoices (id, due_date, user_id, total_price) values ('0be0107b-e454-43bf-8c5d-fc247d9554ad', current_timestamp, 1, 0);

-- dummy invoiceitem
insert into invoice_items (invoice_id, item_id, quantity) values ('0be0107b-e454-43bf-8c5d-fc247d9554ad', 1, 1);
insert into invoice_items (invoice_id, item_id, quantity) values ('0be0107b-e454-43bf-8c5d-fc247d9554ad', 2, 1);