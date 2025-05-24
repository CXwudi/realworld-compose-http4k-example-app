package mikufan.cx.conduit.frontend.logic.service.main

import mikufan.cx.conduit.common.ArticleDto
import mikufan.cx.conduit.frontend.logic.component.main.feed.FullArticleInfo
import mikufan.cx.conduit.frontend.logic.repo.api.ArticleApi
import mikufan.cx.conduit.frontend.logic.repo.api.util.getOrThrow

interface ArticleDetailService {
  /**
   * Get the full article details by slug.
   */
  suspend fun getArticle(slug: String): FullArticleInfo
}

class DefaultArticleDetailService(
  private val articleApi: ArticleApi,
) : ArticleDetailService {

  override suspend fun getArticle(slug: String): FullArticleInfo {
    val articleRsp = articleApi.getArticle(slug)
    val articleDto = articleRsp.getOrThrow()
    return articleDto.article.toFullInfo()
  }
}

private fun ArticleDto.toFullInfo() = FullArticleInfo(
  authorThumbnail = author.image,
  authorUsername = author.username,
  title = title,
  description = description,
  bodyMarkdown = body ?: "", // body should not be null for individual article requests
  tags = tagList,
  createdAt = createdAt,
  slug = slug,
)
