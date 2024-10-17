-- QuestionCategory Insert
INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (1, 'Java');
INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (2, 'Spring');
INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (3, 'NodeJs');
INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (4, 'ExpressJs');
INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (5, 'Django');
INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (6, 'Flask');
INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (7, 'Ruby');
INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (8, 'PHP');
INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (9, 'GraphQL');
INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (10, 'MySQL');

INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (11, 'Networking');
INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (12, 'OS');
INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (13, 'DataStructure');
INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (14, 'Algorithms');
INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (15, 'SoftwareEngineering');
INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (16, 'DesignPatterns');
INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (17, 'ComputerArchitecture');
INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (18, 'Cybersecurity');
INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (19, 'ArtificialIntelligence');

INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (20, 'React');
INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (21, 'Vue');
INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (22, 'Angular');
INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (23, 'HTML5');
INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (24, 'CSS3');
INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (25, 'JavaScriptES6Plus');
INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (26, 'TypeScript');
INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (27, 'SassScss');
INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (28, 'Webpack');
INSERT IGNORE INTO question_category (question_category_id, category_name) VALUES (29, 'ResponsiveWebDesign');

-- Statistics Insert
INSERT IGNORE INTO statistics (statistics_id, type, description, count ) VALUES (1, 'Q_BE', 'Java', 0);
INSERT IGNORE INTO statistics (statistics_id, type, description, count ) VALUES (2, 'Q_BE', 'Spring', 0);
INSERT IGNORE INTO statistics (statistics_id, type, description, count ) VALUES (3, 'Q_BE', 'NodeJs', 0);
INSERT IGNORE INTO statistics (statistics_id, type, description, count ) VALUES (4, 'Q_BE', 'ExpressJs', 0);
INSERT IGNORE INTO statistics (statistics_id, type, description, count ) VALUES (5, 'Q_BE', 'Django', 0);
INSERT IGNORE INTO statistics (statistics_id, type, description, count ) VALUES (6, 'Q_BE', 'Flask', 0);
INSERT IGNORE INTO statistics (statistics_id, type, description, count ) VALUES (7, 'Q_BE', 'Ruby', 0);
INSERT IGNORE INTO statistics (statistics_id, type, description, count ) VALUES (8, 'Q_BE', 'PHP', 0);
INSERT IGNORE INTO statistics (statistics_id, type, description, count ) VALUES (9, 'Q_BE', 'GraphQL', 0);
INSERT IGNORE INTO statistics (statistics_id, type, description, count ) VALUES (10, 'Q_BE', 'MySQL', 0);

INSERT IGNORE INTO statistics (statistics_id, type, description, count ) VALUES (11, 'Q_CS', 'Networking', 0);
INSERT IGNORE INTO statistics (statistics_id, type, description, count ) VALUES (12, 'Q_CS', 'OS', 0);
INSERT IGNORE INTO statistics (statistics_id, type, description, count ) VALUES (13, 'Q_CS', 'DataStructure', 0);
INSERT IGNORE INTO statistics (statistics_id, type, description, count ) VALUES (14, 'Q_CS', 'Algorithms', 0);
INSERT IGNORE INTO statistics (statistics_id, type, description, count ) VALUES (15, 'Q_CS', 'SoftwareEngineering', 0);
INSERT IGNORE INTO statistics (statistics_id, type, description, count ) VALUES (16, 'Q_CS', 'DesignPatterns', 0);
INSERT IGNORE INTO statistics (statistics_id, type, description, count ) VALUES (17, 'Q_CS', 'ComputerArchitecture', 0);
INSERT IGNORE INTO statistics (statistics_id, type, description, count ) VALUES (18, 'Q_CS', 'Cybersecurity', 0);
INSERT IGNORE INTO statistics (statistics_id, type, description, count ) VALUES (19, 'Q_CS', 'ArtificialIntelligence', 0);

INSERT IGNORE INTO statistics (statistics_id, type, description, count ) VALUES (20, 'Q_FE', 'React', 0);
INSERT IGNORE INTO statistics (statistics_id, type, description, count ) VALUES (21, 'Q_FE', 'Vue', 0);
INSERT IGNORE INTO statistics (statistics_id, type, description, count ) VALUES (22, 'Q_FE', 'Angular', 0);
INSERT IGNORE INTO statistics (statistics_id, type, description, count ) VALUES (23, 'Q_FE', 'HTML5', 0);
INSERT IGNORE INTO statistics (statistics_id, type, description, count ) VALUES (24, 'Q_FE', 'CSS3', 0);
INSERT IGNORE INTO statistics (statistics_id, type, description, count ) VALUES (25, 'Q_FE', 'JavaScriptES6Plus', 0);
INSERT IGNORE INTO statistics (statistics_id, type, description, count ) VALUES (26, 'Q_FE', 'TypeScript', 0);
INSERT IGNORE INTO statistics (statistics_id, type, description, count ) VALUES (27, 'Q_FE', 'SassScss', 0);
INSERT IGNORE INTO statistics (statistics_id, type, description, count ) VALUES (28, 'Q_FE', 'Webpack', 0);
INSERT IGNORE INTO statistics (statistics_id, type, description, count ) VALUES (29, 'Q_FE', 'ResponsiveWebDesign', 0);

