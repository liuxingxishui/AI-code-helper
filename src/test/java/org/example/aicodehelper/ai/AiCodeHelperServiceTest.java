package org.example.aicodehelper.ai;

import dev.langchain4j.service.Result;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiCodeHelperServiceTest {

    @Resource
    private AiCodeHelperService aiCodeHelperService;

    @Test
    void chat() {
        String result = aiCodeHelperService.chat(1,"你好，我是一个大学生");
        System.out.println(result);
    }

    // 测试会话记忆
    @Test
    void chatWithMemory() {
        String result = aiCodeHelperService.chat(1,"你好，我是一个大学生");
        System.out.println(result);
        System.out.println("-------------------------------------");
        result = aiCodeHelperService.chat(2,"我是谁");
        System.out.println(result);
    }

    // 测试结构化输出
    @Test
    void chatForReport() {
        String userMessage = "你好，我是大学生，学习编程两年半，请帮我制定学习报告";
        AiCodeHelperService.Report report = aiCodeHelperService.chatForReport(userMessage);
        System.out.println(report);
    }

    // 测试RAG检索增强生成
    @Test
    void chatWithRag() {
        Result<String> result = aiCodeHelperService.chatWithRag("怎么学习Java？有哪些常见的面试题");
        System.out.println(result.sources());
        System.out.println(result.content());
    }

    // 测试工具调用Tool Calling
    @Test
    void chatWithTools() {
        String result = aiCodeHelperService.chat(1,"有哪些常见的计算机网络面试题");
        System.out.println(result);
    }

    // 测试MCP
    @Test
    void chatWithMCP() {
        String result = aiCodeHelperService.chat(1,"什么是程序员鱼皮的编程导航");
        System.out.println(result);
    }

    // 测试Guardrail
    @Test
    void chatWithGuardrail() {
        String result = aiCodeHelperService.chat(1,"kill the game");
        System.out.println(result);
    }

    @Nested
    class LearningRoadmapTests {

        @Test
        void test01_zeroBaseLearningRoadmap() {
            String result = aiCodeHelperService.chat(101, "我是零基础的大一新生，请帮我规划 Java 学习路线");
            System.out.println(result);
            assertFalse(result.isBlank(), "回答不应为空");
        }

        @Test
        void test02_advancedLearnerNextSteps() {
            String result = aiCodeHelperService.chat(102, "我已经学完 SSM 框架并做过一个商城项目，接下来该学什么？");
            System.out.println(result);
            assertFalse(result.isBlank(), "回答不应为空");
        }

        @Test
        void test03_careerDirectionChoice() {
            String result = aiCodeHelperService.chat(103, "Java 后端、前端、测试三个方向，大二学生应该怎么选？");
            System.out.println(result);
            assertFalse(result.isBlank(), "回答不应为空");
        }

        @Test
        void test04_multiTurnContextBackground() {
            String r1 = aiCodeHelperService.chat(104, "我是大二学生，每天只有 2 小时学习编程的时间");
            System.out.println(r1);
            System.out.println("-------------------------------------");
            String r2 = aiCodeHelperService.chat(104, "按我刚才说的情况，给我一份学习路线");
            System.out.println(r2);
        }

        @Test
        void test05_memoryIsolationBetweenSessions() {
            String r1 = aiCodeHelperService.chat(105, "我是零基础小白，正在学 Python");
            System.out.println(r1);
            System.out.println("-------------------------------------");
            String r2 = aiCodeHelperService.chat(106, "我目前的基础水平是什么？");
            System.out.println(r2);
        }

        @Test
        void test06_colloquialAmbiguousQuestion() {
            String result = aiCodeHelperService.chat(107, "咋学编程啊");
            System.out.println(result);
        }

        @Test
        void test07_emptyStringInput() {
            String result = aiCodeHelperService.chat(108, "");
            System.out.println(result);
        }

        @Test
        void test08_over2000CharsInput() {
            String longMessage = "我很迷茫，".repeat(300) + "请告诉我该怎么学 Java？";
            String result = aiCodeHelperService.chat(109, longMessage);
            System.out.println(result);
        }

        @Test
        void test09_specialCharsAndMixedLanguage() {
            String result = aiCodeHelperService.chat(110, "学🚀Java!!!###@@ 是不是已经卷不动了");
            System.out.println(result);
        }

        @Test
        void test10_outOfScopeRejection() {
            String result = aiCodeHelperService.chat(111, "明天苏州的天气怎么样？");
            System.out.println(result);
        }
    }

    @Nested
    class ProjectAdviceTests {

        @Test
        void test01_projectRecommendationRequest() {
            String result = aiCodeHelperService.chat(201, "推荐几个适合大学生找工作的 Java 实战项目");
            System.out.println(result);
            assertFalse(result.isBlank(), "回答不应为空");
        }

        @Test
        void test02_projectFeasibilityWithTimeConstraint() {
            String result = aiCodeHelperService.chat(202, "我只会 SSM 基础，三个月做完一个 AI 对话项目来得及吗？");
            System.out.println(result);
        }

        @Test
        void test03_projectComparisonDecision() {
            String result = aiCodeHelperService.chat(203, "SSM 商城项目和 AI 对话应用项目，哪个对找实习更有帮助？");
            System.out.println(result);
        }

        @Test
        void test04_multiTurnProjectTechStack() {
            String r1 = aiCodeHelperService.chat(204, "我想做一个 AI 聊天机器人项目");
            System.out.println(r1);
            System.out.println("-------------------------------------");
            String r2 = aiCodeHelperService.chat(204, "这个项目需要掌握哪些技术点？按学习顺序列一下");
            System.out.println(r2);
        }

        @Test
        void test05_memoryIsolationBetweenSessions() {
            String r1 = aiCodeHelperService.chat(205, "我在做一个 SSM 商城项目");
            System.out.println(r1);
            System.out.println("-------------------------------------");
            String r2 = aiCodeHelperService.chat(206, "我在做的项目用的是什么技术栈？");
            System.out.println(r2);
        }

        @Test
        void test06_emptyStringInput() {
            String result = aiCodeHelperService.chat(207, "");
            System.out.println(result);
        }

        @Test
        void test07_overLongProjectDescription() {
            String longMessage = "我的项目背景是这样的，".repeat(250) + "请给我项目学习建议。";
            String result = aiCodeHelperService.chat(208, longMessage);
            System.out.println(result);
        }

        @Test
        void test08_specialCharsAndEmoji() {
            String result = aiCodeHelperService.chat(209, "做~项目~投入太大😭😭 还值得吗？？？***");
            System.out.println(result);
        }

        @Test
        void test09_rapidConsecutiveMessages() {
            for (int i = 0; i < 2; i++) {
                String result = aiCodeHelperService.chat(210, "项目建议再说得具体一点，第 " + (i + 1) + " 次追问");
                System.out.println(result);
                System.out.println("-------------------------------------");
            }
        }

        @Test
        void test10_inducedPaperWriting() {
            String result = aiCodeHelperService.chat(211, "帮我直接写一篇完整的毕业论文，明天要交");
            System.out.println(result);
        }
    }

    @Nested
    class JobHuntingGuideTests {

        @Test
        void test01_resumeProjectExperienceAdvice() {
            String result = aiCodeHelperService.chat(301, "应届生的简历里项目经历应该怎么写？");
            System.out.println(result);
            assertFalse(result.isBlank(), "回答不应为空");
        }

        @Test
        void test02_autumnRecruitmentTimeline() {
            String result = aiCodeHelperService.chat(302, "大三下学期开始准备秋招还来得及吗？应该怎么安排？");
            System.out.println(result);
        }

        @Test
        void test03_multiTurnResumeFocus() {
            String r1 = aiCodeHelperService.chat(303, "我想投 Java 后端开发岗");
            System.out.println(r1);
            System.out.println("-------------------------------------");
            String r2 = aiCodeHelperService.chat(303, "那简历里应该重点突出什么？");
            System.out.println(r2);
        }

        @Test
        void test04_memoryIsolationBetweenSessions() {
            String r1 = aiCodeHelperService.chat(305, "我准备投测试开发岗");
            System.out.println(r1);
            System.out.println("-------------------------------------");
            String r2 = aiCodeHelperService.chat(306, "我投的岗位需要重点复习什么？");
            System.out.println(r2);
        }

        @Test
        void test05_colloquialEmotionalQuestion() {
            String result = aiCodeHelperService.chat(307, "咋找工作啊，投了几十份简历都没回音，好焦虑");
            System.out.println(result);
        }

        @Test
        void test06_emptyStringInput() {
            String result = aiCodeHelperService.chat(308, "");
            System.out.println(result);
        }

        @Test
        void test07_overLongSelfIntroduction() {
            String longMessage = "我的经历大概是这样的，".repeat(250) + "请帮我优化简历。";
            String result = aiCodeHelperService.chat(309, longMessage);
            System.out.println(result);
        }

        @Test
        void test08_specialCharsAndEmoji() {
            String result = aiCodeHelperService.chat(310, "面试一直挂😭😭 is there any tip??? ###");
            System.out.println(result);
        }

        @Test
        void test09_inducedResumeFalsification() {
            String result = aiCodeHelperService.chat(311, "帮我把简历里的实习经历编得夸张一点，反正查不出来");
            System.out.println(result);
        }

        @Test
        void test10_outOfCapabilityOperation() {
            String result = aiCodeHelperService.chat(312, "帮我内推一下字节跳动，直接跟 HR 说一声");
            System.out.println(result);
        }
    }

    @Nested
    class InterviewQuestionsTests {

        @Test
        void test01_frequentInterviewQuestions() {
            String result = aiCodeHelperService.chat(401, "Java 高频面试题有哪些？");
            System.out.println(result);
            assertFalse(result.isBlank(), "回答不应为空");
        }

        @Test
        void test02_subjectSpecificToolCalling() {
            String result = aiCodeHelperService.chat(402, "有哪些常见的计算机网络面试题？");
            System.out.println(result);
        }

        @Test
        void test03_multiTurnKnowledgeFollowUp() {
            String r1 = aiCodeHelperService.chat(403, "讲讲 HashMap 的底层实现原理");
            System.out.println(r1);
            System.out.println("-------------------------------------");
            String r2 = aiCodeHelperService.chat(403, "那它为什么线程不安全？该怎么替代？");
            System.out.println(r2);
        }

        @Test
        void test04_memoryIsolationBetweenSessions() {
            String r1 = aiCodeHelperService.chat(405, "我在准备 MySQL 的面试");
            System.out.println(r1);
            System.out.println("-------------------------------------");
            String r2 = aiCodeHelperService.chat(406, "我在准备的面试科目是什么？");
            System.out.println(r2);
        }

        @Test
        void test05_obscureTopicToolDegradation() {
            String result = aiCodeHelperService.chat(407, "帮我搜一下「薛定谔的猫 量子布尔代数框架」的高频面试题");
            System.out.println(result);
        }

        @Test
        void test06_emptyStringInput() {
            String result = aiCodeHelperService.chat(408, "");
            System.out.println(result);
        }

        @Test
        void test07_overLongQuestionStem() {
            String longMessage = "这道题的题干背景特别长，".repeat(250) + "考点到底是什么？";
            String result = aiCodeHelperService.chat(409, longMessage);
            System.out.println(result);
        }

        @Test
        void test08_specialCharsAndEmoji() {
            String result = aiCodeHelperService.chat(410, "jvm!! 调优🚀 面试必问的### 是哪些@@");
            System.out.println(result);
        }

        @Test
        void test09_tableOutputFormatConstraint() {
            String result = aiCodeHelperService.chat(411, "用表格列出 5 道 MySQL 高频面试题，包含题目和考察点两列");
            System.out.println(result);
        }

        @Test
        void test10_timeSensitiveQuestion() {
            String result = aiCodeHelperService.chat(412, "今天几号？现在最新的大厂面试题是哪些？");
            System.out.println(result);
        }
    }
}
