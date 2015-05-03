insert into search_results (search_text, book_id)
select 
    :1 search_text
    , t.book_id
from
    (select 
        b.book_id
        , b.title
        , b.cost
        , b.category_id
        , cat.title cat_name
    from
        books b
        , categories cat
    where
        b.category_id = cat.category_id
        and b.is_shown = 1
        and 
        (
            like_expression[b.title][and]
            or like_expression[cat.title][and]
            or exists (
                select
                    0
                from
                    authors a
                    , books_authors ba
                where
                    ba.book_id = b.book_id
                    and a.author_id = ba.author_id
                    and (
                        like_expression[a.name][or]
                        or like_expression[a.middlename][or]
                        or like_expression[a.surname][or]
                    )
            )
            or (
                :2 = 1
                and like_expression[b.description][and]
            )
        ) 
    order by b.title) t