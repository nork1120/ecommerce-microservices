alter TABLE orders
add  column payment_deadline DATETIME NULL COMMENT '付款截止時間' after status,
add index idx_orders_payment_deadline(payment_deadline);

update orders
set payment_deadline = date_add(orders.created_at,interval 15 minute )
where payment_deadline is null ;

alter table orders
modify column payment_deadline datetime not null comment '付款截止時間';