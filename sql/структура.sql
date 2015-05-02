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


create table search_results
(
    search_text varchar2(255),
    book_id number CONSTRAINT search_results_fk_book_id REFERENCES books(book_id) ON DELETE CASCADE,
    search_timestamp timestamp default current_timestamp
);

-- Если в таблице search_results скопилось слишком много результатов поиска, то удаляем старые результаты поиска
create or replace trigger search_results_before_trigger
    before insert on search_results
    DECLARE
        results_amount number;
        max_results constant number := 10;
        results_to_delete constant number := 5;
        min_minutes constant number := 2;
    begin
        select count(0) into results_amount from (select distinct search_text from search_results);
        if results_amount >= max_results then
            delete 
                from search_results 
            where 
                search_text in 
                (
                    select
                        search_text 
                    from 
                    (
                        select distinct
                            search_text
                        from
                            search_results s
                        where
                            extract(minute from(current_timestamp - search_timestamp)) >= min_minutes
                        order by 
                        (
                            select
                                search_timestamp
                            from
                                search_results ss
                            where
                                ss.search_text = s.search_text
                                and rownum = 1
                        )
                    )
                    where 
                        rownum <= results_to_delete
                );
        end if;
    end;
/

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

insert into authors(author_id, name, surname, middlename, biography) values (2, 'Станислав', 'Лем', '', 'Биография
Лема
на нескольких
строчках.');

insert into authors(author_id, name, surname, middlename, biography) values (3, 'Илья', 'Ильф', 'Арнольдович', 'Биография
Ильфа
на нескольких
строчках.');

insert into authors(author_id, name, surname, middlename, biography) values (4, 'Евгений', 'Петров', 'Петрович', 'Биография
Петрова
на нескольких
строчках.');

insert into authors(author_id, name, surname, middlename, biography) values (5, 'Дуглас', 'Адамс', '', 'Биография
Адамса
на нескольких
строчках.');

insert into authors(author_id, name, surname, middlename, biography) values (6, 'Алан Александр', 'Милн', '', 'Биография
Милна
на нескольких
строчках.');

insert into categories(category_id, title, description) values(1, 'Историческая проза', 'Историческая проза — условное обозначение для разнородных по структуре и композиции романов, повестей, рассказов, в которых повествуется об исторических событиях более или менее отдалённого времени, а действующими лицами (главными или второстепенными) могут выступать исторические личности.');
insert into categories(category_id, title, description) values(2, 'Поэма', 'Большое эпическое стихотворное произведение, принадлежащее определенному автору, большая стихотворная повествовательная форма. Может быть героической, романтической, критической, сатирической и т. п.');
insert into categories(category_id, title, description) values(3, 'Научная фантастика', 'Научная фантастика (НФ) — жанр в литературе, кино и других видах искусства, одна из разновидностей фантастики. Научная фантастика основывается на фантастических допущениях (вымысле, спекуляции) в области науки, включая как точные, так и естественные, и гуманитарные науки.');
insert into categories(category_id, title, description) values(4, 'Роман', 'Роман — литературный жанр, как правило, прозаический, который предполагает развернутое повествование о жизни и развитии личности главного героя (героев) в кризисный, нестандартный период его жизни.');
insert into categories(category_id, title, description) values(5, 'Сказка', 'Сказка — один из жанров фольклора, либо литературы. Эпическое, преимущественно прозаическое произведение волшебного характера, обычно со счастливым концом. Как правило, сказки рассчитаны на детей.');

insert into books(book_id, description, title, link, cost, release_date, category_id, is_shown)
    values (1, '«Капитанская дочка» — короткий исторический роман (или повесть) А. С. Пушкина, действие которого происходит во время восстания Емельяна Пугачёва. Впервые опубликован без указания имени автора в 4-й книжке журнала «Современник», поступившей в продажу в последней декаде 1836 года.', 'Капитанская дочка', 'www.captain_daughter.com', 0, to_date('1836', 'YYYY'), 1, 1);
insert into books(book_id, description, title, link, cost, release_date, category_id, is_shown)
    values (2, '«Руслан и Людмила» — первая законченная поэма Александра Сергеевича Пушкина; волшебная сказка, вдохновлённая древнерусскими былинами.', 'Руслан и Людмила', 'www.ruslan_and_ludmila.com', 0, to_date('1820', 'YYYY'), 2, 1);
insert into books(book_id, description, title, link, cost, release_date, category_id, is_shown)
    values (3, '«Непобедимый» (польск. Niezwyciężony) — научно-фантастический роман Станислава Лема 1964 года.', 'Непобедимый', 'www.Niezwyciężony.com', 0, to_date('1964', 'YYYY'), 3, 1);
insert into books(book_id, description, title, link, cost, release_date, category_id, is_shown)
    values (4, '«Двена́дцать сту́льев» — роман И. Ильфа и Е. Петрова. Написан в 1927 году. Жанр — остросатирический роман-фельетон.', 'Двенадцать стульев', 'www.12chairs.com', 0, to_date('1928', 'YYYY'), 4, 1);
insert into books(book_id, description, title, link, cost, release_date, category_id, is_shown)
    values (5, '«Автостопом по галактике» (англ. The Hitchhiker’s Guide to the Galaxy или дословно «Путеводитель по галактике для автостопщиков», «Путеводитель для путешествующих по галактике автостопом», 1979) — юмористический фантастический роман английского писателя Дугласа Адамса. Первая книга одноимённой серии.', 'Автостопом по галактике', 'www.Guide_to_the_Galaxy.com', 0, to_date('1979', 'YYYY'), 3, 1);
insert into books(book_id, description, title, link, cost, release_date, category_id, is_shown)
    values (6, '«Винни-Пух» представляет собой дилогию, но каждая из двух книг Милна распадается на 10 рассказов (stories) с собственным сюжетом.', 'Винни-Пух', 'www.winny.com', 0, to_date('1928', 'YYYY'), 5, 1);
insert into books(book_id, description, title, link, cost, release_date, category_id, is_shown)
    values (7, '«Сказка о царе Салтане, о сыне его славном и могучем богатыре князе Гвидоне Салтановиче и о прекрасной царевне Лебеди» (укороченный вариант названия — «Сказка о царе Салтане») — сказка в стихах А. С. Пушкина. Создана в 1831 году, впервые издана в 1832 году.', 'Сказка о царе Салтане', 'www.saltan.com', 0, to_date('1832', 'YYYY'), 5, 1);
insert into books(book_id, description, title, link, cost, release_date, category_id, is_shown)
    values (8, '«Ска́зка о рыбаке́ и ры́бке» — сказка Александра Сергеевича Пушкина. Написана 14 октября 1833 года. Впервые напечатана в 1835 году в журнале «Библиотека для чтения»', 'Сказка о рыбаке и рыбке', 'www.fish_and_fisher.com', 0, to_date('1835', 'YYYY'), 5, 1);
insert into books(book_id, description, title, link, cost, release_date, category_id, is_shown)
    values (9, '«Сказка о попе и о работнике его Балде» — сказка А. С. Пушкина. При жизни поэта не печаталась. Написана в Болдине 13 сентября 1830 года. Основой послужила русская народная сказка, записанная Пушкиным в Михайловском.', 'Сказка о попе и о работнике его Балде', 'www.pope_and_noodle.com', 0, to_date('1830', 'YYYY'), 5, 1);
    
insert into books_authors(book_id, author_id) values (1, 1);
insert into books_authors(book_id, author_id) values (2, 1);
insert into books_authors(book_id, author_id) values (3, 2);
insert into books_authors(book_id, author_id) values (4, 3);
insert into books_authors(book_id, author_id) values (4, 4);
insert into books_authors(book_id, author_id) values (5, 5);
insert into books_authors(book_id, author_id) values (6, 6);
insert into books_authors(book_id, author_id) values (7, 1);
insert into books_authors(book_id, author_id) values (8, 1);
insert into books_authors(book_id, author_id) values (9, 1);

update book_params set value = 'Росмен' where book_id = 1 and attr_id = 1;
update book_params set value = 'Москва' where book_id = 1 and attr_id = 2;
update book_params set value = 'Русский' where book_id = 1 and attr_id = 4;

commit;