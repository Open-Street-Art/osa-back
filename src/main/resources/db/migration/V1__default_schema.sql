create sequence hibernate_sequence start 1 increment 1
create table art_pictures (art_id int4 not null, pictures text)
create table art_entity (id int4 not null, author_name varchar(255), creation_date_time timestamp not null, description varchar(255), latitude float8 not null, longitude float8 not null, name varchar(255) not null, artist_id int4, city_id int4, primary key (id))
create table city_entity (id int4 not null, name varchar(255) not null, primary key (id))
create table contrib_pictures (contrib_id int4 not null, pictures text)
create table contrib_entity (id int4 not null, approved boolean, author_name varchar(255), creation_date_time timestamp not null, description varchar(255) not null, latitude float8 not null, longitude float8 not null, name varchar(255) not null, art_id int4, city_id int4, contributor_id int4, primary key (id))
create table fav_artists (user_id int4 not null, artist_id int4 not null)
create table fav_arts (user_id int4 not null, art_id int4 not null)
create table fav_cities (user_id int4 not null, city_id int4 not null)
create table user_roles (user_id int4 not null, role varchar(255))
create table user_entity (id int4 not null, description varchar(255), email varchar(255) not null, is_public boolean, password varchar(255) not null, profile_picture text, username varchar(255) not null, primary key (id))
alter table if exists art_pictures add constraint FKmu96wmkahow0f61ulidlj9d3i foreign key (art_id) references art_entity
alter table if exists art_entity add constraint FKrcbp3vdy81c84lep9ghuugem6 foreign key (artist_id) references user_entity
alter table if exists art_entity add constraint FKkpebn1gx8f37cqx4g1wff2i7l foreign key (city_id) references city_entity
alter table if exists contrib_pictures add constraint FKakej1u51panfqkgmj88qif9rl foreign key (contrib_id) references contrib_entity
alter table if exists contrib_entity add constraint FKjfxgwgdyskg8wxwh2snmqk4ge foreign key (art_id) references art_entity
alter table if exists contrib_entity add constraint FKldd3agwpfydy5xcee8fpf30dg foreign key (city_id) references city_entity
alter table if exists contrib_entity add constraint FKlg11loxmtps4l9jluyuqv0j4f foreign key (contributor_id) references user_entity
alter table if exists fav_artists add constraint FKbsbe6goecftoe0on9dvyjxewb foreign key (artist_id) references user_entity
alter table if exists fav_artists add constraint FKs3lg2imv5ujn8rm849uc5c50d foreign key (user_id) references user_entity
alter table if exists fav_arts add constraint FKo9gbt0juhq37vc4c25bayj87a foreign key (art_id) references art_entity
alter table if exists fav_arts add constraint FKdq8klydqnoj3nvvn1m6ru7432 foreign key (user_id) references user_entity
alter table if exists fav_cities add constraint FK6woftbd2hy5vhxqv8lhgpq7lv foreign key (city_id) references city_entity
alter table if exists fav_cities add constraint FK3226dd3ep02exhuo63dievrew foreign key (user_id) references user_entity
alter table if exists user_roles add constraint FK6y02653x6ebhsu2plf21ard62 foreign key (user_id) references user_entity
