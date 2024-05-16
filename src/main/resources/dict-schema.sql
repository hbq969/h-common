create table `tab_dicinfo`
(
    `field_name` varchar(200) primary key,
    `field_desc` varchar(500),
    `enum_type`  varchar(100) default 'enum'
);

create table `tab_dicinfo_kv`
(
    `field_name` varchar(200),
    `enum_key`   varchar(255),
    `enum_value` varchar(500),
    primary key (`field_name`, `enum_key`)
);

create table `tab_dicinfo_sql`
(
    `field_name` varchar(200) primary key,
    `enum_sql`   varchar(500)
);

create table `bc_foo`
(
    `id`   numeric(10) primary key ,
    `name` varchar(500)
);