-- QuestionCategory Insert
INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (1, 'Java');
INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (2, 'CS');

-- QuestionList Insert
INSERT IGNORE INTO question_list (question_list_id, answer, correct_count, correct_rate, question, wrong_count, question_category_id)
VALUES (1,"Immutable 객체는 상태를 변경할 수 없는 객체를 의미합니다. 이러한 객체는 생성된 이후 내부 상태를 수정할 수 없으며, 주로 final keyword로 field를 선언하여 불변성을 보장합니다. 대표적인 예로 String class가 있으며, 이 객체는 한 번 생성되면 변경할 수 없습니다.",
0,0,"Immutable 특징은?",0,1);

-- keyword Insert
INSERT IGNORE INTO keyword (keyword_id, word, word_explain, question_list_id) VALUES
        (1,"객체","수정할 수 없다.",1),
        (2,"객체","변경할 수 없다.",1);