-- QuestionList Insert
INSERT IGNORE INTO question_list (question_list_id, answer, correct_count, correct_rate, question, wrong_count, question_category_id)
VALUES
(1,"Immutable 객체는 상태를 변경할 수 없는 객체를 의미합니다. 이러한 객체는 생성된 이후 내부 상태를 수정할 수 없으며, 주로 final keyword로 field를 선언하여 불변성을 보장합니다. 대표적인 예로 String class가 있으며, 이 객체는 한 번 생성되면 변경할 수 없습니다.",
0,0,"Immutable 특징은?",0,1),

(2,"ORM은 객체지향 프로그래밍 언어의 객체를 관계형 데이터베이스의 테이블과 매핑하는 기법입니다. 이를 통해 개발자는 SQL 쿼리를 직접 작성하지 않고도 데이터베이스와 상호작용할 수 있습니다. 대표적인 ORM 프레임워크로는 Hibernate가 있습니다.",
0,0,"ORM이 무엇인지 설명해 주세요.",0,1),

(3,"Java는 객체지향 프로그래밍 언어입니다. 기본 자료형을 제외한 모든 요소들이 객체로 표현되고, 객체 지향 개념의 특징인 캡슐화, 상속, 다형성이 잘 적용된 언어입니다. 장점으로는 JVM(자바가상머신) 위에서 동작하기 때문에 운영체제에 독립적이고 GabageCollector를 통한 자동적인 메모리 관리가 가능합니다. 단점으로는 JVM 위에서 동작하기 때문에 실행 속도가 상대적으로 느리다. 다중 상속이나 타입에 엄격하며, 제약이 많습니다.",
0,0,"Java의 특징을 설명해 주세요",0,1),

(4,"CSR은 Client Side Rendering으로 클라이언트 측에서 렌더링이 이루어지는 방식으로, 서버는 HTML 페이지의 기본 구조와 필요한 데이터를 JSON 형태로 전달하고, 클라이언트(주로 JavaScript 프레임워크)가 이 데이터를 사용해 화면을 렌더링합니다. 이 방식은 사용자 경험이 향상될 수 있지만, 초기 로딩 시간이 길어질 수 있습니다. SSR은 Server Side Rendering으로 서버에서 렌더링이 이루어지는 방식으로, 서버가 완전히 렌더링된 HTML을 클라이언트에게 전달합니다. 클라이언트는 바로 렌더링된 페이지를 볼 수 있어 초기 로딩 시간이 짧지만, 이후의 상호작용에서는 CSR보다 느릴 수 있습니다.",
0,0,"Spring MVC에서 제공하는 CSR(Client Side Rendering)과 SSR(Server Side Rendering) 방식에 대해서 설명해 주세요.",0,3),

(5," RestController: 클래스를 RESTful 웹 서비스의 컨트롤러로 지정합니다. `@Controller`와 `@ResponseBody`를 합친 역할을 합니다. RequestMapping**: HTTP 요청을 특정 메서드나 클래스에 매핑하기 위해 사용됩니다. 주로 URI와 HTTP 메서드를 설정합니다. GetMapping, @PostMapping, @PutMapping, @DeleteMapping: 각각 GET, POST, PUT, DELETE HTTP 메서드에 대한 요청을 매핑합니다. PathVariable**: URI 경로의 변수 값을 메서드 인수로 매핑합니다. RequestParam**: 쿼리 매개변수나 폼 데이터를 메서드 인수로 매핑합니다. RequestBody**: 요청 본문을 Java 객체로 변환해 메서드 인수로 전달합니다.",
0,0,"Spring MVC에서 REST API 엔드포인트를 구현하기 위해 사용되는 애너테이션들에 대해서 설명해 주세요.",0,3),

(6," ResponseEntity는 Spring MVC에서 HTTP 응답을 표현하는 클래스입니다. 상태 코드, 헤더, 본문을 포함할 수 있으며, 클라이언트에 대한 완전한 HTTP 응답을 제어할 수 있게 해줍니다.",
0,0,"Controller에서 응답 객체로 사용하는 ResponseEntity에 대해서 설명해 주세요.",0,3),

(7,"REST 클라이언트는 RESTful 웹 서비스와 통신하기 위한 클라이언트 애플리케이션이나 라이브러리입니다. REST API에 요청을 보내고 응답을 받아 처리하는 역할을 합니다.",
0,0,"Rest Client가 무엇인지 설명해 주세요.",0,3),

(8," Spring에서는 `RestTemplate`과 `WebClient`를 REST 클라이언트로 사용합니다. RestTemplate : 간단한 동기식 REST 클라이언트로, Spring 5 이전에 널리 사용되었습니다. WebClient : Spring 5에서 도입된 비동기식, 반응형 REST 클라이언트로, 더 현대적인 사용 사례에 적합합니다.",
0,0,"Spring 에서 사용하는 Rest Client에 대해서 설명해 주세요.",0,3);