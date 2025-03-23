package mikufan.cx.conduit.frontend.logic.service.main

import mikufan.cx.conduit.common.ArticleDto
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticleInfo
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticlesSearchFilter
import mikufan.cx.conduit.frontend.logic.repo.api.ArticleApi
import mikufan.cx.conduit.frontend.logic.repo.api.util.getOrThrow

class ArticlesListService(
  private val articleApi: ArticleApi,
) {

  suspend fun getArticles(
    searchFilter: ArticlesSearchFilter,
    pageSize: Int = 10,
    offset: Int,
  ): List<ArticleInfo> {
    val articlesRsp = articleApi.getArticles(
      tag = searchFilter.tag,
      author = searchFilter.author,
      favorited = searchFilter.favoritedByUsername,
      limit = pageSize,
      offset = offset,
    )

    val articlesDto = articlesRsp.getOrThrow()
    return articlesDto.articles.map { it.toInfo() }
  }

  suspend fun getInitialArticles(searchFilter: ArticlesSearchFilter) =
    getArticles(searchFilter, offset = 0)
}


private fun ArticleDto.toInfo() = ArticleInfo(
  authorThumbnail = author.image,
  authorUsername = author.username,
  title = title,
  description = description,
  tags = tagList,
  createdAt = createdAt,
  slug = slug,
)