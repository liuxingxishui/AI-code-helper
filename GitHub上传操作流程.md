# 上传项目到 GitHub 完整操作流程

> 已按本项目实际状态定制：本地已有 git 仓库（无提交、无远程）、git 已配置用户身份、`.gitignore` 已补充完毕。

## 前置说明（本项目当前状态）

| 项目 | 状态 |
|---|---|
| 本地 git 仓库 | ✅ 已存在（`master` 分支，0 次提交） |
| git 用户配置 | ✅ 流星细水 / 1499945768@qq.com |
| 远程仓库 | ❌ 未配置 |
| 密钥安全 | ✅ 已检查，暂存区无明文密钥（密钥走环境变量） |
| `.gitignore` | ✅ 已补充：node_modules、前端 dist、个人文件 |

## 第一步：在 GitHub 创建空仓库

1. 打开 https://github.com/new ，登录
2. 填写：
   - **Repository name**：`AI-code-helper`
   - 选 **Public**（简历项目建议公开，展示用）或 Private
   - ⚠️ **不要勾选** "Add a README"、".gitignore"、"License"（本地已有，避免冲突）
3. 点 **Create repository**，进入一个新页面，记下其中显示的仓库地址：
   ```
   https://github.com/你的用户名/AI-code-helper.git
   ```

## 第二步：首次推送（在项目根目录执行）

打开 Git Bash 或 IDEA Terminal（注意：不要开在它自己内嵌的 CMD 里执行不了 git？能执行，任意终端都行）：

```bash
cd C:\Users\Acer\Desktop\AI-code-helper

# 1. 暂存所有该上传的文件（.gitignore 里的会被自动跳过）
git add .

# 2. 检查将要提交的内容（确认没有 node_modules、简历、密钥）
git status

# 3. 创建第一次提交
git commit -m "feat: AI 编程小助手 - LangChain4j + Kimi K2.6（RAG/MCP/工具/护栏/SSE 流式）"

# 4. 关联 GitHub 远程仓库（替换成你第一步的真实地址）
git remote add origin https://github.com/你的用户名/AI-code-helper.git

# 5. 推送（首次推送建立关联）
git push -u origin master
```

第 5 步会弹出 **GitHub 登录窗口**（Git Credential Manager）：选 "Sign in with your browser" 完成浏览器授权即可，凭据会自动保存，以后推送不用再登录。

## 第三步：验证

1. 刷新 GitHub 仓库页面，应能看到全部代码和 README.md
2. 重点检查三件事：
   - ✅ 根目录显示 README 渲染效果
   - ✅ **没有** `node_modules/` 目录
   - ✅ **没有** `杨正驰-简历` 等个人文件

## 第四步：日常更新流程（以后每次改代码）

```bash
git add .                      # 暂存改动
git commit -m "fix: 修复流式解析尾字丢失"   # 提交（message 写清做了什么）
git push                       # 推送（第二次起不用 -u）
```

## IDEA 图形化方式（等价操作）

1. **Commit**：左侧 Commit 面板（Ctrl+K）→ 勾选文件 → 写 message → Commit
2. **Push**：Ctrl+Shift+K → Push；首次会提示 "Define remote"，粘贴 GitHub 仓库地址
3. 也可以直接 `Git → GitHub → Share Project on GitHub`（会帮你建仓库+推送一步到位，但分支管理不如手动清晰）

## 常见坑速查

| 问题 | 原因与解决 |
|---|---|
| `remote origin already exists` | 已关联过远程，`git remote remove origin` 后重新 add |
| 推送到 master 被拒绝 | GitHub 默认分支是 main：`git branch -M main` 改名后再 push |
| 弹窗让输入用户名密码 | 密码已不支持，选浏览器授权；或去 Settings→Developer settings→Tokens 建 PAT |
| 不小心提交了 node_modules | `git rm -r --cached node_modules` 后重新 commit |
| 提交了密钥 | 立即去 GitHub 删除该密钥并重新生成；仓库需清空历史（git filter-repo 或重建仓库） |

## 安全红线（每次 push 前自查）

- ❌ 任何 `sk-` 开头的 API 密钥
- ❌ 简历 PDF、个人照片等隐私文件
- ❌ `.idea/workspace.xml`（已在 .gitignore 中排除）
