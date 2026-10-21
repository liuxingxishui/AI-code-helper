package org.example.aicodehelper.ai.arg;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

// 加载RAG
@Configuration
public class RagConfig {

    // 自动注入向量模型
    @Resource
    private EmbeddingModel kimiEmbeddingModel;

    // 向量存储：使用内存版向量库
    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }

    // 内容检索器:接收用户问题，返回与之相关的内容片段（Content）列表
    @Bean
    public ContentRetriever contentRetriever() {
        // 1.加载文档
        List<Document> documents = FileSystemDocumentLoader.loadDocuments("src/main/resources/docs");

        // 2.文档切割：每个文档按照段落进行切割，最大1000个字符，每次最多重叠200字符
        DocumentByParagraphSplitter documentByParagraphSplitter = new DocumentByParagraphSplitter(1000, 200);

        // 3.自定义文档加载器，将文档转化为向量并保存到向量数据库中
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(documentByParagraphSplitter)                                                                                                                                       // 指定文档切割器
                .textSegmentTransformer(textSegment -> TextSegment.from(textSegment.metadata().getString("file_name") + "\n" + textSegment.text(), textSegment.metadata()))     // 为每个切割文档碎片TextSegment添加文档名称作为元信息
                .embeddingModel(kimiEmbeddingModel)                                                                                                                                                  // 指定向量模型
                .embeddingStore(embeddingStore())                                                                                                                                                    // 指定向量存储
                .build();

        // 加载文档
        ingestor.ingest(documents);

        // 4.自定义内容加载器
        EmbeddingStoreContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()      // 创建检索器构建器
                .embeddingStore(embeddingStore())                                                       // 指定向量存储
                .embeddingModel(kimiEmbeddingModel)                                                     // 指定向量模型
                .maxResults(5)                                                                          // 限制最多5条结果
                .minScore(0.75)                                                                         // 过滤掉相似度小于0.75的结果
                .build();

        return contentRetriever;
    }
}