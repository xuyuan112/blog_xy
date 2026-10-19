-- ============================================================================
-- 个人全栈博客系统 - 初始化数据脚本
-- ============================================================================
-- 文件: 02-init-data.sql
-- 作用: 插入初始管理员账号、默认分类/标签、示例一言等基础数据
-- 执行顺序: 在 01-schema.sql 之后执行
-- 数据库: blog
--
-- 密码策略: MD5(32 位十六进制小写)
--   明文 "123456" 的 MD5 = e10adc3949ba59abbe56e057f20f883e
--
-- 执行方式:
--   mysql -u root -p < 02-init-data.sql
--   或在 MySQL 客户端中: SOURCE /path/to/02-init-data.sql;
-- ============================================================================

USE `blog`;

-- ----------------------------------------------------------------------------
-- 1. 初始管理员账号
-- ----------------------------------------------------------------------------
-- username: admin
-- password: 123456 (MD5: e10adc3949ba59abbe56e057f20f883e)
-- status: 1 (正常)
--
-- 注意: 此账号为超级管理员, 上线前务必修改密码或新建独立管理员后删除
-- ----------------------------------------------------------------------------

INSERT INTO `t_user` (`id`, `username`, `password`, `nickname`, `status`, `create_by`, `update_by`)
VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '站长', 1, 1, 1)
ON DUPLICATE KEY UPDATE
    `password`   = VALUES(`password`),
    `nickname`   = VALUES(`nickname`),
    `status`     = VALUES(`status`),
    `update_by`  = VALUES(`update_by`),
    `deleted`    = 0;

-- ----------------------------------------------------------------------------
-- 2. 默认文章分类
-- ----------------------------------------------------------------------------
-- 预置 4 个常用分类, 可在后台管理界面后续调整或新增
-- ----------------------------------------------------------------------------

INSERT INTO `t_category` (`id`, `name`, `description`, `sort`, `create_by`, `update_by`) VALUES
    (1, '后端开发',  'Java、Spring Boot、数据库等后端技术',           10, 1, 1),
    (2, '前端开发',  'Vue、React、CSS、浏览器等前端技术',             20, 1, 1),
    (3, '运维部署',  'Linux、Docker、CI/CD、服务器运维',              30, 1, 1),
    (4, '人文杂谈',  '技术之外的人文思考、读书笔记、生活随笔',         40, 1, 1)
ON DUPLICATE KEY UPDATE
    `description` = VALUES(`description`),
    `sort`        = VALUES(`sort`),
    `deleted`     = 0;

-- ----------------------------------------------------------------------------
-- 3. 默认文章标签
-- ----------------------------------------------------------------------------
-- 预置常用技术栈标签, color 采用 Element Plus 默认色板
-- ----------------------------------------------------------------------------

INSERT INTO `t_tag` (`id`, `name`, `color`, `create_by`, `update_by`) VALUES
    (1,  'Java',         '#F56C6C', 1, 1),
    (2,  'Spring Boot',  '#E6A23C', 1, 1),
    (3,  'MySQL',        '#409EFF', 1, 1),
    (4,  'Redis',        '#F56C6C', 1, 1),
    (5,  'Vue',          '#67C23A', 1, 1),
    (6,  'Docker',       '#409EFF', 1, 1),
    (7,  'Linux',        '#909399', 1, 1),
    (8,  'MyBatis',      '#E6A23C', 1, 1)
ON DUPLICATE KEY UPDATE
    `color`    = VALUES(`color`),
    `deleted`  = 0;

-- ----------------------------------------------------------------------------
-- 4. 示例一言数据
-- ----------------------------------------------------------------------------
-- 预置 10 条精选一言, 前台 GET /xuyuan/site/words/random 从该表随机取一条
-- ----------------------------------------------------------------------------

INSERT INTO `t_word` (`content`, `source`, `create_by`, `update_by`) VALUES
    ('路漫漫其修远兮, 吾将上下而求索。',                     '屈原《离骚》',           1, 1),
    ('不积跬步, 无以至千里; 不积小流, 无以成江海。',         '荀子《劝学》',           1, 1),
    ('纸上得来终觉浅, 绝知此事要躬行。',                     '陆游《冬夜读书示子聿》', 1, 1),
    ('Talk is cheap, show me the code.',                      'Linus Torvalds',        1, 1),
    ('程序是写给人读的, 只是顺便能在机器上运行。',           '《Structure and Interpretation of Computer Programs》', 1, 1),
    ('简洁是可靠的前提。',                                   'Edsger W. Dijkstra',    1, 1),
    ('过早优化是万恶之源。',                                 'Donald Knuth',          1, 1),
    ('代码胜于雄辩。',                                       '《人月神话》',           1, 1),
    ('工欲善其事, 必先利其器。',                             '孔子《论语·卫灵公》',    1, 1),
    ('海纳百川, 有容乃大; 壁立千仞, 无欲则刚。',             '林则徐',                 1, 1);

