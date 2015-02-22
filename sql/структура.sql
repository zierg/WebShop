create table authors
(
    author_id number CONSTRAINT authors_pk_author_id PRIMARY KEY,
    name varchar2(255),
    surname varchar2(255),
    middlename varchar2(255),
    biography CLOB
);

create table categories
(
    category_id number CONSTRAINT categories_pk_category_id PRIMARY KEY,
    parent_id number CONSTRAINT categories_fk_parent_id REFERENCES categories(category_id) ON DELETE CASCADE,
    title varchar2(255),
    description CLOB
);

create table books
(
    book_id number CONSTRAINT books_pk_book_id PRIMARY KEY,
    description CLOB,
    title varchar2(255),
    link varchar2(4000),
    cost number,
    release_date date,
    category_id number CONSTRAINT books_fk_category_id REFERENCES categories(category_id) ON DELETE SET NULL,
    is_shown number(1) default 1 not null
);

create table books_authors
(
    book_id number CONSTRAINT books_authors_fk_book_id REFERENCES books(book_id) ON DELETE CASCADE,
    author_id number CONSTRAINT books_authors_fk_author_id REFERENCES books(book_id) ON DELETE CASCADE,
    CONSTRAINT book_author PRIMARY KEY (book_id, author_id)
);

create table book_attrs
(
    attr_id number CONSTRAINT book_attrs_pk_attr_id PRIMARY KEY,
    name varchar2(255)
);

create table book_params
(
    attr_id number CONSTRAINT book_params_fk_attr_id REFERENCES book_attrs(attr_id) ON DELETE CASCADE,
    book_id number CONSTRAINT book_params_fk_book_id REFERENCES books(book_id) ON DELETE CASCADE,
    value varchar2(4000),
    CONSTRAINT book_params_pk PRIMARY KEY (attr_id, book_id)
);

create table users
(
    user_id number CONSTRAINT users_pk_user_id PRIMARY KEY,
    name varchar2(255),
    surname varchar2(255),
    middlename varchar2(255),
    mail varchar2(1000),
    login varchar2(255),
    password varchar2(255),
    is_admin number(1) default 0 not null
);

create table history
(
    user_id number CONSTRAINT history_fk_user_id REFERENCES users(user_id) ON DELETE CASCADE,
    book_id number CONSTRAINT history_fk_book_id REFERENCES books(book_id) ON DELETE CASCADE,
    amount number,
    book_cost number,
    purchase_date date
);

create table shopping_carts
(
    user_id number CONSTRAINT shopping_carts_fk_user_id REFERENCES users(user_id) ON DELETE CASCADE,
    book_id number CONSTRAINT shopping_carts_fk_book_id REFERENCES books(book_id) ON DELETE CASCADE,
    amount number
);

-- Генерит уникальный ID для таблицы. Если entered_id не null, то его и возвращает
create or replace function get_table_id(entered_id number, table_name varchar2, column_name varchar2, seq_name varchar2) return number
    is
        id number;
        exists_flag number;
    begin
        if (entered_id is not null)
            then
                id := entered_id;
            else
                loop
                    execute immediate 'select ' || seq_name || '.nextval from dual' into id;
                    execute immediate
                    'select (
                        case
                            when exists(select null from ' || table_name || ' where ' || column_name || ' = :id)
                                then 1
                            else 0
                        end)
                        from dual'
                        into exists_flag
                        using id;
                    exit when (exists_flag = 0);
                end loop;
        end if;
        return id;
    end;
/

create sequence authors_seq_author_id increment by 1 start with 1;
create or replace trigger authors_trigger
    before insert on authors
    for each row
    begin
        :NEW.author_id := get_table_id(:NEW.author_id, 'authors', 'author_id', 'authors_seq_author_id');
    end;
/

create sequence categories_seq_category_id increment by 1 start with 1;
create or replace trigger categories_trigger
    before insert on categories
    for each row
    begin
        :NEW.category_id := get_table_id(:NEW.category_id, 'categories', 'category_id', 'categories_seq_category_id');
    end;
/

create sequence books_seq_book_id increment by 1 start with 1;
create or replace trigger books_trigger
    before insert on books
    for each row
    begin
        :NEW.book_id := get_table_id(:NEW.book_id, 'books', 'book_id', 'books_seq_book_id');
    end;
/

create sequence book_attrs_seq_attr_id increment by 1 start with 1;
create or replace trigger book_attrs_trigger
    before insert on book_attrs
    for each row
    begin
        :NEW.attr_id := get_table_id(:NEW.attr_id, 'book_attrs', 'attr_id', 'book_attrs_seq_attr_id');
    end;
/
    
create sequence users_seq_user_id increment by 1 start with 1;
create or replace trigger users_trigger
    before insert on users
    for each row
    begin
        :NEW.user_id := get_table_id(:NEW.user_id, 'users', 'user_id', 'users_seq_user_id');
    end;
/

create or replace trigger books_params_trigger
    after insert on books
    for each row
    begin
        insert into book_params(attr_id, book_id, value)
            select 
                attr_id, 
                :NEW.book_id, 
                value
            from
                book_params
            where
                book_id = -1;
    end;
/

insert into book_attrs(attr_id, name) values (1, 'Издательство');
insert into book_attrs(attr_id, name) values (2, 'Город');
insert into book_attrs(attr_id, name) values (3, 'Номер издания');
insert into book_attrs(attr_id, name) values (4, 'Язык');
insert into book_attrs(attr_id, name) values (5, 'Переводчик');

-- Шаблон для книги. Новые книги будут по умолчанию иметь те же атрибуты, что и у шаблона.
insert into books(book_id, title, is_shown) values(-1, 'default book', 0);
insert into book_params(book_id, attr_id) values(-1, 1);
insert into book_params(book_id, attr_id) values(-1, 2);
insert into book_params(book_id, attr_id) values(-1, 4);

insert into authors(author_id, name, surname, middlename, biography) values (1, 'Александр', 'Пушкин', 'Сергеевич', 'Биография
Пушкина
на нескольких
строчках.');

insert into categories(category_id, parent_id, title, description) values(1, null, 'Историческая проза', 'Историческая проза — условное обозначение для разнородных по структуре и композиции романов, повестей, рассказов, в которых повествуется об исторических событиях более или менее отдалённого времени, а действующими лицами (главными или второстепенными) могут выступать исторические личности.');

insert into books(book_id, description, title, link, cost, release_date, category_id, is_shown)
    values (1, '«Капитанская дочка» — короткий исторический роман (или повесть) А. С. Пушкина, действие которого происходит во время восстания Емельяна Пугачёва. Впервые опубликован без указания имени автора в 4-й книжке журнала «Современник», поступившей в продажу в последней декаде 1836 года.', 'Капитанская дочка', 'www.captain_daughter.com', 0, to_date('1836', 'YYYY'), 1, 1);
    
insert into books_authors(book_id, author_id) values (1, 1);

update book_params set value = 'Росмен' where book_id = 1 and attr_id = 1;
update book_params set value = 'Москва' where book_id = 1 and attr_id = 2;
update book_params set value = 'Русский' where book_id = 1 and attr_id = 4;

commit;