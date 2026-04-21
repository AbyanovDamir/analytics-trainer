-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    role VARCHAR(50) DEFAULT 'student',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица заданий
CREATE TABLE IF NOT EXISTS tasks (
    id SERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    content JSONB NOT NULL,
    max_score INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица попыток
CREATE TABLE IF NOT EXISTS attempts (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    task_id INTEGER REFERENCES tasks(id) ON DELETE CASCADE,
    answer JSONB,
    score INTEGER,
    completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица прогресса
CREATE TABLE IF NOT EXISTS user_progress (
    user_id INTEGER PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    total_points INTEGER DEFAULT 0,
    tasks_completed INTEGER DEFAULT 0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Тестовые задания (3 тестовых)
INSERT INTO tasks (type, title, description, content, max_score) VALUES
('test', 'SQL JOINs', 'Выберите правильные варианты', '{"options": ["INNER JOIN возвращает только совпадающие строки", "LEFT JOIN возвращает все строки из левой таблицы", "CROSS JOIN - это декартово произведение", "RIGHT JOIN не существует"], "correct": [0,1,2]}', 100),
('test', 'Метрики продукта', 'Какие метрики относятся к Retention?', '{"options": ["DAU", "Churn Rate", "LTV", "ROI"], "correct": [1]}', 100),
('test', 'Типы аналитики', 'Выберите описательные типы аналитики', '{"options": ["Что произошло?", "Почему произошло?", "Что произойдёт?", "Что делать?"], "correct": [0]}', 100);

-- 3 задания на поиск ошибок
INSERT INTO tasks (type, title, description, content, max_score) VALUES
('error_spotting', 'Ошибки в SQL запросе', 'Найдите ошибки в запросе', '{"broken_code": "SELECT * FORM users WHRE id = 1", "expected_errors": ["FORM вместо FROM", "WHRE вместо WHERE"]}', 100),
('error_spotting', 'Ошибки в метриках', 'Найдите ошибки в расчётах', '{"broken_code": "Retention = new_users / total_users", "expected_errors": ["Retention рассчитывается как количество вернувшихся / количество новых"]}', 100),
('error_spotting', 'Ошибки в визуализации', 'Что не так с графиком?', '{"broken_code": "Ось Y начинается не с нуля", "expected_errors": ["Ось Y должна начинаться с 0"]}', 100);

-- 4 открытых задания
INSERT INTO tasks (type, title, description, content, max_score) VALUES
('open', 'Когортный анализ', 'Опишите процесс проведения когортного анализа', '{"hint": "Что такое когорты, шаги расчёта", "auto_check": false}', 100),
('open', 'A/B тестирование', 'Спроектируйте A/B тест для нового функционала', '{"hint": "Гипотеза, метрики, длительность", "auto_check": false}', 100),
('open', 'RFM-анализ', 'Как провести RFM-анализ клиентов?', '{"hint": "Recency, Frequency, Monetary", "auto_check": false}', 100),
('open', 'Построение дашборда', 'Опишите ключевые метрики для дашборда продаж', '{"hint": "Воронка, конверсии, средний чек", "auto_check": false}', 100);