-- ----------------------------------------------------------------------------
-- 5. 站点默认配置
-- ----------------------------------------------------------------------------
-- t_site_config 按 key-value 存储, 这里预置基础配置项
-- ----------------------------------------------------------------------------

INSERT INTO `t_site_config` (`config_key`, `config_value`, `remark`, `create_by`, `update_by`) VALUES
    ('site.name',        'xuyuan\'s Blog',        '站点名称'),
    ('site.subtitle',    '用代码记录思考, 用文字丈量生活', '站点副标题'),
    ('site.keywords',    '博客, Java, Spring Boot, Vue, 全栈开发', '站点 SEO 关键词'),
    ('site.description', '一名热爱技术的全栈开发者, 在这里分享技术与生活的思考。', '站点 SEO 描述'),
    ('site.icp',         '',                       'ICP 备案号'),
    ('site.footer',      '© 2026 xuyuan. All rights reserved.', '页脚版权信息'),
    ('social.github',    'https://github.com/xuyuan', 'GitHub 主页'),
    ('social.email',     'xuyuan@example.com',    '联系邮箱')
ON DUPLICATE KEY UPDATE
    `config_value` = VALUES(`config_value`),
    `remark`       = VALUES(`remark`);

-- ----------------------------------------------------------------------------
-- 6. 关于我默认配置
-- ----------------------------------------------------------------------------
-- t_about 是单行配置, 预置一条记录, 后台 PUT /xuyuan/admin/about 更新
-- milestones / skills 字段为 JSON, 走对应 TypeHandler
-- ----------------------------------------------------------------------------

INSERT INTO `t_about` (`id`, `nickname`, `avatar`, `signature`, `intro`, `skills`, `milestones`, `create_by`, `update_by`) VALUES
    (
        1,
        'xuyuan',
        '',
        '一名热爱技术的全栈开发者, 相信代码能改变世界。',
        '你好, 我是 xuyuan。我是一名全栈工程师, 专注于 Java 后端与 Vue 前端开发。在这个博客里, 我会分享技术学习心得、项目实战经验, 以及一些对生活的思考。希望这里的内容能对你有所帮助。',
        '[{"name":"Java","level":90,"category":"后端"},{"name":"Spring Boot","level":85,"category":"后端"},{"name":"MySQL","level":80,"category":"数据库"},{"name":"Redis","level":75,"category":"数据库"},{"name":"Vue","level":85,"category":"前端"},{"name":"Docker","level":70,"category":"运维"}]',
        '[{"year":"2020","title":"开始学习编程","description":"第一次接触 Java, 从此踏入编程世界"},{"year":"2022","title":"入职某公司","description":"成为一名后端工程师, 参与多个项目开发"},{"year":"2024","title":"搭建个人博客","description":"使用 Spring Boot 3 + Vue 3 搭建了这个博客系统"},{"year":"2026","title":"持续输出","description":"分享技术与生活的思考, 持续学习中"}]',
        1,
        1
    )
ON DUPLICATE KEY UPDATE
    `nickname`  = VALUES(`nickname`),
    `signature` = VALUES(`signature`),
    `intro`     = VALUES(`intro`),
    `update_by` = VALUES(`update_by`);

-- ----------------------------------------------------------------------------
-- 验证查询(可选, 用于执行后确认数据已插入)
-- ----------------------------------------------------------------------------
-- SELECT id, username, nickname, status FROM t_user WHERE username = 'admin';
-- SELECT id, name, sort FROM t_category ORDER BY sort;
-- SELECT id, name, color FROM t_tag ORDER BY id;
-- SELECT COUNT(*) AS word_count FROM t_word;
-- SELECT config_key, config_value FROM t_site_config;
-- SELECT id, nickname, signature FROM t_about WHERE id = 1;
-- SELECT id, username, nickname, status FROM t_app_user WHERE username = 'demo';

-- ============================================================================
-- 初始化完成
-- ============================================================================
--
-- 默认管理员账号(后台管理端登录):
--   用户名: admin
--   密码:   123456
--
-- 示例前台用户账号(前台展示端登录):
--   用户名: demo
--   密码:   123456
--
-- 建议首次登录后立即修改密码
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 7. 示例前台用户账号
-- ----------------------------------------------------------------------------
-- username: demo
-- password: 123456 (MD5: e10adc3949ba59abbe56e057f20f883e)
-- status: 1 (正常)
--
-- 用于前台展示端登录测试, 评论/点赞/友链申请等互动场景
-- ----------------------------------------------------------------------------

INSERT INTO `t_app_user` (`id`, `username`, `password`, `nickname`, `email`, `status`) VALUES
    (1, 'demo', 'e10adc3949ba59abbe56e057f20f883e', '示例用户', 'demo@example.com', 1)
ON DUPLICATE KEY UPDATE
    `password` = VALUES(`password`),
    `nickname` = VALUES(`nickname`),
    `status`   = VALUES(`status`),
    `deleted`  = 0;
