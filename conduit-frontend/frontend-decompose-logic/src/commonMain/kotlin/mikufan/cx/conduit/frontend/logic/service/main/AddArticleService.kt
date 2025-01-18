package mikufan.cx.conduit.frontend.logic.service.main

import mikufan.cx.conduit.common.ArticleDtoUtils
import mikufan.cx.conduit.frontend.logic.repo.api.ArticleApi


interface AddArticleService {
  suspend fun createArticle(
    title: String,
    description: String,
    body: String,
    tagList: List<String>
  )
}

class DefaultAddArticleService(
  private val articleApi: ArticleApi,
) : AddArticleService {

  override suspend fun createArticle(
    title: String,
    description: String,
    body: String,
    tagList: List<String>
  ) {
    val createArticleReq = ArticleDtoUtils.createArticleReq(
      title = title,
      description = description,
      body = body,
      tagList = tagList
    )

    articleApi.createArticle(createArticleReq)
  }
}