# 🔐 安全配置指南

## 配置文件说明
本项目使用环境变量保护敏感信息，请不要提交真实密码到Git！

## 如何配置
1. 复制配置文件模板：
   \\\ash
   cp AIPM/src/main/resources/application.properties.example AIPM/src/main/resources/application.properties
   \\\

2. 编辑配置文件，设置你的数据库密码：
   \\\properties
   spring.datasource.password=你的数据库密码
   \\\

3. 如果需要DeepSeek API密钥，在代码中配置环境变量

## 安全注意事项
- ❌ 不要提交 \pplication.properties\ 到Git
- ✅ 只提交 \pplication.properties.example\ 模板
- ❌ 不要在代码中硬编码密码
- ✅ 使用环境变量或配置文件（被.gitignore忽略）

## 紧急情况
如果意外提交了密码，立即：
1. 从Git历史中清除文件
2. 修改数据库密码
3. 重新生成API密钥
