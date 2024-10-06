-- QuestionCategory Insert
INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (1, 'Java');
INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (2, 'CS');

-- QuestionList Insert
INSERT IGNORE INTO question_list (question_list_id, answer, correct_count, correct_rate, question, wrong_count, question_category_id)
VALUES
(1,"Immutable 객체는 상태를 변경할 수 없는 객체를 의미합니다. 이러한 객체는 생성된 이후 내부 상태를 수정할 수 없으며, 주로 final keyword로 field를 선언하여 불변성을 보장합니다. 대표적인 예로 String class가 있으며, 이 객체는 한 번 생성되면 변경할 수 없습니다.",
0,0,"Immutable 특징은?",0,1),
(2,"ORM은 객체지향 프로그래밍 언어의 객체를 관계형 데이터베이스의 테이블과 매핑하는 기법입니다. 이를 통해 개발자는 SQL 쿼리를 직접 작성하지 않고도 데이터베이스와 상호작용할 수 있습니다. 대표적인 ORM 프레임워크로는 Hibernate가 있습니다.",
0,0,"ORM이 무엇인지 설명해 주세요.",0,1);

-- keyword Insert
INSERT IGNORE INTO keyword (keyword_id, word, word_explain, question_list_id) VALUES
        (1,"불변성",null,1),
        (2,"객체","수정할 수 없으며 변경할 수 없다.",1),
        (3,"객체","객체를 데이터베이스 테이블과 매핑",2);

-- keyword Insert
INSERT IGNORE INTO synonyms (synonyms_id, word, mean) VALUES
        (1,"불가능","X"),
        (2,"불가","X"),
        (3,"없","X"),
        (4,"가능","O"),
        (5,"있","O"),
        (6,"데이터베이스","DB"),
        (7,"디비","DB"),
        (8,"database","DB"),
        (9,"매핑","mapping"),
        (10,"테이블","table");